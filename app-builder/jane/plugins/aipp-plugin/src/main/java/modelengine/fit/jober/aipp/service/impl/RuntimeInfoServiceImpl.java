/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static modelengine.fit.jober.aipp.constants.AippConst.BS_NODE_ID_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.NODE_START_TIME_KEY;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderRuntimeInfo;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.repository.AppBuilderRuntimeInfoRepository;
import modelengine.fit.jober.aipp.service.RuntimeInfoService;
import modelengine.fit.jober.aipp.util.ConvertUtils;
import modelengine.fit.jober.aipp.util.DataUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fit.jober.entity.consts.NodeTypes;
import modelengine.fit.jober.util.FlowDataUtils;
import modelengine.fit.runtime.entity.Parameter;

import lombok.AllArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 流程运行时信息实现类
 *
 * @author 邬涨财
 * @since 2024-12-17
 */
@Component
@AllArgsConstructor
public class RuntimeInfoServiceImpl implements RuntimeInfoService {
    private static final Logger LOGGER = Logger.get(RuntimeInfoServiceImpl.class);

    private final AppBuilderRuntimeInfoRepository runtimeInfoRepository;
    private final AppTaskService appTaskService;
    private final AppTaskInstanceService appTaskInstanceService;
    private final AppVersionService appVersionService;

    @Override
    public boolean isPublished(Map<String, Object> businessData) {
        String aippId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_ID_KEY));
        String version = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_VERSION_KEY));
        AppTask task =
                this.appTaskService.getLatest(aippId, version, DataUtils.getOpContext(businessData)).orElseThrow(() -> {
                    LOGGER.error("The app task is not found. [aippId={}, version={}]", aippId, version);
                    return new AippException(AippErrCode.APP_NOT_FOUND);
                });
        String appId = task.getEntity().getAppId();
        AppVersion appVersion = this.appVersionService.retrieval(appId);
        return appVersion.isPublished();
    }

    @Override
    public List<Parameter> buildParameters(Map<String, Object> map, String nodeId) {
        // 如果根据nodeId找不到，则说明节点没有出入参.
        List<Map<String, Object>> executeInfos = cast(
                FlowDataUtils.getValueByKeyPath(map, Arrays.asList("_internal", "executeInfo", nodeId), List.class)
                        .orElseGet(Collections::emptyList));
        if (executeInfos.isEmpty()) {
            return Collections.emptyList();
        }
        return executeInfos.stream().map(info -> {
            Parameter parameter = new Parameter();
            Optional.ofNullable(info.get("input")).ifPresent(in -> parameter.setInput(JsonUtils.toJsonString(in)));
            Optional.ofNullable(info.get("output")).ifPresent(out -> parameter.setOutput(JsonUtils.toJsonString(out)));
            return parameter;
        }).collect(Collectors.toList());
    }

    @Override
    public void insertRuntimeInfo(String instanceId, Map<String, Object> map, MetaInstStatusEnum status,
            String errorMsg, OperationContext context) {
        AppTaskInstance instance = this.appTaskInstanceService.getInstanceById(instanceId, context)
                .orElseThrow(() -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                        StringUtils.format("App task instance[{0}] not found.", instanceId)));
        String flowTraceId = instance.getEntity().getFlowTranceId();
        AppTask appTask = this.appTaskService.getTaskById(instance.getTaskId(), context)
                .orElseThrow(() -> new AippException(AippErrCode.TASK_NOT_FOUND));
        String appId = appTask.getEntity().getAppId();
        AppVersion appVersion = this.appVersionService.retrieval(appId);
        String nodeId = cast(map.getOrDefault(BS_NODE_ID_KEY, StringUtils.EMPTY));
        AppBuilderRuntimeInfo runtimeInfo = AppBuilderRuntimeInfo.builder()
                .traceId(flowTraceId)
                .flowDefinitionId(appTask.getEntity().getFlowDefinitionId())
                .instanceId(instanceId)
                .nodeId(nodeId)
                .nodeType(NodeTypes.STATE.getType())
                .startTime(cast(map.getOrDefault(NODE_START_TIME_KEY, ConvertUtils.toLong(LocalDateTime.now()))))
                .endTime(ConvertUtils.toLong(LocalDateTime.now()))
                .published(appVersion.isPublished())
                .parameters(this.buildParameters(map, nodeId))
                .errorMsg(errorMsg)
                .status(status.name())
                .createBy("system")
                .updateBy("system")
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();
        this.runtimeInfoRepository.insertOne(runtimeInfo);
    }
}
