/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.dto;

import lombok.Data;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.NotBlank;
import modelengine.fitframework.validation.constraints.NotEmpty;

import java.util.Map;

/**
 * 表示评估任务单用例执行结果传输对象。
 *
 * @author 何嘉斌
 * @since 2024-08-26
 */
@Data
public class EvalRecordCreateDto {
    @Property(description = "算法输入", required = true)
    @NotBlank(message = "Task name cannot be empty.")
    private Map<String, String> input;

    @Property(description = "节点唯一标识", required = true)
    @NotEmpty(message = "Status cannot be empty.")
    private String nodeId;

    @Property(description = "节点名称", required = true)
    @NotEmpty(message = "Status cannot be empty.")
    private String nodeName;

    @Property(description = "算法评分", required = true, defaultValue = "-1")
    private Double score;

    @Property(description = "用例通过结果", required = true)
    private Boolean isPass;

    @Property(description = "评估算法及格分", required = true)
    private Double passScore;
}