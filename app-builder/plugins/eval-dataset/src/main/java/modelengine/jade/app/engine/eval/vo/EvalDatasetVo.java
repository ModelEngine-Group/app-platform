/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.vo;

import modelengine.jade.app.engine.eval.entity.EvalVersionEntity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 表示评估数据集的展示对象。
 *
 * @author 何嘉斌
 * @since 2024-09-02
 */
@Data
public class EvalDatasetVo {
    /**
     * 主键。
     */
    private Long id;

    /**
     * 评估数据集名字。
     */
    private String name;

    /**
     * 评估数据集描述。
     */
    private String description;

    /**
     * 评估数据集数据规范。
     */
    private String schema;

    /**
     * 数据集创建人。
     */
    private String createdBy;

    /**
     * 数据集修改人。
     */
    private String updatedBy;

    /**
     * 数据集创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 数据集修改时间。
     */
    private LocalDateTime updatedAt;

    /**
     * 数据集版本列表。
     */
    private List<EvalVersionEntity> versions;
}