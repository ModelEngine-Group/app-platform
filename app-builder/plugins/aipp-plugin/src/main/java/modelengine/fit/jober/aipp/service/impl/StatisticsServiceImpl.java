/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.condition.AppQueryCondition;
import modelengine.fit.jober.aipp.dto.StatisticsDTO;
import modelengine.fit.jober.aipp.enums.AppState;
import modelengine.fit.jober.aipp.enums.AppTypeEnum;
import modelengine.fit.jober.aipp.service.AppBuilderAppService;
import modelengine.fit.jober.aipp.service.AppBuilderFormService;
import modelengine.fit.jober.aipp.service.StatisticsService;
import modelengine.jade.store.service.PluginService;
import modelengine.jade.store.service.support.DeployStatus;

import modelengine.fitframework.annotation.Component;

/**
 * Statistics相关服务实现
 *
 * @author 陈潇文
 * @since 2024-12-26
 */
@Component
public class StatisticsServiceImpl implements StatisticsService {
    private static final String RUNTIME = "runtime";

    private final PluginService pluginService;
    private final AppBuilderFormService appBuilderFormService;
    private final AppBuilderAppService appBuilderAppService;

    public StatisticsServiceImpl(PluginService pluginService, AppBuilderFormService appBuilderFormService,
            AppBuilderAppService appBuilderAppService) {
        this.pluginService = pluginService;
        this.appBuilderFormService = appBuilderFormService;
        this.appBuilderAppService = appBuilderAppService;
    }

    @Override
    public StatisticsDTO getStatistics(OperationContext operationContext) {
        String tenantId = operationContext.getTenantId();
        long appNum = this.appBuilderAppService.getAppCount(tenantId,
                AppQueryCondition.builder().type(AppTypeEnum.APP.code()).build());
        long publishedAppNum = this.appBuilderAppService.getAppCount(tenantId,
                AppQueryCondition.builder().type(AppTypeEnum.APP.code()).state(AppState.PUBLISHED.getName()).build());
        long formNum = this.appBuilderFormService.countByType(RUNTIME, operationContext.getTenantId());
        int deployedPluginNum = this.pluginService.getPluginsCount(DeployStatus.DEPLOYED);
        int undeployedPluginNum = this.pluginService.getPluginsCount(DeployStatus.UNDEPLOYED);
        int deployingPluginNum = this.pluginService.getPluginsCount(DeployStatus.DEPLOYING);
        int deploymentFailedPluginNum = this.pluginService.getPluginsCount(DeployStatus.DEPLOYMENT_FAILED);
        return StatisticsDTO.builder()
                .publishedAppNum(publishedAppNum)
                .unpublishedAppNum(appNum - publishedAppNum)
                .formNum(formNum)
                .pluginNum(deployedPluginNum + undeployedPluginNum + deployingPluginNum + deploymentFailedPluginNum)
                .build();
    }
}
