/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.po;

import modelengine.jade.common.po.BasePo;

import lombok.Data;

/**
 * 评估任务用例结果 ORM 对象。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@Data
public class EvalRecordPo extends BasePo {
    /**
     * 算法输入。
     */
    private String input;

    /**
     * 节点唯一标识。
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