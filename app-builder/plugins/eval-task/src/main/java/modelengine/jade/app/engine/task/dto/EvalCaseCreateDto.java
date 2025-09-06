/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.dto;

import lombok.Data;
import modelengine.fitframework.annotation.Property;

import java.util.Map;

/**
 * 表示评估任务用例创建传输对象。
 *
 * @author 何嘉斌
 * @since 2024-08-26
 */
@Data
public class EvalCaseCreateDto {
    @Property(description = "评估任务单例结果", required = true)
    private Map<String, EvalRecordCreateDto> evalOutput;

    @Property(description = "是否调试运行", name = "isDebug", required = true)
    private Boolean isDebug;
}