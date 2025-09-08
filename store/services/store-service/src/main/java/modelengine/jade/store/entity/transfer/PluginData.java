/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.entity.transfer;

import modelengine.fel.tool.model.transfer.DefinitionGroupData;
import modelengine.fel.tool.model.transfer.ToolGroupData;

import java.util.List;
import java.util.Map;

/**
 * 表示插件的数据内容。
 *
 * @author 鲁为
 * @since 2024-07-25
 */
public class PluginData {
    /**
     * 表示插件的创建者。
     * <p>
     *     <ul>
     *         <li>添加插件时可选。</li>
     *         <li>查询插件时会返回。</li>
     *     </ul>
     * </p>
     */
    private String creator;

    /**
     * 表示插件的修改者。
     * <p>
     *     <ul>
     *         <li>添加插件时可选。</li>
     *         <li>查询插件时会返回。</li>
     *     </ul>
     * </p>
     */
    private String modifier;

    /**
     * 表示插件的唯一标识。
     * <p>
     *     <ul>
     *         <li>添加插件时不需要设置。</li>
     *         <li>查询插件时会返回。</li>
     *     </ul>
     * </p>
     */
    private String pluginId;

    /**
     * 表示插件的名字。
     * <p>
     *     <ul>
     *         <li>添加插件时需传入。</li>
     *         <li>查询插件时会返回。</li>
     *     </ul>
     * </p>
     */
    private String pluginName;

    /**
     * 表示插件的扩展信息。
     * <p>
     *     <ul>
     *         <li>添加插件时需传入。</li>
     *         <li>查询插件时会返回。</li>
     *     </ul>
     * </p>
     */
    private Map<String, Object> extension;

    /**
     * 表示插件的部署状态。
     * <p>
     *     <ul>
     *         <li>添加插件时不传入。</li>
     *         <li>查询插件时会返回。</li>
     *     </ul>
     * </p>
     */
    private String deployStatus;

    /**
     * 表示插件包含的插件工具数据。
     * <p>
     *     <ul>
     *         <li>添加插件时不传入。</li>
     *         <li>查询插件时会返回。</li>
     *     </ul>
     * </p>
     */
    private List<PluginToolData> pluginToolDataList;

    /**
     * 表示插件包含的定义组数据。
     * <p>
     *     <ul>
     *         <li>添加插件时需传入。</li>
     *         <li>查询插件时会返回。</li>
     *     </ul>
     * </p>
     */
    private List<DefinitionGroupData> definitionGroupDataList;

    /**
     * 表示插件包含的工具组数据。
     * <p>
     *     <ul>
     *         <li>添加插件时需传入。</li>
     *         <li>查询插件时会返回。</li>
     *     </ul>
     * </p>
     */
    private List<ToolGroupData> toolGroupDataList;

    /**
     * 表示插件是否内置。
     * <p>
     *     <ul>
     *         <li>添加插件时不传入。</li>
     *         <li>查询插件时会返回。</li>
     *     </ul>
     * </p>
     */
    private Boolean isBuiltin;

    /**
     * 表示插件的来源。
     */
    private String source;

    /**
     * 表示插件的图标。
     */
    private String icon;

    /**
     * 表示应用种类信息。
     */
    private String appCategory;

    /**
     * 表示用户组。
     */
    private String userGroupId;

    /**
     * 表示是否可修改。
     */
    private boolean isModifiable;

    /**
     * 获取插件的创建者。
     *
     * @return 表示插件创建者的 {@link String}。
     */
    public String getCreator() {
        return this.creator;
    }

    /**
     * 设置插件的创建者。
     *
     * @param creator 表示插件创建者的 {@link String}。
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * 获取插件的修改者。
     *
     * @return 表示插件修改者的 {@link String}。
     */
    public String getModifier() {
        return this.modifier;
    }

    /**
     * 设置插件的修改者。
     *
     * @param modifier 表示插件修改者的 {@link String}。
     */
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    /**
     * 获取插件的唯一标识。
     *
     * @return 表示插件唯一标识的 {@link String}。
     */
    public String getPluginId() {
        return this.pluginId;
    }

    /**
     * 设置插件唯一标识。
     *
     * @param pluginId 表示插件唯一标识的 {@link String}。
     */
    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    /**
     * 获取插件的名字。
     *
     * @return 表示插件名字的 {@link String}。
     */
    public String getPluginName() {
        return this.pluginName;
    }

