/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.controller;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.service.AppWsCommand;
import modelengine.fit.jober.aipp.service.AppWsRegistryService;
import modelengine.fit.jober.websocket.dto.AippWebsocketRsp;
import modelengine.fit.jober.websocket.dto.AppWsParams;
import modelengine.jade.authentication.AuthenticationService;
import modelengine.jade.authentication.context.HttpRequestUtils;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;

import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.websocket.Session;
import modelengine.fit.http.websocket.annotation.OnClose;
import modelengine.fit.http.websocket.annotation.OnError;
import modelengine.fit.http.websocket.annotation.OnMessage;
import modelengine.fit.http.websocket.annotation.OnOpen;
import modelengine.fit.http.websocket.annotation.TextMessage;
import modelengine.fit.http.websocket.annotation.WebSocketEndpoint;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.jade.authentication.AuthenticationService;
import modelengine.jade.authentication.context.HttpRequestUtils;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;

import java.util.Map;

/**
 * app-websocket 流式接口。
 *
 * @author 姚江
 * @since 2024-07-23
 */
@WebSocketEndpoint(path = "/v1/api/{tenant_id}/ws")
@Component
public class AppStreamController extends AbstractController {
    private static final Logger log = Logger.get(AppStreamController.class);

    private final ObjectSerializer serializer;
    private final AuthenticationService authenticationService;
    private final AppWsRegistryService registry;

    AppStreamController(Authenticator authenticator, @Fit(alias = "json") ObjectSerializer serializer,
            AuthenticationService authenticationService, AppWsRegistryService registry) {
        super(authenticator);
        this.serializer = serializer;
        this.authenticationService = authenticationService;
        this.registry = registry;
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
     * @param tenantId 表示租户唯一标识符的 {@link String}。
     */
    @OnMessage
    public void onMessage(Session session, @TextMessage String message, @PathVariable("tenant_id") String tenantId) {
        log.info("WebSocket session start. sessionId: {}", session.getId());
        HttpClassicServerRequest request = cast(session.getHandshakeMessage());
        UserContext operationContext = new UserContext(this.authenticationService.getUserName(request),
                HttpRequestUtils.getUserIp(request),
                HttpRequestUtils.getAcceptLanguages(request));
        UserContextHolder.apply(operationContext, () -> {
            String requestIdLog = "";
            try {
                AppWsParams wsParams = this.serializer.deserialize(message, AppWsParams.class);
                String method = wsParams.getMethod();
                String requestId = wsParams.getRequestId();
                requestIdLog = requestId;
                Map<String, Object> params = cast(wsParams.getParams());
                params.put("tenant_id", tenantId);
                log.info("Dispatch method: {}", method);
                OperationContext context = this.contextOf(request, tenantId);
                AppWsCommand command = this.registry.getCommand(method);
                notNull(command, () -> new AippException(AippErrCode.NOT_FOUND, method));
                Choir<Object> result =
                        command.execute(context, this.castParam(params, this.registry.getParamClass(method)));
                result.subscribe(null, (subscription, data) -> {
                    session.send(this.createUnCompleteRsp(requestId, data));
                }, (subscription) -> {
                    session.send(this.createCompletedRsp(requestId));
                }, (subscription, exception) -> {
                    session.send(this.createFailedRsp(requestId, exception));
                });
                log.info("End dispatch method.");
            } catch (Exception e) {
                log.error("Apply method error.", e);
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
     * 当 WebSocket 会话异常时的处理方法。
     *
     * @param session 表示 WebSocket 会话的 {@link Session}。
     * @param e 表示 WebSocket 会话异常的 {@link Throwable}。
     */
    @OnError
    public void onError(Session session, Throwable e) {
        log.error("Websocket meet error.", e);
        session.send(createFailedRsp("error", cast(e)));
    }

    private <T> T castParam(Object param, Class<T> type) {
        return this.serializer.deserialize(this.serializer.serialize(param), type);
    }

    private String createUnCompleteRsp(String requestId, Object data) {
        return this.serializer.serialize(createRsp(requestId, AippErrCode.OK.getErrorCode(), null, data, false));
    }

    private String createCompletedRsp(String requestId) {
        return this.serializer.serialize(createRsp(requestId, AippErrCode.OK.getErrorCode(), null, null, true));
    }

    private String createFailedRsp(String requestId, Exception exception) {
        return this.serializer.serialize(createRsp(requestId,
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
}
