/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool.cvAnalyzer.analyzer.entry;

import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AopAippLogService;
import modelengine.fit.jober.aipp.tool.cvAnalyzer.analyzer.dto.CvAnalyzerDto;
import modelengine.fit.jober.aipp.tool.cvAnalyzer.analyzer.header.enums.HeaderEnum;
import modelengine.fit.jober.aipp.tool.cvAnalyzer.analyzer.header.regexp.HeaderRegExp;
import modelengine.fit.jober.aipp.tool.cvAnalyzer.extractor.TextExtractor;
import modelengine.fit.jober.aipp.tool.cvAnalyzer.extractor.TextExtractorFactory;
import modelengine.fit.jober.aipp.util.AippLogUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;
import modelengine.fel.tool.annotation.Attribute;
import modelengine.fel.tool.annotation.Group;
import modelengine.fel.tool.annotation.ToolMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 简历解析插件实现类
 * 1、提取简历文件的文本内容
 * 2、解析简历章节内容
 * 3、填充提示词模板，输出提示词
 *
 * @author 杨璨宇
 * @since 2024/09/04
 */
@Component
@Group(name = "Resume_Parsing_Plugin_Impl")
public class CvAnalyzerAppToolImpl implements CvAnalyzerAppTool {
    public static final String HANDLE_FILE_KEY = "isFileHandled";

    private static final String FORMAT_ERROR_MESSAGE = "解析简历文件失败，如果是PDF格式，请尝试将PDF转换为Word格式，"
            + "再重新上传；如果已是Word格式，请向应用负责人反馈";
    private static final String CONTENT_ERROR_MESSAGE = "经过文件内容分析，上传的文件可能不是简历。";

    private static final String CV_CONTENT = "{简历内容}\n";

    private static final String INVALID_SYMBOL_PATTERN = "[^\\p{L}\\p{N}\\p{P}\\p{Z}\\p{Sm}\\n]";
    private static final String SPECIAL_SYMBOL_PATTERN = "[　•·_]";
    private static final String LEADING_NEW_LINE_PATTERN = "^[\\n\\r]+";

    // 用户上传超大文本的异常场景，先截断文本再在结尾补全提示词，让大模型判断文本是否为简历
    private static final int CV_TEXT_MAX_LEN = 6000;

    private final Logger logger = Logger.get(CvAnalyzerAppToolImpl.class);
    private final AippLogService aippLogService;
    private final AopAippLogService aopAippLogService;

    public CvAnalyzerAppToolImpl(AippLogService aippLogService, AopAippLogService aopAippLogService) {
        this.aippLogService = aippLogService;
        this.aopAippLogService = aopAippLogService;
    }

    public static boolean isUseTzFileHandler(Map<String, Object> variables) {
        if (variables == null) {
            return false;
        }
        return Boolean.TRUE.equals(variables.get(HANDLE_FILE_KEY));
    }

