/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.extract.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.support.AiMessage;
import modelengine.fel.core.tool.ToolCall;
import modelengine.fit.jade.aipp.extract.code.ContentExtractRetCode;
import modelengine.fit.jade.aipp.extract.domain.entity.ContentExtractor;
import modelengine.fit.jade.aipp.extract.domain.entity.support.FunctionCallContentExtractor;
import modelengine.fit.jade.aipp.extract.utils.Constant;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.flowable.Solo;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.common.exception.ModelEngineException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link ContentExtractor} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-10-28
 */
@DisplayName("测试 ContentExtractor 的实现")
public class ContentExtractorTest {
    private static ChatModel modelService;

    private static ContentExtractor extractor;

    @BeforeAll
    static void beforeAll() throws IOException {
        modelService = mock(ChatModel.class);
        extractor = new FunctionCallContentExtractor(modelService, new JacksonObjectSerializer(null, null, null, true));
    }

    @Test
    @DisplayName("测试信息提取算子")
    void shouldOkWhenRunExtractor() throws IOException {
        List<ToolCall> toolCalls = Collections.singletonList(
                ToolCall.custom().id("id").name("tool1").arguments("{\"result\":\"hello world\"}").build());
        ChatMessage msg = new AiMessage("1", toolCalls);
        Choir<ChatMessage> answer = Solo.just(msg).toChoir();
        ArgumentCaptor<ChatOption> chatOptionCaptor = ArgumentCaptor.forClass(ChatOption.class);
        when(modelService.generate(any(), chatOptionCaptor.capture())).thenReturn(answer);

        Object result = extractor.run(MapBuilder.<String, String>get()
                .put(Constant.TEXT_KEY, "text")
                .put(Constant.DESC_KEY, "desc")
                .put(Constant.HISTORY_KEY, "")
                .build(), "{}", ChatOption.custom().build());
        Map<String, String> schema = ObjectUtils.cast(result);
        assertThat(schema).hasFieldOrPropertyWithValue("result", "hello world");
        ChatOption chatOption = chatOptionCaptor.getValue();
        assertThat(chatOption.stream()).isFalse();
    }

    @Test
    @DisplayName("测试信息提取算子，大模型返回消息数量异常")
    void shouldOkWhenRunExtractorReturnInvalidChatMessages() {
        List<ToolCall> toolCalls = Collections.singletonList(
                ToolCall.custom().id("id").name("tool1").arguments("{\"result\":\"hello world\"}").build());
        ChatMessage msg = new AiMessage("1", toolCalls);
        Choir<ChatMessage> answer = Choir.empty();
        ArgumentCaptor<ChatOption> chatOptionCaptor = ArgumentCaptor.forClass(ChatOption.class);
        when(modelService.generate(any(), chatOptionCaptor.capture())).thenReturn(answer);

        assertThatThrownBy(() -> extractor.run(MapBuilder.<String, String>get()
                .put(Constant.TEXT_KEY, "text")
                .put(Constant.DESC_KEY, "desc")
                .put(Constant.HISTORY_KEY, "").build(), "{}", ChatOption.custom().build())).isInstanceOf(
                        ModelEngineException.class)
                .extracting("code", "message")
                .containsExactly(ContentExtractRetCode.MODEL_RESPONSE_ERROR.getCode(), "Model respond no message.");
    }

    @Test
    @DisplayName("测试信息提取算子，大模型返回2个Toolcall，数量异常")
    void shouldOkWhenRunExtractorReturnTwoToolcalls() {
        List<ToolCall> toolCalls = Arrays.asList(
                ToolCall.custom().id("id").name("tool1").arguments("{\"result\":\"hello world\"}").build(),
                ToolCall.custom().id("id").name("tool2").arguments("{\"result\":\"hello world\"}").build());
        ChatMessage msg = new AiMessage("1", toolCalls);
        Choir<ChatMessage> answer = Solo.just(msg).toChoir();
        ArgumentCaptor<ChatOption> chatOptionCaptor = ArgumentCaptor.forClass(ChatOption.class);
        when(modelService.generate(any(), chatOptionCaptor.capture())).thenReturn(answer);

        assertThatThrownBy(() -> extractor.run(MapBuilder.<String, String>get()
                .put(Constant.TEXT_KEY, "text")
                .put(Constant.DESC_KEY, "desc")
                .put(Constant.HISTORY_KEY, "").build(), "{}", ChatOption.custom().build())).isInstanceOf(
                        ModelEngineException.class)
                .extracting("code", "message")
                .containsExactly(ContentExtractRetCode.TOOLCALL_SIZE_ERROR.getCode(),
                        "Invalid model toolcalls size: 2, expected: 1.");
    }

    @Test
    @DisplayName("测试信息提取算子，大模型返回0个Toolcall，数量异常")
    void shouldOkWhenRunExtractorReturnNoToolcalls() {
        ChatMessage msg = new AiMessage("1");
        Choir<ChatMessage> answer = Solo.just(msg).toChoir();
        ArgumentCaptor<ChatOption> chatOptionCaptor = ArgumentCaptor.forClass(ChatOption.class);
        when(modelService.generate(any(), chatOptionCaptor.capture())).thenReturn(answer);

        assertThatThrownBy(() -> extractor.run(MapBuilder.<String, String>get()
                .put(Constant.TEXT_KEY, "text")
                .put(Constant.DESC_KEY, "desc")
                .put(Constant.HISTORY_KEY, "").build(), "{}", ChatOption.custom().build())).isInstanceOf(
                        ModelEngineException.class)
                .extracting("code", "message")
                .containsExactly(ContentExtractRetCode.TOOLCALL_SIZE_ERROR.getCode(),
                        "Invalid model toolcalls size: 0, expected: 1.");
    }

    @Test
    @DisplayName("测试信息提取算子，大模型返回Toolcall结构异常")
    void shouldOkWhenRunExtractorReturnInvalidToolcallArguments() {
        String badJson = "{\"result\":'hello world\"}";
        List<ToolCall> toolCalls =
                Collections.singletonList(ToolCall.custom().id("id").name("tool1").arguments(badJson).build());
        ChatMessage msg = new AiMessage("1", toolCalls);
        Choir<ChatMessage> answer = Solo.just(msg).toChoir();
        ArgumentCaptor<ChatOption> chatOptionCaptor = ArgumentCaptor.forClass(ChatOption.class);
        when(modelService.generate(any(), chatOptionCaptor.capture())).thenReturn(answer);

        assertThatThrownBy(() -> extractor.run(MapBuilder.<String, String>get()
                .put(Constant.TEXT_KEY, "text")
                .put(Constant.DESC_KEY, "desc")
                .put(Constant.HISTORY_KEY, "").build(), "{}", ChatOption.custom().build())).isInstanceOf(
                        ModelEngineException.class)
                .extracting("code", "message")
                .containsExactly(ContentExtractRetCode.DESERIALIZE_ERROR.getCode(),
                        StringUtils.format("Failed to deserialize json schema by Jackson. Json schema: {0}.", badJson));
    }
}