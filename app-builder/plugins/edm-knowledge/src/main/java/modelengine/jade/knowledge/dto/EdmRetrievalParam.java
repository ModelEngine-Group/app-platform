/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.dto;

import lombok.Data;

import java.util.List;

/**
 * Edm 知识检索参数。
 *
 * @author 何嘉斌
 * @since 2024-09-25
 */
@Data
public class EdmRetrievalParam {
    /**
     * 查询内容。
     */
    private String context;

    /**
     * 知识库唯一标识列表。
     */
    private List<String> knowledgeId;

    /**
     * 检索 top k。
     */
    private Integer topK;

    /**
     * 阈值。
     */
    private Float threshold;
}