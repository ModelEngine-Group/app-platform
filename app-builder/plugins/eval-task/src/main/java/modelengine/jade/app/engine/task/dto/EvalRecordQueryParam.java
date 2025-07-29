/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.dto;

import modelengine.jade.common.query.PageQueryParam;

import lombok.Data;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.NotEmpty;
import modelengine.jade.app.engine.task.controller.EvalRecordController;

import java.util.List;

/**
 * 表示评估任务用例结果的查询参数。
 *
 * @author 何嘉斌
 * @see EvalRecordController#queryEvalRecord
 * @since 2024-08-31
 */
@Data
public class EvalRecordQueryParam extends PageQueryParam {
    @Property(description = "评估用例结果唯一标识", required = true)
    @NotEmpty(message = "The nodeIds cannot be empty.")
    List<String> nodeIds;
}