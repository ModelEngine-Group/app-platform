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

import modelengine.fel.tool.model.ListResult;
import modelengine.fel.tool.model.transfer.DefinitionGroupData;
import modelengine.jade.store.service.ToolService;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fitframework.annotation.Component;
import modelengine.jade.common.Result;
import modelengine.jade.store.entity.query.ToolQuery;
import modelengine.jade.store.entity.transfer.StoreToolData;
import modelengine.jade.store.service.StoreToolService;

import java.util.HashSet;
import java.util.List;

/**
 * 处理 HTTP 请求的控制器。
 *
 * @author 李金绪
 * @since 2024-05-10
 */
@Component
@RequestMapping("/tools")
public class ToolController {
    private final StoreToolService storeToolService;

    /**
     * 通过工具服务和序列化工具来初始化 {@link ToolController} 的新实例。
     *
     * @param storeToolService 表示工具服务的 {@link ToolService}。
     */
    public ToolController(StoreToolService storeToolService) {
        this.storeToolService = notNull(storeToolService, "The store tool service cannot be null.");
    }

    /**
     * 基于工具的唯一标识查询某个工具。
     *
     * @param uniqueName 表示工具的唯一索引的 {@link String}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link StoreToolData}{@code >}。
     */
    @GetMapping("/{uniqueName}")
    public Result<StoreToolData> getToolByUniqueName(@PathVariable("uniqueName") String uniqueName) {
        notBlank(uniqueName, "The tool unique name cannot be blank.");
        return Result.ok(this.storeToolService.getTool(uniqueName));
    }

    /**
     * 根据动态查询条件准确获取工具列表。
     *
     * @param name 表示工具名的 {@link String}。
     * @param includeTags 表示包含标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示排除标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param mode 表示查询工具的标签选择与和或的方式的 {@link String}。
     * @param pageNum 表示页码的 {@code int}。
     * @param pageSize 表示限制的 {@code int}。
     * @param version 表示工具版本的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link StoreToolData}{@code >}{@code >}。
     */
    @GetMapping
    public Result<List<StoreToolData>> getTools(@RequestQuery(value = "name", required = false) String name,
            @RequestQuery(value = "includeTags", required = false) List<String> includeTags,
            @RequestQuery(value = "excludeTags", required = false) List<String> excludeTags,
            @RequestQuery(value = "mode", defaultValue = "AND", required = false) String mode,
            @RequestQuery(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestQuery(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestQuery(value = "version", required = false) String version) {
        notNegative(pageNum, "The page number cannot be negative.");
        notNegative(pageSize, "The page size cannot be negative.");
        ToolQuery toolQuery = new ToolQuery.Builder().toolName(name)
                .includeTags(new HashSet<>(includeTags))
                .excludeTags(new HashSet<>(excludeTags))
                .mode(validateTagMode(mode))
                .offset(calculateOffset(pageNum, pageSize))
                .limit(pageSize)
                .version(version)
                .build();
        ListResult<StoreToolData> res = this.storeToolService.getTools(toolQuery);
        return Result.ok(res.getData(), res.getCount());
    }

    /**
     * 获取某个版本的工具。
     *
     * @param toolUniqueName 表示工具唯一标识的 {@link String}。
     * @param version 表示工具版本的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link StoreToolData}{@code >}。
     */
    @GetMapping("/{toolUniqueName}/{version}")
    public Result<StoreToolData> getToolVersion(@PathVariable(value = "toolUniqueName") String toolUniqueName,
            @PathVariable(value = "version") String version) {
        notBlank(toolUniqueName, "The unique name cannot be blank.");
        return Result.ok(this.storeToolService.getToolByVersion(toolUniqueName, version));
    }

    /**
     * 获取工具的所有版本。
     *
     * @param toolUniqueName 表示工具唯一标识的 {@link String}。
     * @param mode 表示工具标签查询的方式的 {@link String}。
     * @param pageNum 表示页码的 {@code int}。
     * @param pageSize 表示限制的 {@code int}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link StoreToolData}{@code >}{@code >}。
     */
    @GetMapping("/{toolUniqueName}/versions")
    public Result<List<StoreToolData>> getAllToolVersions(@PathVariable(value = "toolUniqueName") String toolUniqueName,
            @RequestQuery(value = "mode", defaultValue = "AND", required = false) String mode,
            @RequestQuery(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestQuery(value = "pageSize", defaultValue = "10") int pageSize) {
        notBlank(toolUniqueName, "The unique name cannot be blank.");
        notNegative(pageNum, "The page number cannot be negative.");
        notNegative(pageSize, "The page size cannot be negative.");
        ToolQuery toolQuery = new ToolQuery.Builder().toolName(toolUniqueName)
                .includeTags(null)
                .excludeTags(null)
                .mode(validateTagMode(mode))
                .offset(calculateOffset(pageNum, pageSize))
                .limit(pageSize)
                .version(null)
                .build();
        ListResult<StoreToolData> res = this.storeToolService.getAllToolVersions(toolQuery);
        return Result.ok(res.getData(), res.getCount());
    }

    /**
     * 根据动态查询条件模糊获取工具列表。
     *
     * @param name 表示工具名的 {@link String}。
     * @param includeTags 表示包含标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示排除标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param mode 表示查询工具的标签与和或的方式的 {@link String}。
     * @param pageNum 表示页码的 {@code int}。
     * @param pageSize 表示限制的 {@code int}。
     * @param version 表示工具版本的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link StoreToolData}{@code >}{@code >}。
     */
    @GetMapping("/search")
    public Result<List<StoreToolData>> searchTools(@RequestQuery(value = "name", required = false) String name,
            @RequestQuery(value = "includeTags", required = false) List<String> includeTags,
            @RequestQuery(value = "excludeTags", required = false) List<String> excludeTags,
            @RequestQuery(value = "mode", defaultValue = "AND", required = false) String mode,
            @RequestQuery(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestQuery(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestQuery(value = "version", required = false) String version) {
        notNegative(pageNum, "The page number cannot be negative.");
        notNegative(pageSize, "The page size cannot be negative.");
        ToolQuery toolQuery = new ToolQuery.Builder().toolName(name)
                .includeTags(new HashSet<>(includeTags))
                .excludeTags(new HashSet<>(excludeTags))
                .mode(validateTagMode(mode))
                .offset(calculateOffset(pageNum, pageSize))
                .limit(pageSize)
                .version(version)
                .build();
        ListResult<StoreToolData> res = this.storeToolService.searchTools(toolQuery);
        return Result.ok(res.getData(), res.getCount());
    }

    /**
     * 查询是否存在定义组。
     *
     * @param defGroupNames 表示定义组名的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示存在的定义组的 {@link Result}{@code <}{@link List}{@code <}{@link DefinitionGroupData}{@code >>}。
     */
    @GetMapping(value = "/exist/defs", description = "查询是否存在定义组")
    public Result<List<DefinitionGroupData>> queryExistDefinitions(
            @RequestQuery(value = "defGroupNames", required = false) List<String> defGroupNames) {
        ListResult<DefinitionGroupData> existDefGroups = this.storeToolService.findExistDefGroups(defGroupNames);
        return Result.ok(existDefGroups.getData(), existDefGroups.getCount());
    }
}