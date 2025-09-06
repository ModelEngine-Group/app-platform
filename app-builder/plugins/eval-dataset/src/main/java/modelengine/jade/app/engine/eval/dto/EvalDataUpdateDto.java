/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.dto;

import lombok.Data;
import modelengine.jade.app.engine.eval.controller.EvalDataController;
import modelengine.fitframework.annotation.Property;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * 表示评估数据修改传输对象。
 *
 * @author 何嘉斌
 * @see EvalDataController#updateEvalData。
 * @since 2024-07-25
 */
@Data
public class EvalDataUpdateDto {
    @Property(description = "数据集编号", required = true, defaultValue = "1")
    @NotNull(message = "The dataset id cannot be null.")
    @Positive(message = "The dataset id is invalid.")
    private Long datasetId;

    @Property(description = "数据编号", required = true, defaultValue = "1")
    @NotNull(message = "The dataset id cannot be null.")
    @Positive(message = "The data id is invalid.")
    private Long dataId;

    @Property(description = "评估内容", required = true)
    @NotBlank(message = "The content cannot be blank.")
    private String content;
}