/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.dto;

import modelengine.jade.app.engine.task.entity.EvalTaskStatusEnum;

import lombok.Data;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.NotBlank;
import modelengine.jade.app.engine.task.controller.EvalTaskController;

/**
 * 表示评估任务创建传输对象。
 *
 * @author 何嘉斌
 * @see EvalTaskController#createEvalTask。
 * @since 2024-08-09
 */
@Data
public class EvalTaskCreateDto {
    @Property(description = "评估任务名字", required = true)
    @NotBlank(message = "Task name cannot be empty.")
    private String name;

    @Property(description = "评估任务介绍", required = true)
    @NotBlank(message = "Task description cannot be empty.")
    private String description;

    @Property(description = "状态", required = true)
    private EvalTaskStatusEnum status;

    @Property(description = "应用唯一标识", required = true)
    @NotBlank(message = "The application id cannot be empty.")
    private String appId;

    @Property(description = "评估工作流唯一标识", required = true)
    @NotBlank(message = "The workflow id cannot be empty.")
    private String workflowId;
}