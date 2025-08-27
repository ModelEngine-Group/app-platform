/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import cn.idev.excel.ExcelReader;
import cn.idev.excel.FastExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.DataFormatData;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import cn.idev.excel.read.listener.ReadListener;
import cn.idev.excel.read.metadata.ReadSheet;
import cn.idev.excel.util.DateUtils;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.service.LLMService;
import modelengine.fit.jober.aipp.service.OperatorService;
import modelengine.fit.jober.aipp.util.AippFileUtils;
import modelengine.fit.jober.aipp.util.AippStringUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtr;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    private final Function<String, String> pdfExtractor = this::extractPdfFile;
    private final Function<String, String> excelExtractor = this::extractExcelFile;
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
            put(FileType.EXCEL, excelExtractor);
            put(FileType.TXT, textExtractor);
            put(FileType.HTML, textExtractor);
            put(FileType.MARKDOWN, textExtractor);
            put(FileType.CSV, textExtractor);
        }
    };

    public OperatorServiceImpl(LLMService llmService, BrokerClient client) {
        this.llmService = llmService;
        this.client = client;
    }

    private static String getCellValueAsString(ReadCellData<?> cell) {
        switch (cell.getType()) {
            case STRING:
                return cell.getStringValue();
            case NUMBER:
                DataFormatData fmt = cell.getDataFormatData();
                short formatIndex = fmt.getIndex();
                String formatString = fmt.getFormat();
                if (DateUtils.isADateFormat(formatIndex,formatString)) {
                    double value = cell.getNumberValue().doubleValue();
                    Date date = DateUtils.getJavaDate(value,true);
                    return new SimpleDateFormat("yyyy-MM-dd").format(date);
                } else {
                    BigDecimal num = cell.getNumberValue();
                    return num.stripTrailingZeros().toPlainString();
                }
            case BOOLEAN:
                return Boolean.toString(cell.getBooleanValue());
            default:
                return "";
        }
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
            return Optional.ofNullable(function).map(f -> f.apply(fileUrl)).orElse(StringUtils.EMPTY);
        }
        return this.extractTextFile(fileUrl);
    }

    /**
     * 从指定路径的 Excel 文件中提取内容，并返回为字符串形式。
     * 实现方式：
     * 基于 fast-excel 包，使用流式读取（ReadListener）逐行解析，避免一次性加载整表造成的内存开销。
     * 每行数据会被转换为以制表符（\t）分隔的文本，并在行末追加换行符。
     * 支持多 sheet 解析，会依次读取工作簿中的每一个 sheet。
     *
     * @param fileUrl 表示文件路径的 {@link String}.
     * @return 表示文件内容的 {@link String}。
     * @throws RuntimeException 当文件读取或解析失败时抛出
     */
    private String extractExcelFile(String fileUrl) {
        File file = Paths.get(fileUrl).toFile();
        StringBuilder excelContent = new StringBuilder();
        ReadListener<Map<Integer, String>> listener = new ReadListener<>() {
            @Override
            public void invoke(Map<Integer, String> data, AnalysisContext context) {
                String line = data.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(e -> e.getValue() == null ? "" : e.getValue())
                        .collect(Collectors.joining("\t"));
                excelContent.append(line).append('\n');
            }
            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
            }
        };
        try (InputStream is = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
            ExcelReader reader = FastExcel.read(is, listener)
                    .registerConverter(new CustomCellStringConverter())
                    .headRowNumber(0)
                    .build();

            List<ReadSheet> sheets = reader.excelExecutor().sheetList();
            for (ReadSheet meta : sheets) {
                excelContent.append("Sheet ").append(meta.getSheetNo() + 1).append(':').append('\n');
                ReadSheet readSheet = FastExcel.readSheet(meta.getSheetNo())
                        .headRowNumber(0)
                        .build();
                reader.read(readSheet);
            }
            excelContent.append('\n');
            reader.finish(); // 关闭资源
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return excelContent.toString();
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

    /**
     * 自定义单元格数据转换器。
     * 将 Excel 单元格数据统一转换为字符串，避免数值/日期等类型在读取时格式不一致的问题。
     * 缺点：由于采用fast excel包,没有 FORMULA类,会将公式单元格自动计算为值
     *
     */
    public static class CustomCellStringConverter implements Converter<String> {
        @Override
        public Class<String> supportJavaTypeKey() {
            return String.class;
        }
        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return null;
        }
        @Override
        public String convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                                        GlobalConfiguration globalConfiguration) {
            return getCellValueAsString(cellData);
        }
    }
}
