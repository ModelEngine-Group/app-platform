/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.jade.store.entity.query;

import modelengine.jade.store.service.support.DeployStatus;

/**
 * 部署插件查询条件
 *
 * @author 邬涨财
 * @since 2025-08-19
 */
public class DeployPluginQuery {
    /**
     * 表示部署状态。
     * <p>构造条件时按需传入。</p>
     */
    private DeployStatus deployStatus;

    /**
     * 表示用户组 id。
     * <p>构造条件时按需传入。</p>
     */
    private String userGroupId;

    /**
     * 获取用户组 id。
     *
     * @return 表示用户组 id的 {@link String}。
     */
    public String getUserGroupId() {
        return userGroupId;
    }

    /**
     * 设置用户组 id。
     *
     * @param userGroupId 表示用户组 id的 {@link String}。
     */
    public void setUserGroupId(String userGroupId) {
        this.userGroupId = userGroupId;
    }

    /**
     * 获取部署状态。
     *
     * @return 表示部署状态的 {@link DeployStatus}。
     */
    public DeployStatus getDeployStatus() {
        return deployStatus;
    }

    /**
     * 设置部署状态。
     *
     * @param deployStatus 表示部署状态的 {@link DeployStatus}。
     */
    public void setDeployStatus(DeployStatus deployStatus) {
        this.deployStatus = deployStatus;
    }

    /**
     * {@link DeployPluginQuery} 的构建器。
     */
    public static class Builder<B extends DeployPluginQuery.Builder<B>> {
        protected DeployStatus deployStatus;
        protected String userGroupId;

        /**
         * 返回当前构建器的实例。
         *
         * @return 表示当前构建器的 {@link B}。
         */
        protected B self() {
            return (B) this;
        }

        /**
         * 向构建器中设置部署状态。
         *
         * @param deployStatus 表示部署状态的 {@link DeployStatus}。
         * @return 表示当前构建器的 {@link DeployPluginQuery.Builder}。
         */
        public B deployStatus(DeployStatus deployStatus) {
            this.deployStatus = deployStatus;
            return this.self();
        }

        /**
         * 向构建器中设置用户组id。
         *
         * @param userGroupId 表示用户组的 {@link String}。
         * @return 表示当前构建器的 {@link DeployPluginQuery.Builder}。
         */
        public B userGroupId(String userGroupId) {
            this.userGroupId = userGroupId;
            return this.self();
        }

        /**
         * 构建 DeployPluginQuery 对象。
         *
         * @return 表示构建的 {@link DeployPluginQuery}。
         */
        public DeployPluginQuery build() {
            DeployPluginQuery deployPluginQuery = new DeployPluginQuery();
            deployPluginQuery.setDeployStatus(this.deployStatus);
            deployPluginQuery.setUserGroupId(this.userGroupId);
            return deployPluginQuery;
        }
    }
}
