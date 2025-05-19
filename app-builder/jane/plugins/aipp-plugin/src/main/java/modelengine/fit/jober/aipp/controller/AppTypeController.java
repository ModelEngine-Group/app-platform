/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.dto.AppTypeDto;
import modelengine.fit.jober.aipp.service.AppTypeService;
import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;

import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.PutMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.annotation.Component;

import java.util.List;

/**
 * 应用业务分类对外接口。
 *
 * @author songyongtan
 * @since 2025/1/5
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/app/type")
public class AppTypeController extends AbstractController {
    private final AppTypeService appTypeService;

    /**
     * 构造函数。
     *
     * @param authenticator 表示认证处理对象的 {@link Authenticator}。
     * @param appTypeService 表示应用业务分类服务的 {@link AppTypeService}。
     */
    public AppTypeController(Authenticator authenticator, AppTypeService appTypeService) {
        super(authenticator);
        this.appTypeService = appTypeService;
    }

    /**
     * 查询所有应用业务分类。
     *
     * @param request 表示查询请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @return 表示列表信息的 {@link Rsp}{@code <}{@link List}{@code <}{@link AppTypeDto}{@code >>}。
     */
    @GetMapping(description = "查询所有应用业务分类")
    public Rsp<List<AppTypeDto>> queryAll(HttpClassicServerRequest request,
        @PathVariable("tenant_id") String tenantId) {
        return Rsp.ok(this.appTypeService.queryAll(tenantId));
    }

    /**
     * 根据业务分类查询应用业务分类。
     *
     * @param request 表示查询请求。
     * @param tenantId 表示租户 id。
     * @param id 应用业务分类的 id。
     * @return 应用业务分类。
     */
    @GetMapping(value = "/{id}", description = "查询一条应用业务分类")
    public Rsp<AppTypeDto> query(HttpClassicServerRequest request, @PathVariable("tenant_id") String tenantId,
        @PathVariable("id") String id) {
        return Rsp.ok(this.appTypeService.query(id, tenantId));
    }

    /**
     * 创建应用业务分类。
     *
     * @param request 表示查询请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param createDto 表示要创建的应用业务分类信息的 {@link AppTypeDto}。
     * @return 表示创建完成后的信息的 {@link AppTypeDto}。
     */
    @PostMapping(description = "创建一条应用业务分类")
    @CarverSpan(value = "operation.appType.create")
    public Rsp<AppTypeDto> create(HttpClassicServerRequest request, @PathVariable("tenant_id") String tenantId,
        @RequestBody @SpanAttr("name:$.name") AppTypeDto createDto) {
        return Rsp.ok(this.appTypeService.add(createDto, tenantId));
    }

    /**
     * 更新应用业务分类。
     *
     * @param request 表示查询请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param id 表示应用分类唯一标识的 {@link String}。
     * @param dto 表示要创建的应用业务分类信息的 {@link AppTypeDto}。
     * @return 表示空结果的 {@link Rsp}{@code <}{@link Void}{@code >}。
     */
    @PutMapping(value = "/{id}", description = "更新一条应用业务分类")
    @CarverSpan(value = "operation.appType.update")
    public Rsp<Void> update(HttpClassicServerRequest request, @PathVariable("tenant_id") String tenantId,
        @PathVariable("id") @SpanAttr("id") String id, @RequestBody @SpanAttr("name:$.name") AppTypeDto dto) {
        dto.setId(id);
        this.appTypeService.update(dto, tenantId);
        return Rsp.ok();
    }

    /**
     * 根据应用业务分类唯一标识删除应用业务分类。
     *
     * @param request 表示查询请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param id 表示应用业务分类唯一标识的 {@link String}。
     * @return 表示空结果的 {@link Rsp}{@code <}{@link Void}{@code >}。
     */
    @DeleteMapping(value = "/{id}", description = "根据id删除")
    @CarverSpan(value = "operation.appType.delete")
    public Rsp<Void> delete(HttpClassicServerRequest request, @PathVariable("tenant_id") String tenantId,
        @PathVariable("id") @SpanAttr("id") String id) {
        this.appTypeService.delete(id, tenantId);
        return Rsp.ok();
    }
}
