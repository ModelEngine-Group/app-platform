/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool.impl;

import modelengine.fel.core.format.OutputParser;
import modelengine.fel.core.format.json.JsonOutputParser;
import modelengine.fel.core.format.MarkdownCompatibleParser;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.converters.ConverterFactory;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto;
import modelengine.fit.jober.aipp.dto.AppCreateToolDto;
import modelengine.fit.jober.aipp.enums.AppTypeEnum;
import modelengine.fit.jober.aipp.service.AppBuilderAppService;
import modelengine.fit.jober.aipp.tool.AppBuilderAppTool;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.serialization.SerializationException;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * app工具实现类
 *
 * @author 邬涨财
 * @since 2024-05-20
 */
@Component
public class AppBuilderAppToolImpl implements AppBuilderAppTool {
    private static final String DEFAULT_TENANT_ID = "31f20efc7e0848deab6a6bc10fc3021e";
    private static final String SYSTEM_PROMPT_KEY = "systemPrompt";
    private static final String DEFAULT_TEMPLATE_ID = "df87073b9bc85a48a9b01eccc9afccc4";
    private static final String INDEX_URL_FORMAT =
            "应用创建成功！ \n访问地址：{0}//#//app-develop//31f20efc7e0848deab6a6bc10fc3021e//app-detail//{1}";
    private static final Logger log = Logger.get(AppBuilderAppToolImpl.class);
    private static final String APP_BUILT_TYPE = "app";
    private static final String APP_CATEGORY = "chatbot";

    private final AppBuilderAppService appService;
    private final String appEngineUrl;
    private final ObjectSerializer objectSerializer;
    private final AppVersionService appVersionService;
    private final ConverterFactory converterFactory;

    public AppBuilderAppToolImpl(AppBuilderAppService appService,
            @Fit(alias = "json") ObjectSerializer objectSerializer,
            @Value("${app-engine.endpoint}") String appEngineUrl, AppVersionService appVersionService,
            ConverterFactory converterFactory) {
        this.appService = appService;
        this.appEngineUrl = appEngineUrl;
        this.objectSerializer = objectSerializer;
        this.appVersionService = appVersionService;
        this.converterFactory = converterFactory;
    }

    @Override
    @Fitable("default")
    public String createApp(String appInfo, String userId) {
        AppCreateToolDto dto;
        try {
            OutputParser<AppCreateToolDto> parser =
                    new MarkdownCompatibleParser<>(JsonOutputParser.create(this.objectSerializer,
                            AppCreateToolDto.class), "json");
            dto = parser.parse(appInfo);
        } catch (SerializationException exception) {
            log.error("Failed to create app, parse json str error: {}", appInfo, exception);
            log.info("use default app attributes.");
            dto = AppCreateToolDto.builder()
                    .name("defaultApplicationCreatedAt" + System.currentTimeMillis())
                    .description("this is a default application.")
                    .icon("")
                    .greeting("hello world!")
                    .appType("")
                    .type(AppTypeEnum.APP.code())
                    .build();
        }
        dto.setAppType(StringUtils.isEmpty(dto.getAppType()) ? StringUtils.EMPTY : dto.getAppType());
        dto.setName(StringUtils.isEmpty(dto.getName()) ? StringUtils.EMPTY : dto.getName());
        dto.setGreeting(StringUtils.isEmpty(dto.getGreeting()) ? StringUtils.EMPTY : dto.getGreeting());
        dto.setDescription(StringUtils.isEmpty(dto.getDescription()) ? StringUtils.EMPTY : dto.getDescription());
        dto.setIcon(StringUtils.isEmpty(dto.getIcon()) ? StringUtils.EMPTY : dto.getIcon());
        dto.setType(StringUtils.isEmpty(dto.getType()) ? AppTypeEnum.APP.code() : dto.getType());
        OperationContext context = this.buildOperationContext(userId);
        AppBuilderAppDto appDto;
        try {
            AppVersion appVersion = this.appVersionService.create(DEFAULT_TEMPLATE_ID, this.convert(dto), context);
            appDto = this.converterFactory.convert(appVersion, AppBuilderAppDto.class);
        } catch (AippException exception) {
            log.error("Failed to create app: {}", exception.getMessage(), exception);
            return "创建应用失败：" + exception.getMessage();
        }
        if (StringUtils.isNotEmpty(dto.getSystemPrompt())) {
            this.updateConfig(dto, context, appDto, appDto.getConfig());
        }
        String appId = appDto.getId();
        return StringUtils.format(INDEX_URL_FORMAT, this.appEngineUrl, appId);
    }

    private void updateConfig(AppCreateToolDto dto, OperationContext context, AppBuilderAppDto appDto,
            AppBuilderConfigDto configDto) {
        if (configDto.getForm() == null) {
            return;
        }
        List<AppBuilderConfigFormPropertyDto> properties = appDto.getConfigFormProperties();
        if (CollectionUtils.isEmpty(properties)) {
            return;
        }
        Optional<AppBuilderConfigFormPropertyDto> systemPromptOptional = properties.stream()
                .filter(Objects::nonNull)
                .filter(property -> Objects.equals(property.getName(), SYSTEM_PROMPT_KEY))
                .findFirst();
        if (!systemPromptOptional.isPresent()) {
            return;
        }
        AppBuilderConfigFormPropertyDto systemPrompt = systemPromptOptional.get();
        systemPrompt.setDefaultValue(dto.getSystemPrompt());
        this.appService.updateConfig(appDto.getId(), configDto, properties, context);
    }

    private AppBuilderAppCreateDto convert(AppCreateToolDto dto) {
        return AppBuilderAppCreateDto.builder()
                .appType(dto.getAppType())
                .description(dto.getDescription())
                .greeting(dto.getGreeting())
                .icon(dto.getIcon())
                .name(dto.getName())
                .type(dto.getType())
                .appBuiltType(APP_BUILT_TYPE)
                .appCategory(APP_CATEGORY) // 修复新增字段后缺少赋值导致的异常问题
                .build();
    }

    private OperationContext buildOperationContext(String userId) {
        OperationContext context = new OperationContext();
        context.setTenantId(DEFAULT_TENANT_ID);
        context.setOperator(userId);
        return context;
    }
}
