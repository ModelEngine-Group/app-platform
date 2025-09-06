/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.model.http.SecureConfig;
import modelengine.fel.core.util.Tip;
import modelengine.fel.engine.flows.AiFlows;
import modelengine.fel.engine.flows.AiProcessFlow;
import modelengine.fel.engine.operators.models.ChatBlockModel;
import modelengine.fel.engine.operators.prompts.Prompts;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.utils.ContentProcessUtils;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.serialization.SerializationException;
import modelengine.jade.app.engine.base.dto.AppBuilderRecommendDto;
import modelengine.jade.app.engine.base.service.AppBuilderRecommendService;

import java.util.ArrayList;
import java.util.List;

/**
 * 猜你想问serviceImpl
 *
 * @author 杨海波
 * @since 2024-05-25
 */
@Component
public class AppBuilderRecommendServiceImpl implements AppBuilderRecommendService {
    private static final Logger log = Logger.get(AppBuilderRecommendServiceImpl.class);

    private static final String DEFAULT_MODEL_SOURCE = "internal";

    private final ChatModel chatModelService;

    private final AippModelCenter aippModelCenter;

    public AppBuilderRecommendServiceImpl(ChatModel chatModelService, AippModelCenter aippModelCenter) {
        this.chatModelService = chatModelService;
        this.aippModelCenter = aippModelCenter;
    }

    @Override
    public List<String> queryRecommends(AppBuilderRecommendDto recommendDto, OperationContext context) {
        ModelAccessInfo defaultModel = this.aippModelCenter.getDefaultModel(AippConst.CHAT_MODEL_TYPE, context);
        ModelAccessInfo modelAccessInfo =
                this.aippModelCenter.getModelAccessInfo(defaultModel.getTag(), defaultModel.getServiceName(), context);
        String model = defaultModel.getServiceName();
        String historyPrompt = "Here are the chat histories between user and assistant, "
                + "inside <history></history> XML tags.\n<history>\n{{history}}\n</history>\n\n";

        String recommendPrompt = "Please predict the three most likely questions that human would ask, "
                + "and keeping each question under 20 characters.\n"
                + "Do not include any explanations, "
                + "only provide output that strictly following the specified JSON format:\n"
                + "[\"question1\",\"question2\",\"question3\"]\n";

        List<String> res;
        try {
            AiProcessFlow<Tip, String> flow = AiFlows.<Tip>create()
                .prompt(Prompts.human(historyPrompt + recommendPrompt))
                .generate(new ChatBlockModel(chatModelService)
                        .bind(ChatOption.custom()
                                .model(model)
                                .stream(false)
                                .temperature(0.3)
                                .baseUrl(modelAccessInfo.getBaseUrl())
                                .secureConfig(modelAccessInfo.isSystemModel()
                                        ? null
                                        : SecureConfig.custom().ignoreTrust(true).build())
                                .apiKey(modelAccessInfo.getAccessKey())
                                .build()))
                .map(ChatMessage::text)
                .close();

            String chatHistory = "User: " + recommendDto.getQuestion() + '\n' + "Assistant: " + recommendDto.getAnswer()
                + '\n';
            String response = flow.converse().offer(Tip.from("history", chatHistory)).await();

            res = JSONArray.parseArray(ContentProcessUtils.filterReasoningContent(response), String.class);
        } catch (SerializationException | JSONException | IllegalStateException e) {
            log.error("{}\nparse model {} response error", e.getMessage(), model);
            return new ArrayList<>();
        }

        return res;
    }
}
