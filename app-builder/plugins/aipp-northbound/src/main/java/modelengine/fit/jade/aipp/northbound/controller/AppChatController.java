/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.northbound.controller;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.jane.task.gateway.Authenticator;

import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;
import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.ResponseStatus;
import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.dto.chat.ChatDeleteParams;
import modelengine.fit.jober.aipp.dto.chat.ChatInfo;
import modelengine.fit.jober.aipp.dto.chat.ChatQueryParams;
import modelengine.fit.jober.aipp.dto.chat.ChatRequest;
import modelengine.fit.jober.aipp.genericable.adapter.AippChatServiceAdapter;
import modelengine.fit.jober.aipp.genericable.adapter.AppChatServiceAdapter;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * 应用会话管理北向接口。
 *
 * @author 曹嘉美
 * @since 2024-12-12
 */
@Component
@RequestMapping(path = "/api/app/v1/tenants/{tenantId}/chats", group = "应用对话管理接口")
public class AppChatController extends AbstractController {
    private final AppChatServiceAdapter appChatService;
    private final AippChatServiceAdapter aippChatService;
    private final String defaultType = "active";

    /**
     * 构造方法。
     *
     * @param authenticator 表示身份校验器的 {@link Authenticator}。
     * @param appChatService 表示应用对话服务的 {@link AppChatServiceAdapter}。
     * @param aippChatService 表示 AI 聊天服务的 {@link AippChatServiceAdapter}。
     */
    public AppChatController(Authenticator authenticator, AppChatServiceAdapter appChatService,
            AippChatServiceAdapter aippChatService) {
        super(authenticator);
        this.appChatService = notNull(appChatService, "The appChatService cannot be null.");
        this.aippChatService = notNull(aippChatService, "The aippChatService cannot be null.");
    }

    /**
     * 会话接口。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户的唯一标识符的 {@link String}。
     * @param appId 表示应用的唯一标识符的 {@link String}。
     * @param params 表示会话信息的 {@link ChatRequest}。
     * @return 表示会话的 sse 流的 {@link Choir}。
     */
    @CarverSpan(value = "operation.appChat.app.chat")
    @PostMapping(path = "/apps/{appId}", summary = "发送对话消息",
            description = "该接口向大模型发送一个问题信息，并开启一个对话。支持 SSE 和 Websocket 两种流式调用方式。")
    public Choir<Object> chat(HttpClassicServerRequest httpRequest,
            @PathVariable("tenantId") @Property(description = "租户的唯一标识符") String tenantId,
            @PathVariable("appId") @Property(description = "应用的唯一标识符") String appId,
            @RequestBody @Property(description = "会话信息，包含创建会话所需的数据") ChatRequest params) {
        this.validateChatParams(appId, params);
        notBlank(params.getQuestion(), () -> new AippParamException(AippErrCode.APP_CHAT_QUESTION_IS_NULL));
        return this.appChatService.chat(appId, params, this.contextOf(httpRequest, tenantId), false);
    }

    /**
     * 重新发起指定会话。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param currentInstanceId 表示需要重新发起会话的实例的唯一标识符的 {@link String}。
     * @param tenantId 表示租户的唯一标识符的 {@link String}。
     * @param additionalContext 表示重新会话所需的附加信息的 {@link Map}。
     * @return 表示会话的 sse 流的 {@link Choir}。
     */
    @CarverSpan(value = "operation.appChat.restartChat")
    @PostMapping(path = "/instances/{currentInstanceId}", summary = "重新对话",
            description = "该接口可以重新发起指定会话，需要指定需要重新发起会话的实例id，同时可添加附加信息")
    public Choir<Object> restartChat(HttpClassicServerRequest httpRequest,
            @PathVariable("tenantId") @Property(description = "租户的唯一标识符") String tenantId,
            @PathVariable("currentInstanceId") @Property(description = "需要重新发起会话的实例的唯一标识符")
            @SpanAttr("currentInstanceId") String currentInstanceId,
            @RequestBody @Property(description = "重新会话所需的附加信息，如是否使用多轮对话等")
            Map<String, Object> additionalContext) {
        return this.appChatService.restartChat(currentInstanceId,
                additionalContext,
                this.contextOf(httpRequest, tenantId));
    }

    /**
     * 查询会话历史。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户的唯一标识符的 {@link String}。
     * @param chatQueryParams 表示查询会话历史的请求参数的 {@link ChatQueryParams}。
     * @return 表示查询会话历史的响应体的
     * {@link Rsp}{@code <}{@link RangedResultSet}{@code <}{@link ChatInfo}{@code >}{@code >}。
     */
    @CarverSpan(value = "operation.aippChat.queryList")
    @GetMapping(summary = "查询会话历史", description = "该接口用于查询指定租户的会话历史，并通过指定条件进行筛选。")
    public Rsp<RangedResultSet<ChatInfo>> queryChatList(HttpClassicServerRequest httpRequest,
            @PathVariable("tenantId") @Property(description = "租户的唯一标识符") String tenantId,
            @RequestBean ChatQueryParams chatQueryParams) {
        if (StringUtils.isEmpty(chatQueryParams.getAppState())) {
            chatQueryParams.setAppState(this.defaultType);
        }
        return Rsp.ok(this.aippChatService.queryChatList(chatQueryParams, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 删除指定的对话。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户的唯一标识符的 {@link String}。
     * @param chatDeleteParams 表示删除对话的参数的 {@link ChatDeleteParams}。
     * @return 表示删除对话的响应体的 {@link Rsp}{@code <}{@link Void}{@code >}。
     */
    @CarverSpan(value = "operation.aippChat.delete")
    @DeleteMapping(summary = "删除对话API", description = "该接口用于删除一个指定应用下一个或多个对话。")
    @ResponseStatus(HttpResponseStatus.NO_CONTENT)
    public Rsp<Void> deleteChat(HttpClassicServerRequest httpRequest,
            @PathVariable("tenantId") @Property(description = "租户的唯一标识符") String tenantId,
            @RequestBean ChatDeleteParams chatDeleteParams) {
        this.aippChatService.deleteChat(chatDeleteParams.getChatId(),
                chatDeleteParams.getAppId(),
                this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }

    private void validateChatParams(String appId, ChatRequest params) {
        notNull(params,
                () -> new AippParamException(AippErrCode.APP_CHAT_REQUEST_IS_NULL, "The chat params cannot be null."));
        notNull(params.getContext(),
                () -> new AippParamException(AippErrCode.APP_CHAT_REQUEST_IS_NULL, "The chat context cannot be null."));
        notBlank(appId,
                () -> new AippParamException(AippErrCode.APP_CHAT_REQUEST_IS_NULL, "The app id cannot be blank."));
    }
}
