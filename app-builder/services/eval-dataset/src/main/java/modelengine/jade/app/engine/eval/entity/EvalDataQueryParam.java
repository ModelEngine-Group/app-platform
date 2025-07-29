/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.entity;

import lombok.Data;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.Positive;
import modelengine.jade.common.query.PageQueryParam;

/**
 * 表示数据查询参数。
 *
 * @author 兰宇晨
 * @since 2024-07-24
 */
@Data
public class EvalDataQueryParam extends PageQueryParam {
    @Property(description = "数据集编号", required = true)
    @Positive(message = "The dataset id is invalid.")
    private Long datasetId;

    @Property(description = "版本号", required = true)
    @Positive(message = "The version is invalid.")
    private Long version;
}