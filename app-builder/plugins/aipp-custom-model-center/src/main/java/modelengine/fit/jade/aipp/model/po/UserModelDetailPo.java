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
 * 用户模型关系信息用于前端表单展示。
 *
 * @author 李智超
 * @since 2025-04-08
 */
@Data
public class UserModelDetailPo extends BasePo {
    /**
     * 表示模型标识。
     */
    private String modelId;

    /**
     * 表示用户标识。
     */
    private String userId;

    /**
     * 表示模型名称。
     */
    private String modelName;

    /**
     * 表示模型访问地址。
     */
    private String baseUrl;

    /**
     * 表示是否为默认模型（1 表示默认，0 表示非默认）。
     */
    private int isDefault;

    /**
     * 用于构建 {@link UserModelDetailPo} 实例的构建器类。
     */
    public static class Builder {
        private final UserModelDetailPo instance = new UserModelDetailPo();

        /**
         * 设置数据库主键标识。
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
         * @param createdAt 表示该用户模型记录的创建时间的 {@link LocalDateTime}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder createdAt(LocalDateTime createdAt) {
            this.instance.setCreatedAt(createdAt);
            return this;
        }

        /**
         * 设置更新时间。
         *
         * @param updatedAt 表示该用户模型记录的最后更新时间的 {@link LocalDateTime}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.instance.setUpdatedAt(updatedAt);
            return this;
        }

        /**
         * 设置创建人标识。
         *
         * @param createdBy 表示创建该用户模型记录的用户标识的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder createdBy(String createdBy) {
            this.instance.setCreatedBy(createdBy);
            return this;
        }

        /**
         * 设置更新人标识。
         *
         * @param updatedBy 表示最近更新该用户模型记录的用户标识的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder updatedBy(String updatedBy) {
            this.instance.setUpdatedBy(updatedBy);
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
         * 设置用户标识。
         *
         * @param userId 表示关联的用户标识的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder userId(String userId) {
            this.instance.setUserId(userId);
            return this;
        }

        /**
         * 设置模型名称。
         *
         * @param modelName 表示模型名称的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder modelName(String modelName) {
            this.instance.setModelName(modelName);
            return this;
        }

        /**
         * 设置模型访问地址。
         *
         * @param baseUrl 表示模型服务访问地址的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder baseUrl(String baseUrl) {
            this.instance.setBaseUrl(baseUrl);
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
         * 构建并返回 {@link UserModelDetailPo} 实例。
         *
         * @return 构建完成的 {@link UserModelDetailPo} 实例。
         */
        public UserModelDetailPo build() {
            return this.instance;
        }
    }

    /**
     * 创建并返回一个新的 {@link Builder} 构建器实例。
     *
     * @return {@link Builder} 实例，用于构建 {@link UserModelDetailPo}。
     */
    public static Builder builder() {
        return new Builder();
    }
}