/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fel;

import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.engine.operators.patterns.AbstractAgent;
import modelengine.fit.jade.tool.SyncToolCall;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fitframework.annotation.Bean;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;

/**
 * FelComponentConfig
 *
 * @author 易文渊
 * @since 2024-04-23
 */
@Component
public class FelComponentConfig {
    /**
     * 注入 WaterFlow 场景的 Agent。
     *
     * @param syncToolCall 表示同步工具调用服务的 {@link SyncToolCall}。
     * @param chatModel 表示模型流式服务的 {@link ChatModel}。
     * @return 返回 WaterFlow 场景的 Agent 服务的 {@link AbstractAgent}{@code <}{@link Prompt}{@code ,
     * }{@link Prompt}{@code >}。
     */
    @Bean(AippConst.WATER_FLOW_AGENT_BEAN)
    public AbstractAgent getWaterFlowAgent(@Fit SyncToolCall syncToolCall, ChatModel chatModel) {
        return new WaterFlowAgent(syncToolCall, chatModel);
    }
}