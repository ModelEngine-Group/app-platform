/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fel;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.chat.support.AiMessage;
import modelengine.fel.core.chat.support.ChatMessages;
import modelengine.fel.core.chat.support.HumanMessage;
import modelengine.fel.core.tool.ToolCall;
import modelengine.fel.core.tool.ToolInfo;
import modelengine.fel.engine.flows.AiProcessFlow;
import modelengine.fel.tool.service.ToolExecuteService;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.util.LangChain4jMcpClient;
import modelengine.fit.jober.aipp.util.McpClientFactory;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.util.MapBuilder;

import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.InjectMocks;
import modelengine.fit.jober.aipp.util.McpUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link WaterFlowAgent} 的测试。
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class WaterFlowAgentTest {
    private static final String TEXT_STEP = "textStep";
    private static final String TOOL_CALL_STEP = "toolCallStep";

    @Mock
    private ToolExecuteService toolExecuteService;
    @Mock
    private ChatModel chatModel;

    @Test
    void shouldGetResultWhenRunFlowGivenNoToolCall() {
        WaterFlowAgent waterFlowAgent =
                new WaterFlowAgent(this.toolExecuteService, this.chatModel, mock(McpClientFactory.class));

        String expectResult = "0123";
        doAnswer(invocation -> Choir.create(emitter -> {
            for (int i = 0; i < 4; i++) {
                emitter.emit(new AiMessage(String.valueOf(i)));
            }
            emitter.complete();
        })).when(chatModel).generate(any(), any());

        AiProcessFlow<Prompt, ChatMessage> flow = waterFlowAgent.buildFlow();
        ChatMessage result = flow.converse()
                .bind(ChatOption.custom().build())
                .offer(ChatMessages.from(new HumanMessage("hi"))).await();

        assertEquals(expectResult, result.text());
    }

    @Test
    void shouldGetResultWhenRunFlowGivenStoreToolCall() {
        WaterFlowAgent waterFlowAgent = new WaterFlowAgent(this.toolExecuteService, this.chatModel, mock(McpClientFactory.class));

        String expectResult = "tool result:0123";
        String realName = "realName";
        ToolInfo toolInfo = buildToolInfo(realName);
        ToolCall toolCall = ToolCall.custom().id("id").name(toolInfo.name()).arguments("{}").build();
        List<ToolCall> toolCalls = Collections.singletonList(toolCall);
        AtomicReference<String> step = new AtomicReference<>(TOOL_CALL_STEP);
        doAnswer(invocation -> {
            Prompt prompt = invocation.getArgument(0);
            Choir<Object> result = mockGenerateResult(step.get(), toolCalls, prompt);
            step.set(TEXT_STEP);
            return result;
        }).when(chatModel).generate(any(), any());
        Map<String, Object> toolContext = MapBuilder.<String, Object>get().put("key", "value").build();
        when(this.toolExecuteService.execute(realName, toolCall.arguments())).thenReturn("tool result:");

        AiProcessFlow<Prompt, ChatMessage> flow = waterFlowAgent.buildFlow();
        ChatMessage result = flow.converse()
                .bind(ChatOption.custom().build())
                .bind(AippConst.TOOL_CONTEXT_KEY, toolContext)
                .bind(AippConst.TOOLS_KEY, Collections.singletonList(toolInfo))
                .offer(ChatMessages.from(new HumanMessage("hi"))).await();

        assertEquals(expectResult, result.text());
    }

    @Test
    void shouldGetResultWhenRunFlowGivenMcpToolCall() {
        String expectResult = "tool result:0123";
        String realName = "realName";
        String url = "http://localhost/sse";
        ToolInfo toolInfo = buildMcpToolInfo(realName, url);
        ToolCall toolCall = ToolCall.custom().id("id").name(toolInfo.name()).arguments("{}").build();
        List<ToolCall> toolCalls = Collections.singletonList(toolCall);
        AtomicReference<String> step = new AtomicReference<>(TOOL_CALL_STEP);
        doAnswer(invocation -> {
            Prompt prompt = invocation.getArgument(0);
            Choir<Object> result = mockGenerateResult(step.get(), toolCalls, prompt);
            step.set(TEXT_STEP);
            return result;
        }).when(chatModel).generate(any(), any());
        Map<String, Object> toolContext = MapBuilder.<String, Object>get().put("key", "value").build();
        
        LangChain4jMcpClient mockMcpClient = mock(LangChain4jMcpClient.class);
        when(mockMcpClient.callTool(realName, "{}")).thenReturn("tool result:");
        doNothing().when(mockMcpClient).close();
        
        McpClientFactory mockFactory = mock(McpClientFactory.class);
        when(mockFactory.create(any())).thenReturn(mockMcpClient);
        
        WaterFlowAgent waterFlowAgent = new WaterFlowAgent(this.toolExecuteService, this.chatModel, mockFactory);
        
        AiProcessFlow<Prompt, ChatMessage> flow = waterFlowAgent.buildFlow();
        ChatMessage result = flow.converse()
                .bind(ChatOption.custom().build())
                .bind(AippConst.TOOL_CONTEXT_KEY, toolContext)
                .bind(AippConst.TOOLS_KEY, Collections.singletonList(toolInfo))
                .offer(ChatMessages.from(new HumanMessage("hi"))).await();

        verify(this.toolExecuteService, times(0)).execute(any(String.class), any(String.class));
        assertEquals(expectResult, result.text());
    }

    private static Choir<Object> mockGenerateResult(String step, List<ToolCall> toolCalls, Prompt prompt) {
        return Choir.create(emitter -> {
            if (TOOL_CALL_STEP.equals(step)) {
                emitter.emit(new AiMessage("tool_data", toolCalls));
                emitter.complete();
                return;
            }
            if (CollectionUtils.isNotEmpty(prompt.messages())) {
                emitter.emit(new AiMessage(prompt.messages().get(prompt.messages().size() - 1).text()));
            }
            for (int i = 0; i < 4; i++) {
                emitter.emit(new AiMessage(String.valueOf(i)));
            }
            emitter.complete();
        });
    }

    private static ToolInfo buildToolInfo(String realName) {
        return ToolInfo.custom()
                .name("tool1")
                .description("desc")
                .parameters(new HashMap<>())
                .extensions(MapBuilder.<String, Object>get().put(AippConst.TOOL_REAL_NAME, realName).build())
                .build();
    }

    private static ToolInfo buildMcpToolInfo(String realName,  String url) {
        return ToolInfo.custom()
                .name("tool1")
                .description("desc")
                .parameters(new HashMap<>())
                .extensions(MapBuilder.<String, Object>get()
                        .put(AippConst.TOOL_REAL_NAME, realName)
                        .put(AippConst.MCP_SERVER_KEY,
                                MapBuilder.get().put(AippConst.MCP_SERVER_URL_KEY, url).build())
                        .build())
                .build();
    }
}