/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.po;

import lombok.Data;
import modelengine.jade.common.po.BasePo;

import java.time.LocalDateTime;

/**
 * 用户模型关系信息 ORM 对象。
 *
 * @author lixin
 * @since 2025/3/11
 */
@Data
public class UserModelPo extends BasePo {
    private String userId;
    private String modelId;
    private String apiKey;
    private int isDefault;

    /**
     * 用于构建 {@link UserModelPo} 实例的构建器类。
     */
    public static class Builder {
        private final UserModelPo instance = new UserModelPo();

        /**
         * 设置用户标识。
         *
         * @param userId 表示用户标识的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder userId(String userId) {
            this.instance.setUserId(userId);
            return this;
        }

        /**
         * 设置模型标识。
         *
         * @param modelId 表示关联的模型标识的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder modelId(String modelId) {
            this.instance.setModelId(modelId);
            return this;
        }

        /**
         * 设置模型访问所需的访问密钥。
         *
         * @param apiKey 表示模型访问密钥的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder apiKey(String apiKey) {
            this.instance.setApiKey(apiKey);
            return this;
        }

        /**
         * 设置是否为默认模型。
         *
         * @param isDefault 表示是否为默认模型的 {@code int}（1 表示默认，0 表示非默认）。
         * @return {@link Builder} 构建器本身。
         */
        public Builder isDefault(int isDefault) {
            this.instance.setIsDefault(isDefault);
            return this;
        }

        /**
         * 设置数据库主键 ID。
         *
         * @param id 表示用户模型记录的唯一数据库主键的 {@link Long}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder id(Long id) {
            this.instance.setId(id);
            return this;
        }

        /**
         * 设置创建时间。
         *
         * @param createdAt 表示用户模型记录的创建时间的 {@link LocalDateTime}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder createdAt(LocalDateTime createdAt) {
            this.instance.setCreatedAt(createdAt);
            return this;
        }

        /**
         * 设置更新时间。
         *
         * @param updatedAt 表示用户模型记录的最后更新时间的 {@link LocalDateTime}。
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
         * 构建并返回 {@link UserModelPo} 实例。
         *
         * @return 构建完成的 {@link UserModelPo} 实例。
         */
        public UserModelPo build() {
            return this.instance;
        }
    }

    /**
     * 创建并返回一个新的 {@link Builder} 构建器实例。
     *
     * @return {@link Builder} 实例，用于构建 {@link UserModelPo}。
     */
    public static Builder builder() {
        return new Builder();
    }
}