/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.entity;

import lombok.Data;

/**
 * 表示评估任务单用例执行结果传输对象。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@Data
public class EvalRecordEntity {
    /**
     * 主键。
     */
    private Long id;

    /**
     * 算法输入。
     */
    private String input;

    /**
     * 评估节点唯一标识。
     */
    private String nodeId;

    /**
     * 评估节点命名。
     */
    private String nodeName;

    /**
     * 算法评分。
     */
    private Double score;

    /**
     * 评估任务用例唯一标识。
     */
    private Long taskCaseId;
}