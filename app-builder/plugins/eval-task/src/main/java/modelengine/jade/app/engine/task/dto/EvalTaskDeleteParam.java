/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.dto;

import lombok.Data;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 表示评估任务删除参数的 {@link EvalTaskDeleteParam}
 *
 * @author 兰宇晨
 * @since 2024-8-15
 */
@Data
public class EvalTaskDeleteParam {
    @Property(description = "评估任务唯一表示列表", required = true)
    @NotEmpty(message = "The taskIds cannot be empty.")
    private List<Long> taskIds;
}
