/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.http.HttpResource;
import modelengine.fit.http.protocol.Address;
import modelengine.fit.http.protocol.MessageHeaders;
import modelengine.fit.http.protocol.ReadableMessageBody;
import modelengine.fit.http.protocol.RequestLine;
import modelengine.fit.http.protocol.ServerRequest;
import modelengine.fit.http.server.support.DefaultHttpClassicServerRequest;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRequest;
import modelengine.fit.jober.aipp.service.AippChatService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 为 {@link AippChatController} 提供测试
 *
 * @author 罗帅
 * @since 2024/9/11
 */
@ExtendWith(MockitoExtension.class)
class AippChatControllerTest {
    private final HttpResource httpResource = mock(HttpResource.class);
    private final ServerRequest serverRequest = mock(ServerRequest.class);
    private final AippChatService aippChatService = mock(AippChatService.class);
    private final Authenticator authenticator = mock(Authenticator.class);

    private DefaultHttpClassicServerRequest httpClassicServerRequest;
    private AippChatController aippChatController;

    @BeforeEach
    void before() {
        RequestLine startLine = mock(RequestLine.class);
        MessageHeaders headers = mock(MessageHeaders.class);
        when(this.serverRequest.startLine()).thenReturn(startLine);
        when(this.serverRequest.headers()).thenReturn(headers);
        ReadableMessageBody body = mock(ReadableMessageBody.class);
        when(this.serverRequest.body()).thenReturn(body);
        when(startLine.requestUri()).thenReturn("?");
        Address remoteAddress = mock(Address.class);
        when(this.serverRequest.remoteAddress()).thenReturn(remoteAddress);
        when(this.serverRequest.remoteAddress().hostAddress()).thenReturn("127.0.0.1");
        this.httpClassicServerRequest = new DefaultHttpClassicServerRequest(this.httpResource, this.serverRequest);
        this.aippChatController = new AippChatController(this.authenticator, this.aippChatService);
    }

    @Test
    @DisplayName("测试查对话历史记录")
    public void testQueryChatList() {
        QueryChatRequest body = new QueryChatRequest();
        when(this.authenticator.authenticate(Mockito.any())).thenReturn(null);
        aippChatController.queryChatList(this.httpClassicServerRequest, "tenantld", body);
        verify(aippChatService, times(1)).queryChatList(Mockito.any(), Mockito.any());
    }
}