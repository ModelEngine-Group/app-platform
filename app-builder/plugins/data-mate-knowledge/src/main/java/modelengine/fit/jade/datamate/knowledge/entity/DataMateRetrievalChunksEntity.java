/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.datamate.knowledge.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * DataMate 知识库检索 chunk 结果。
 *
 * @author 陈镕希
 * @since 2025-12-15
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataMateRetrievalChunksEntity {
    /**
     * 查询结果实体。
     */
    private RagChunk entity;

    /**
     * 匹配分值。
     */
    private Double score;

    /**
     * 主键字段名。
     */
    private String primaryKey;

    public String chunkId() {
        return this.entity != null ? this.entity.getId() : null;
    }

    public String content() {
        return this.entity != null ? this.entity.getText() : null;
    }

    public double retrievalScore() {
        return this.score == null ? 0 : this.score;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RagChunk {
        private String id;
        private String text;
        private String metadata;
    }
}