    /**
     * 设置插件的名字。
     *
     * @param pluginName 表示插件名字的 {@link String}。
     */
    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    /**
     * 获取插件的扩展。
     *
     * @return 表示插件扩展的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public Map<String, Object> getExtension() {
        return this.extension;
    }

    /**
     * 设置插件的扩展。
     *
     * @param extension 表示插件扩展的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public void setExtension(Map<String, Object> extension) {
        this.extension = extension;
    }

    /**
     * 获取插件的部署状态。
     *
     * @return 表示部署状态的 {@link String}。
     */
    public String getDeployStatus() {
        return deployStatus;
    }

    /**
     * 设置插件的部署状态。
     *
     * @param deployStatus 表示插件部署状态的 {@link String}。
     */
    public void setDeployStatus(String deployStatus) {
        this.deployStatus = deployStatus;
    }

    /**
     * 获取插件包含的插件工具的列表。
     *
     * @return 表示插件工具列表的 {@link List}{@code <}{@link PluginToolData}{@code >}。
     */
    public List<PluginToolData> getPluginToolDataList() {
        return this.pluginToolDataList;
    }

    /**
     * 设置插件工具列表。
     *
     * @param pluginToolDataList 表示插件工具列表的 {@link List}{@code <}{@link PluginToolData}{@code >}。
     */
    public void setPluginToolDataList(List<PluginToolData> pluginToolDataList) {
        this.pluginToolDataList = pluginToolDataList;
    }

    /**
     * 获取是否内置。
     *
     * @return 表示是否内置的 {@link Boolean}。
     */
    public Boolean getBuiltin() {
        return this.isBuiltin;
    }

    /**
     * 设置是否内置。
     *
     * @param builtin 表示是否内置的 {@link Boolean}。
     */
    public void setBuiltin(Boolean builtin) {
        this.isBuiltin = builtin;
    }

    /**
     * 获取插件的来源。
     *
     * @return 表示插件来源的 {@link String}。
     */
    public String getSource() {
        return source;
    }

    /**
     * 设置插件的来源。
     *
     * @param source 表示插件来源的 {@link String}。
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 获取插件的图标。
     *
     * @return 表示插件图标的 {@link String}。
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置插件的图标。
     *
     * @param icon 表示插件图标的 {@link String}。
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 获取插件包含的定义组数据。
     *
     * @return 表示插件包含的定义组数据的 {@link List}{@code <}{@link DefinitionGroupData}{@code >}。
     */
    public List<DefinitionGroupData> getDefinitionGroupDataList() {
        return this.definitionGroupDataList;
    }

    /**
     * 设置插件包含的定义组数据。
     *
     * @param defGroupDataList 表示插件包含的定义组数据的 {@link List}{@code <}{@link DefinitionGroupData}{@code >}。
     */
    public void setDefinitionGroupDataList(List<DefinitionGroupData> defGroupDataList) {
        this.definitionGroupDataList = defGroupDataList;
    }

    /**
     * 获取插件包含的工具组数据。
     *
     * @return 表示插件包含的工具组数据的 {@link List}{@code <}{@link ToolGroupData}{@code >}。
     */
    public List<ToolGroupData> getToolGroupDataList() {
        return this.toolGroupDataList;
    }

    /**
     * 设置插件包含的工具组数据。
     *
     * @param toolGroupDataList 表示插件包含的工具组数据的 {@link List}{@code <}{@link ToolGroupData}{@code >}。
     */
    public void setToolGroupDataList(List<ToolGroupData> toolGroupDataList) {
        this.toolGroupDataList = toolGroupDataList;
    }

    /**
     * 设置应用种类信息。
     *
     * @param appCategory 表示应用种类信息的 {@link String}。
     */
    public void setAppCategory(String appCategory) {
        this.appCategory = appCategory;
    }

    /**
     * 获取应用种类信息。
     *
     * @return 表示应用种类信息的 {@link String}.
     */
    public String getAppCategory() {
        return appCategory;
    }

    /**
     * 获取用户组 id。
     *
     * @return 表示用户组 id 的 {@link String}.
     */
    public String getUserGroupId() {
        return this.userGroupId;
    }

    /**
     * 设置用户组 id。
     *
     * @param userGroupId 表示用户组 id 的 {@link String}.
     */
    public void setUserGroupId(String userGroupId) {
        this.userGroupId = userGroupId;
    }

    /**
     * 获取是否可修改。
     *
     * @return 表示是否可修改的 {@code boolean}.
     */
    public boolean getModifiable() {
        return this.isModifiable;
    }

    /**
     * 设置是否可修改。
     *
     * @return 表示是否可修改的 {@code boolean}.
     */
    public void setModifiable(boolean isModifiable) {
        this.isModifiable = isModifiable;
    }
}
