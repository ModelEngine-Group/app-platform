/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.chat.support.ChatMessages;
import modelengine.fel.core.chat.support.FlatChatMessage;
import modelengine.fel.core.chat.support.ToolMessage;
import modelengine.fel.core.tool.ToolCall;
import modelengine.fel.core.tool.ToolInfo;
import modelengine.fel.engine.flows.AiFlows;
import modelengine.fel.engine.flows.AiProcessFlow;
import modelengine.fel.engine.operators.models.ChatChunk;
import modelengine.fel.engine.operators.models.ChatFlowModel;
import modelengine.fel.engine.operators.patterns.AbstractAgent;
import modelengine.fel.tool.mcp.client.McpClient;
import modelengine.fel.tool.mcp.client.McpClientFactory;
import modelengine.fit.jade.tool.SyncToolCall;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.util.McpUtils;
import modelengine.fit.waterflow.domain.context.StateContext;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * WaterFlow 场景的 {@link AbstractAgent} 实现。
 *
 * @author 刘信宏
 * @since 2024-06-04
 */
public class WaterFlowAgent extends AbstractAgent {
    private static final String AGENT_MSG_KEY = "water_flow_agent_request";
    private static final String GOTO_NODE_ID = "ahead_llm_node";

    private final String agentMsgKey;
    private final SyncToolCall syncToolCall;
    private final McpClientFactory mcpClientFactory;

    /**
     * {@link WaterFlowAgent} 的构造方法。
     *
     * @param syncToolCall 表示工具调用服务的 {@link SyncToolCall}。
     * @param chatStreamModel 表示流式对话大模型的 {@link ChatModel}。
     * @param mcpClientFactory 表示大模型上下文客户端工厂的 {@link McpClientFactory}。
     */
    public WaterFlowAgent(@Fit SyncToolCall syncToolCall, ChatModel chatStreamModel,
            McpClientFactory mcpClientFactory) {
        super(new ChatFlowModel(chatStreamModel, null));
        this.syncToolCall = Validation.notNull(syncToolCall, "The tool sync tool call cannot be null.");
        this.mcpClientFactory = Validation.notNull(mcpClientFactory, "The mcp client factory cannot be null.");
        this.agentMsgKey = AGENT_MSG_KEY;
    }

    @Override
    protected Prompt doToolCall(List<ToolCall> toolCalls, StateContext ctx) {
        Validation.notNull(ctx, "The state context cannot be null.");
        return ChatMessages.from(this.callTools(toolCalls, ctx)
                .stream()
                .map(message -> (ChatMessage) FlatChatMessage.from(message))
                .collect(Collectors.toList()));
    }

    @Override
    public AiProcessFlow<Prompt, ChatMessage> buildFlow() {
        return AiFlows.<Prompt>create()
                .just((input, ctx) -> ctx.setState(this.agentMsgKey, ChatMessages.from(input.messages())))
                .id(GOTO_NODE_ID)
                .generate(this.getModel())
                .reduce(ChatChunk::new, (acc, chunk, context) -> {
                    acc.merge(chunk);
                    return acc;
                })
                .map(input -> (ChatMessage) input)
                .conditions()
                .matchTo(ChatMessage::isToolCall,
                        node -> node.map(this::handleTool).map(this::getAgentMsg).to(GOTO_NODE_ID))
                .others(node -> node)
                .close();
    }

    private ChatMessage handleTool(ChatMessage input, StateContext ctx) {
        Validation.notNull(ctx, "The state context cannot be null.");
        Validation.notNull(input, "The input message cannot be null.");
        ChatMessages lastRequest = ctx.getState(this.agentMsgKey);
        lastRequest.add(input);
        lastRequest.addAll(this.callTools(input.toolCalls(), ctx));
        return input;
    }

    private List<ChatMessage> callTools(List<ToolCall> toolCalls, StateContext ctx) {
        if (CollectionUtils.isEmpty(toolCalls)) {
            return Collections.emptyList();
        }
        List<ToolInfo> tools = ctx.getState(AippConst.TOOLS_KEY);
        Validation.notEmpty(tools, "Missing tool detected during call.");
        Map<String, ToolInfo> toolsMap = tools.stream().collect(Collectors.toMap(ToolInfo::name, Function.identity()));
        Map<String, Object> toolContext =
                ObjectUtils.getIfNull(ctx.getState(AippConst.TOOL_CONTEXT_KEY), Collections::emptyMap);
        return toolCalls.stream()
                .map(toolCall -> this.callTool(toolCall, toolsMap, toolContext))
                .collect(Collectors.toList());
    }

    private ChatMessage callTool(ToolCall toolCall, Map<String, ToolInfo> toolsMap, Map<String, Object> toolContext) {
        ToolInfo toolInfo = toolsMap.get(toolCall.name());
        if (toolInfo == null) {
            throw new IllegalStateException(String.format("The tool call's tool is not exist. [toolName=%s]",
                    toolCall.name()));
        }
        Map<String, Object> extensions = Validation.notNull(toolInfo.extensions(),
                "The tool call's extension is not exist. [toolName={0}]", toolCall.name());
        String toolRealName = Validation.notBlank(ObjectUtils.cast(extensions.get(AippConst.TOOL_REAL_NAME)),
                "Can not find the tool real name. [toolName={0}]",
                toolCall.name());
        Map<String, Object> mcpServerConfig = ObjectUtils.cast(extensions.get(AippConst.MCP_SERVER_KEY));
        if (mcpServerConfig != null) {
            String url = Validation.notBlank(ObjectUtils.cast(mcpServerConfig.get(AippConst.MCP_SERVER_URL_KEY)),
                    "The mcp url should not be empty.");
            try (McpClient mcpClient = this.mcpClientFactory.create(McpUtils.getBaseUrl(url),
                    McpUtils.getSseEndpoint(url))) {
                mcpClient.initialize();
                Object result = mcpClient.callTool(toolRealName, JSONObject.parseObject(toolCall.arguments()));
                return new ToolMessage(toolCall.id(), JSON.toJSONString(result));
            } catch (IOException exception) {
                throw new AippException(AippErrCode.CALL_MCP_SERVER_FAILED, exception.getMessage());
            }
        }
        return new ToolMessage(toolCall.id(), this.syncToolCall.call(toolRealName, toolCall.arguments(), toolContext));
    }

    private ChatMessages getAgentMsg(ChatMessage input, StateContext ctx) {
        Validation.notNull(ctx, "The state context cannot be null.");
        return ctx.getState(this.agentMsgKey);
    }
}
