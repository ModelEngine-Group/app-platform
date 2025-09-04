/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.controller;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNegative;
import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.jade.carver.validation.ValidateTagMode.validateTagMode;
import static modelengine.jade.common.Result.calculateOffset;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fit.jade.aipp.domain.division.annotation.GetSource;
import modelengine.fitframework.annotation.Component;
import modelengine.jade.carver.ListResult;
import modelengine.jade.common.Result;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.store.code.PluginRetCode;
import modelengine.jade.store.entity.aop.PluginValidation;
import modelengine.jade.store.entity.query.PluginQuery;
import modelengine.jade.store.entity.query.PluginToolQuery;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.service.PluginService;
import modelengine.jade.store.service.PluginToolService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * 处理插件 HTTP 请求的控制器。
 *
 * @author 鲁为
 * @since 2024-06-20
 */
@Component
@RequestMapping("/store/plugins")
public class PluginController {
    private static final int MAX_PLUGIN_TOOLS_COUNT = 50;

    private final PluginToolService pluginToolService;

    private final PluginService pluginService;

    /**
     * 通过插件服务来初始化 {@link PluginController} 的新实例。
     *
     * @param pluginToolService 表示插件工具服务的 {@link PluginToolService}。
     * @param pluginService 表示插件服务的 {@link PluginService}。
     */
    public PluginController(PluginToolService pluginToolService, PluginService pluginService) {
        this.pluginToolService = notNull(pluginToolService, "The plugin tool service cannot be null.");
        this.pluginService = notNull(pluginService, "The plugin service cannot be null.");
    }

    /**
     * 基于插件工具的唯一标识查询某个插件工具。
     *
     * @param uniqueName 表示工具的唯一索引的 {@link String}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link PluginToolData}{@code >}。
     */
    @GetMapping("/tools/{uniqueName}")
    public Result<PluginToolData> getPluginToolByUniqueName(@PathVariable("uniqueName") String uniqueName) {
        notBlank(uniqueName, "The plugin tool unique name cannot be blank.");
        return Result.ok(this.pluginToolService.getPluginTool(uniqueName), 1);
    }

    /**
     * 基于插件的唯一标识查询某个插件。
     *
     * @param pluginId 表示工具的唯一索引的 {@link String}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link PluginData}{@code >}。
     */
    @GetMapping("/{pluginId}")
    @PluginValidation
    public Result<PluginData> getPluginByPluginId(@PathVariable("pluginId") String pluginId) {
        notBlank(pluginId, "The plugin id cannot be blank.");
        return Result.ok(this.pluginService.getPlugin(pluginId), 1);
    }

