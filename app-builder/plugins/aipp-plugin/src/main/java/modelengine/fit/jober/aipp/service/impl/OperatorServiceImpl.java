/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.service.LLMService;
import modelengine.fit.jober.aipp.service.OperatorService;
import modelengine.fit.jober.aipp.tool.FileExtractorContainer;
import modelengine.fit.jober.aipp.util.AippFileUtils;
import modelengine.fit.jober.aipp.util.AippStringUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtr;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文件处理服务实现
 *
 * @author 孙怡菲
 * @since 2024-05-10
 */
@Component
public class OperatorServiceImpl implements OperatorService {
    private static final Logger log = Logger.get(OperatorServiceImpl.class);

    private final Function<File, String> docOutlineExtractor = docFile -> {
        try {
            try (InputStream fis = new BufferedInputStream(Files.newInputStream(docFile.toPath()))) {
                if (FileMagic.valueOf(fis) == FileMagic.OOXML) {
                    try (XWPFDocument doc = new XWPFDocument(fis)) {
                        XWPFStyles styles = doc.getStyles();
                        List<XWPFParagraph> paragraphs = doc.getParagraphs();
                        // 最多6级标题
                        List<Integer> titleCounter = new ArrayList<>(Collections.nCopies(6, 0));
                        return paragraphs.stream()
                                // 过滤掉所有没有样式的正文
                                .filter(paragraph -> Objects.nonNull(styles.getStyle(paragraph.getStyleID())))
                                // 转换为形如 1.1 章节名 的标题
                                .map(paragraph -> extractHeadings(paragraph,
                                        styles.getStyle(paragraph.getStyleID()).getName().toLowerCase(Locale.ROOT),
                                        titleCounter))
                                // 对于不认识的样式返回的是null, 过滤掉
                                .filter(Objects::nonNull).collect(Collectors.joining("\n"));
                    }
                } else {
                    log.error("not support: {}, file name:{}", FileMagic.valueOf(fis).name(), docFile.getName());
                }
            }
        } catch (IOException e) {
            log.error("read doc fail.", e);
            throw new AippException(AippErrCode.EXTRACT_FILE_FAILED);
        }
        return "";
    };

    private final LLMService llmService;
    private final BrokerClient client;
    private final FileExtractorContainer fileExtractorContainer;
    private final Function<String, String> pdfExtractor = this::extractPdfFile;
    private final Function<String, String> wordExtractor = this::extractWordFile;
    private final Function<String, String> textExtractor = this::extractTextFile;
    private final EnumMap<FileType, Function<File, String>> outlineOperatorMap =
            new EnumMap<FileType, Function<File, String>>(FileType.class) {
                {
                    put(FileType.WORD, docOutlineExtractor);
                }
            };

    private final EnumMap<FileType, Function<String, String>> fileOperatorMap
            = new EnumMap<FileType, Function<String, String>>(FileType.class) {
        {
            put(FileType.PDF, pdfExtractor);
            put(FileType.WORD, wordExtractor);
            put(FileType.TXT, textExtractor);
            put(FileType.HTML, textExtractor);
            put(FileType.MARKDOWN, textExtractor);
            put(FileType.CSV, textExtractor);
        }
    };

    public OperatorServiceImpl(LLMService llmService, BrokerClient client,
            FileExtractorContainer fileExtractorContainer) {
        this.llmService = llmService;
        this.client = client;
        this.fileExtractorContainer = fileExtractorContainer;
    }

