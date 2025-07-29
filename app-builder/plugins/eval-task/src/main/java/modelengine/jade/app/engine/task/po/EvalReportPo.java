/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.po;

import modelengine.jade.common.po.BasePo;

import lombok.Data;

/**
 * 评估任务报告 ORM 对象。
 *
 * @author 何嘉斌
 * @since 2024-08-14
 */
@Data
public class EvalReportPo extends BasePo {
    /**
     * 评估节点命名。
     */
    private String nodeId;

    /**
     * 评估算法平均分。
     */
    private Double averageScore;

    /**
     * 评估算法直方图。
     */
    private String histogram;

    /**
     * 评估算法实例编号。
     */
    private Long instanceId;
}