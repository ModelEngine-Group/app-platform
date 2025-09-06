/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import modelengine.fit.jober.aipp.service.AppChatService;
import modelengine.fit.jober.websocket.dto.ChatParams;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fitframework.flowable.Choir;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link ChatAppWsCommand} 的测试类。
 *
 * @author 曹嘉美
 * @since 2025-01-15
 */
public class ChatAppWsCommandTest {
    private final AppChatService appChatService = mock(AppChatService.class);

    private final OperationContext context = mock(OperationContext.class);

    private ChatAppWsCommand command;

    @BeforeEach
    public void setUp() {
        this.command = new ChatAppWsCommand(this.appChatService);
    }

    @Test
    @DisplayName("测试 appChat 接口正常运行")
    void testSuccessRunningAppChat() {
        assertThat(this.command.method()).isEqualTo("appChat");
        assertThat(this.command.paramClass()).isEqualTo(ChatParams.class);
        when(appChatService.chat(any(), any(), anyBoolean())).thenReturn(Choir.just("111"));
        CreateAppChatRequest request = CreateAppChatRequest.builder()
                .appId("123")
                .chatId("456")
                .question("789")
                .context(CreateAppChatRequest.Context.builder()
                        .useMemory(false)
                        .dimensionId("123")
                        .atChatId("456")
                        .atAppId("789")
                        .build())
                .build();
        ChatParams chatParams =
                ChatParams.builder().tenantId("123").isDebug(true).data(request).name("123").account("456").build();
        Choir<Object> result = this.command.execute(context, chatParams);
        assertThat(result.blockAll()).hasSize(1).contains("111");
    }
}
