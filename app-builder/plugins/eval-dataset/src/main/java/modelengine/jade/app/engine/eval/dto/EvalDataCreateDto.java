/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.dto;

import lombok.Data;
import modelengine.jade.app.engine.eval.controller.EvalDataController;
import modelengine.fitframework.annotation.Property;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * 表示评估数据集创建传输对象。
 *
 * @author 易文渊
 * @see EvalDataController#createEvalData。
 * @since 2024-07-19
 */
@Data
public class EvalDataCreateDto {
    @Property(description = "数据集编号", required = true, defaultValue = "1")
    @NotNull(message = "The dataset id cannot be null.")
    @Positive(message = "The dataset id is invalid.")
    private Long datasetId;

    @Property(description = "评估内容列表", required = true)
    @NotEmpty(message = "The contents cannot be empty.")
    private List<String> contents;
}