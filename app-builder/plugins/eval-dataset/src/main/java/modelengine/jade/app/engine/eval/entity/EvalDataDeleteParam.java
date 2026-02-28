/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.entity;

import lombok.Data;
import modelengine.fitframework.annotation.Property;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * 表示评估数据集创建传输对象。
 *
 * @author 何嘉斌
 * @since 2024-07-23
 */
@Data
public class EvalDataDeleteParam {
    @Valid
    @Property(description = "数据编号", required = true)
    @NotEmpty(message = "The dataIds cannot be empty.")
    private List<@Valid @Positive(message = "Some data ids are invalid.") @NotNull(
            message = "The data id cannot be null.") Long> dataIds;
}