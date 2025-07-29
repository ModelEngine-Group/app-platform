/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;

import io.opentelemetry.api.trace.Span;
import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fitframework.annotation.Component;

import java.util.List;

/**
 * aipp实例log管理接口
 *
 * @author 刘信宏
 * @since 2024-01-08
 */
@Component
@RequestMapping(path = "/v1/api/{tenant_id}/log", group = "aipp实例log管理接口")
public class AippLogController extends AbstractController {
    private final AippLogService aippLogService;

    /**
     * AippLogController
     *
     * @param authenticator 验证器
     * @param aippLogService aippLogService
     */
    public AippLogController(Authenticator authenticator, AippLogService aippLogService) {
        super(authenticator);
        this.aippLogService = aippLogService;
    }

    /**
     * 指定appId查询实例历史记录
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param appId appId
     * @param type type
     * @return Rsp<List < AippInstLogDataDto>> 应用历史记录
     */
    @GetMapping(path = "/app/{app_id}/recent", description = "指定appId查询实例历史记录（查询最新5个实例）")
    public Rsp<List<AippInstLogDataDto>> queryRecentInstanceLog(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @RequestParam("type") String type) {
        return Rsp.ok(this.aippLogService.queryAippRecentInstLog(appId, type, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 清除appId查询实例的全部历史记录
     *
     * @param httpRequest httpRequest
     * @param tenantId tenantId
     * @param appId appId
     * @param type type
     * @return Rsp<Void>
     */
    @CarverSpan(value = "operation.aippLog.eraseHistory")
    @DeleteMapping(path = "/app/{app_id}", description = "清除appId查询实例的全部历史记录")
    public Rsp<Void> deleteInstanceLog(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("app_id") @SpanAttr("app_id") String appId, @RequestParam("type") String type) {
        this.aippLogService.deleteAippInstLog(appId, type, this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }

    /**
     * 指定instanceId条件查询实例记录
     *
     * @param instanceId instanceId
     * @param sinceTime sinceTime
     * @return Rsp<List < AippInstLog>>
     */
    @GetMapping(path = "/instance/{instance_id}", description = "指定instanceId条件查询实例记录")
    public Rsp<List<AippInstLog>> queryInstanceSince(@PathVariable("instance_id") String instanceId,
            @RequestParam(name = "after_at", required = false) String sinceTime) {
        return Rsp.ok(aippLogService.queryInstanceLogSince(instanceId, sinceTime));
    }

    /**
     * 根据appId查询应用历史记录
     *
     * @param httpRequest Http请求体
     * @param tenantId 租户id
     * @param appId 应用id
     * @param type 应用类型
     * @return Rsp<List < AippInstLogDataDto>> 应用历史记录
     * @deprecated
     */
    @Deprecated
    @GetMapping(path = "/app/{app_id}/chat/recent", description = "指定appId查询实例历史记录（查询最新1个会话）")
    public Rsp<List<AippInstLogDataDto>> queryRecentChatLog(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @RequestParam("type") String type) {
        return Rsp.ok(this.aippLogService.queryAppRecentChatLog(appId, type, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 根据chatId查询历史记录
     *
     * @param httpRequest Http请求体
     * @param tenantId 租户id
     * @param chatId 会话id
     * @param appId 应用Id
     * @return Rsp<List < AippInstLogDataDto>> 会话历史记录
     */
    @GetMapping(path = "/app/{app_id}/chat/{chat_id}", description = "指定chatId查询实例历史记录（查询最近10个实例）")
    public Rsp<List<AippInstLogDataDto>> queryChatRecentChatLog(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @PathVariable("chat_id") String chatId) {
        return Rsp.ok(this.aippLogService.queryChatRecentChatLog(chatId, appId, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 删除指定的应用对话记录
     *
     * @param logIds 需要删除的对话记录列表
     * @return 返回空回复的 {@link Rsp}{@code <}{@link Void}{@code >}。
     */
    @CarverSpan(value = "operation.aippLog.deleteHistory")
    @DeleteMapping(path = "/logs", description = "删除指定的应用对话记录")
    public Rsp<Void> deleteLogs(@RequestBody List<Long> logIds) {
        Span.current().setAttribute("logIds", logIds.toString());
        this.aippLogService.deleteLogs(logIds);
        return Rsp.ok();
    }

    /**
     * 删除指定的应用对话记录（针对不能使用delete方式的接口）
     *
     * @param logIds 需要删除的对话记录列表
     * @return 返回空回复的 {@link Rsp}{@code <}{@link Void}{@code >}。
     */
    @PostMapping(path = "/logs_delete", description = "删除指定的应用对话记录")
    @CarverSpan(value = "operation.aippLog.deleteHistory")
    public Rsp<Void> deleteLogsV2(@RequestBody List<Long> logIds) {
        return this.deleteLogs(logIds);
    }
}
