/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.PutMapping;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.common.PageResponse;
import modelengine.fit.jober.aipp.condition.AippQueryCondition;
import modelengine.fit.jober.aipp.condition.PaginationCondition;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.dto.AippCreateDto;
import modelengine.fit.jober.aipp.dto.AippDetailDto;
import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fit.jober.aipp.dto.AippOverviewRspDto;
import modelengine.fit.jober.aipp.dto.AippVersionDto;
import modelengine.fit.jober.aipp.service.AippFlowService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.validation.Validated;
import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;

import java.util.List;

/**
 * Aipp编排管理接口
 *
 * @author 刘信宏
 * @since 2023-12-08
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/aipp-info", group = "aipp编排管理接口")
public class AippFlowController extends AbstractController {
    private final AippFlowService aippFlowService;
    private final AppVersionService appVersionService;

    /**
     * AippFlowController 的构造方法
     *
     * @param authenticator authenticator
     * @param aippFlowService aippFlowService
     */
    public AippFlowController(Authenticator authenticator, @Fit AippFlowService aippFlowService,
            AppVersionService appVersionService) {
        super(authenticator);
        this.aippFlowService = aippFlowService;
        this.appVersionService = appVersionService;
    }

    /**
     * 查询aipp列表
     *
     * @param tenantId 租户id
     * @param cond 过滤条件
     * @param page 分页
     * @param httpRequest 操作上下文
     * @return aipp 概况
     */
    @GetMapping(description = "批量查询aipp")
    public Rsp<PageResponse<AippOverviewRspDto>> listAipp(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBean AippQueryCondition cond,
            @RequestBean @Validated PaginationCondition page) {
        return Rsp.ok(aippFlowService.listAipp(cond, page, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 查询aipp详情
     *
     * @param aippId aippId
     * @param tenantId tenantId
     * @param version 版本
     * @param httpRequest http操作上下文
     * @return aipp 详情
     */
    @GetMapping(value = "/{aipp_id}", description = "查询aipp详情")
    public Rsp<AippDetailDto> queryAipp(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("aipp_id") String aippId,
            @RequestParam("version") String version) {
        return this.aippFlowService.queryAippDetail(aippId, version, this.contextOf(httpRequest, tenantId));
    }

    /**
     * 创建aipp
     *
     * @param tenantId 租户id
     * @param aippDto aipp定义
     * @param httpRequest 操作上下文
     * @return aipp id信息
     */
    @CarverSpan(value = "operation.flow.create")
    @PostMapping(description = "创建aipp")
    public Rsp<AippCreateDto> createAipp(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody @Validated AippDto aippDto) {
        return Rsp.ok(aippFlowService.create(aippDto, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 删除aipp
     *
     * @param aippId aippId
     * @param tenantId tenantId
     * @param baselineVersion 基线版本
     * @param httpRequest 操作上下文
     * @return aipp id信息
     */
    @CarverSpan(value = "operation.flow.delete")
    @DeleteMapping(path = "/{aipp_id}", description = "删除aipp")
    public Rsp<Integer> deleteAipp(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("aipp_id") @SpanAttr("aipp_id") String aippId,
            @RequestParam("version") @SpanAttr("version") String baselineVersion) {
        aippFlowService.deleteAipp(aippId, baselineVersion, this.contextOf(httpRequest, tenantId));
        return Rsp.ok(1);
    }

    /**
     * 更新aipp
     *
     * @param tenantId 租户id
     * @param aippId aippId
     * @param aippDto aipp定义
     * @param httpRequest 操作上下文
     * @return aipp id信息
     */
    @CarverSpan(value = "operation.flow.update")
    @PutMapping(path = "/{aipp_id}", description = "更新aipp")
    public Rsp<AippCreateDto> updateAipp(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("aipp_id") @SpanAttr("aipp_id") String aippId,
            @RequestBody @SpanAttr("version:$.version") AippDto aippDto) {
        aippDto.setId(aippId);
        return Rsp.ok(this.aippFlowService.update(aippDto, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 预览aipp
     *
     * @param tenantId 租户id
     * @param aippId aippId
     * @param baselineVersion aipp 的基线版本
     * @param aippDto aipp定义
     * @param httpRequest 操作上下文
     * @return 创建预览aipp的id和version
     */
    @CarverSpan(value = "operation.flow.preview")
    @PostMapping(path = "/{aipp_id}/preview", description = "预览aipp")
    public Rsp<AippCreateDto> previewAipp(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId,
            @PathVariable("aipp_id") @SpanAttr("aipp_id") String aippId,
            @RequestParam("version") @SpanAttr("version") String baselineVersion, @RequestBody AippDto aippDto) {
        aippDto.setId(aippId);
        return Rsp.ok(this.appVersionService.retrieval(aippDto.getAppId())
                .preview(baselineVersion, aippDto, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 退出预览
     *
     * @param tenantId 租户id
     * @param previewAippId aippId
     * @param previewVersion aipp preview version
     * @param httpRequest 操作上下文
     * @return Void
     */
    @CarverSpan(value = "operation.flow.quitPreview")
    @DeleteMapping(path = "/{aipp_id}/preview", description = "退出预览")
    public Rsp<Void> cleanPreviewAipp(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("aipp_id") @SpanAttr("aipp_id") String previewAippId,
            @RequestParam("preview_version") @SpanAttr("version") String previewVersion) {
        this.aippFlowService.cleanPreviewAipp(previewAippId, previewVersion, contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }

    /**
     * 升级 aipp
     *
     * @param tenantId 租户id
     * @param aippId aippId
     * @param baselineVersion aipp baseline version
     * @param aippDto 升级aipp定义
     * @param httpRequest 操作上下文
     * @return Void
     */
    @CarverSpan(value = "operation.flow.upgrade")
    @PostMapping(path = "/{aipp_id}/upgrade", description = "升级aipp")
    public Rsp<AippCreateDto> upgradeAipp(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId,
            @PathVariable("aipp_id") @SpanAttr("aipp_id") String aippId,
            @RequestParam("version") @SpanAttr("version") String baselineVersion, @RequestBody AippDto aippDto) {
        aippDto.setId(aippId);
        return Rsp.ok(this.aippFlowService.upgrade(baselineVersion, aippDto, contextOf(httpRequest, tenantId)));
    }

    /**
     * 查询aipp历史版本列表
     *
     * @param tenantId 租户id
     * @param aippId aippId
     * @param httpRequest 操作上下文
     * @return aipp历史版本列表
     */
    @GetMapping(value = "/{aipp_id}/version", description = "查询aipp历史版本列表")
    public Rsp<List<AippVersionDto>> listAippVersion(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("aipp_id") String aippId) {
        return Rsp.ok(this.aippFlowService.listAippVersions(aippId, contextOf(httpRequest, tenantId)));
    }
}
