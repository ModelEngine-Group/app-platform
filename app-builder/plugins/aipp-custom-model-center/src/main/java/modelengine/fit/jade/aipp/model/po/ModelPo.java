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

        public Builder modelId(String modelId) {
            instance.setModelId(modelId);
            return this;
        }

        public Builder name(String name) {
            instance.setName(name);
            return this;
        }

        public Builder tag(String tag) {
            instance.setTag(tag);
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            instance.setBaseUrl(baseUrl);
            return this;
        }

        public Builder type(String type) {
            instance.setType(type);
            return this;
        }

        public Builder id(Long id) {
            instance.setId(id);
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            instance.setCreatedAt(createdAt);
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            instance.setUpdatedAt(updatedAt);
            return this;
        }

        public Builder createdBy(String createdBy) {
            instance.setCreatedBy(createdBy);
            return this;
        }

        public Builder updatedBy(String updatedBy) {
            instance.setUpdatedBy(updatedBy);
            return this;
        }

        public ModelPo build() {
            return instance;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
