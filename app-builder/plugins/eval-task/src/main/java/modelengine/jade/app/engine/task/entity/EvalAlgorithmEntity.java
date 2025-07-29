/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.entity;

import lombok.Data;

/**
 * 表示评估算法的实体对象。
 *
 * @author 何嘉斌
 * @since 2024-09-09
 */
@Data
public class EvalAlgorithmEntity {
    /**
     * 主键。
     */
    private Long id;

    /**
     * 评估算法唯一标识。
     */
    private String nodeId;

    /**
     * 评估算法节点名称。
     */
    private String nodeName;

    /**
     * 评估算法格式规范。
     */
    private String algorithmSchema;

    /**
     * 评估算法及格分。
     */
    private Double passScore;

    /**
     * 评估任务唯一标识。
     */
    private Long taskId;
}