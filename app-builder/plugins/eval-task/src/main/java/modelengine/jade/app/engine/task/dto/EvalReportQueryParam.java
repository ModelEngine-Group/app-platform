/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.dto;

import lombok.Data;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.Positive;
import modelengine.jade.app.engine.task.controller.EvalReportController;

/**
 * 表示评估任务报告查询参数。
 *
 * @author 何嘉斌
 * @see EvalReportController#queryEvalReport
 * @since 2024-08-14
 */
@Data
public class EvalReportQueryParam {
    @Property(description = "评估任务实例唯一标识", required = true)
    @Positive(message = "The instance id is invalid.")
    private Long instanceId;
}