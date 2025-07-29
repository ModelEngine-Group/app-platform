/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.serializer.impl;

import modelengine.fit.jober.aipp.domain.AppBuilderRuntimeInfo;
import modelengine.fit.jober.aipp.po.AppBuilderRuntimeInfoPo;
import modelengine.fit.jober.aipp.serializer.BaseSerializer;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.runtime.entity.Parameter;

import java.util.Objects;

/**
 * {@link AppBuilderRuntimeInfo} 以及 {@link AppBuilderRuntimeInfoPo} 之间互相转换的序列化器.
 *
 * @author 张越
 * @since 2024-07-29
 */
public class AppBuilderRuntimeInfoSerializer implements BaseSerializer<AppBuilderRuntimeInfo, AppBuilderRuntimeInfoPo> {
    @Override
    public AppBuilderRuntimeInfoPo serialize(AppBuilderRuntimeInfo appBuilderRuntimeInfo) {
        if (Objects.isNull(appBuilderRuntimeInfo)) {
            return null;
        }
        return AppBuilderRuntimeInfoPo.builder()
                .id(appBuilderRuntimeInfo.getId())
                .traceId(appBuilderRuntimeInfo.getTraceId())
                .flowDefinitionId(appBuilderRuntimeInfo.getFlowDefinitionId())
                .instanceId(appBuilderRuntimeInfo.getInstanceId())
                .nodeId(appBuilderRuntimeInfo.getNodeId())
                .nodeType(appBuilderRuntimeInfo.getNodeType())
                .startTime(appBuilderRuntimeInfo.getStartTime())
                .endTime(appBuilderRuntimeInfo.getEndTime())
                .status(appBuilderRuntimeInfo.getStatus())
                .published(appBuilderRuntimeInfo.isPublished() ? 1 : 0)
                .errorMsg(appBuilderRuntimeInfo.getErrorMsg())
                .parameters(JsonUtils.toJsonString(appBuilderRuntimeInfo.getParameters()))
                .createAt(appBuilderRuntimeInfo.getCreateAt())
                .updateAt(appBuilderRuntimeInfo.getUpdateAt())
                .createBy(appBuilderRuntimeInfo.getCreateBy())
                .updateBy(appBuilderRuntimeInfo.getUpdateBy())
                .nextPositionId(appBuilderRuntimeInfo.getNextPositionId())
                .build();
    }

    @Override
    public AppBuilderRuntimeInfo deserialize(AppBuilderRuntimeInfoPo dataObject) {
        return Objects.isNull(dataObject)
                ? AppBuilderRuntimeInfo.builder().build()
                : AppBuilderRuntimeInfo.builder()
                        .id(dataObject.getId())
                        .traceId(dataObject.getTraceId())
                        .flowDefinitionId(dataObject.getFlowDefinitionId())
                        .instanceId(dataObject.getInstanceId())
                        .nodeId(dataObject.getNodeId())
                        .nodeType(dataObject.getNodeType())
                        .startTime(dataObject.getStartTime())
                        .endTime(dataObject.getEndTime())
                        .status(dataObject.getStatus())
                        .published(dataObject.getPublished() == 1)
                        .errorMsg(dataObject.getErrorMsg())
                        .parameters(JsonUtils.parseArray(dataObject.getParameters(), Parameter[].class))
                        .createAt(dataObject.getCreateAt())
                        .updateAt(dataObject.getUpdateAt())
                        .createBy(dataObject.getCreateBy())
                        .updateBy(dataObject.getUpdateBy())
                        .nextPositionId(dataObject.getNextPositionId())
                        .build();
    }
}
