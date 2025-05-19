/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.chat.support.HumanMessage;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.jober.aipp.enums.LlmModelNameEnum;
import modelengine.fit.jober.aipp.service.impl.LLMServiceImpl;
import modelengine.fitframework.flowable.Choir;
import modelengine.jade.voice.service.VoiceService;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;

/**
 * LlmService测试类
 *
 * @since 2024-07-31
 */
public class LlmServiceTest {
    private static final ChatModel OPENAI_CLIENT_MOCK = mock(ChatModel.class);
    private static final VoiceService VOICE_SERVICE_MOCK = mock(VoiceService.class);
    private static final HttpClassicClientFactory FACTORY_MOCK =
            mock(HttpClassicClientFactory.class, RETURNS_DEEP_STUBS);

    @Test
    void testAskWithText() {
        LLMService llmServiceMock = new LLMServiceImpl(OPENAI_CLIENT_MOCK);
        Choir<ChatMessage> responseMock = mock(Choir.class, RETURNS_DEEP_STUBS);
        try {
            when(OPENAI_CLIENT_MOCK.generate(any(Prompt.class), any(ChatOption.class))).thenReturn(responseMock);
            when(responseMock.blockAll()).thenReturn(Collections.singletonList(new HumanMessage("测试回复")));
            String res = llmServiceMock.askModelWithText("测试1", LlmModelNameEnum.QWEN_72B);
            assertEquals("测试回复", res);
            res = llmServiceMock.askModelWithText("测试2", 16000, 0.2, LlmModelNameEnum.QWEN_72B);
            assertEquals("测试回复", res);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
