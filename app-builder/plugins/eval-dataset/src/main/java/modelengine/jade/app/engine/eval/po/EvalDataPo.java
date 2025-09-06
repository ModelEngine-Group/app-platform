/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.po;

import modelengine.jade.common.po.BasePo;

import lombok.Data;

/**
 * 评估数据 ORM 对象。
 *
 * @author 易文渊
 * @since 2024-07-19
 */
@Data
public class EvalDataPo extends BasePo {
    /**
     * 评估内容。
     */
    private String content;

    /**
     * 创建版本。
     */
    private Long createdVersion;

    /**
     * 过期版本。
     */
    private Long expiredVersion;

    /**
     * 外键，关联评估数据集。
     */
    private Long datasetId;
}