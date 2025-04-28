/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import modelengine.fitframework.annotation.Property;

/**
 * qianfan 知识库检索chunk结果。
 *
 * @author 陈潇文
 * @since 2025-04-25
 */
@Data
@Getter
@Setter
public class QianfanRetrievalChunksEntity {
    /**
     * chunk id。
     */
    @Property(description = "chunk_id", name = "chunk_id")
    private String chunkId;
    /**
     * chunk内容。
     */
    private String content;
    /**
     * chunk类型。
     */
    @Property(description = "chunk_type", name = "chunk_type")
    private String chunkType;
    /**
     * 知识库id。
     */
    @Property(description = "knowledgebase_id", name = "knowledgebase_id")
    private String knowledgebaseId;
    /**
     * 文档id。
     */
    @Property(description = "document_id", name = "document_id")
    private String documentId;
    /**
     * 文档名。
     */
    @Property(description = "document_name", name = "document_name")
    private String documentName;
    /**
     * 粗检索分值。
     */
    @Property(description = "retrieval_score", name = "retrieval_score")
    private float retrievalScore;
    /**
     * rerank分值。
     */
    @Property(description = "rank_score", name = "rank_score")
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
}
