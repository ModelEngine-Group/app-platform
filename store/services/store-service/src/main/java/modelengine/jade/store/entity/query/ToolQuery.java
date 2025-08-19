/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.entity.query;

import static modelengine.jade.carver.validation.ValidateTagMode.validateTagMode;

import modelengine.fitframework.util.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 动态条件查询的类。
 *
 * @author 李金绪
 * @since 2024-05-10
 */
public class ToolQuery extends CommonQuery {
    /**
     * 表示工具名称。
     * <p>构造条件时按需传入。</p>
     */
    private String toolName;

    /**
     * 表示需要包括的标签列表。
     * <p>构造条件时按需传入。</p>
     */
    private Set<String> includeTags;

    /**
     * 表示需要排除的标签列表。
     * <p>构造条件时按需传入。</p>
     */
    private Set<String> excludeTags;

    /**
     * 表示选择标签的与和或逻辑。
     * <p>构造条件时按需传入，默认为 AND，表示与逻辑；可传 OR 表示或逻辑。</p>
     */
    private String mode;

    /**
     * 表示工具版本。
     * <p>构造条件时按需传入。</p>
     */
    private String version;

    /**
     * 表示应用类型。
     * <p>构造条件时按需传入。</p>
     */
    private String appCategory;

    /**
     * 表示用户组唯一标识。
     * <p>构造条件时按需传入。</p>
     */
    private String userGroupId;

    /**
     * {@link ToolQuery} 的构建器。
     */
    public static class Builder<B extends Builder<B>> {
        /**
         * 表示工具名称。
         */
        protected String toolName;

        /**
         * 表示需要包括的标签列表。
         */
        protected Set<String> includeTags;

        /**
         * 表示需要排除的标签列表。
         */
        protected Set<String> excludeTags;

        /**
         * 表示选择标签的与和或逻辑。
         */
        protected String mode;

        /**
         * 表示页面偏移量。
         */
        protected Integer offset;

        /**
         * 表示页面大小。
         */
        protected Integer limit;

        /**
         * 表示工具版本。
         */
        protected String version;

        /**
         * 表示应用类型。
         */
        protected String appCategory;

        /**
         * 表示用户组唯一标识。
         */
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
         * 向当前构建器中设置工具名。
         *
         * @param toolName 表示工具名的 {@link String}。
         * @return 表示当前构建器的 {@link B}。
         */
        public B toolName(String toolName) {
            this.toolName = toolName;
            return this.self();
        }

        /**
         * 向当前构建器中设置包含标签。
         *
         * @param includeTags 表示包含标签的 {@link Set}{@code <}{@link String}{@code >}。
         * @return 表示当前构建器的 {@link B}。
         */
        public B includeTags(Set<String> includeTags) {
            this.includeTags = includeTags;
            return this.self();
        }

        /**
         * 向当前构建器中设置排除标签。
         *
         * @param excludeTags 表示排除标签的 {@link Set}{@code <}{@link String}{@code >}。
         * @return 表示当前构建器的 {@link B}。
         */
        public B excludeTags(Set<String> excludeTags) {
            this.excludeTags = excludeTags;
            return this.self();
        }

        /**
         * 向当前构建器中设置模式。
         *
         * @param mode 表示标签与和或模式的 {@link String}。
         * @return 表示当前构建器的 {@link B}。
         */
        public B mode(String mode) {
            this.mode = mode;
            return this.self();
        }

        /**
         * 向当前构建器中设置页面大小。
         *
         * @param offset 表示页码的 {@link Integer}。
         * @return 表示当前构建器的 {@link B}。
         */
        public B offset(Integer offset) {
            this.offset = offset;
            return this.self();
        }

        /**
         * 向当前构建器中设置页面大小。
         *
         * @param limit 表示页码的 {@link Integer}。
         * @return 表示当前构建器的 {@link B}。
         */
        public B limit(Integer limit) {
            this.limit = limit;
            return this.self();
        }

        /**
         * 向当前构建器中设置版本。
         *
         * @param version 表示页码的 {@link String}。
         * @return 表示当前构建器的 {@link B}。
         */
        public B version(String version) {
            this.version = version;
            return this.self();
        }

