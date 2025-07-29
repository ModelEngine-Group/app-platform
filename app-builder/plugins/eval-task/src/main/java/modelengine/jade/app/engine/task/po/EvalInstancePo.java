/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.po;

import modelengine.jade.app.engine.task.entity.EvalInstanceStatusEnum;
import modelengine.jade.common.po.BasePo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评估任务实例 ORM 对象。
 *
 * @author 何嘉斌
 * @since 2024-08-12
 */
@Data
public class EvalInstancePo extends BasePo {
    /**
     * 评估任务实例唯一标识。
     */
    private Long taskId;

    /**
     * 任务实例通过数量。
     */
    private int passCount;

    /**
     * 用例通过率。
     */
    private Double passRate;

    /**
     * 任务实例状态。
     */
    private EvalInstanceStatusEnum status;

    /**
     * 工作流实例唯一标识。
     */
    private String traceId;

    /**
     * 完成时间。
     */
    private LocalDateTime finishedAt;
}