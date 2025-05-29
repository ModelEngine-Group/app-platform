/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.dto.AppBuilderPromptCategoryDto;
import modelengine.fit.jober.aipp.dto.AppBuilderPromptDto;
import modelengine.fit.jober.aipp.service.AppBuilderPromptService;
import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;

import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.PutMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.annotation.Component;

import java.util.List;

/**
 * 为灵感大全提供查询接口
 *
 * @author 姚江
 * @since 2024-04-25
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/app/{app_id}/prompt")
public class AppBuilderPromptController extends AbstractController {
    private final AppBuilderPromptService service;

    /**
     * 表示构造函数，初始化服务对象。
     *
     * @param authenticator 表示权限校验认的证器对象的 {@link Authenticator}。
     * @param service 表示处理业务逻辑的服务对象的 {@link AppBuilderPromptService}。
     */
    public AppBuilderPromptController(Authenticator authenticator, AppBuilderPromptService service) {
        super(authenticator);
        this.service = service;
    }

    /**
     * 查询所有的灵感类别。
     *
     * @param httpRequest 表示http请求对象的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户id的 {@link String}。
     * @param appId 表示应用id的 {@link String}。
     * @param isDebug 表示是否是调试状态{@link Boolean}。
     * @return 返回所有的灵感类别。
     */
    @GetMapping
    public Rsp<List<AppBuilderPromptCategoryDto>> listCategories(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @RequestParam(value = "isDebug", defaultValue = "true", required = false) boolean isDebug) {
        return service.listPromptCategories(appId, this.contextOf(httpRequest, tenantId), isDebug);
    }

    /**
     * 查询指定类别的所有灵感。
     *
     * @param httpRequest 表示http请求对象的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户id的 {@link String}。
     * @param appId 表示应用id的 {@link String}。
     * @param categoryId 表示类别id的 {@link String}。
     * @param isDebug 表示是否是调试状态{@link Boolean}。
     * @return 返回指定类别的所有灵感 {@link Rsp}{@code <}{@link AppBuilderPromptDto}{@code >}。
     */
    @GetMapping("/{category_id}")
    public Rsp<AppBuilderPromptDto> queryInspirations(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @PathVariable("category_id") String categoryId,
            @RequestParam(value = "isDebug", defaultValue = "true", required = false) boolean isDebug) {
        return service.queryInspirations(appId, categoryId, this.contextOf(httpRequest, tenantId), isDebug);
    }

    /**
     * 添加我的灵感。
     *
     * @param httpRequest 表示http请求对象的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户id的 {@link String}。
     * @param parentId 表示父类别id的 {@link String}。
     * @param appId 表示应用id的 {@link String}。
     * @param inspirationDto 添加的灵感内容的 {@link Rsp}{@code <}{@link AppBuilderPromptDto.AppBuilderInspirationDto}{@code >}。
     * @return {@link Rsp}{@code <}{@link Void}{@code >}
     */
    @CarverSpan(value = "operation.inspiration.addMy")
    @PostMapping("/{parent_id}")
    public Rsp<Void> addMyInspiration(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("parent_id") String parentId, @SpanAttr("app_id") @PathVariable("app_id") String appId,
            @RequestBody AppBuilderPromptDto.AppBuilderInspirationDto inspirationDto) {
        this.service.addCustomInspiration(appId, parentId, inspirationDto, this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }

    /**
     * 更新我的灵感。
     *
     * @param httpRequest 表示http请求对象的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户id的 {@link String}。
     * @param categoryId 表示“我的”类别id的 {@link String}。
     * @param appId 表示应用id的 {@link String}。
     * @param inspirationId 表示要修改的灵感id的 {@link String}。
     * @param inspirationDto 添加的灵感内容的 {@link Rsp}{@code <}{@link AppBuilderPromptDto.AppBuilderInspirationDto}{@code >}。
     * @return {@link Rsp}{@code <}{@link Void}{@code >}
     */
    @CarverSpan(value = "operation.inspiration.updateMy")
    @PutMapping("/{category_id}/inspiration/{inspiration_id}")
    public Rsp<Void> updateMyInspiration(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("category_id") String categoryId,
            @SpanAttr("app_id") @PathVariable("app_id") String appId,
            @SpanAttr("inspiration_id") @PathVariable("inspiration_id") String inspirationId,
            @RequestBody AppBuilderPromptDto.AppBuilderInspirationDto inspirationDto) {
        this.service.updateCustomInspiration(appId,
                categoryId,
                inspirationId,
                inspirationDto,
                this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }

    /**
     * 删除我的灵感。
     *
     * @param httpRequest 表示http请求对象的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户id的 {@link String}。
     * @param categoryId 表示“我的”类别id的 {@link String}。
     * @param appId 表示应用id的 {@link String}。
     * @param inspirationId 表示要删除的灵感id的 {@link String}。
     * @return {@link Rsp}{@code <}{@link Void}{@code >}
     */
    @CarverSpan(value = "operation.inspiration.delete")
    @DeleteMapping("/{category_id}/inspiration/{inspiration_id}")
    public Rsp<Void> deleteMyInspiration(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("category_id") String categoryId,
            @SpanAttr("app_id") @PathVariable("app_id") String appId,
            @SpanAttr("inspiration_id") @PathVariable("inspiration_id") String inspirationId) {
        this.service.deleteCustomInspiration(appId, categoryId, inspirationId, this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }
}
