/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表示评估任务的实体对象。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
@Data
public class EvalTaskEntity {
    /**
     * 主键。
     */
    private Long id;

    /**
     * 评估任务名字。
     */
    private String name;

    /**
     * 评估任务描述。
     */
    private String description;

    /**
     * 评估任务状态。
     */
    private EvalTaskStatusEnum status;

    /**
     * 评估任务创建人。
     */
    private String createdBy;

    /**
     * 评估任务修改人。
     */
    private String updatedBy;

    /**
     * 评估任务创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 评估任务修改时间。
     */
    private LocalDateTime updatedAt;

    /**
     * 应用唯一标识。
     */
    private String appId;

    /**
     * 评估工作流唯一标识。
     */
    private String workflowId;
}
