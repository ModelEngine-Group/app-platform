/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * 表示评估算法节点输出的实体。
 *
 *  @author 兰宇晨
 *  @since 2024-9-26
 */
@Data
@AllArgsConstructor
public class EvalOutputEntity {
    /**
     * 表示评估算法的输入。
     */
    private Map<String, Object> input;

    /**
     * 表示评估节点唯一标识。
     */
    private String nodeId;

    /**
     * 表示评估节点名称。
     */
    private String nodeName;

    /**
     * 表示评估分数。
     */
    private double score;

    /**
     * 表示是否用例分数通过。
     */
    private Boolean isPass;

    /**
     * 表示算法及格分。
     */
    private double passScore;
}