        /**
         * 向当前构建器中设置应用类型。
         *
         * @param appCategory 表示应用类型的 {@link String}。
         * @return 表示当前构建器的 {@link B}。
         */
        public B appCategory(String appCategory) {
            this.appCategory = appCategory;
            return this.self();
        }

        /**
         * 向当前构建器中设置用户组唯一标识。
         *
         * @param userGroupId 表示用户组唯一标识的 {@link String}。
         * @return 表示当前构建器的 {@link B}。
         */
        public B userGroupId(String userGroupId) {
            this.userGroupId = userGroupId;
            return this.self();
        }

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link ToolQuery}。
         */
        public ToolQuery build() {
            ToolQuery toolQuery = new ToolQuery();
            toolQuery.setToolName(this.toolName);
            toolQuery.setIncludeTags(this.includeTags);
            toolQuery.setExcludeTags(this.excludeTags);
            toolQuery.setMode(this.mode);
            toolQuery.setOffset(this.offset);
            toolQuery.setLimit(this.limit);
            toolQuery.setVersion(this.version);
            toolQuery.setUserGroupId(this.userGroupId);
            return toolQuery;
        }
    }

    /**
     * 获取工具名。
     *
     * @return 工具名的 {@link String}。
     */
    public String getToolName() {
        return this.toolName;
    }

    /**
     * 设置工具名。
     *
     * @param toolName 表示工具名的 {@link String}。
     */
    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    /**
     * 获取需要包含的标签。
     *
     * @return 表示包含标签的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public Set<String> getIncludeTags() {
        return this.includeTags;
    }

    /**
     * 设置包含标签。
     *
     * @param includeTags 表示包含标签的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public void setIncludeTags(Set<String> includeTags) {
        this.includeTags = includeTags;
    }

    /**
     * 获取排除标签。
     *
     * @return 表示排除标签的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public Set<String> getExcludeTags() {
        return this.excludeTags;
    }

    /**
     * 设置包含标签。
     *
     * @param excludeTags 表示排除标签的 {@link Set}{@code <}{@link String}{@code >}。
     */
    public void setExcludeTags(Set<String> excludeTags) {
        this.excludeTags = excludeTags;
    }

    /**
     * 获取标签与和或的模式。
     *
     * @return 表示标签与和或模式的 {@link String}。
     */
    public String getMode() {
        return this.mode;
    }

    /**
     * 设置标签的与和或的模式。
     *
     * @param mode 表示标签与和或模式的 {@link String}。
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * 获取版本。
     *
     * @return 表示版本的 {@link String}。
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * 设置版本。
     *
     * @param version 表示版本的 {@link String}。
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 获取应用类型。
     *
     * @return 表示应用类型的 {@link String}.
     */
    public String getAppCategory() {
        return this.appCategory;
    }

    /**
     * 设置应用类型。
     *
     * @param appCategory 表示版本的 {@link String}。
     */
    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }

    /**
     * 获取用户组的唯一标识。
     *
     * @return 表示用户组的唯一标识的 {@link String}。
     */
    public String getUserGroupId() {
        return this.userGroupId;
    }

    /**
     * 设置用户组的唯一标识。
     *
     * @return 表示用户组的唯一标识的 {@link String}。
     */
    public void setUserGroupId(String userGroupId) {
        this.userGroupId = userGroupId;
    }

    /**
     * 将查询条件中的标签及模式转为大写。
     *
     * @param toolQuery 表示工具查询的 {@link ToolQuery}。
     * @return 表示转换后的工具查询的 {@link ToolQuery}。
     */
    public static ToolQuery toUpperCase(ToolQuery toolQuery) {
        toolQuery.setMode(validateTagMode(toolQuery.getMode()));
        if (toolQuery.getExcludeTags() != null) {
            toolQuery.setIncludeTags(toolQuery.getIncludeTags()
                    .stream()
                    .map(StringUtils::toUpperCase)
                    .collect(Collectors.toSet()));
        }
        if (toolQuery.getExcludeTags() != null) {
            toolQuery.setExcludeTags(toolQuery.getExcludeTags()
                    .stream()
                    .map(StringUtils::toUpperCase)
                    .collect(Collectors.toSet()));
        }
        return toolQuery;
    }
}

