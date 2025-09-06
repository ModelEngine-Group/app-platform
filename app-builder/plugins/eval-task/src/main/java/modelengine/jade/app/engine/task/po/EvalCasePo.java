/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.po;

import modelengine.jade.common.po.BasePo;

import lombok.Data;

/**
 * 评估任务用例 ORM 对象。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@Data
public class EvalCasePo extends BasePo {
    /**
     * 评估用例通过结果。
     */
    private Boolean pass;

    /**
     * 评估任务实例唯一标识。
     */
    private Long instanceId;
}