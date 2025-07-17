/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.mapper;

import modelengine.fel.tool.model.entity.ToolIdentifier;
import modelengine.jade.carver.tool.repository.pgsql.model.entity.ToolDo;

import java.util.List;

/**
 * 表示用于 MyBatis 持久层引用的 Tool 接口。
 *
 * @author 李金绪
 * @since 2024/5/11
 */
public interface ToolMapper {
    /**
     * 注册工具。
     *
     * @param toolDo 表示待注册的工具信息的 {@link ToolDo}。
     */
    void addTool(ToolDo toolDo);

    /**
     * 注册工具列表。
     *
     * @param toolDoList 表示待注册的工具信息的 {@link List}{@code <}{@link ToolDo}{@code >}。
     */
    void addTools(List<ToolDo> toolDoList);

    /**
     * 删除工具。
     *
     * @param uniqueName 表示工具名的 {@link String}。
     */
    void deleteTool(String uniqueName);

    /**
     * 删除工具列表。
     *
     * @param uniqueNames 表示待删除工具唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    void deleteTools(List<String> uniqueNames);

    /**
     * 删除定义组下所有的工具。
     *
     * @param definitionGroupName 表示待删除工具定义组名称的 {@link String}。
     */
    void deleteToolsByDefinitionGroupName(String definitionGroupName);

    /**
     * 删除工具列表。
     *
     * @param definitionGroupName 表示待删除工具定义组名称的 {@link String}。
     * @param groupName 表示待删除工具组名称的 {@link String}。
     */
    void deleteToolsByGroupName(String definitionGroupName, String groupName);

    /**
     * 删除工具的某一个版本。
     *
     * @param uniqueName 表示待删除工具的唯一标识的 {@link String}。
     * @param version 表示待删除工具的版本的 {@link String}。
     */
    void deleteToolByVersion(String uniqueName, String version);

    /**
     * 基于工具的唯一标识查询某个工具。
     *
     * @param uniqueName 表示工具的唯一标识的 {@link String}。
     * @return 表示工具信息的 {@link ToolDo}。
     */
    ToolDo getToolByUniqueName(String uniqueName);

    /**
     * 查询定义组下所有的工具。
     *
     * @param definitionGroupName 表示待注册工具定义组名的 {@link String}。
     * @return 表示工具详细信息的 {@link List}{@code <}{@link ToolDo}{@code >}。
     */
    List<ToolDo> getToolsByDefinitionGroupName(String definitionGroupName);

    /**
     * 查询工具组下所有的工具。
     *
     * @param definitionGroupName 表示待注册工具定义组名的 {@link String}。
     * @param groupName 表示工具组名的 {@link String}。
     * @return 表示工具信息的 {@link List}{@code <}{@link ToolDo}{@code >}。
     */
    List<ToolDo> getToolsByGroupName(String definitionGroupName, String groupName);

    /**
     * 将工具的最新版本设置为不是最新。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     */
    void setNotLatest(String toolUniqueName);

    /**
     * 将工具的某一个版本设置为最新。
     *
     * @param toolUniqueName 表示待更新工具的唯一标识的 {@link String}。
     * @param version 表示待更新工具的版本的 {@link String}。
     */
    void setLatest(String toolUniqueName, String version);

    /**
     * 查询工具的某一个版本。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @param version 表示工具的版本的 {@link String}。
     * @return 表示工具信息的 {@link ToolDo}。
     */
    ToolDo getToolByVersion(String toolUniqueName, String version);

    /**
     * 查询一个工具的所有版本。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @return 表示工具版本列表的 {@link List}{@code <}{@link ToolDo}{@code >}。
     */
    List<ToolDo> getAllToolVersions(String toolUniqueName);

    /**
     * 查询工具的所有版本的总数。
     *
     * @param toolUniqueName 表示工具的唯一标识的 {@link String}。
     * @return 表示工具版本总数的 {@code int}。
     */
    int getAllToolVersionsCount(String toolUniqueName);

    /**
     * 根据工具的唯一标识和版本查询工具列表。
     *
     * @param toolIdentifiers 表示工具的唯一标识和版本列表的 {@link List}{@code <}{@link ToolIdentifier}{@code >}。
     * @return 表示工具信息的 {@link List}{@code <}{@link ToolDo}{@code >}。
     */
    List<ToolDo> getToolsByIdentifier(List<ToolIdentifier> toolIdentifiers);

    /**
     * 查询所有工具信息。
     *
     * @return 表示工具信息的 {@link List}{@code <}{@link ToolDo}{@code >}。
     */
    List<ToolDo> getAllTools();
}
