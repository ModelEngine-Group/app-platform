/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import modelengine.fit.http.protocol.Address;
import modelengine.fit.http.protocol.support.DefaultMessageHeaders;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.support.DefaultCookieCollection;
import modelengine.fit.http.websocket.Session;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.service.AppWsCommand;
import modelengine.fit.jober.aipp.service.AppWsRegistryService;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.jade.authentication.AuthenticationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link AppStreamController} 测试类。
 *
 * @author 方誉州
 * @since 2024-09-12
 */
@ExtendWith(MockitoExtension.class)
public class AppStreamControllerTest {
    private final String chatMsg = """
            {
                "method": "appChat",
                "request_id": "1",
                "params": {
                    "tenantId": "123",
                    "isDebug": true,
                    "data": {
                        "app_id": "app_id",
                        "chat_id": "chat_id",
                        "question": "question",
                        "context": {
                            "use_memory": true,
                            "user_context": null,
                            "at_app_id": "at_app_id",
                            "at_chat_id": "at_chai_id",
                            "dimension": "test"
                        }
                    }
                }
            }""";
    private final List<String> result = new ArrayList<>();
    @Mock
    private Session session;
    @Mock
    private HttpClassicServerRequest request;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private Authenticator authenticator;
    @Mock
    private AppWsRegistryService registry;
    @Mock
    private AppWsCommand command;
    private ObjectSerializer serializer;
    private AppStreamController appStreamController;

    @BeforeEach
    public void setup() {
        this.serializer = new JacksonObjectSerializer(null, null, null, true);
        this.appStreamController =
                new AppStreamController(this.authenticator, this.serializer, this.authenticationService, this.registry);
        when(this.request.headers()).thenReturn(new DefaultMessageHeaders());
        when(this.request.cookies()).thenReturn(new DefaultCookieCollection());
        when(this.request.remoteAddress()).thenReturn(Address.builder().hostAddress("127.0.0.1").port(6666).build());
        when(this.session.getHandshakeMessage()).thenReturn(this.request);
        when(this.authenticationService.getUserName(any())).thenReturn("test_user_name");
        when(this.registry.getCommand(anyString())).thenReturn(this.command);
        this.result.clear();
        doAnswer(invocationOnMock -> this.result.add(invocationOnMock.getArgument(0))).when(this.session)
                .send(anyString());
    }

    @Test
    @DisplayName("测试路由成功")
    void testAppStreamControllerRouter() {
        when(this.command.execute(any(), any())).then(invocationOnMock -> Choir.<Object>just("test route success"));
        this.appStreamController.onMessage(this.session, this.chatMsg, "123");
        assertThat(this.result).hasSize(2)
                .contains("{\"requestId\":\"1\",\"code\":0,\"data\":\"test route success\","
                                + "\"completed\":false}",
                        "{\"requestId\":\"1\",\"code\":0,\"completed\":true}");
    }

    @Test
    @DisplayName("测试路由非注册接口失败")
    void testRouterUnregister() {
        when(this.registry.getCommand(anyString())).thenReturn(null);
        String message = """
                {
                    "method": "nonexist",
                    "request_id": "1",
                    "params": {
                        "tenantId": "123",
                        "isDebug": true,
                        "data": {
                            "app_id": "app_id",
                            "chat_id": "chat_id",
                            "question": "question",
                            "context": {
                                "use_memory": true,
                                "user_context": null,
                                "at_app_id": "at_app_id",
                                "at_chat_id": "at_chai_id",
                                "dimension": "test"
                            }
                        }
                    }
                }""";
        this.appStreamController.onMessage(this.session, message, "123");
        assertThat(this.result).hasSize(1)
                .contains("{\"requestId\":\"1\",\"code\":90000001,\"msg\":\"资源不存在: nonexist。\","
                        + "\"completed\":true}");
    }

    @Test
    @DisplayName("测试接口直接返回 FitException 异常")
    void testInterfaceThrowFitException() {
        when(this.command.execute(any(), any())).thenThrow(new AippException(AippErrCode.UNKNOWN));
        this.appStreamController.onMessage(this.session, this.chatMsg, "123");
        assertThat(this.result).hasSize(1)
                .contains("{\"requestId\":\"1\",\"code\":90000002,\"msg\":\"服务器内部错误，请联系管理员。\","
                        + "\"completed\":true}");
    }

    @Test
    @DisplayName("测试接口直接返回其他类型的异常")
    void testInterfaceThrowException() {
        when(this.command.execute(any(), any())).thenThrow(new RuntimeException("other exception"));
        this.appStreamController.onMessage(this.session, this.chatMsg, "123");
        assertThat(this.result).hasSize(1)
                .contains("{\"requestId\":\"1\",\"code\":90000002,\"msg\":\"other exception\","
                        + "\"completed\":true}");
    }

    @Test
    @DisplayName("测试响应流中出现错误")
    void testFailWhenStream() {
        when(this.command.execute(any(), any())).thenReturn(Choir.create(emitter -> {
            emitter.emit("emit success");
            emitter.fail(new IOException("emit fail"));
        }));
        this.appStreamController.onMessage(this.session, this.chatMsg, "123");
        assertThat(this.result).hasSize(2)
                .contains("{\"requestId\":\"1\",\"code\":0,\"data\":\"emit success\",\"completed\":false}",
                        "{\"requestId\":\"1\",\"code\":90000002,\"msg\":\"emit fail\","
                                + "\"completed\":true}");
    }
}
