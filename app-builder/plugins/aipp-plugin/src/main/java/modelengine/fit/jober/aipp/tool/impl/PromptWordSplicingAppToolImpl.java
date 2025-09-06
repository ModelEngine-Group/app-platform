/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.tool.impl;

import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.dto.AppBuilderPromptDto;
import modelengine.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.factory.AppBuilderAppFactory;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AopAippLogService;
import modelengine.fit.jober.aipp.service.AppBuilderPromptService;
import modelengine.fit.jober.aipp.tool.PromptWordSplicingAppTool;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI提示词拼接工具实现类
 * 1、使用流程中的app_id找到对应的模板
 * 2、根据模板改写流程中的用户输入
 *
 * @author 晏钰坤
 * @since 2024/5/31
 */
@Component
public class PromptWordSplicingAppToolImpl implements PromptWordSplicingAppTool {
    private static final Logger log = Logger.get(PromptWordSplicingAppToolImpl.class);

    private static final int TEMPLATE_TITLE_INDEX = 0;
    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("<步骤[：:](.*?)>");
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("<([^<>]*)>[:：]\\s*(.*?)(?=(<[^<>]*>[:：]\\s*|$))",
            Pattern.DOTALL);
    private static final String VARIABLE_TEMPLATE_FORMAT = "{{%s}}";
    private static final int VARIABLE_KEY_INDEX = 1;
    private static final int VARIABLE_VALUE_INDEX = 2;
    private static final String LINE_BREAK = "\n";
    private static final String EMPTY_STRING = "";
    private static final String COLON = ":";
    private static final String BACK_QUOTE = "```";

    private final AippLogService aippLogService;
    private final AopAippLogService aopAippLogService;
    private final AppBuilderAppFactory appFactory;

    public PromptWordSplicingAppToolImpl(AppBuilderPromptService appBuilderPromptService,
            AippLogService aippLogService, AopAippLogService aopAippLogService, AppBuilderAppFactory appFactory) {
        this.aippLogService = aippLogService;
        this.aopAippLogService = aopAippLogService;
        this.appFactory = appFactory;
    }

    @Override
    @Fitable("prompt.word.splice")
    public String promptWordSplice(String appId, String instanceId, String input) {
        // 判断输入中是否包含模板 不需要使用模板 直接返回String
        String title = getTitle(input);
        if (StringUtils.isEmpty(title)) {
            log.info("[promptWordSplice]: conversation without template, the input is {}", input);
            return input;
        }

        log.info("[promptWordSplice]: Start a conversation with template name {}", title);
        try {
            Map<String, String> toBeReplacedVariables = getToBeReplacedVariables(input, title);
            // 根据输入首行步骤名查询某个灵感模板的详情
            String promptTemplate = getPromptTemplate(appId, title);
            if (StringUtils.isEmpty(promptTemplate)) {
                log.info("[promptWordSplice]: the prompt template is null.");
                return input;
            }
            // 替换模板中的变量
            String overridePromptTemplate = overridePromptTemplate(toBeReplacedVariables, promptTemplate);
            log.info("[promptWordSplice]: overridePromptTemplate is {}", overridePromptTemplate);
            // 将改写后的写入历史记录
            writePromptLog(instanceId, overridePromptTemplate);
            return overridePromptTemplate;
        } catch (IllegalArgumentException e) {
            log.error("regex syntax error, error is {}", e.getMessage());
            return "AI提示词拼接工具失败：" + e.getMessage();
        } catch (Exception e) {
            log.error("Failed to prompt word splice, error is {}", e.getMessage(), e);
            return "AI提示词拼接工具失败：" + e.getMessage();
        }
    }

    private void writePromptLog(String instanceId, String promptTemplate) {
        List<AippInstLog> aippInstLogs = this.aippLogService.queryInstanceLogSince(instanceId, null);
        AippInstLog aippInstLog = aippInstLogs.get(0);
        AippLogCreateDto aippLogCreateDto = convertAippInstToDto(aippInstLog, promptTemplate);
        this.aopAippLogService.insertLog(aippLogCreateDto);
    }

    private String overridePromptTemplate(Map<String, String> toBeReplacedVariables, String promptTemplate) {
        String prompt = promptTemplate;
        for (Map.Entry<String, String> entry : toBeReplacedVariables.entrySet()) {
            String key = entry.getKey();
            if (prompt.contains(key)) {
                String msg = entry.getValue();
                prompt = prompt.replace(key, msg);
            } else {
                log.info("Input key {} can not found in prompt template.", key);
            }
        }
        return prompt;
    }

    private String getPromptTemplate(String appId, String title) {
        AppBuilderFormProperty inspiration = this.findInspirationProperty(appId);
        Object defaultValue = inspiration.getDefaultValue();
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(defaultValue));
        List<AppBuilderPromptDto.AppBuilderInspirationDto> inspirations = jsonObject.getObject("inspirations",
                new TypeReference<List<AppBuilderPromptDto.AppBuilderInspirationDto>>() {});
        return inspirations.stream()
                .filter(inspirationDto -> inspirationDto.getPrompt().startsWith(title))
                .map(AppBuilderPromptDto.AppBuilderInspirationDto::getPromptTemplate)
                .findFirst()
                .orElse(EMPTY_STRING);
    }

    private AppBuilderFormProperty findInspirationProperty(String appId) {
        AppBuilderApp appBuilderApp = this.appFactory.create(appId);
        List<AppBuilderFormProperty> formProperties = appBuilderApp.getFormProperties();
        return formProperties.stream()
                .filter(fp -> fp.getName().equals("inspiration"))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Don't find property 'inspiration'."));
    }

    private Map<String, String> getToBeReplacedVariables(String input, String title) {
        String variableInput = input.replaceAll(title, EMPTY_STRING);
        Map<String, String> toBeReplacedVariables = new HashMap<>();
        Matcher matcherVar = VARIABLE_PATTERN.matcher(variableInput);
        while (matcherVar.find()) {
            String key = String.format(VARIABLE_TEMPLATE_FORMAT, matcherVar.group(VARIABLE_KEY_INDEX));
            String value = matcherVar.group(VARIABLE_VALUE_INDEX).trim();
            toBeReplacedVariables.put(key, value);
        }
        return toBeReplacedVariables;
    }

    private String getTitle(String title) {
        Matcher matcher = TEMPLATE_PATTERN.matcher(title);
        return matcher.find() ? matcher.group(0) : EMPTY_STRING;
    }

    private AippLogCreateDto convertAippInstToDto(AippInstLog aippInstLog, String rewrittenInput) {
        Map<String, Object> map = JsonUtils.parseObject(aippInstLog.getLogData());
        map.put("msg", rewrittenInput);
        return AippLogCreateDto.builder()
                .aippId(aippInstLog.getAippId())
                .version(aippInstLog.getVersion())
                .aippType(aippInstLog.getAippType())
                .instanceId(aippInstLog.getInstanceId())
                .logType(AippInstLogType.HIDDEN_QUESTION.name())
                .logData(JsonUtils.toJsonString(map))
                .createUserAccount(aippInstLog.getCreateUserAccount())
                .path(aippInstLog.getPath())
                .build();
    }
}