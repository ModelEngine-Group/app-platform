/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.entity;

import lombok.Data;

/**
 * 知识库单条查询结果。
 *
 * @author 何嘉斌
 * @since 2024-09-25
 */
@Data
public class EdmRetrievalResult {
    /**
     * 文本id
     */
    private Long id;

    /**
     * 召回文本信息
     */
    private String content;

    /**
     * 文本得分
     */
    private Float score;

    /**
     * 召回文本元信息
     */
    private Object metaInfo;
}