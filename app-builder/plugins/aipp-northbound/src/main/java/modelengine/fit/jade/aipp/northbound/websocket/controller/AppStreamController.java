/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.northbound.websocket.controller;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.websocket.Session;
import modelengine.fit.http.websocket.annotation.OnClose;
import modelengine.fit.http.websocket.annotation.OnError;
import modelengine.fit.http.websocket.annotation.OnMessage;
import modelengine.fit.http.websocket.annotation.OnOpen;
import modelengine.fit.http.websocket.annotation.TextMessage;
import modelengine.fit.http.websocket.annotation.WebSocketEndpoint;
import modelengine.fit.jade.aipp.northbound.websocket.dto.AippWebsocketRsp;
import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import modelengine.fit.jober.aipp.service.AppChatService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.authentication.AuthenticationService;
import modelengine.jade.authentication.context.HttpRequestUtils;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * app-websocket 会话流式接口。
 *
 * @author 曹嘉美
 * @since 2024-07-23
 */
@WebSocketEndpoint(path = "/api/app/v1/chat")
@Component
public class AppStreamController extends AbstractController {
    private static final String METHOD = "appChat";
    private static final String REQUEST_ID = "requestId";
    private static final String PARAMS = "params";
    private static final String TENANT_ID = "tenantId";
    private static final String DATA = "data";
    private static final String NAME = "name";
    private static final String ACCOUNT = "account";
    private static final Logger log = Logger.get(AppStreamController.class);

    private final AppChatService appChatService;
    private final ObjectSerializer serializer;
    private final AuthenticationService authenticationService;

    private final Map<String, BiFunction<HttpClassicServerRequest, Map<String, Object>, Choir<Object>>> router;

    AppStreamController(Authenticator authenticator, AppChatService appChatService,
            @Fit(alias = "json") ObjectSerializer serializer, AuthenticationService authenticationService) {
        super(authenticator);
        this.appChatService = notNull(appChatService, "The appChatService cannot be null.");
        this.router = this.register();
        this.serializer = notNull(serializer, "The serializer cannot be null.");
        this.authenticationService = notNull(authenticationService, "The authenticationService cannot be null.");
    }

    /**
     * 当一个新的 WebSocket 会话打开时的处理方法。
     *
     * @param session 表示 WebSocket 会话的 {@link Session}。
     */
    @OnOpen
    public void onOpen(Session session) {
        log.info("WebSocket connection open by client. sessionId: {}", session.getId());
    }

    /**
     * 当收到 WebSocket 二进制消息时的处理方法。
     *
     * @param session 表示 WebSocket 会话的 {@link Session}。
     * @param message 表示收到的 WebSocket 二进制消息的 {@link String}。
     */
    @OnMessage
    public void onMessage(Session session, @TextMessage String message) {
        log.info("WebSocket session start. sessionId: {}", session.getId());
        HttpClassicServerRequest request = ObjectUtils.cast(session.getHandshakeMessage());
        UserContext operationContext = new UserContext(this.authenticationService.getUserName(request),
                HttpRequestUtils.getUserIp(request),
                HttpRequestUtils.getAcceptLanguages(request));
        UserContextHolder.apply(operationContext, () -> {
            String requestIdLog = StringUtils.EMPTY;
            try {
                Map<String, Object> messageObj = this.serializer.deserialize(message, Map.class);
                String requestId = ObjectUtils.cast(messageObj.get(REQUEST_ID));
                requestIdLog = requestId;
                Map<String, Object> params = ObjectUtils.cast(messageObj.get(PARAMS));
                notNull(this.router.get(METHOD), () -> new AippException(AippErrCode.NOT_FOUND, METHOD));
                log.info("dispatch method: {}", METHOD);
                Choir<Object> result = this.router.get(METHOD).apply(request, params);
                result.subscribe(null, (subscription, data) -> {
                    session.send(this.createUnCompleteRsp(requestId, data));
                }, (subscription) -> {
                    session.send(this.createCompletedRsp(requestId));
                }, (subscription, exception) -> {
                    session.send(this.createFailedRsp(requestId, exception));
                });
                log.info("end dispatch method.");
            } catch (AippException e) {
                log.error("apply method error.", e);
                session.send(this.createFailedRsp(requestIdLog, e));
            }
        });
    }

