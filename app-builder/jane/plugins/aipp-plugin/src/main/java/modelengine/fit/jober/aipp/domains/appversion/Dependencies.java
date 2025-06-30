/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.appversion;

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
import modelengine.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.UploadedFileManageService;
import modelengine.jade.common.globalization.LocaleService;
import modelengine.jade.knowledge.KnowledgeCenterService;
import modelengine.jade.store.service.AppService;
import modelengine.jade.store.service.PluginService;

import lombok.Builder;
import lombok.Data;

/**
 * 注入依赖项.
 *
 * @author 张越
 * @since 2025-01-26
 */
@Data
@Builder
public class Dependencies {
    private AppBuilderFormPropertyRepository formPropertyRepository;
    private AppTaskService appTaskService;
    private AppBuilderConfigRepository configRepository;
    private AppBuilderFormRepository formRepository;
    private AppBuilderConfigPropertyRepository configPropertyRepository;
    private AppBuilderFlowGraphRepository flowGraphRepository;
    private FlowsService flowsService;
    private AppService appService;
    private PluginService pluginService;
    private ToolService toolService;
    private AppVersionRepository appVersionRepository;
    private AppChatRepository appChatRepository;
    private AppDefinitionService appDefinitionService;
    private AippLogService aippLogService;
    private UploadedFileManageService uploadedFileManageService;
    private AppTemplateFactory templateFactory;
    private AppTaskInstanceService appTaskInstanceService;
    private LocaleService localeService;
    private AippModelCenter aippModelCenter;
    private ConverterFactory converterFactory;
    private AippFlowDefinitionService aippFlowDefinitionService;
    private FlowDefinitionService flowDefinitionService;
    private Integer maxQuestionLen;
    private Integer maxUserContextLen;
    private KnowledgeCenterService knowledgeCenterService;
    private String resourcePath;
}
