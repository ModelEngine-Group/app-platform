/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.serializer.impl;

import modelengine.fit.jober.aipp.domain.EndNodeStatus;
import modelengine.fit.jober.aipp.po.EndNodeStatusPo;
import modelengine.fit.jober.aipp.serializer.BaseSerializer;

import java.util.Objects;

/**
 * {@link EndNodeStatus} 以及 {@link EndNodeStatusPo} 之间互相转换的序列化器.
 *
 * @author 张越
 * @since 2026-04-01
 */
public class EndNodeStatusSerializer implements BaseSerializer<EndNodeStatus, EndNodeStatusPo> {
    @Override
    public EndNodeStatusPo serialize(EndNodeStatus endNodeStatus) {
        if (Objects.isNull(endNodeStatus)) {
            return null;
        }
        return EndNodeStatusPo.builder()
                .id(endNodeStatus.getId())
                .traceId(endNodeStatus.getTraceId())
                .endNodeId(endNodeStatus.getEndNodeId())
                .status(endNodeStatus.getStatus())
                .startTime(endNodeStatus.getStartTime())
                .endTime(endNodeStatus.getEndTime())
                .instanceId(endNodeStatus.getInstanceId())
                .flowDefinitionId(endNodeStatus.getFlowDefinitionId())
                .createAt(endNodeStatus.getCreateAt())
                .updateAt(endNodeStatus.getUpdateAt())
                .build();
    }

    @Override
    public EndNodeStatus deserialize(EndNodeStatusPo dataObject) {
        return Objects.isNull(dataObject)
                ? EndNodeStatus.builder().build()
                : EndNodeStatus.builder()
                        .id(dataObject.getId())
                        .traceId(dataObject.getTraceId())
                        .endNodeId(dataObject.getEndNodeId())
                        .status(dataObject.getStatus())
                        .startTime(dataObject.getStartTime())
                        .endTime(dataObject.getEndTime())
                        .instanceId(dataObject.getInstanceId())
                        .flowDefinitionId(dataObject.getFlowDefinitionId())
                        .createAt(dataObject.getCreateAt())
                        .updateAt(dataObject.getUpdateAt())
                        .build();
    }
}
