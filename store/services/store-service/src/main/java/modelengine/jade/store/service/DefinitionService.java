/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.jade.store.service;

import modelengine.fel.tool.model.transfer.DefinitionData;
import modelengine.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 提供工具定义的服务。
 *
 * @author 王攀博
 * @since 2024-10-25
 */
public interface DefinitionService {
    /**
     * 添加定义。
     *
     * @param definition 表示待增加的定义信息的 {@link DefinitionData}。
     * @return 表示添加后的定义的唯一标识的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.carver.definition.add")
    String add(DefinitionData definition);

    /**
     * 添加定义列表。
     *
     * @param definitions 表示待注册的工具信息的 {@link List}{@code <}{@link DefinitionData}{@code >}。
     */
    @Genericable(id = "modelengine.jade.carver.definition.add.list")
    void add(List<DefinitionData> definitions);

    /**
     * 删除定义。
     *
     * @param groupName 表示待删除的定义组名称的 {@link String}。
     * @param name 表示待删除定义名称的 {@link String}。
     * @return 表示删除定义的名称或失败提示的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.carver.definition.delete")
    String delete(String groupName, String name);

    /**
     * 删除定义组下所有的定义。
     *
     * @param groupName 表示待删除定义组名称的 {@link String}。
     */
    @Genericable(id = "modelengine.jade.carver.definition.delete.list")
    void delete(String groupName);

    /**
     * 基于定义的名称查询某个定义。
     *
     * @param groupName 表示待删除的定义组名称的 {@link String}。
     * @param name 表示待删除定义名称的 {@link String}。
     * @return 表示定义数据的 {@link DefinitionData}。
     */
    @Genericable(id = "modelengine.jade.carver.definition.get")
    DefinitionData get(String groupName, String name);

    /**
     * 查询定义组下所有的工具。
     *
     * @param groupName 表示待删除的定义组名称的 {@link String}。
     * @return 表示定义组下的定义列表的 {@link List}{@code <}{@link DefinitionData}{@code >}。
     */
    @Genericable(id = "modelengine.jade.carver.definition.get.byGroup")
    List<DefinitionData> get(String groupName);
}