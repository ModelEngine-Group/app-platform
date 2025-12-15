/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.datamate.knowledge.knowledge.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DataMate 知识库列表 Entity。
 *
 * @author 陈镕希
 * @since 2025-12-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataMateKnowledgeListEntity {
    /**
     * 当前页码（从 0 开始）。
     */
    private Integer page;
    /**
     * 每页数量。
     */
    private Integer size;
    /**
     * 总条目数。
     */
    private Integer totalElements;
    /**
     * 总页数。
     */
    private Integer totalPages;
    /**
     * 知识库列表查询数据。
     */
    @JsonProperty("content")
    private List<DataMateKnowledgeEntity> content;
}

