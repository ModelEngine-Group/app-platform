/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jober.aipp.dto.AppBuilderWaterFlowInfoDto;
import modelengine.fit.jober.aipp.dto.ModelDto;
import modelengine.fit.jober.aipp.dto.PluginToolDto;
import modelengine.fit.jober.aipp.dto.StoreNodeConfigResDto;
import modelengine.fit.jober.aipp.dto.StoreNodeInfoDto;

import modelengine.jade.store.entity.query.PluginToolQuery;
import modelengine.jade.store.entity.query.ToolQuery;

import modelengine.fit.jane.common.entity.OperationContext;

import java.util.List;

/**
 * Store 相关服务
 *
 * @author 邬涨财
 * @since 2024-05-13
 */
public interface StoreService {
    /**
     * 获取基础节点和工具。
     *
     * @param tag 表示标签的 {@link String}。
     * @param mode 表示选择标签的与和或逻辑的 {@link String}。
     * @param pageNum 表示分页查询的页数的 {@code int}。
     * @param pageSize 表示分页查询的大小的 {@code int}。
     * @param version 表示工具的版本的 {@link String}。
     * @return 表示基础节点和工具的响应的 {@link StoreNodeConfigResDto}。
     */
    StoreNodeConfigResDto getBasicNodesAndTools(String tag, String mode, int pageNum, int pageSize, String version);

    /**
     * 获取工具流配置对象列表。
     *
     * @param mode 表示选择标签的与和或逻辑的 {@link String}。
     * @param pageNum 表示分页的页数的 {@link int}。
     * @param pageSize 表示分页的每页个数的 {@link int}。
     * @param version 表示工具的版本的 {@link String}。
     * @return 表示获取到的工具流配置对象列表的 {@link List}{@code <}{@link AppBuilderWaterFlowInfoDto}{@code >}。
     */
    List<AppBuilderWaterFlowInfoDto> getWaterFlowInfos(String mode, int pageNum, int pageSize, String version);

    /**
     * 获取指定插件列表。
     *
     * @param pluginToolQuery 表示插件工具的动态查询条件的 {@link ToolQuery}。
     * @param operationContext 表示操作上下文的 {@link OperationContext}。
     * @return 表示插件列表和总数的 {@link PluginToolDto}。
     */
    PluginToolDto getPlugins(PluginToolQuery pluginToolQuery, OperationContext operationContext);

    /**
     * 根据 type {@link String} 获取相应节点配置。
     *
     * @param type 表示列表类型的 {@link String}。
     * @return 表示基础或者评估节点列表的 {@link List}{@code <}{@link StoreNodeInfoDto}{@code >}。
     */
    List<StoreNodeInfoDto> getNode(String type);

    /**
     * 获取模型列表。
     *
     * @param taskName 表示任务名的 {@link String}。
     * @param pageNum 表示分页查询的页数的 {@code int}。
     * @param pageSize 表示分页查询的大小的 {@code int}。
     * @return 模型列表的 {@link ModelDto}。
     */
    ModelDto getModels(String taskName, int pageNum, int pageSize);
}
