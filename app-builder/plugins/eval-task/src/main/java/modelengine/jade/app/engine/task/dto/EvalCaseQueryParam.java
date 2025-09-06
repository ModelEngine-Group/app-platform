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
import modelengine.jade.app.engine.task.controller.EvalCaseController;

/**
 * 表示评估任务用例创建传输对象。
 *
 * @author 何嘉斌
 * @see EvalCaseController#queryEvalCase
 * @since 2024-09-23
 */
@Data
public class EvalCaseQueryParam extends PageQueryParam {
    @Property(description = "评估实例编号", required = true)
    @Positive(message = "The instance id is invalid.")
    private Long instanceId;
}