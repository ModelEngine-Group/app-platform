/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表示评估数据的实体对象。
 *
 * @author 兰宇晨
 * @since 2024-07-24
 */
@Data
public class EvalDataEntity {
    /**
     * 主键。
     */
    private Long id;

    /**
     * 内容。
     */
    private String content;

    /**
     * 创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间。
     */
    private LocalDateTime updatedAt;
}
