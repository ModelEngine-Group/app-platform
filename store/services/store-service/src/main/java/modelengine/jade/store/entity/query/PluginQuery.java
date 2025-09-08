/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.entity.query;

/**
 * 动态条件查询插件的类。
 *
 * @author 鲁为
 * @since 2024-07-25
 */
public class PluginQuery extends ToolQuery {
    /**
     * 表示插件是否内置。
     * <p>构造条件时按需传入。</p>
     */
    private Boolean isBuiltin;

    /**
     * 表示插件的创建者。
     * <p>构造条件时按需传入。</p>
     */
    private String creator;

    /**
     * 表示插件是否已部署。
     * <p>构造条件时按需传入。</p>
     */
    private Boolean isDeployed;

    /**
     * 获取是否内置。
     *
     * @return 表示是否内置的 {@link Boolean}。
     */
    public Boolean getIsBuiltin() {
        return this.isBuiltin;
    }

    /**
     * 设置是否内置。
     *
     * @param builtin 表示是否内置的 {@link Boolean}。
     */
    public void setIsBuiltin(Boolean builtin) {
        this.isBuiltin = builtin;
    }

    /**
     * 获取插件的创建者。
     *
     * @return 表示插件的创建者的 {@link String}。
     */
    public String getCreator() {
        return this.creator;
    }

    /**
     * 设置插件的创建者。
     *
     * @param creator 表示插件的创建者 {@link String}。
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * 获取是否已部署。
     *
     * @return 表示插件是否已部署的 {@link Boolean}。
     */
    public Boolean getDeployed() {
        return this.isDeployed;
    }

    /**
     * 设置是否已部署
     *
     * @param deployed 表示插件是否已部署的 {@link Boolean}。
     */
    public void setDeployed(Boolean deployed) {
        this.isDeployed = deployed;
    }

    /**
     * {@link PluginQuery} 的构建器。
     */
    public static class Builder extends ToolQuery.Builder<Builder> {
        private Boolean isBuiltin;
        private String creator;
        private Boolean isDeployed;

        /**
         * 向构建器中设置是否内置。
         *
         * @param isBuiltin 表示是否内置的 {@link Boolean}。
         * @return 表示当前构建器的 {@link PluginToolQuery.Builder}。
         */
        public Builder isBuiltin(Boolean isBuiltin) {
            this.isBuiltin = isBuiltin;
            return this;
        }

        /**
         * 向当前构建器中设置创建者。
         *
         * @param creator 表示插件的创建者的 {@link String}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder creator(String creator) {
            this.creator = creator;
            return this;
        }

        /**
         * 向当前构建器中设置是否已部署。
         *
         * @param isDeployed 表示插件是否已部署的 {@link Boolean}。
         * @return 表示当前构建器的 {@link Builder}。
         */
        public Builder isDeployed(Boolean isDeployed) {
            this.isDeployed = isDeployed;
            return this;
        }

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link PluginToolQuery}。
         */
        public PluginQuery build() {
            PluginQuery pluginQuery = new PluginQuery();
            pluginQuery.setToolName(this.toolName);
            pluginQuery.setIncludeTags(this.includeTags);
            pluginQuery.setExcludeTags(this.excludeTags);
            pluginQuery.setMode(this.mode);
            pluginQuery.setOffset(this.offset);
            pluginQuery.setLimit(this.limit);
            pluginQuery.setIsBuiltin(this.isBuiltin);
            pluginQuery.setCreator(this.creator);
            pluginQuery.setDeployed(this.isDeployed);
            pluginQuery.setUserGroupId(this.userGroupId);
            return pluginQuery;
        }
    }
}
