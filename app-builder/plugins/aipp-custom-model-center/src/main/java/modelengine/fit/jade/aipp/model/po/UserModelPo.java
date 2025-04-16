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

        public Builder userId(String userId) {
            instance.setUserId(userId);
            return this;
        }

        public Builder modelId(String modelId) {
            instance.setModelId(modelId);
            return this;
        }

        public Builder apiKey(String apiKey) {
            instance.setApiKey(apiKey);
            return this;
        }

        public Builder isDefault(int isDefault) {
            instance.setIsDefault(isDefault);
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

        public UserModelPo build() {
            return instance;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
