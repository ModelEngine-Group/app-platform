/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表示评估数据集版本的实体对象。
 *
 * @author 何嘉斌
 * @since 2024-08-05
 */
@Data
public class EvalVersionEntity {
    /**
     * 版本号。
     */
    private Long version;

    /**
     * 版本创建时间。
     */
    private LocalDateTime createdTime;
}
