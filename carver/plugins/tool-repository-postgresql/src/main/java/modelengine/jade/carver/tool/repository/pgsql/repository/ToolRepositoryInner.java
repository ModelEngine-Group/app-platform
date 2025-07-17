/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.repository;

import modelengine.fel.tool.Tool;
import modelengine.fel.tool.model.entity.ToolIdentifier;

import java.util.List;
import java.util.Optional;

/**
 * 表示工具的仓库。
 *
 * @author 王攀博
 * @since 2024-04-27
 */
public interface ToolRepositoryInner {
    /**
     * 添加工具。
     *
     * @param info 表示待增加的工具信息的 {@link Tool.Info}。
     */
    void addTool(Tool.Info info);

    /**
     * 添加工具列表。
     *
     * @param infos 表示待增加的工具列表的 {@link Tool.Info}。
     */
    void addTools(List<Tool.Info> infos);

    /**
     * 按照组添加工具列表。
     *
     * @param definitionGroupName 表示待增加的工具定义组名字的 {@link String}。
     * @param groupName 表示待增加的工具组名字的 {@link String}。
     * @param infos 表示待增加的工具列表的 {@link Tool.Info}。
     */
    void addTools(String definitionGroupName, String groupName, List<Tool.Info> infos);

    /**
     * 删除工具。
     *
     * @param uniqueName 表示待删除工具唯一标识的 {@link String}。
     */
    void deleteTool(String uniqueName);

    /**
     * 删除工具列表。
     *
     * @param uniqueNames 表示待删除工具唯一标识的 {@link List}{@code <}{@link String}{@code >}。
     */
    void deleteTools(List<String> uniqueNames);

    /**
     * 删除定义组下所有的工具。
     *
     * @param definitionGroupName 表示待删除工具定义组名称的 {@link String}。
     */
    void deleteTools(String definitionGroupName);

    /**
     * 删除工具列表。
     *
     * @param definitionGroupName 表示待删除工具定义组名称的 {@link String}。
     * @param groupName 表示待删除工具组名称的 {@link String}。
     */
    void deleteTools(String definitionGroupName, String groupName);

    /**
     * 删除工具的某一个版本。
     *
     * @param uniqueName 表示待删除工具的唯一标识的 {@link String}。
     * @param version 表示待删除工具的版本的 {@link String}。
     * @return 表示删除工具的唯一标识名或失败提示的 {@link String}。
     */
    String deleteToolByVersion(String uniqueName, String version);

    /**
     * 将工具的某一个版本置为最新。
     *
     * @param uniqueName 表示待删除工具的唯一标识的 {@link String}。
     * @param version 表示待删除工具的版本的 {@link String}。
     */
    void setLatest(String uniqueName, String version);

    /**
     * 基于工具的唯一标识查询某个工具。
     *
     * @param uniqueName 表示工具的唯一标识的 {@link String}。
     * @return 表示工具详细信息的 {@link Optional}{@code <}{@link Tool.Info}{@code >}。
     */
    Optional<Tool.Info> getTool(String uniqueName);

    /**
     * 查询定义组下所有的工具。
     *
     * @param definitionGroupName 表示待注册工具定义组名的 {@link String}。
     * @return 表示工具详细信息的 {@link List}{@code <}{@link Tool.Info}{@code >}。
     */
    List<Tool.Info> getTools(String definitionGroupName);

    /**
     * 查询工具组下所有的工具数据。
     *
     * @param definitionGroupName 表示待增加的工具定义组名字的 {@link String}。
     * @param groupName 表示工具组名的 {@link String}。
     * @return 表示工具详细信息的 {@link List}{@code <}{@link Tool.Info}{@code >}。
     */
    List<Tool.Info> getTools(String definitionGroupName, String groupName);

    /**
     * 将工具的最新版本设置为不是最新。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     */
    void setNotLatest(String toolUniqueName);

    /**
     * 查询工具的某一个版本。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @param version 表示工具的版本的 {@link String}。
     * @return 表示工具详细信息的 {@link Optional}{@code <}{@link Tool.Info}{@code >}。
     */
    Optional<Tool.Info> getToolByVersion(String toolUniqueName, String version);

    /**
     * 查询一个工具的所有版本。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @return 表示工具版本列表的 {@link List}{@code <}{@link Tool.Info}{@code >}。
     */
    List<Tool.Info> getAllToolVersions(String toolUniqueName);

    /**
     * 查询一个工具的所有版本的总数。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @return 表示工具版本总数的 {@code int}。
     */
    int getAllToolVersionsCount(String toolUniqueName);

    /**
     * 根据工具的唯一标识和版本列表查询工具列表。
     *
     * @param toolIdentifiers 表示工具的唯一标识和版本列表的 {@link List}{@code <}{@link ToolIdentifier}{@code >}。
     * @return 表示工具详细信息列表的 {@link List}{@code <}{@link Tool.Info}{@code >}。
     */
    List<Tool.Info> getToolsByIdentifier(List<ToolIdentifier> toolIdentifiers);

    /**
     * 查询所有工具信息。
     *
     * @return 表示工具详细信息列表的 {@link List}{@code <}{@link Tool.Info}{@code >}。
     */
    List<Tool.Info> getAllTools();
}
