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
     * 表示是否为默认模型（1表示默认，0表示非默认）。
     */
    private int isDefault;

    /**
     * 用于构建 {@link UserModelDetailPo} 实例的构建器类。
     */
    public static class Builder {
        private final UserModelDetailPo instance = new UserModelDetailPo();

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

        public Builder modelId(String modelId) {
            instance.setModelId(modelId);
            return this;
        }

        public Builder userId(String userId) {
            instance.setUserId(userId);
            return this;
        }

        public Builder modelName(String modelName) {
            instance.setModelName(modelName);
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            instance.setBaseUrl(baseUrl);
            return this;
        }

        public Builder isDefault(int isDefault) {
            instance.setIsDefault(isDefault);
            return this;
        }

        public UserModelDetailPo build() {
            return instance;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}