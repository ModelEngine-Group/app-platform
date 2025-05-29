/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.http.protocol.Address;
import modelengine.fit.http.protocol.support.DefaultMessageHeaders;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.support.DefaultCookieCollection;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import modelengine.fit.jober.aipp.service.AppChatService;

import modelengine.fit.http.protocol.Address;
import modelengine.fit.http.protocol.support.DefaultMessageHeaders;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.support.DefaultCookieCollection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

/**
 * 为 {@link AppChatController} 提供测试
 *
 * @author 姚江
 * @since 2024-08-07
 */
@ExtendWith(MockitoExtension.class)
public class AppChatControllerTest {
    private AppChatController controller;

    @Mock
    private Authenticator authenticator;
    @Mock
    private AppChatService appChatService;
    @Mock
    private HttpClassicServerRequest request;

    @BeforeEach
    void before() {
        this.controller = new AppChatController(authenticator, appChatService);
    }

    @Test
    @DisplayName("测试app_chat接口")
    void testChat() {
        CreateAppChatRequest body = CreateAppChatRequest.builder()
                .appId("appid")
                .context(CreateAppChatRequest.Context.builder().build())
                .question("q")
                .build();
        Mockito.when(request.headers()).thenReturn(new DefaultMessageHeaders());
        Mockito.when(request.cookies()).thenReturn(new DefaultCookieCollection());
        Mockito.when(request.remoteAddress()).thenReturn(Address.builder().hostAddress("127.0.0.1").port(6666).build());
        Assertions.assertDoesNotThrow(() -> this.controller.chat(request, "123", body));
    }

    @Test
    @DisplayName("测试app_chat接口")
    void testChatFailedByNoQuestion() {
        CreateAppChatRequest body = CreateAppChatRequest.builder()
                .appId("appid")
                .context(CreateAppChatRequest.Context.builder().build())
                .build();
        DefaultMessageHeaders headers = new DefaultMessageHeaders();
        headers.add("Auto-Chat-On-Upload", "false");
        Mockito.when(request.headers()).thenReturn(headers);
        AippParamException exception = Assertions.assertThrows(AippParamException.class,
                () -> this.controller.chat(request, "123", body));
        Assertions.assertEquals(AippErrCode.APP_CHAT_QUESTION_IS_NULL.getErrorCode(), exception.getCode());
    }

    @Test
    @DisplayName("测试app_chat接口")
    void testChatFailedByNoBody() {
        AippParamException exception =
                Assertions.assertThrows(AippParamException.class, () -> this.controller.chat(request, "123", null));
        Assertions.assertEquals(AippErrCode.APP_CHAT_REQUEST_IS_NULL.getErrorCode(), exception.getCode());
    }

    @Test
    @DisplayName("测试app_chat_debug接口")
    void testChatDebug() {
        CreateAppChatRequest body = CreateAppChatRequest.builder()
                .appId("appid")
                .context(CreateAppChatRequest.Context.builder().build())
                .question("q")
                .build();
        Mockito.when(request.headers()).thenReturn(new DefaultMessageHeaders());
        Mockito.when(request.cookies()).thenReturn(new DefaultCookieCollection());
        Mockito.when(request.remoteAddress()).thenReturn(Address.builder().hostAddress("127.0.0.1").port(6666).build());
        Assertions.assertDoesNotThrow(() -> this.controller.chatDebug(request, "123", body));
    }

    @Test
    @DisplayName("测试water_flow_chat_debug接口")
    void testWaterFlowChatDebug() {
        CreateAppChatRequest body = CreateAppChatRequest.builder()
                .appId("appid")
                .context(CreateAppChatRequest.Context.builder().build())
                .build();
        Mockito.when(request.headers()).thenReturn(new DefaultMessageHeaders());
        Mockito.when(request.cookies()).thenReturn(new DefaultCookieCollection());
        Mockito.when(request.remoteAddress()).thenReturn(Address.builder().hostAddress("127.0.0.1").port(6666).build());
        Assertions.assertDoesNotThrow(() -> this.controller.waterFlowChatDebug(request, "123", body));
    }

    @Test
    @DisplayName("测试restartChat接口")
    void testRestartChat() {
        Mockito.when(request.headers()).thenReturn(new DefaultMessageHeaders());
        Mockito.when(request.cookies()).thenReturn(new DefaultCookieCollection());
        Mockito.when(request.remoteAddress()).thenReturn(Address.builder().hostAddress("127.0.0.1").port(6666).build());
        Assertions.assertDoesNotThrow(() -> this.controller.restartChat(request, "123", "123", new HashMap<>()));
    }
}
