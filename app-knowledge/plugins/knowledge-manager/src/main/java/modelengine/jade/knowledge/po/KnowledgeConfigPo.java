/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.po;

import lombok.Data;
import lombok.EqualsAndHashCode;
import modelengine.jade.common.po.BasePo;

import java.time.LocalDateTime;

/**
 * 知识库配置信息 ORM 对象。
 *
 * @author 陈潇文
 * @since 2025-04-22
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class KnowledgeConfigPo extends BasePo {
    /**
     * 知识库平台名称。
     */
    private String name;
    /**
     * 用户id。
     */
    private String userId;
    /**
     * 知识库api key。
     */
    private String apiKey;
    /**
     * 知识库平台groupId。
     */
    private String groupId;
    /**
     * 是否为默认使用。
     */
    private int isDefault;

    /**
     * 用于构建 {@link KnowledgeConfigPo} 实例的构建器类。
     */
    public static class Builder {
        private final KnowledgeConfigPo instance = new KnowledgeConfigPo();

        /**
         * 设置知识库平台名称。
         *
         * @param name 表示知识库平台名称的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder name(String name) {
            this.instance.setName(name);
            return this;
        }

        /**
         * 设置用户ID。
         *
         * @param userId 表示用户ID的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder userId(String userId) {
            this.instance.setUserId(userId);
            return this;
        }

        /**
         * 设置知识库API密钥。
         *
         * @param apiKey 表示知识库API密钥的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder apiKey(String apiKey) {
            this.instance.setApiKey(apiKey);
            return this;
        }

        /**
         * 设置知识库平台组ID。
         *
         * @param groupId 表示知识库平台组ID的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder groupId(String groupId) {
            this.instance.setGroupId(groupId);
            return this;
        }

        /**
         * 设置是否为默认使用。
         *
         * @param isDefault 表示是否为默认使用的 {@link int} (1 表示默认，0 表示非默认）。
         * @return {@link Builder} 构建器本身。
         */
        public Builder isDefault(int isDefault) {
            this.instance.setIsDefault(isDefault);
            return this;
        }

        /**
         * 设置数据库主键ID。
         *
         * @param id 表示记录的唯一数据库主键的 {@link Long}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder id(Long id) {
            this.instance.setId(id);
            return this;
        }

        /**
         * 设置创建时间。
         *
         * @param createdAt 表示记录的创建时间的 {@link LocalDateTime}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder createdAt(LocalDateTime createdAt) {
            this.instance.setCreatedAt(createdAt);
            return this;
        }

        /**
         * 设置更新时间。
         *
         * @param updatedAt 表示记录的最后更新时间的 {@link LocalDateTime}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.instance.setUpdatedAt(updatedAt);
            return this;
        }

        /**
         * 设置创建人标识。
         *
         * @param createdBy 表示创建该记录的用户标识的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder createdBy(String createdBy) {
            this.instance.setCreatedBy(createdBy);
            return this;
        }

        /**
         * 设置更新人标识。
         *
         * @param updatedBy 表示最近更新该记录的用户标识的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder updatedBy(String updatedBy) {
            this.instance.setUpdatedBy(updatedBy);
            return this;
        }

        /**
         * 构建并返回 {@link KnowledgeConfigPo} 实例。
         *
         * @return 构建完成的 {@link KnowledgeConfigPo} 实例。
         */
        public KnowledgeConfigPo build() {
            return this.instance;
        }
    }

    /**
     * 创建并返回一个新的 {@link Builder} 构建器实例。
     *
     * @return {@link Builder} 实例，用于构建 {@link KnowledgeConfigPo}。
     */
    public static Builder builder() {
        return new Builder();
    }
}
