/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 混合检索类，根据表具体类型执行标量/向量检索
 *
 * @since 2024-05-21
 */
@Getter
@Setter
public class KbChunkSearchDto {
    /**
     * 知识库id
     */
    private Long knowledgeId;

    /**
     * 拥有者id
     */
    private Long ownerId;

    /**
     * 知识库名称
     */
    private String knowledgeName;

    /**
     * 知识表id
     */
    private Long tableId;

    /**
     * 知识表名
     */
    private String tableName;

    /**
     * 查询关键字
     */
    private String content;

    /**
     * 仅表格查询场景生效，是否进行向量检索
     */
    private Boolean isVectorSearch = false;

    /**
     * 是否使用 content 进行模糊查询
     */
    private Boolean isFuzzySearch;

    /**
     * topK
     */
    private Integer topK;

    /**
     * 阈值
     */
    private Float threshold;

    /**
     * 列
     */
    private String columnName;
}
