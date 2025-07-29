/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.entity;

import modelengine.jade.knowledge.KnowledgeProperty;
import modelengine.jade.knowledge.ReferenceLimit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 检索节点服务的配置实体。
 *
 * @author 刘信宏
 * @since 2024-10-08
 */
@Data
public class RetrieverServiceOption {
    /**
     * 检索方式。
     */
    private KnowledgeProperty.IndexInfo indexType;

    /**
     * 引用上限。
     */
    private ReferenceLimit referenceLimit;

    /**
     * 检索相似度阈值。
     */
    private float similarityThreshold;

    /**
     * 重排模型参数。
     */
    private RetrieverOption.RerankParam rerankParam;

    /**
     * 知识库组标识。
     */
    private String groupId;

    /**
     * 知识库配置唯一id。
     */
    private String knowledgeConfigId;

    /**
     * 重排模型参数实体。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RerankParam {
        /**
         * 重排使能标记。
         */
        private boolean enableRerank;

        /**
         * 重排模型的名称。
         */
        private String model;

        /**
         * 重排模型的资源标识符。
         */
        private String baseUri;

        /**
         * 重排后保留最相关的文档数量。
         */
        private Integer topK;
    }
}
