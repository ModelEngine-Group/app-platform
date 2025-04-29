/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * qianfan 知识库检索chunk结果。
 *
 * @author 陈潇文
 * @since 2025-04-25
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QianfanRetrievalChunksEntity {
    /**
     * chunk id。
     */
    @JsonProperty("chunk_id")
    private String chunkId;
    /**
     * chunk内容。
     */
    private String content;
    /**
     * chunk类型。
     */
    @JsonProperty("chunk_type")
    private String chunkType;
    /**
     * 知识库id。
     */
    @JsonProperty("knowledgebase_id")
    private String knowledgebaseId;
    /**
     * 文档id。
     */
    @JsonProperty("document_id")
    private String documentId;
    /**
     * 文档名。
     */
    @JsonProperty("document_name")
    private String documentName;
    /**
     * 粗检索分值。
     */
    @JsonProperty("retrieval_score")
    private float retrievalScore;
    /**
     * rerank分值。
     */
    @JsonProperty("rank_score")
    private float rankScore;

    public String documentId() {
        return this.documentId;
    }

    public String content() {
        return this.content;
    }

    public float retrievalScore() {
        return this.retrievalScore;
    }

    public String chunkId() {
        return this.chunkId;
    }
}
