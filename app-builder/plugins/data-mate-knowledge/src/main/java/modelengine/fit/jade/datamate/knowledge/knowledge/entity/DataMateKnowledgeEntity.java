/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.datamate.knowledge.knowledge.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DataMate 知识库 Entity。
 *
 * @author 陈镕希
 * @since 2025-12-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataMateKnowledgeEntity {
    /**
     * 知识库id。
     */
    private String id;

    /**
     * 知识库名称。
     */
    private String name;

    /**
     * 知识库描述。
     */
    private String description;

    /**
     * 知识库创建时间。
     */
    private String createdAt;

    /**
     * 知识库更新时间。
     */
    private String updatedAt;

    /**
     * 创建人。
     */
    private String createdBy;

    /**
     * 更新人。
     */
    private String updatedBy;

    /**
     * 嵌入模型名称。
     */
    private String embeddingModel;

    /**
     * 聊天模型名称。
     */
    private String chatModel;

    /**
     * 文件数量。
     */
    private Long fileCount;

    /**
     * chunk 数量。
     */
    private Long chunkCount;

    /**
     * 嵌入模型配置。
     */
    private ModelConfig embedding;

    /**
     * 聊天模型配置。
     */
    private ModelConfig chat;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ModelConfig {
        private String id;
        private String createdAt;
        private String updatedAt;
        private String createdBy;
        private String updatedBy;
        private String modelName;
        private String provider;
        private String baseUrl;
        private String apiKey;
        private String type;
        private Boolean isEnabled;
        private Boolean isDefault;
    }
}

