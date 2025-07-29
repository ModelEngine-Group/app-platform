/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.vo;

import lombok.Data;

/**
 * 表示评估报告的展示对象。
 *
 * @author 何嘉斌
 * @since 2024-09-13
 */
@Data
public class EvalReportVo {
    /**
     * 主键。
     */
    private Long id;

    /**
     * 评估节点唯一标识。
     */
    private String nodeId;

    /**
     * 评估节点命名。
     */
    private String nodeName;

    /**
     * 评估算法及格分。
     */
    private Double passScore;

    /**
     * 评估算法格式规范。
     */
    private String algorithmSchema;

    /**
     * 评估算法平均分。
     */
    private Double averageScore;

    /**
     * 评估算法直方图。
     */
    private int[] histogram;

    /**
     * 评估算法实例编号。
     */
    private Long instanceId;
}