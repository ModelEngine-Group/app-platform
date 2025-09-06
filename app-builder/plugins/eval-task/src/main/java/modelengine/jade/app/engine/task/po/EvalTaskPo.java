/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.po;

import modelengine.jade.app.engine.task.entity.EvalTaskStatusEnum;
import modelengine.jade.common.po.BasePo;

import lombok.Data;

/**
 * 评估任务 ORM 对象。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
@Data
public class EvalTaskPo extends BasePo {
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
     * 应用唯一标识。
     */
    private String appId;

    /**
     * 评估工作流唯一标识。
     */
    private String workflowId;
}
