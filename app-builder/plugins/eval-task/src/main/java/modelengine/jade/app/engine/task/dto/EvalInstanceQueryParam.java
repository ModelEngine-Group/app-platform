/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.dto;

import modelengine.jade.common.query.PageQueryParam;

import lombok.Data;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.Positive;
import modelengine.jade.app.engine.task.controller.EvalInstanceController;

/**
 * 表示评估任务实例的查询参数。
 *
 * @author 兰宇晨
 * @see EvalInstanceController#queryEvalInstance
 * @since 2024-08-15
 */
@Data
public class EvalInstanceQueryParam extends PageQueryParam {
    @Property(description = "评估任务编号", required = true)
    @Positive(message = "The task id is invalid.")
    private Long taskId;
}