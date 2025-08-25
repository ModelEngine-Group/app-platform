/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.controller;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNegative;
import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.jade.carver.validation.ValidateTagMode.validateTagMode;
import static modelengine.jade.common.Result.calculateOffset;

import modelengine.fel.tool.ToolSchema;
import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fit.jade.aipp.domain.division.annotation.GetSource;
import modelengine.fitframework.annotation.Component;
import modelengine.jade.carver.ListResult;
import modelengine.jade.common.Result;
import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;
import modelengine.jade.store.entity.query.AppQuery;
import modelengine.jade.store.entity.transfer.AppData;
import modelengine.jade.store.entity.transfer.AppPublishData;
import modelengine.jade.store.service.AppService;

import java.util.HashSet;
import java.util.List;

/**
 * 处理应用 HTTP 请求的控制器。
 *
 * @author 鲁为
 * @since 2024-06-20
 */
@Component
@RequestMapping("/store/apps")
public class AppController {
    private final AppService appService;

    /**
     * 通过应用服务来初始化 {@link AppController} 的新实例。
     *
     * @param appService 表示应用服务的 {@link AppService}。
     */
    public AppController(AppService appService) {
        this.appService = notNull(appService, "The app service cannot be null.");
    }

    /**
     * 添加应用。
     *
     * @param appData 表示 Http 请求的参数的 {@link AppData}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @CarverSpan(value = "operation.app.upload")
    @PostMapping
    public Result<String> addApp(@RequestBody @SpanAttr("name:$.name,version:$.version") AppPublishData appData) {
        notNull(appData.getSchema(), "The app schema cannot be null.");
        String name = cast(appData.getSchema().get(ToolSchema.NAME));
        notBlank(name, "The app name cannot be blank.");
        return Result.ok(this.appService.publishApp(appData), 1);
    }

    /**
     * 基于应用的唯一标识查询某个应用。
     *
     * @param uniqueName 表示应用的唯一索引的 {@link String}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link AppData}{@code >}。
     */
    @GetMapping("/{uniqueName}")
    public Result<AppPublishData> getAppByUniqueName(@PathVariable("uniqueName") String uniqueName) {
        notBlank(uniqueName, "The app unique name cannot be blank.");
        return Result.ok(this.appService.getApp(uniqueName), 1);
    }

    /**
     * 根据动态查询条件模糊获取应用列表。
     *
     * @param name 表示应用名的 {@link String}。
     * @param includeTags 表示包含标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param excludeTags 表示排除标签的 {@link List}{@code <}{@link String}{@code >}。
     * @param mode 表示查询工具选择标签与和或的方式的 {@link String}。
     * @param pageNum 表示页码的 {@code int}。
     * @param pageSize 表示限制的 {@code int}。
     * @param version 表示工具的版本的 {@link String}。
     * @param appCategory 表示应用类型的 {@link String}。
     * @return 表示格式化之后的返回消息的 {@link Result}{@code <}{@link List}{@code <}{@link AppPublishData}{@code >}{@code >}。
     */
    @GetMapping("/search")
    @GetSource
    public Result<List<AppPublishData>> searchApps(@RequestQuery(value = "name", required = false) String name,
            @RequestQuery(value = "includeTags", required = false) List<String> includeTags,
            @RequestQuery(value = "excludeTags", required = false) List<String> excludeTags,
            @RequestQuery(value = "mode", defaultValue = "AND", required = false) String mode,
            @RequestQuery(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestQuery(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestQuery(value = "version", required = false) String version,
            @RequestQuery(value = "appCategory", required = false) String appCategory) {
        notNegative(pageNum, "The page number cannot be negative.");
        notNegative(pageSize, "The page size cannot be negative.");
        AppQuery appQuery = new AppQuery.Builder().toolName(name)
                .includeTags(new HashSet<>(includeTags))
                .excludeTags(new HashSet<>(excludeTags))
                .mode(validateTagMode(mode))
                .offset(calculateOffset(pageNum, pageSize))
                .limit(pageSize)
                .version(version)
                .appCategory(appCategory)
                .build();
        ListResult<AppPublishData> res = this.appService.getApps(appQuery);
        return Result.ok(res.getData(), res.getCount());
    }

    /**
     * 删除应用。
     *
     * @param uniqueName 表示应用的唯一索引的 {@link String}。
     * @return 格式化之后的返回消息的 {@link Result}{@code <}{@link String}{@code >}。
     */
    @CarverSpan(value = "operation.app.delete")
    @DeleteMapping("/{uniqueName}")
    public Result<String> deleteApp(@PathVariable("uniqueName") @SpanAttr("uniqueName") String uniqueName) {
        notBlank(uniqueName, "The unique name cannot be blank.");
        return Result.ok(this.appService.deleteApp(uniqueName), 1);
    }
}