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
 * 模型信息 ORM 对象。
 *
 * @author lixin
 * @since 2025/3/11
 */
@Data
public class ModelPo extends BasePo {
    private String modelId;
    private String name;
    private String tag;
    private String baseUrl;
    private String type;

    /**
     * 用于构建 {@link ModelPo} 实例的构建器类。
     */
    public static class Builder {
        private final ModelPo instance = new ModelPo();

        /**
         * 设置模型标识符。
         *
         * @param modelId 表示模型唯一标识的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder modelId(String modelId) {
            this.instance.setModelId(modelId);
            return this;
        }

        /**
         * 设置模型名称。
         *
         * @param name 表示模型名称的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder name(String name) {
            this.instance.setName(name);
            return this;
        }

        /**
         * 设置模型标签。
         *
         * @param tag 表示模型所属标签的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder tag(String tag) {
            this.instance.setTag(tag);
            return this;
        }

        /**
         * 设置模型基础地址。
         *
         * @param baseUrl 表示模型服务访问基础地址的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder baseUrl(String baseUrl) {
            this.instance.setBaseUrl(baseUrl);
            return this;
        }

        /**
         * 设置模型类型。
         *
         * @param type 表示模型类型的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder type(String type) {
            this.instance.setType(type);
            return this;
        }

        /**
         * 设置数据库主键标识。
         *
         * @param id 表示模型数据的唯一数据库主键的 {@link Long}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder id(Long id) {
            this.instance.setId(id);
            return this;
        }

        /**
         * 设置创建时间。
         *
         * @param createdAt 表示模型创建时间的 {@link LocalDateTime}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder createdAt(LocalDateTime createdAt) {
            this.instance.setCreatedAt(createdAt);
            return this;
        }

        /**
         * 设置更新时间。
         *
         * @param updatedAt 表示模型最近更新时间的 {@link LocalDateTime}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.instance.setUpdatedAt(updatedAt);
            return this;
        }

        /**
         * 设置创建人标识。
         *
         * @param createdBy 表示创建该模型记录的用户标识的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder createdBy(String createdBy) {
            this.instance.setCreatedBy(createdBy);
            return this;
        }

        /**
         * 设置更新人标识。
         *
         * @param updatedBy 表示最近更新该模型记录的用户标识的 {@link String}。
         * @return {@link Builder} 构建器本身。
         */
        public Builder updatedBy(String updatedBy) {
            this.instance.setUpdatedBy(updatedBy);
            return this;
        }

        /**
         * 构建并返回 {@link ModelPo} 实例。
         *
         * @return 构建完成的 {@link ModelPo} 实例。
         */
        public ModelPo build() {
            return this.instance;
        }
    }

    /**
     * 创建并返回一个新的 {@link Builder} 构建器实例。
     *
     * @return {@link Builder} 实例，用于构建 {@link ModelPo}。
     */
    public static Builder builder() {
        return new Builder();
    }
}