    @Override
    @Fitable("cv.analyzer")
    @ToolMethod(name = "AI简历解析插件", description = "解析简历内容，填充提示词模板，输出大模型提示词",
            extensions = {
                    @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "BUILTIN"),
            })
    @Property(description = "Map结构包含提示词以及插件处理过文件的标志位")
    public final CvAnalyzerDto analyzeCv(@Property(description = "简历文件URL", required = true) String fileUrl,
            @Property(description = "实例ID", required = true) String instanceId) {
        if (!isValidFilePath(fileUrl)) {
            logger.error("[CV Analyzer] invalid file URL: {}", fileUrl);
            return new CvAnalyzerDto(false, StringUtils.EMPTY, StringUtils.EMPTY);
        }

        String cvText = extractText(fileUrl);
        if (StringUtils.isEmpty(cvText.trim())) {
            logger.error("[CV Analyzer] failed to extract text from file: {}", fileUrl);
            return new CvAnalyzerDto(false, StringUtils.EMPTY, FORMAT_ERROR_MESSAGE);
        }

        String cleanedCvText = cleanCvText(cvText);
        Map<String, String> sections = extractSections(cleanedCvText);
        String prompt = buildPrompt(cleanedCvText);

        AippLogUtils.writePromptLog(instanceId, prompt, this.aippLogService, this.aopAippLogService);
        return new CvAnalyzerDto(true, prompt, isCvFile(sections) ? StringUtils.EMPTY : CONTENT_ERROR_MESSAGE);
    }

    private String extractText(String filePath) {
        TextExtractor extractor = TextExtractorFactory.getTextExtractor(filePath);
        if (extractor == null) {
            logger.error("[CV Analyzer] failed to get text extractor for file: {}", filePath);
            return StringUtils.EMPTY;
        }

        try {
            return extractor.extractText();
        } catch (IOException e) {
            logger.error("[CV Analyzer] failed to extract text from file: {}", filePath);
            return StringUtils.EMPTY;
        }
    }

    protected String cleanCvText(String cvText) {
        String noInvalidSymbols = cvText.replaceAll(INVALID_SYMBOL_PATTERN, StringUtils.EMPTY);
        String noSpecialSymbols = noInvalidSymbols.replaceAll(SPECIAL_SYMBOL_PATTERN, StringUtils.EMPTY);
        String lessSpaces = noSpecialSymbols.replaceAll(" +", " ");
        return Arrays.stream(lessSpaces.split("\n"))
                .map(String::trim)
                .filter(e -> !e.isEmpty())
                .collect(Collectors.joining("\n"));
    }

    private Map<String, String> extractSections(String textContent) {
        int flags = Pattern.MULTILINE | Pattern.CASE_INSENSITIVE;
        Matcher matcher = Pattern.compile(HeaderRegExp.getOverallRegExp(), flags).matcher(textContent);

        List<Integer> startIndexes = new ArrayList<>();
        List<String> sectionNames = new ArrayList<>();

        while (matcher.find()) {
            startIndexes.add(matcher.start());
            sectionNames.add(matcher.group());
        }
        return buildSections(startIndexes, sectionNames, textContent);
    }

    private Map<String, String> buildSections(List<Integer> startIndexes, List<String> sectionNames,
            String textContent) {
        Map<String, String> sectionMap = new LinkedHashMap<>();
        for (int i = 0; i < sectionNames.size(); ++i) {
            String sectionHeader = sectionNames.get(i);
            String processedHeader = processSectionHeader(sectionHeader);

            int start = findLineStartIdx(startIndexes.get(i), textContent);
            int end = (i + 1 < startIndexes.size()) ? startIndexes.get(i + 1) : textContent.length();
            String newSectionText = textContent.substring(start, end);
            String curSectionText = sectionMap.getOrDefault(processedHeader, StringUtils.EMPTY);
            String processedText = processSectionText(processedHeader, curSectionText, newSectionText);

            sectionMap.put(processedHeader, processedText);
        }
        return sectionMap;
    }

    private int findLineStartIdx(int startIdx, String textContent) {
        int newlineIndex = -1;
        for (int i = startIdx; i >= 0; --i) {
            char curChar = textContent.charAt(i);
            if (curChar == '\n' || curChar == '\r') {
                newlineIndex = i;
                break;
            }
        }
        return newlineIndex + 1;
    }

    private String processSectionHeader(String sectionHeader) {
        return HeaderEnum.normalizeHeader(sectionHeader);
    }

    private String processSectionText(String sectionHeader, String curSectionText, String newSectionText) {
        String result;
        if (StringUtils.isEmpty(curSectionText)) {
            result = replaceHeader(newSectionText, sectionHeader);
        } else {
            result = curSectionText + removeLeadingNewLines(replaceHeader(newSectionText, StringUtils.EMPTY));
        }

        return result.endsWith("\n") ? result : result + "\n";
    }

    private String buildPrompt(String cvText) {
        return CV_CONTENT.replaceAll("\\{简历内容\\}", truncateText(cvText));
    }

    private String replaceHeader(String sectionText, String replacement) {
        if (StringUtils.isEmpty(sectionText)) {
            return replacement;
        }

        int newlinePos = sectionText.indexOf("\n");
        String curHeader = newlinePos == -1 ? sectionText : sectionText.substring(0, newlinePos);
        return sectionText.replace(curHeader, replacement);
    }

    private String removeLeadingNewLines(String text) {
        return text.replaceFirst(LEADING_NEW_LINE_PATTERN, StringUtils.EMPTY);
    }

    private boolean isValidFilePath(String filePath) {
        return !StringUtils.isEmpty(filePath) && (filePath.endsWith(".pdf") || filePath.endsWith(".docx"));
    }

    private boolean isCvFile(Map<String, String> sections) {
        Set<String> sectionHeaders = sections.keySet();
        return sectionHeaders.contains(HeaderEnum.EDUCATION.getHeader()) && (
                sectionHeaders.contains(HeaderEnum.PROFESSION.getHeader())
                        || sectionHeaders.contains(HeaderEnum.PROJECT.getHeader()));
    }

    private String truncateText(String text) {
        return text.length() <= CV_TEXT_MAX_LEN ? text : text.substring(0, CV_TEXT_MAX_LEN);
    }
}