    private static String extractDocHandle(InputStream fis, String fileName) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(fis);
             XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(doc)) {
            // 去页眉页脚
            doc.getHeaderList().forEach(h -> h.setHeaderFooter(newCTHdrFtrInstance()));
            doc.getFooterList().forEach(h -> h.setHeaderFooter(newCTHdrFtrInstance()));
            // 文档内容不能为空
            String text = xwpfWordExtractor.getText();
            // 文档内容为空
            if (StringUtils.isBlank(text)) {
                log.info("file is empty, fileName: {}", fileName);
                return StringUtils.EMPTY;
            }
            // 过滤多余空行
            return deleteBlankLine(text);
        }
    }

    private static String extractHeadings(XWPFParagraph paragraph, String styleName, List<Integer> titleCounter) {
        // 样式名中包含title或者以heading开头的是标题
        if (styleName.contains("title") || styleName.startsWith("heading")) {
            String trimmedHeading = AippStringUtils.trimLine(paragraph.getText());
            if (trimmedHeading.isEmpty()) {
                return null;
            }
            int level;
            try {
                // heading后面会添加级别, 形如: Heading 1
                level = Integer.parseInt(styleName.substring(styleName.lastIndexOf(" ") + 1));
                // 超过title计数器的标题丢弃
                if (level > titleCounter.size()) {
                    return null;
                }
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                // 对于title这种后面没有数字级别的, 直接返回
                return trimmedHeading;
            }
            // 对于本级标题计数+1, 对于更小级别的归0
            titleCounter.set(level - 1, titleCounter.get(level - 1) + 1);
            for (int i = level; i < titleCounter.size(); ++i) {
                titleCounter.set(i, 0);
            }
            // 组成标题, 形如 2.1
            String currentTitle = titleCounter.subList(0, level)
                    .stream()
                    .map(i -> Objects.toString(Math.max(i, 1)))
                    .collect(Collectors.joining("."));
            // 返回完整标题, 形如 2.1 内容
            return String.join(" ", currentTitle, trimmedHeading);
        }
        return null;
    }

    private static String deleteBlankLine(String text) {
        String[] arrays = text.split("\n");
        List<String> allBlankList = Arrays.stream(arrays).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < allBlankList.size(); i++) {
            sBuilder.append(allBlankList.get(i));
            if (i != allBlankList.size() - 1) {
                sBuilder.append("\n");
            }
        }
        return sBuilder.toString();
    }

    private static CTHdrFtr newCTHdrFtrInstance() {
        return org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtr.Factory.newInstance();
    }

    @Override
    public String outlineExtractor(File file, FileType fileType) {
        return Optional.ofNullable(outlineOperatorMap.get(fileType)).map(f -> f.apply(file)).orElse("");
    }

    @Override
    public File createDoc(String instanceId, String fileName, String txt) throws IOException {
        final String paragraphPrefix = "        "; // 8个空格模拟2个中文占位符
        File docFile = AippFileUtils.createFile(instanceId, fileName + ".docx");
        try (XWPFDocument document = new XWPFDocument(); FileOutputStream of = new FileOutputStream(docFile)) {
            String[] txtLines = txt.split("\n");
            for (int i = 0; i < txtLines.length; i++) {
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(paragraphPrefix + txtLines[i]);
            }
            document.write(of);
        }
        return docFile;
    }

    /**
     * 提取文件内容。
     *
     * @param fileUrl 表示文件路径的 {@link String}.
     * @param optionalFileType 表示可选文件类型的 {@link FileType}。
     * @return 表示文件内容的 {@link String}。
     */
    public String fileExtractor(String fileUrl, Optional<FileType> optionalFileType) {
        if (optionalFileType.isPresent()) {
            Function<String, String> function = this.fileOperatorMap.get(optionalFileType.get());
            return fileExtractorContainer.extract(fileUrl, optionalFileType.get())
                    .or(() -> Optional.ofNullable(function).map(f -> f.apply(fileUrl)))
                    .orElse(StringUtils.EMPTY);

        }
        return this.extractTextFile(fileUrl);
    }

    private String iterPdf(PDDocument doc) throws IOException {
        int pages = doc.getNumberOfPages();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pages; i++) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            stripper.setStartPage(i + 1);
            stripper.setEndPage(i + 1);
            String text = stripper.getText(doc);
            sb.append(deleteBlankLine(text));
            if (i != pages - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private String extractPdfFile(String fileUrl) {
        File pdfFile = Paths.get(fileUrl).toFile();
        try {
            try (PDDocument doc = PDDocument.load(pdfFile)) {
                return this.iterPdf(doc);
            }
        } catch (IOException e) {
            log.error("read pdf fail.", e);
            throw new AippException(AippErrCode.EXTRACT_FILE_FAILED);
        }
    }

    private String extractWordFile(String fileUrl) {
        File docFile = Paths.get(fileUrl).toFile();
        try (InputStream fis = new BufferedInputStream(Files.newInputStream(docFile.toPath()))) {
            if (FileMagic.valueOf(fis) == FileMagic.OOXML) {
                return extractDocHandle(fis, docFile.getName());
            } else {
                log.error("not support: {}, file name:{}", FileMagic.valueOf(fis).name(), docFile.getName());
            }
        } catch (IOException e) {
            log.error("read doc fail.", e);
            throw new AippException(AippErrCode.EXTRACT_FILE_FAILED);
        }
        return "";
    }

    private String extractTextFile(String fileUrl) {
        File file = Paths.get(fileUrl).toFile();
        try {
            return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("io exception on file {}, reason {}", file.getName(), e.getMessage());
            throw new AippException(AippErrCode.EXTRACT_FILE_FAILED);
        }
    }
}
