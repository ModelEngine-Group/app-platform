/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.entity;

import modelengine.jade.common.query.PageQueryParam;

import lombok.Data;
import modelengine.fitframework.annotation.Property;

import javax.validation.constraints.NotBlank;

/**
 * 表示数据集查询参数。
 *
 * @author 兰宇晨
 * @since 2024-07-24
 */
@Data
public class EvalDatasetQueryParam extends PageQueryParam {
    @Property(description = "应用 ID", required = true)
    @NotBlank(message = "appId should not be null")
    private String appId;
}