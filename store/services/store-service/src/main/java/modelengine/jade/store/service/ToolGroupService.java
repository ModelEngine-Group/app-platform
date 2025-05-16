/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.jade.store.service;

import modelengine.fel.tool.model.transfer.ToolGroupData;
import modelengine.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Set;

/**
 * 提工具组的服务。
 *
 * @author 王攀博
 * @since 2024-10-25
 */
public interface ToolGroupService {
    /**
     * 添加工具组。
     *
     * @param toolGroup 表示待增加的工具组信息的 {@link ToolGroupData}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.add.group")
    void add(ToolGroupData toolGroup);

    /**
     * 添加工具组列表。
     *
     * @param toolGroupDataList 表示待注册的工具组信息的 {@link List}{@code <}{@link ToolGroupData}{@code >}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.add.groups")
    void add(List<ToolGroupData> toolGroupDataList);

    /**
     * 删除工具组。
     *
     * @param definitionGroupName 表示待删除的定义组的名字的 {@link String}。
     * @param toolGroupName 表示待删除工具组名称的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.delete.group")
    void delete(String definitionGroupName, String toolGroupName);

    /**
     * 删除定义列表。
     *
     * @param definitionGroupName 表示待删除的定义组的名字的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.delete.definition.group")
    void deleteByDefinitionGroupName(String definitionGroupName);

    /**
     * 基于工具组名称查询工具组。
     *
     * @param definitionGroupName 表示定义组名称的 {@link String}。
     * @param toolGroupNames 表示工具组的名称列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示工具组数据的 {@link List}{@code <}{@link ToolGroupData}{@code >}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.get.groups")
    List<ToolGroupData> get(String definitionGroupName, List<String> toolGroupNames);

    /**
     * 查询定义组下所有工具组数据。
     *
     * @param definitionGroupName 表示工具定义组的名称的 {@link String}。
     * @return 表示工具组数据的 {@link List}{@code <}{@link ToolGroupData}{@code >}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.get.definition.group")
    List<ToolGroupData> get(String definitionGroupName);

    /**
     * 查找数据库中是否有任意一个指定的 实现组-实现 组合，用于判断重复。
     *
     * @param defGroupName 表示指定的定义组名的 {@link String}。
     * @param toolGroupName 表示指定的实现组名的 {@link String}。
     * @param toolNames 表示指定的实现名的 {@link Set}{@code <}{@link String}{@code >}。
     * @return 表示是否存在的 {@code boolean}。
     */
    @Deprecated
    @Genericable(id = "modelengine.jade.carver.tool.exist.tool.names.inGroup")
    boolean isExistAnyToolInToolGroup(String defGroupName, String toolGroupName, Set<String> toolNames);

    /**
     * 查找数据库中第一个匹配的 定义组-实现组 组合，用于判断重复。
     *
     * @param toolGroupName 表示指定的实现组名的 {@link String}。
     * @param defGroupName 表示指定的定义组名的 {@link String}。
     * @return 表示第一个匹配的定义组名-实现组名的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.carver.tool.exist.toolGroup.inDefGroup")
    String findFirstExistToolGroupInDefGroup(String toolGroupName, String defGroupName);
}