/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.dto.AppBuilderFormDto;
import modelengine.fit.jober.aipp.service.AppBuilderFormService;
import modelengine.fit.jober.common.RangedResultSet;
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
 * app表单管理接口
 *
 * @author 邬涨财
 * @since 2024-04-19
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/form")
public class AppBuilderFormController extends AbstractController {
    private final AppBuilderFormService formService;

    /**
     * 构造函数，初始化表单服务.
     *
     * @param authenticator 表示认证器的 {@link Authenticator}。
     * @param formService 表示表单服务对象的 {@link AppBuilderFormService}。
     */
    public AppBuilderFormController(Authenticator authenticator, AppBuilderFormService formService) {
        super(authenticator);
        this.formService = formService;
    }

    /**
     * 根据 type 查询表单。
     *
     * @param httpRequest 请求对象。
     * @param type 表单类型。
     * @param tenantId 租户 ID。
     * @return 返回查询结果。
     */
    @GetMapping(value = "/type/{type}", description = "查询指定 type 的表单")
    public Rsp<List<AppBuilderFormDto>> queryByType(HttpClassicServerRequest httpRequest,
            @PathVariable("type") String type, @PathVariable("tenant_id") String tenantId) {
        return this.formService.queryByType(httpRequest, type, tenantId);
    }

    /**
     * 创建智能表单
     *
     * @param httpRequest 表示http请求对象的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户id的 {@link String}。
     * @param appBuilderFormDto 表示表单创建结构体的 {@link AppBuilderFormDto}。
     * @return 表单结构体
     */
    @PostMapping(value = "/smart_form", description = "创建智能表单")
    @CarverSpan(value = "operation.create.smart.form")
    public Rsp<AppBuilderFormDto> create(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody AppBuilderFormDto appBuilderFormDto) {
        return Rsp.ok(this.formService.create(appBuilderFormDto, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 更新智能表单
     *
     * @param httpRequest 表示http请求对象的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户id的 {@link String}。
     * @param appBuilderFormDto 表示表单创建结构体的 {@link AppBuilderFormDto}。
     * @param formId 表示表单id的 {@link String}。
     * @return 表单结构体
     */
    @PutMapping(value = "/smart_form/{form_id}", description = "更新智能表单")
    @CarverSpan(value = "operation.update.smart.form")
    public Rsp<AppBuilderFormDto> update(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody AppBuilderFormDto appBuilderFormDto,
            @PathVariable("form_id") @SpanAttr("form_id") String formId) {
        return Rsp.ok(this.formService.update(appBuilderFormDto, formId, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 分页查询智能表单
     *
     * @param httpRequest 表示http请求对象的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户id的 {@link String}。
     * @param pageNum 表示分页页数的 {@link Long}。
     * @param pageSize 表示分页大小的 {@link Integer}。
     * @param name 表示模糊查询表单名称的 {@link String}。
     * @return 表示查询结果的的 {@link RangedResultSet}{@code <}{@link AppBuilderFormDto}{@code >}。
     */
    @GetMapping(value = "/smart_form/", description = "查询智能表单")
    public Rsp<RangedResultSet<AppBuilderFormDto>> query(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId,
            @RequestParam(value = "pageNum", defaultValue = "0") long pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "name", required = false) String name) {
        return Rsp.ok(this.formService.query(pageNum, pageSize, name, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 删除智能表单
     *
     * @param httpRequest 表示http请求对象的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户id的 {@link String}。
     * @param formId 表示表单id的 {@link String}。
     * @return void。
     */
    @DeleteMapping(value = "/smart_form/{form_id}", description = "删除智能表单")
    @CarverSpan(value = "operation.delete.smart.form")
    public Rsp<Void> delete(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("form_id") @SpanAttr("form_id") String formId) {
        return Rsp.ok(this.formService.delete(formId, this.contextOf(httpRequest, tenantId)));
    }
}
