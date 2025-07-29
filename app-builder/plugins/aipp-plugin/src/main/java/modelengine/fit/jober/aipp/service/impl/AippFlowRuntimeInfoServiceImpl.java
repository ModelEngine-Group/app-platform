/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.domain.AppBuilderRuntimeInfo;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.repository.AppBuilderRuntimeInfoRepository;
import modelengine.fit.jober.aipp.service.AippFlowRuntimeInfoService;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fit.jober.entity.consts.NodeTypes;
import modelengine.fit.runtime.entity.NodeInfo;
import modelengine.fit.runtime.entity.RuntimeData;
import modelengine.fit.waterflow.domain.enums.FlowNodeStatus;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 流程运行时服务接口.
 *
 * @author 张越
 * @since 2024-05-25
 */
@Component
public class AippFlowRuntimeInfoServiceImpl implements AippFlowRuntimeInfoService {
    private static final Logger LOGGER = Logger.get(AippFlowRuntimeInfoServiceImpl.class);

    private final AppBuilderRuntimeInfoRepository runtimeInfoRepository;
    private final AppTaskInstanceService appTaskInstanceService;
    private final AppTaskService appTaskService;

    public AippFlowRuntimeInfoServiceImpl(AppBuilderRuntimeInfoRepository runtimeInfoRepository,
            AppTaskInstanceService appTaskInstanceService, AppTaskService appTaskService) {
        this.runtimeInfoRepository = runtimeInfoRepository;
        this.appTaskInstanceService = appTaskInstanceService;
        this.appTaskService = appTaskService;
    }

    @Override
    public Optional<RuntimeData> getRuntimeData(String aippId, String version, String instanceId,
            OperationContext context) {
        AppTask task = this.appTaskService.getLatest(aippId, version, context).orElseThrow(() -> {
            LOGGER.error("The app task is not found. [appSuiteId={}, version={}]", aippId, version);
            return new AippException(AippErrCode.APP_NOT_FOUND_WHEN_DEBUG);
        });
        String versionId = task.getEntity().getTaskId();
        AppTaskInstance instance = this.appTaskInstanceService.getInstance(versionId, instanceId, context)
                .orElseThrow(() -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                        StringUtils.format("App task instance[{0}] not found.", instanceId)));
        String traceId = instance.getEntity().getFlowTranceId();
        List<AppBuilderRuntimeInfo> runtimeInfoList = this.runtimeInfoRepository.selectByTraceId(traceId);
        if (CollectionUtils.isEmpty(runtimeInfoList)) {
            return Optional.empty();
        }

        AppBuilderRuntimeInfo start = runtimeInfoList.stream()
                .filter(r -> r.getNodeType().equals(NodeTypes.START.getType()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(StringUtils.format("No START node info in runtime info.")));
        AppBuilderRuntimeInfo end = runtimeInfoList.get(runtimeInfoList.size() - 1);
        RuntimeData runtimeData = new RuntimeData();
        runtimeData.setStartTime(start.getStartTime());
        runtimeData.setEndTime(end.getEndTime());
        runtimeData.setFlowDefinitionId(end.getFlowDefinitionId());
        runtimeData.setPublished(end.isPublished());
        runtimeData.setTraceId(traceId);
        runtimeData.setAippInstanceId(end.getInstanceId());
        runtimeData.setExecuteTime(end.getEndTime() - start.getStartTime());
        runtimeData.setNodeInfos(runtimeInfoList.stream().map(this::toNodeInfo).collect(Collectors.toList()));
        runtimeData.setFinished(isFinished(runtimeInfoList));
        return Optional.of(runtimeData);
    }

    private boolean isFinished(List<AppBuilderRuntimeInfo> runtimeInfoList) {
        return runtimeInfoList.stream()
                .anyMatch(runtimeInfo -> runtimeInfo.getNextPositionId() == null || NodeTypes.END.getType()
                        .equals(runtimeInfo.getNodeType()) || FlowNodeStatus.ERROR.name()
                        .equals(runtimeInfo.getStatus()));
    }

    private NodeInfo toNodeInfo(AppBuilderRuntimeInfo info) {
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setNodeId(info.getNodeId());
        nodeInfo.setNodeType(info.getNodeType());
        nodeInfo.setStartTime(info.getStartTime());
        nodeInfo.setRunCost(info.getExecutionCost());
        nodeInfo.setNextLineId(info.getNextPositionId());
        nodeInfo.setErrorMsg(info.getErrorMsg());
        nodeInfo.setParameters(info.getParameters());
        nodeInfo.setStatus(info.getStatus());
        return nodeInfo;
    }
}
