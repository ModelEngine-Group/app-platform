/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.jade.store.service;

import modelengine.fel.tool.model.transfer.DefinitionGroupData;
import modelengine.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Set;

/**
 * 提供定义组的服务。
 *
 * @author 王攀博
 * @since 2024-10-25
 */
public interface DefinitionGroupService {
    /**
     * 添加定义组。
     *
     * @param definitionGroup 表示待增加的定义组信息的 {@link DefinitionGroupData}。
     * @return 表示添加后的定义的唯一标识的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.carver.definition.add.group")
    String add(DefinitionGroupData definitionGroup);

    /**
     * 添加定义组列表。
     *
     * @param definitionGroups 表示待注册的定义组信息的 {@link List}{@code <}{@link DefinitionGroupData}{@code >}。
     */
    @Genericable(id = "modelengine.jade.carver.definition.add.groups")
    void add(List<DefinitionGroupData> definitionGroups);

    /**
     * 删除定义。
     *
     * @param definitionGroupName 表示待删除定义组名称的 {@link String}。
     * @return 表示删除定义组的名称或失败提示的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.carver.definition.delete.group")
    String delete(String definitionGroupName);

    /**
     * 删除定义列表。
     *
     * @param definitionGroupNames 表示待删除定义组名称列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    @Genericable(id = "modelengine.jade.carver.definition.delete.groups")
    void delete(List<String> definitionGroupNames);

    /**
     * 基于定义的名称查询某个工具。
     *
     * @param name 表示定义组的名称的 {@link String}。
     * @return 表示定义组数据的 {@link DefinitionGroupData}。
     */
    @Genericable(id = "modelengine.jade.carver.definition.get.group")
    DefinitionGroupData get(String name);

    /**
     * 查找数据库中第一个匹配的定义名，用于判断重复。
     *
     * @param defGroupNames 表示指定的定义组名的 {@link String}{@code <}{@link String}{@code >}。
     * @return 表示第一个匹配的定义组名的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.carver.definition.find.first.exist.group")
    String findFirstExistDefGroup(Set<String> defGroupNames);

    /**
     * 查找数据库中已存在的定义名集合，用于判断重复。
     *
     * @param defGroupNames 表示指定的定义组名的 {@link String}{@code <}{@link String}{@code >}。
     * @return 表示第一个匹配的定义组名的 {@link List}{@code <}{@link String}{@code >}。
     */
    @Genericable(id = "modelengine.jade.carver.definition.find.exist.groups")
    List<String> findExistDefGroups(Set<String> defGroupNames);

    /**
     * 查找数据库中第一个匹配的 定义组-定义 组合，用于判断重复。
     *
     * @param defGroupName 表示指定的定义组名的 {@link String}。
     * @param defNames 表示指定的定义名的 {@link Set}{@code <}{@link String}{@code >}。
     * @return 表示第一个匹配的定义组名+定义名的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.carver.definition.exist.def.names.inGroup")
    String findFirstExistDefNameInDefGroup(String defGroupName, Set<String> defNames);
}
