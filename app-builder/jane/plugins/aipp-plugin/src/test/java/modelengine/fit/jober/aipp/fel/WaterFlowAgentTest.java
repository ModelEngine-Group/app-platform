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
import modelengine.fel.tool.mcp.client.McpClient;
import modelengine.fel.tool.mcp.client.McpClientFactory;
import modelengine.fit.jade.tool.SyncToolCall;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.util.MapBuilder;

import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link WaterFlowAgent} 的测试。
 */
@ExtendWith(MockitoExtension.class)
class WaterFlowAgentTest {
    private static final String TEXT_STEP = "textStep";
    private static final String TOOL_CALL_STEP = "toolCallStep";

    @Mock
    private SyncToolCall syncToolCall;
    @Mock
    private ChatModel chatModel;
    @Mock
    private McpClientFactory mcpClientFactory;

    @Test
    void shouldGetResultWhenRunFlowGivenNoToolCall() {
        WaterFlowAgent waterFlowAgent = new WaterFlowAgent(this.syncToolCall, this.chatModel, this.mcpClientFactory);

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
        WaterFlowAgent waterFlowAgent = new WaterFlowAgent(this.syncToolCall, this.chatModel, this.mcpClientFactory);

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
        when(this.syncToolCall.call(realName, toolCall.arguments(), toolContext)).thenReturn("tool result:");

        AiProcessFlow<Prompt, ChatMessage> flow = waterFlowAgent.buildFlow();
        ChatMessage result = flow.converse()
                .bind(ChatOption.custom().build())
                .bind(AippConst.TOOL_CONTEXT_KEY, toolContext)
                .bind(AippConst.TOOLS_KEY, Collections.singletonList(toolInfo))
                .offer(ChatMessages.from(new HumanMessage("hi"))).await();

        verify(this.mcpClientFactory, times(0)).create(any(), any());
        assertEquals(expectResult, result.text());
    }

    @Test
    void shouldGetResultWhenRunFlowGivenMcpToolCall() {
        WaterFlowAgent waterFlowAgent = new WaterFlowAgent(this.syncToolCall, this.chatModel, this.mcpClientFactory);

        String expectResult = "\"tool result:\"0123";
        String realName = "realName";
        String baseUrl = "http://localhost";
        String sseEndpoint = "/sse";
        ToolInfo toolInfo = buildMcpToolInfo(realName, baseUrl, sseEndpoint);
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
        McpClient mcpClient = mock(McpClient.class);
        when(this.mcpClientFactory.create(baseUrl, sseEndpoint)).thenReturn(mcpClient);
        when(mcpClient.callTool(realName, new HashMap<>())).thenReturn("tool result:");

        AiProcessFlow<Prompt, ChatMessage> flow = waterFlowAgent.buildFlow();
        ChatMessage result = flow.converse()
                .bind(ChatOption.custom().build())
                .bind(AippConst.TOOL_CONTEXT_KEY, toolContext)
                .bind(AippConst.TOOLS_KEY, Collections.singletonList(toolInfo))
                .offer(ChatMessages.from(new HumanMessage("hi"))).await();

        verify(this.syncToolCall, times(0)).call(any(), any(), any());
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

    private static ToolInfo buildMcpToolInfo(String realName,  String baseUrl, String sseEndpoint) {
        return ToolInfo.custom()
                .name("tool1")
                .description("desc")
                .parameters(new HashMap<>())
                .extensions(MapBuilder.<String, Object>get()
                        .put(AippConst.TOOL_REAL_NAME, realName)
                        .put(AippConst.MCP_SERVER_KEY,
                                MapBuilder.get().put(AippConst.MCP_SERVER_URL_KEY, baseUrl + sseEndpoint).build())
                        .build())
                .build();
    }
}