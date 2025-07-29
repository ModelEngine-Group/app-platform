/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.dto;

import lombok.Data;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.NotBlank;
import modelengine.fitframework.validation.constraints.NotEmpty;
import modelengine.fitframework.validation.constraints.Positive;
import modelengine.jade.app.engine.task.controller.EvalInstanceController;

import java.util.Map;

/**
 * 表示评估任务实例创建传输对象。
 *
 * @author 何嘉斌
 * @see EvalInstanceController#createEvalInstance
 * @since 2024-08-12
 */
@Data
public class EvalInstanceCreateDto {
    @Property(description = "评估任务唯一标识", required = true, defaultValue = "1")
    @Positive(message = "The task id is invalid.")
    private Long taskId;

    @Property(description = "应用编号", required = true)
    @NotBlank(message = "The application id cannot be blank.")
    private String appId;

    @Property(description = "评估任务编号", required = true)
    @NotBlank(message = "The application id cannot be blank.")
    private String workflowId;

    @Property(description = "评估实例初始化业务数据", required = true)
    @NotEmpty(message = "The init context cannot be empty.")
    private Map<String, Object> initContext;

    @Property(description = "是否调试启动", required = true)
    private Boolean isDebug;

    @Property(description = "租户唯一标识", required = true)
    @NotBlank(message = "The tenantId cannot be blank.")
    private String tenantId;

    @Property(description = "工作流实例唯一标识", required = true)
    @NotBlank(message = "Trace Id cannot be empty.")
    private String traceId;
}