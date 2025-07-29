/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.vo;

import modelengine.jade.app.engine.task.entity.EvalInstanceStatusEnum;
import modelengine.jade.app.engine.task.entity.EvalTaskStatusEnum;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表示评估任务的展示对象.
 *
 * @author 何嘉斌
 * @since 2024-09-20
 */
@Data
public class EvalTaskVo {
    /**
     * 评估任务唯一标识。
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

    /**
     * 评估实例唯一标识。
     */
    private Long instanceId;

    /**
     * 任务实例状态。
     */
    private EvalInstanceStatusEnum instanceStatus;

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
    private String instanceCreatedBy;

    /**
     * 任务实例创建时间。
     */
    private LocalDateTime instanceCreatedAt;

    /**
     * 任务实例完成时间。
     */
    private LocalDateTime instanceFinishedAt;
}