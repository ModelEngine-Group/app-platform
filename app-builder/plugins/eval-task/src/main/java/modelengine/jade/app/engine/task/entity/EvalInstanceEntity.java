/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表示评估任务实例的实体对象。
 *
 * @author 兰宇晨
 * @since 2024-08-14
 */
@Data
public class EvalInstanceEntity {
    /**
     * 主键。
     */
    private Long id;

    /**
     * 任务实例状态。
     */
    private EvalInstanceStatusEnum status;

    /**
     * 任务实例通过数量。
     */
    private int passCount;

    /**
     * 任务实例通过率。
     */
    private Double passRate;

    /**
     * 任务实例创建人。
     */
    private String createdBy;

    /**
     * 任务实例创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 任务实例完成时间。
     */
    private LocalDateTime finishedAt;

    /**
     * 评估任务唯一标识。
     */
    private Long taskId;
}