/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.task_new.serializer.impl;

import modelengine.fit.task_new.entity.MetaInstance;
import modelengine.fit.task_new.po.MetaInstancePo;
import modelengine.fit.task_new.serializer.BaseSerializer;

import java.util.Objects;

/**
 * MetaInstance 序列化与反序列化实现类
 *
 * @author 邬涨财
 * @since 2025-03-31
 */
public class MetaInstanceSerializer implements BaseSerializer<MetaInstance, MetaInstancePo> {
    @Override
    public MetaInstancePo serialize(MetaInstance metaInstance) {
        if (metaInstance == null) {
            return null;
        }
        return MetaInstancePo.builder()
                .id(metaInstance.getId())
                .taskId(metaInstance.getTaskId())
                .taskName(metaInstance.getTaskName())
                .creator(metaInstance.getCreator())
                .createTime(metaInstance.getCreateTime())
                .modifyBy(metaInstance.getModifyBy())
                .modifyTime(metaInstance.getModifyTime())
                .finishTime(metaInstance.getFinishTime())
                .flowInstanceId(metaInstance.getFlowInstanceId())
                .currFormId(metaInstance.getCurrFormId())
                .currFormVersion(metaInstance.getCurrFormVersion())
                .currFormData(metaInstance.getCurrFormData())
                .smartFormTime(metaInstance.getSmartFormTime())
                .resumeDuration(metaInstance.getResumeDuration())
                .instanceStatus(metaInstance.getInstanceStatus())
                .instanceProgress(metaInstance.getInstanceProgress())
                .instanceAgentResult(metaInstance.getInstanceAgentResult())
                .instanceChildInstanceId(metaInstance.getInstanceChildInstanceId())
                .instanceCurrNodeId(metaInstance.getInstanceCurrNodeId())
                .build();
    }

    @Override
    public MetaInstance deserialize(MetaInstancePo metaInstancePo) {
        return Objects.isNull(metaInstancePo)
                ? MetaInstance.builder().build()
                : MetaInstance.builder()
                        .id(metaInstancePo.getId())
                        .taskId(metaInstancePo.getTaskId())
                        .taskName(metaInstancePo.getTaskName())
                        .creator(metaInstancePo.getCreator())
                        .createTime(metaInstancePo.getCreateTime())
                        .modifyBy(metaInstancePo.getModifyBy())
                        .modifyTime(metaInstancePo.getModifyTime())
                        .finishTime(metaInstancePo.getFinishTime())
                        .flowInstanceId(metaInstancePo.getFlowInstanceId())
                        .currFormId(metaInstancePo.getCurrFormId())
                        .currFormVersion(metaInstancePo.getCurrFormVersion())
                        .currFormData(metaInstancePo.getCurrFormData())
                        .smartFormTime(metaInstancePo.getSmartFormTime())
                        .resumeDuration(metaInstancePo.getResumeDuration())
                        .instanceStatus(metaInstancePo.getInstanceStatus())
                        .instanceProgress(metaInstancePo.getInstanceProgress())
                        .instanceAgentResult(metaInstancePo.getInstanceAgentResult())
                        .instanceChildInstanceId(metaInstancePo.getInstanceChildInstanceId())
                        .instanceCurrNodeId(metaInstancePo.getInstanceCurrNodeId())
                        .build();
    }
}
