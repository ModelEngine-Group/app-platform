/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.condition.TemplateQueryCondition;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.template.TemplateAppCreateDto;
import modelengine.fit.jober.aipp.dto.template.TemplateInfoDto;
import modelengine.fit.jober.aipp.service.AppTemplateService;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;

import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.annotation.Component;

/**
 * 应用模板相关接口的 Controller 类。
 *
 * @author 方誉州
 * @since 2024-12-30
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/template")
public class AppTemplateController extends AbstractController {
    private final AppTemplateService appTemplateService;

    AppTemplateController(Authenticator authenticator, AppTemplateService appTemplateService) {
        super(authenticator);
        this.appTemplateService = appTemplateService;
    }

    /**
     * 查询符合筛选条件的应用模板的列表。
     *
     * @param request 表示查询请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户 id 的 {@link String}。
     * @param cond 表示应用模板筛选条件的 {@link TemplateQueryCondition}。
     * @return 表示查询结果的 {@link Rsp}{@code <}{@link RangedResultSet}{@code <}{@link TemplateInfoDto}{@code >>}。
     */
    @GetMapping(description = "查询应用模板")
    public Rsp<RangedResultSet<TemplateInfoDto>> query(HttpClassicServerRequest request,
            @PathVariable("tenant_id") String tenantId, @RequestBean TemplateQueryCondition cond) {
        return Rsp.ok(this.appTemplateService.query(cond, this.contextOf(request, tenantId)));
    }

    /**
     * 将应用导出为应用模板。
     *
     * @param request 表示查询请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户 id 的 {@link String}。
     * @param createDto 表示创建的应用模板的基础信息的 {@link TemplateAppCreateDto}。
     * @return 表示创建完成后的应用模板信息的 {@link Rsp}{@code <}{@link TemplateInfoDto}{@code >}。
     */
    @PostMapping(value = "/publish", description = "将应用导出为应用模板")
    @CarverSpan(value = "operation.appTemplate.publish")
    public Rsp<TemplateInfoDto> publish(HttpClassicServerRequest request, @PathVariable("tenant_id") String tenantId,
            @RequestBody @SpanAttr("name:$.name") TemplateAppCreateDto createDto) {
        return Rsp.ok(this.appTemplateService.publish(createDto, this.contextOf(request, tenantId)));
    }

    /**
     * 根据模板创建应用。
     *
     * @param request 表示查询请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户 id 的 {@link String}。
     * @param createDto 表示根据模板创建的应用的基础信息的 {@link TemplateAppCreateDto}。
     * @return 表示创建完成后的应用信息的 {@link Rsp}{@code <}{@link AppBuilderAppDto}{@code >}。
     */
    @PostMapping(value = "/create", description = "根据应用模板创建应用")
    @CarverSpan(value = "operation.appTemplate.create")
    public Rsp<AppBuilderAppDto> create(HttpClassicServerRequest request, @PathVariable("tenant_id") String tenantId,
            @RequestBody @SpanAttr("name:$.name") TemplateAppCreateDto createDto) {
        return Rsp.ok(this.appTemplateService.createAppByTemplate(createDto, this.contextOf(request, tenantId)));
    }

    /**
     * 删除指定模板 id 的应用模板。
     *
     * @param request 表示查询请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户 id 的 {@link String}。
     * @param templateId 表示待删除的应用模板 id 的 {@link String}。
     */
    @DeleteMapping(value = "/{template_id}", description = "删除指定 id 的应用模板")
    @CarverSpan(value = "operation.appTemplate.delete")
    public void delete(HttpClassicServerRequest request, @PathVariable("tenant_id") String tenantId,
            @PathVariable("template_id") @SpanAttr("template_id") String templateId) {
        this.appTemplateService.delete(templateId, this.contextOf(request, tenantId));
    }
}
