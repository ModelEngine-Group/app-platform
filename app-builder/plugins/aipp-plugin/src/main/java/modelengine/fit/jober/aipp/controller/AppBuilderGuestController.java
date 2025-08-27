/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jober.aipp.controller;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.common.exception.AippTaskNotFoundException;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import modelengine.fit.jober.aipp.genericable.AppBuilderAppService;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AppChatService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 免登录接口
 *
 * @author 陈潇文
 * @since 2025-08-27
 */
@Component
@RequestMapping(path = "/v1/api/guest")
public class AppBuilderGuestController extends AbstractController {
    private static final Logger LOGGER = Logger.get(AppBuilderGuestController.class);
    private final AppBuilderAppService appBuilderAppService;
    private final AppChatService appChatService;
    private final AippLogService aippLogService;

    /**
     * 用限校验认的证器对象 {@link Authenticator}， app 通用服务 {@link AppBuilderAppService}，对话服务 {@link AppChatService} 和实例历史记录服务
     * {@link AippLogService} 构造 {@link AppBuilderGuestController}。
     *
     * @param authenticator 表示权限校验认的证器对象的 {@link Authenticator}。
     * @param appBuilderAppService 表示 app 通用服务的 {@link AppBuilderAppService}。
     * @param appChatService 表示对话服务的 {@link AppChatService}。
     * @param aippLogService 表示实例历史记录服务的 {@link AippLogService}。
     */
    public AppBuilderGuestController(Authenticator authenticator, AppBuilderAppService appBuilderAppService,
            AppChatService appChatService, AippLogService aippLogService) {
        super(authenticator);
        this.appBuilderAppService = appBuilderAppService;
        this.appChatService = appChatService;
        this.aippLogService = aippLogService;
    }

    /**
     * 查询应用是否打开游客模式。
     *
     * @param httpRequest 表示 http 请求对象的 {@link HttpClassicServerRequest}。
     * @param path 表示待查询 app 的 Path {@link String}。
     * @return 表示是否打开游客模式的 {@link Rsp}{@code <}{@link Boolean}{@code >}。
     */
    @GetMapping(value = "/{path}/is_open", description = "查询应用是否打开游客模式")
    public Rsp<Boolean> queryAppIsOpen(HttpClassicServerRequest httpRequest, @PathVariable("path") String path) {
        AppBuilderAppDto appDto = this.appBuilderAppService.queryByPath(path);
        return Rsp.ok(ObjectUtils.cast(appDto.getAttributes().getOrDefault("allow_guest", false)));
    }

    /**
     * 查询应用详情。
     *
     * @param httpRequest 表示 http 请求对象的 {@link HttpClassicServerRequest}。
     * @param path 表示待查询 app 的 Path {@link String}。
     * @return 表示查询 app 的最新可编排版本的 DTO {@link Rsp}{@code <}{@link AppBuilderAppDto}{@code >}。
     */
    @GetMapping(value = "/{path}", description = "查询应用详情")
    public Rsp<AppBuilderAppDto> queryByPath(HttpClassicServerRequest httpRequest, @PathVariable("path") String path) {
        return Rsp.ok(this.appBuilderAppService.queryByPath(path));
    }

    /**
     * 对话接口。
     *
     * @param httpRequest 表示 http 请求对象的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户 id 的 {@link String}。
     * @param body 表示会话参数的 {@link CreateAppChatRequest}。
     * @return 表示 SSE 流的 {@link Choir}{@code <}{@link Object}{@code >}。
     * @throws AippTaskNotFoundException 表示任务不存在异常的 {@link AippTaskNotFoundException}。
     */
    @PostMapping(value = "/{tenant_id}/app_chat", description = "会话接口，传递会话信息")
    public Choir<Object> chat(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestBody CreateAppChatRequest body) throws AippTaskNotFoundException {
        this.validateChat(httpRequest, body);
        return this.appChatService.chat(body, this.contextOf(httpRequest, tenantId), false);
    }

    /**
     * 根据 chatId 查询历史记录。
     *
     * @param httpRequest 表示 http 请求对象的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户 id 的 {@link String}。
     * @param appId 表示应用 id 的 {@link String}。
     * @param chatId 表示会话 id 的 {@link String}。
     * @return 表示会话历史记录的 {@link Rsp}{@code <}{@link List}{@code <}{@link AippInstLogDataDto}{@code >}{@code >}。
     */
    @GetMapping(value = "/{tenant_id}/log/app/{app_id}/chat/{chat_id}",
            description = "指定chatId查询实例历史记录（查询最近10个实例）")
    public Rsp<List<AippInstLogDataDto>> queryChatRecentChatLog(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @PathVariable("chat_id") String chatId) {
        return Rsp.ok(this.aippLogService.queryChatRecentChatLog(chatId, appId, this.contextOf(httpRequest, tenantId)));
    }

    private void validateChatBody(CreateAppChatRequest body) {
        if (body == null || body.getContext() == null || StringUtils.isEmpty(body.getAppId())) {
            LOGGER.error("The input chat body is incorrect.");
            throw new AippParamException(AippErrCode.APP_CHAT_REQUEST_IS_NULL);
        }
    }

    private void validateChatQuestion(CreateAppChatRequest body) {
        if (StringUtils.isEmpty(body.getQuestion())) {
            LOGGER.error("The input chat body is incorrect.");
            throw new AippParamException(AippErrCode.APP_CHAT_QUESTION_IS_NULL);
        }
    }

    private void validateChat(HttpClassicServerRequest httpRequest, CreateAppChatRequest body) {
        this.validateChatBody(body);
        if (httpRequest.headers().contains("Auto-Chat-On-Upload") && !Objects.equals(httpRequest.headers()
                .require("Auto-Chat-On-Upload"), "true")) {
            this.validateChatQuestion(body);
        }
    }
}
