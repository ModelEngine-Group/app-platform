/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.appversion.publish;

import static modelengine.fit.jober.aipp.enums.AippMetaStatusEnum.ACTIVE;
import static modelengine.fit.jober.aipp.enums.AippTypeEnum.NORMAL;

import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.PublishContext;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.util.FlowInfoUtil;

import lombok.AllArgsConstructor;
import modelengine.fitframework.log.Logger;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * 任务修改发布器.
 *
 * @author 张越
 * @since 2025-01-16
 */
@AllArgsConstructor
public class TaskPublisher implements Publisher {
    private static final Logger log = Logger.get(TaskPublisher.class);

    private final AppTaskService appTaskService;

    @Override
    public void publish(PublishContext context, AppVersion appVersion) {
        FlowInfo flowInfo = context.getFlowInfo();
        OperationContext operationContext = context.getOperationContext();

        // 清除所有的preview任务.
        CompletableFuture.runAsync(
                () -> this.appTaskService.getPreviewTasks(appVersion.getData().getAppSuiteId(), operationContext)
                        .forEach(t -> t.cleanResource(operationContext)));

        // 创建任务.
        AppTask createArgs = AppTask.asCreateEntity()
                .setName(context.getPublishData().getName())
                .setStatus(ACTIVE.getCode())
                .setDescription(context.getPublishData().getDescription())
                .setIcon(context.getPublishData().getIcon())
                .setPublishTime(LocalDateTime.now().toString())
                .setPublishDescription(context.getPublishData().getPublishedDescription())
                .setPublishLog(context.getPublishData().getPublishedUpdateLog())
                .setVersion(context.getPublishData().getVersion())
                .setAttributeVersion(context.getPublishData().getVersion())
                .setAppId(context.getPublishData().getId())
                .setUniqueName(appVersion.getData().getUniqueName())
                .fetch(FlowInfoUtil.buildAippNodeForms(flowInfo, appVersion.getFormProperties()))
                .setAippType(NORMAL.name())
                .setFlowConfigId(flowInfo.getFlowId())
                .setFlowDefinitionId(flowInfo.getFlowDefinitionId())
                .setAppSuiteId(appVersion.getData().getAppSuiteId())
                .build();
        log.debug("create aipp, task info {}", createArgs.getEntity().toString());
        this.appTaskService.createTask(createArgs, context.getOperationContext());
    }
}
