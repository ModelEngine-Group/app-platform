/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.dto;

import lombok.Data;
import modelengine.jade.app.engine.eval.controller.EvalDatasetController;
import modelengine.fitframework.annotation.Property;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * 表示评估数据集创建传输对象。
 *
 * @author 何嘉斌
 * @see EvalDatasetController#createEvalDataset。
 * @since 2024-07-31
 */
@Data
public class EvalDatasetCreateDto {
    @Property(description = "数据集名字", required = true)
    @NotBlank(message = "Dataset name cannot be empty.")
    private String name;

    @Property(description = "数据集介绍", required = true)
    @NotBlank(message = "Dataset description cannot be empty.")
    private String description;

    @Property(description = "评估内容", required = true)
    @NotEmpty(message = "Contents cannot be empty.")
    private List<String> contents;

    @Property(description = "数据集数据规范", required = true)
    @NotBlank(message = "Dataset schema cannot be empty.")
    private String schema;

    @Property(description = "应用编号", required = true)
    @NotBlank(message = "The application id cannot be empty.")
    private String appId;
}