    /**
     * 当 WebSocket 会话关闭时的处理方法。
     *
     * @param session 表示 WebSocket 会话的 {@link Session}。
     */
    @OnClose
    public void onClose(Session session) {
        log.info("WebSocket connection closed by client. [code={}, reason={}, sessionId={}]",
                session.getCloseCode(),
                session.getCloseReason(),
                session.getId());
    }

    /**
     * 当 WebSocket 会话关闭时的处理方法。
     *
     * @param session 表示 WebSocket 会话的 {@link Session}。
     * @param e 表示发生的错误 {@link Throwable}。
     */
    @OnError
    public void onError(Session session, Throwable e) {
        log.error("Websocket meet error: {} ", e);
        session.send(createFailedRsp("error", ObjectUtils.cast(e)));
    }

    private Map<String, BiFunction<HttpClassicServerRequest, Map<String, Object>, Choir<Object>>> register() {
        return MapBuilder.<String, BiFunction<HttpClassicServerRequest, Map<String, Object>, Choir<Object>>>get()
                .put("appChat", this::appChat)
                .build();
    }

    private Choir<Object> appChat(HttpClassicServerRequest request, Map<String, Object> params) {
        String tenantId = ObjectUtils.cast(params.get(TENANT_ID));
        CreateAppChatRequest requestBody = ObjectUtils.toCustomObject(params.get(DATA), CreateAppChatRequest.class);
        validateChatBody(requestBody);
        OperationContext context = this.contextOf(request, tenantId);
        this.setUserInOperationContext(context, params);
        return this.appChatService.chat(requestBody, context, false);
    }

    private void validateChatBody(CreateAppChatRequest body) {
        notNull(body, () -> new AippParamException(AippErrCode.APP_CHAT_REQUEST_IS_NULL));
        notNull(body.getContext(), () -> new AippParamException(AippErrCode.APP_CHAT_REQUEST_IS_NULL));
        notBlank(body.getAppId(), () -> new AippParamException(AippErrCode.APP_CHAT_REQUEST_IS_NULL));
    }

    private String createUnCompleteRsp(String requestId, Object data) {
        return this.serializer.serialize(createRsp(requestId, AippErrCode.OK.getErrorCode(), null, data, false));
    }

    private String createCompletedRsp(String requestId) {
        return this.serializer.serialize(createRsp(requestId, AippErrCode.OK.getErrorCode(), null, null, true));
    }

    private String createFailedRsp(String requestId, Exception exception) {
        return serializer.serialize(createRsp(requestId,
                exception instanceof FitException
                        ? ObjectUtils.<FitException>cast(exception).getCode()
                        : AippErrCode.UNKNOWN.getErrorCode(),
                exception instanceof FitException
                        ? ObjectUtils.<FitException>cast(exception).getMessage()
                        : exception.getMessage(),
                null,
                true));
    }

    private AippWebsocketRsp createRsp(String requestId, Integer code, String msg, Object data, boolean isCompleted) {
        return AippWebsocketRsp.builder()
                .requestId(requestId)
                .code(code)
                .msg(msg)
                .data(data)
                .isCompleted(isCompleted)
                .build();
    }

    private void setUserInOperationContext(OperationContext context, Map<String, Object> params) {
        String name = ObjectUtils.cast(params.getOrDefault(NAME, StringUtils.EMPTY));
        String account = ObjectUtils.cast(params.getOrDefault(ACCOUNT, StringUtils.EMPTY));
        context.setName(name);
        context.setAccount(account);
        if (!account.isEmpty() && !Character.isDigit(account.charAt(0))) {
            context.setOperator(name + ' ' + account.substring(1));
        } else {
            context.setOperator(name + ' ' + account);
        }
    }
}
