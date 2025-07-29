/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 百度千帆知识库Entity。
 *
 * @author 陈潇文
 * @since 2025-04-25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QianfanKnowledgeEntity {
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
     * 知识库配置。
     */
    private QianfanKnowledgeConfigEntity config;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QianfanKnowledgeConfigEntity {
        /**
         * 知识库索引配置。
         */
        private QianfanKnowledgeConfigIndexEntity index;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class QianfanKnowledgeConfigIndexEntity {
        /**
         * 知识库索引存储配置 (public | bes | vdb)。
         */
        private String type;
        /**
         * bes 访问地址。
         */
        private String esUrl;
    }
}
