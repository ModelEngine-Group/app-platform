/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.task_new.entity;

import lombok.Builder;
import lombok.Data;
import modelengine.fitframework.annotation.Property;

import java.time.LocalDateTime;

/**
 * Meta 实例实体类
 *
 * @author 邬涨财
 * @since 2025-03-31
 */
@Data
@Builder
public class MetaInstance {
    @Property(description = "id")
    private String id;

    @Property(description = "meta id")
    private String taskId;

    @Property(description = "meta 实例名称")
    private String taskName;

    @Property(description = "创建人")
    private String creator;

    @Property(description = "创建时间")
    private LocalDateTime createTime;

    @Property(description = "更新人")
    private String modifyBy;

    @Property(description = "更新时间")
    private LocalDateTime modifyTime;

    @Property(description = "完成时间")
    private LocalDateTime finishTime;

    @Property(description = "flow实例id")
    private String flowInstanceId;

    @Property(description = "当前表单id")
    private String currFormId;

    @Property(description = "当前表单版本")
    private String currFormVersion;

    @Property(description = "当前表单数据")
    private String currFormData;

    @Property(description = "elsa 表单渲染时间戳")
    private LocalDateTime smartFormTime;

    @Property(description = "人工节点时间耗时")
    private String resumeDuration;

    @Property(description = "实例状态")
    private String instanceStatus;

    @Property(description = "实例进度")
    private String instanceProgress;

    @Property(description = "aipp agent结果")
    private String instanceAgentResult;

    @Property(description = "aipp子流程instanceId")
    private String instanceChildInstanceId;

    @Property(description = "当前节点id")
    private String instanceCurrNodeId;
}
