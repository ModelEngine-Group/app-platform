/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.dto;

import lombok.Data;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.Range;

import java.time.LocalDateTime;

/**
 * 表示评估任务实例更新传输对象。
 *
 * @author 何嘉斌
 * @since 2024-08-14
 */
@Data
public class EvalInstanceUpdateDto {
    @Property(description = "评估任务实例编号", required = true, defaultValue = "1")
    @Range(min = 1, max = Long.MAX_VALUE, message = "The instance id is invalid.")
    private Long id;

    @Property(description = "评估任务实例用例通过率", required = true)
    private Double passRate;

    @Property(description = "评估任务实例用例通过数量", required = true)
    private Integer passCount;

    @Property(description = "评估任务实例完成时间", required = true)
    private LocalDateTime finishAt;
}