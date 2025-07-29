/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fel;

import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.engine.operators.patterns.AbstractAgent;
import modelengine.fel.tool.mcp.client.McpClientFactory;
import modelengine.fel.tool.service.ToolExecuteService;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fitframework.annotation.Bean;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;

/**
 * {@link AbstractAgent} 的实例提供器。
 *
 * @author 易文渊
 * @since 2024-04-23
 */
@Component
public class FelComponentConfig {
    /**
     * 注入 WaterFlow 场景的 Agent。
     *
     * @param toolExecuteService 表示工具调用服务的 {@link ToolExecuteService}。
     * @param chatModel 表示模型流式服务的 {@link ChatModel}。
     * @param mcpClientFactory 表示大模型上下文客户端工厂的 {@link McpClientFactory}。
     * @return 返回 WaterFlow 场景的 Agent 服务的 {@link AbstractAgent}{@code <}{@link Prompt}{@code ,
     * }{@link Prompt}{@code >}。
     */
    @Bean(AippConst.WATER_FLOW_AGENT_BEAN)
    public AbstractAgent getWaterFlowAgent(@Fit ToolExecuteService toolExecuteService, ChatModel chatModel, McpClientFactory mcpClientFactory) {
        return new WaterFlowAgent(toolExecuteService, chatModel, mcpClientFactory);
    }
}