    /**
     * 根据动态查询条件模糊获取插件工具列表。
     *
     * @param name 表示插件名的 {@link String}。
     * @param includeTags 表示包含标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示排除标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param mode 表示查询工具的标签与和或方式的 {@link String}。
     * @param pageNum 表示页码的 {@code int}。
     * @param pageSize 表示限制的 {@code int}。
     * @param version 表示工具的版本的 {@link String}。
     * @param creator 表示插件工具的创建者的 {@link String}。
     * @param isBuiltin 表示插件是否内置的 {@link Boolean}。
     * @param isDeployed 表示插件是否已部署的 {@link Boolean}。
     * @return 表示格式化的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link PluginToolData}{@code >}{@code >}。
     */
    @GetMapping("/tools/search")
    @GetSource
    public Result<List<PluginToolData>> getPluginTools(@RequestQuery(value = "name", required = false) String name,
            @RequestQuery(value = "includeTags", required = false) List<String> includeTags,
            @RequestQuery(value = "excludeTags", required = false) List<String> excludeTags,
            @RequestQuery(value = "mode", defaultValue = "AND", required = false) String mode,
            @RequestQuery(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestQuery(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestQuery(value = "version", required = false) String version,
            @RequestQuery(value = "creator", required = false) String creator,
            @RequestQuery(value = "isBuiltin", required = false) Boolean isBuiltin,
            @RequestQuery(value = "isDeployed", defaultValue = "true") Boolean isDeployed) {
        notNegative(pageNum, "The page number cannot be negative.");
        notNegative(pageSize, "The page size cannot be negative.");
        PluginToolQuery pluginToolQuery = new PluginToolQuery.Builder().toolName(name)
                .includeTags(new HashSet<>(includeTags))
                .excludeTags(new HashSet<>(excludeTags))
                .mode(validateTagMode(mode))
                .offset(calculateOffset(pageNum, pageSize))
                .limit(pageSize)
                .version(version)
                .creator(creator)
                .isBuiltin(isBuiltin)
                .isDeployed(isDeployed)
                .build();
        ListResult<PluginToolData> res = this.pluginToolService.getPluginTools(pluginToolQuery);
        return Result.ok(res.getData(), res.getCount());
    }

    /**
     * 根据插件工具唯一标识列表获取插件工具列表。
     *
     * @param uniqueNames 表示插件名的 {@link List}{@code <{@link String}{@code >}。
     * @return 表示格式化的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link PluginToolData}{@code >}{@code >}。
     */
    @GetMapping("/tools")
    public Result<List<PluginToolData>> getPluginTools(
            @RequestQuery(value = "uniqueNames", required = false) List<String> uniqueNames) {
        if (uniqueNames.isEmpty()) {
            return Result.ok(Collections.emptyList(), 0);
        }
        if (uniqueNames.size() > MAX_PLUGIN_TOOLS_COUNT) {
            throw new ModelEngineException(PluginRetCode.PLUGIN_TOOL_COUNT_EXCEEDED_LIMIT);
        }
        List<PluginToolData> res = this.pluginToolService.getPluginTools(uniqueNames);
        return Result.ok(res, res.size());
    }

    /**
     * 根据动态查询条件模糊获取插件列表。
     *
     * @param name 表示插件名的 {@link String}。
     * @param includeTags 表示包含标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示排除标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param mode 表示查询工具的标签与和或方式的 {@link String}。
     * @param pageNum 表示页码的 {@code int}。
     * @param pageSize 表示限制的 {@code int}。
     * @param isBuiltin 表示插件是否内置的 {@link Boolean}。
     * @param creator 表示插件工具的创建者的 {@link String}。
     * @param isDeployed 表示插件是否已经部署的 {@link Boolean}。
     * @return 表示格式化的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link PluginToolData}{@code >}{@code >}。
     */
    @GetMapping("/search")
    @GetSource
    public Result<List<PluginData>> getPlugins(@RequestQuery(value = "name", required = false) String name,
            @RequestQuery(value = "includeTags", required = false) List<String> includeTags,
            @RequestQuery(value = "excludeTags", required = false) List<String> excludeTags,
            @RequestQuery(value = "mode", defaultValue = "AND", required = false) String mode,
            @RequestQuery(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestQuery(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestQuery(value = "isBuiltin", required = false) Boolean isBuiltin,
            @RequestQuery(value = "creator", required = false) String creator,
            @RequestQuery(value = "isDeployed", required = false) Boolean isDeployed) {
        notNegative(pageNum, "The page number cannot be negative.");
        notNegative(pageSize, "The page size cannot be negative.");
        PluginQuery pluginQuery = new PluginQuery.Builder().toolName(name)
                .includeTags(new HashSet<>(includeTags))
                .excludeTags(new HashSet<>(excludeTags))
                .mode(validateTagMode(mode))
                .offset(calculateOffset(pageNum, pageSize))
                .limit(pageSize)
                .isBuiltin(isBuiltin)
                .creator(creator)
                .isDeployed(isDeployed)
                .build();
        ListResult<PluginData> res = this.pluginService.getPlugins(pluginQuery);
        return Result.ok(res.getData(), res.getCount());
    }
}