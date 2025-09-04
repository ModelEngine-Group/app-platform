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
 * @since 2024-06-15
 */
public class AppQuery extends ToolQuery {
    /**
     * {@link AppQuery} 的构建器。
     */
    public static class Builder extends ToolQuery.Builder<Builder> {
        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link AppQuery}。
         */
        public AppQuery build() {
            AppQuery appQuery = new AppQuery();
            appQuery.setToolName(this.toolName);
            appQuery.setIncludeTags(this.includeTags);
            appQuery.setExcludeTags(this.excludeTags);
            appQuery.setMode(this.mode);
            appQuery.setOffset(this.offset);
            appQuery.setLimit(this.limit);
            appQuery.setVersion(this.version);
            appQuery.setAppCategory(this.appCategory);
            appQuery.setUserGroupId(this.userGroupId);
            return appQuery;
        }
    }
}
