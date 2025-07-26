/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jade.aipp.northbound.controller;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.dto.aipplog.AippInstLogData;
import modelengine.fit.jober.aipp.genericable.adapter.AippLogServiceAdapter;
import modelengine.fitframework.annotation.Component;

import java.util.List;

/**
 * aipp 实例日志管理接口。
 *
 * @author 陈潇文
 * @since 2025-07-08
 */
@Component
@RequestMapping(path = "/api/app/v1/tenants/{tenant_id}/log", group = "aipp 实例日志管理北向接口")
public class AippLogController extends AbstractController {
    private final AippLogServiceAdapter aippLogServiceAdapter;

    /**
     * 用身份校验器 {@link Authenticator} 和 aipp 实例历史记录适配器类 {@link AippLogServiceAdapter} 构造 {@link AippLogController}。
     *
     * @param authenticator 表示身份校验器的 {@link Authenticator}。
     * @param aippLogServiceAdapter 表示 aipp 实例历史记录适配器类的 {@link AippLogServiceAdapter}。
     */
    public AippLogController(Authenticator authenticator, AippLogServiceAdapter aippLogServiceAdapter) {
        super(authenticator);
        this.aippLogServiceAdapter = aippLogServiceAdapter;
    }

    /**
     * 根据 chatId 查询历史记录。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户 id 的 {@link String}。
     * @param chatId 表示会话 id 的 {@link String}。
     * @param appId 表示应用 id 的 {@link String}。
     * @return 表示会话历史记录的 {@link Rsp}{@code <}{@link List}{@code <}{@link AippInstLogData}{@code >>}。
     */
    @GetMapping(path = "/app/{app_id}/chat/{chat_id}", summary = "查询会话历史记录",
            description = "指定 chatId 查询实例历史记录（查询最近 10 个实例）。")
    public Rsp<List<AippInstLogData>> queryChatRecentChatLog(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @PathVariable("chat_id") String chatId) {
        return Rsp.ok(this.aippLogServiceAdapter.queryChatRecentChatLog(chatId,
                appId,
                this.contextOf(httpRequest, tenantId)));
    }
}
