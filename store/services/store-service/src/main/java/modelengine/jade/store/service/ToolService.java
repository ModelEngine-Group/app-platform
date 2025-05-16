/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.jade.store.service;

import modelengine.fel.tool.model.ListResult;
import modelengine.fel.tool.model.entity.ToolIdentifier;
import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 提供工具的通用服务。
 *
 * @author 鲁为
 * @since 2024-04-16
 */
public interface ToolService {
    /**
     * 添加工具。
     *
     * @param tool 表示待增加的工具信息的 {@link ToolData}。
     * @return 表示添加后的工具的唯一标识的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.addTool")
    String addTool(ToolData tool);

    /**
     * 注册工具列表。
     *
     * @param toolDataList 表示待注册的工具信息的 {@link List}{@code <}{@link ToolData}{@code >}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.addTools")
    void addTools(List<ToolData> toolDataList);

    /**
     * 注册一个工具组下所有工具。
     *
     * @param definitionGroupName 表示待注册工具定义组名的 {@link String}。
     * @param groupName 表示待注册的工具组名的 {@link String}。
     * @param toolDataList 表示待注册的工具信息的 {@link List}{@code <}{@link ToolData}{@code >}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.addGroupTools")
    void addTools(String definitionGroupName, String groupName, List<ToolData> toolDataList);

    /**
     * 删除工具。
     *
     * @param toolUniqueName 表示待删除工具唯一标识的 {@link String}。
     * @return 表示删除工具的唯一标识名或失败提示的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.deleteTool")
    String deleteTool(String toolUniqueName);

    /**
     * 删除工具列表。
     *
     * @param uniqueNames 表示待删除工具唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.deleteTools")
    void deleteTools(List<String> uniqueNames);

    /**
     * 删除工具组下所有的工具。
     *
     * @param definitionGroupName 表示待删除工具定义组名的 {@link String}。
     * @param groupName 表示待删除的工具组名的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.deleteTools.byToolGroup")
    void deleteTools(String definitionGroupName, String groupName);

    /**
     * 删除工具组下所有的工具。
     *
     * @param definitionGroupName 表示待删除工具定义组名的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.deleteTools.byDefinitionGroup")
    void deleteToolsByDefinitionGroupName(String definitionGroupName);

    /**
     * 删除工具的某一个版本。
     *
     * @param uniqueName 表示待删除工具的唯一标识的 {@link String}。
     * @param version 表示待删除工具的版本的 {@link String}。
     * @return 表示删除工具的唯一标识名或失败提示的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.deleteToolByVersion")
    String deleteToolByVersion(String uniqueName, String version);

    /**
     * 基于工具的唯一标识查询某个工具。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @return 表示工具详细信息的 {@link ToolData}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.getTool.byUniqueName")
    ToolData getTool(String toolUniqueName);

    /**
     * 查询工具组下所有的工具。
     *
     * @param definitionGroupName 表示待注册工具定义组名的 {@link String}。
     * @param groupName 表示工具组名的 {@link String}。
     * @return 表示工具详细信息的 {@link List}{@code <}{@link ToolData}{@code >}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.get.group.tools")
    List<ToolData> getTools(String definitionGroupName, String groupName);

    /**
     * 查询定义组下所有的工具。
     *
     * @param definitionGroupName 表示待注册工具定义组名的 {@link String}。
     * @return 表示工具详细信息的 {@link List}{@code <}{@link ToolData}{@code >}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.get.definition.group.tools")
    List<ToolData> getTools(String definitionGroupName);

    /**
     * 查询工具的某一个版本。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @param version 表示工具的版本的 {@link String}。
     * @return 表示工具详细信息的 {@link ToolData}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.getTool.byVersion")
    ToolData getToolByVersion(String toolUniqueName, String version);

    /**
     * 查询一个工具的所有版本。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @return 表示工具版本列表的 {@link ListResult}{@code <}{@link ToolData}{@code >}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.getAllToolVersions")
    ListResult<ToolData> getAllToolVersions(String toolUniqueName);

    /**
     * 升级工具。
     *
     * @param toolData 表示待升级的工具的 {@link ToolData}。
     * @return 表示工具唯一标识的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.upgradeTool")
    String upgradeTool(ToolData toolData);

    /**
     * 根据工具的唯一标识和版本查询工具。
     *
     * @param toolIdentifiers 表示工具的唯一标识和版本的 {@link List}{@code <}{@link ToolIdentifier}{@code >}。
     * @return 表示工具列表的 {@link ListResult}{@code <}{@link ToolData}{@code >}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.getTools.byUniqueNameAndVersion")
    ListResult<ToolData> getToolsByIdentifier(List<ToolIdentifier> toolIdentifiers);
}
