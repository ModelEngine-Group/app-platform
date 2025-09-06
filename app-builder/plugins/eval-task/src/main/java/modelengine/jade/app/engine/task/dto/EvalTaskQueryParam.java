/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.dto;

import modelengine.jade.common.query.PageQueryParam;

import lombok.Data;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.NotBlank;

/**
 * 表示评估任务询参数。
 *
 * @author 何嘉斌
 * @since 2024/08/12
 */
@Data
public class EvalTaskQueryParam extends PageQueryParam {
    @Property(description = "应用唯一标识", required = true)
    @NotBlank(message = "appId should not be null")
    private String appId;
}