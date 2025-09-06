/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.appversion;

import modelengine.fit.jober.aipp.converters.IconConverter;
import modelengine.jade.store.service.ToolService;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jade.waterflow.AippFlowDefinitionService;
import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jade.waterflow.service.FlowDefinitionService;
import modelengine.fit.jober.aipp.converters.ConverterFactory;
import modelengine.fit.jober.aipp.domains.appversion.repository.AppVersionRepository;
import modelengine.fit.jober.aipp.domains.chat.repository.AppChatRepository;
import modelengine.fit.jober.aipp.domains.definition.service.AppDefinitionService;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.factory.AppTemplateFactory;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.UploadedFileManageService;
import modelengine.fitframework.annotation.Value;
import modelengine.jade.common.globalization.LocaleService;
import modelengine.jade.knowledge.KnowledgeCenterService;
import modelengine.jade.store.service.AppService;
import modelengine.jade.store.service.PluginService;

import modelengine.fitframework.annotation.Component;

import java.util.Optional;

/**
 * {@link AppVersion} 工厂类.
 *
 * @author 张越
 * @since 2025-01-14
 */
@Component
public class AppVersionFactory {
    private final AppBuilderFormPropertyRepository formPropertyRepository;
    private final AppTaskService appTaskService;
    private final AppBuilderConfigRepository configRepository;
    private final AppBuilderFormRepository formRepository;
    private final AppBuilderConfigPropertyRepository configPropertyRepository;
    private final AppBuilderFlowGraphRepository flowGraphRepository;
    private final FlowsService flowsService;
    private final AppService appService;
    private final PluginService pluginService;
    private final ToolService toolService;
    private final AppChatRepository appChatRepository;
    private final AppDefinitionService appDefinitionService;
    private final AippLogService aippLogService;
    private final UploadedFileManageService uploadedFileManageService;
    private final AppTemplateFactory templateFactory;
    private final AppTaskInstanceService appTaskInstanceService;
    private final LocaleService localeService;
    private final AippModelCenter aippModelCenter;
    private final ConverterFactory converterFactory;
    private final AippFlowDefinitionService aippFlowDefinitionService;
    private final FlowDefinitionService flowDefinitionService;
    private final Integer maxQuestionLen;
    private final Integer maxUserContextLen;
    private final KnowledgeCenterService knowledgeCenterService;
    private final String resourcePath;
    private final IconConverter iconConverter;

    public AppVersionFactory(AppBuilderFormPropertyRepository formPropertyRepository, AppTaskService appTaskService,
            AppBuilderConfigRepository configRepository, AppBuilderFormRepository formRepository,
            AppBuilderConfigPropertyRepository configPropertyRepository,
            AppBuilderFlowGraphRepository flowGraphRepository, FlowsService flowsService, AppService appService,
            PluginService pluginService, ToolService toolService, AppChatRepository appChatRepository,
            AppDefinitionService appDefinitionService, AippLogService aippLogService,
            UploadedFileManageService uploadedFileManageService, AppTemplateFactory templateFactory,
            AppTaskInstanceService appTaskInstanceService, LocaleService localeService, AippModelCenter aippModelCenter,
            ConverterFactory converterFactory, AippFlowDefinitionService aippFlowDefinitionService,
            FlowDefinitionService flowDefinitionService,
            @Value("${app-engine.question.max-length}") Integer maxQuestionLen,
            @Value("${app-engine.user-context.max-length}") Integer maxUserContextLen,
            KnowledgeCenterService knowledgeCenterService, @Value("${app-engine.resource.path}") String resourcePath,
            IconConverter iconConverter) {
        this.formPropertyRepository = formPropertyRepository;
        this.appTaskService = appTaskService;
        this.configRepository = configRepository;
        this.formRepository = formRepository;
        this.configPropertyRepository = configPropertyRepository;
        this.flowGraphRepository = flowGraphRepository;
        this.flowsService = flowsService;
        this.appService = appService;
        this.pluginService = pluginService;
        this.toolService = toolService;
        this.appChatRepository = appChatRepository;
        this.appDefinitionService = appDefinitionService;
        this.aippLogService = aippLogService;
        this.uploadedFileManageService = uploadedFileManageService;
        this.templateFactory = templateFactory;
        this.appTaskInstanceService = appTaskInstanceService;
        this.localeService = localeService;
        this.aippModelCenter = aippModelCenter;
        this.converterFactory = converterFactory;
        this.aippFlowDefinitionService = aippFlowDefinitionService;
        this.flowDefinitionService = flowDefinitionService;
        this.maxQuestionLen = maxQuestionLen != null ? maxQuestionLen : 20000;
        this.maxUserContextLen = maxUserContextLen != null ? maxUserContextLen : 500;
        this.knowledgeCenterService = knowledgeCenterService;
        this.resourcePath = resourcePath;
        this.iconConverter = iconConverter;
    }

    /**
     * 创建 {@link AppVersion} 对象.
     *
     * @param data 数据类.
     * @param appVersionRepository {@link AppVersionRepository} 对象.
     * @return {@link AppVersion} 对象.
     */
    public AppVersion create(AppBuilderAppPo data, AppVersionRepository appVersionRepository) {
        return new AppVersion(Optional.ofNullable(data).orElseGet(AppBuilderAppPo::new), Dependencies.builder()
                .formPropertyRepository(this.formPropertyRepository)
                .appTaskService(this.appTaskService)
                .configRepository(this.configRepository)
                .formRepository(this.formRepository)
                .configPropertyRepository(this.configPropertyRepository)
                .flowGraphRepository(this.flowGraphRepository)
                .flowsService(this.flowsService)
                .appService(this.appService)
                .pluginService(this.pluginService)
                .toolService(this.toolService)
                .appVersionRepository(appVersionRepository)
                .appChatRepository(this.appChatRepository)
                .appDefinitionService(this.appDefinitionService)
                .aippLogService(this.aippLogService)
                .uploadedFileManageService(this.uploadedFileManageService)
                .templateFactory(this.templateFactory)
                .appTaskInstanceService(this.appTaskInstanceService)
                .localeService(this.localeService)
                .aippModelCenter(this.aippModelCenter)
                .converterFactory(this.converterFactory)
                .aippFlowDefinitionService(this.aippFlowDefinitionService)
                .flowDefinitionService(this.flowDefinitionService)
                .maxQuestionLen(this.maxQuestionLen)
                .maxUserContextLen(this.maxUserContextLen)
                .knowledgeCenterService(this.knowledgeCenterService)
                .resourcePath(this.resourcePath)
                .iconConverter(this.iconConverter)
                .build());
    }
}
