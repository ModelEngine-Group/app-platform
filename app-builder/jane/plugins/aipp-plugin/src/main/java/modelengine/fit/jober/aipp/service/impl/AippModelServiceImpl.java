/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.support.ChatMessages;
import modelengine.fel.core.chat.support.HumanMessage;
import modelengine.fel.core.model.http.SecureConfig;
import modelengine.fel.core.template.support.DefaultStringTemplate;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.utils.ContentProcessUtils;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.dto.model.PromptGenerateDto;
import modelengine.fit.jober.aipp.repository.AippSystemConfigRepository;
import modelengine.fit.jober.aipp.service.AippModelService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.exception.ClientException;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;

/**
 * ${@link AippModelService} 大模型服务接口实现类.
 *
 * @author 张越
 * @since 2024-11-30
 */
@Component
public class AippModelServiceImpl implements AippModelService {
    private static final Logger log = Logger.get(AippModelServiceImpl.class);

    private static final String INPUT = "input";
    private static final String TEMPLATE_GROUP = "template";
    private static final String TEMPLATE_ATTRIBUTE = "template";

    private final ChatModel modelService;
    private final AippModelCenter aippModelCenter;
    private final AippSystemConfigRepository aippSystemConfigRepository;

    public AippModelServiceImpl(ChatModel modelService, AippModelCenter aippModelCenter,
            AippSystemConfigRepository aippSystemConfigRepository) {
        this.modelService = modelService;
        this.aippModelCenter = aippModelCenter;
        this.aippSystemConfigRepository = aippSystemConfigRepository;
    }

    @Override
    public String chat(String model, String tag, Double temperature, String prompt) {
        ModelAccessInfo modelAccessInfo = this.aippModelCenter.getModelAccessInfo(tag, model, null);
        ChatOption chatOption = ChatOption.custom()
                .model(model)
                .baseUrl(modelAccessInfo.getBaseUrl())
                .secureConfig(modelAccessInfo.isSystemModel() ? null : SecureConfig.custom().ignoreTrust(true).build())
                .apiKey(modelAccessInfo.getAccessKey())
                .temperature(temperature)
                .stream(false)
                .build();
        ChatMessages chatMessages = new ChatMessages();
        chatMessages.add(new HumanMessage(prompt));
        Choir<ChatMessage> message = this.modelService.generate(chatMessages, chatOption);
        ChatMessage answer = message.blockAll().get(0);
        return answer.text();
    }

    @Override
    public String generatePrompt(PromptGenerateDto param, OperationContext context) {
        Map<String, String> values = MapBuilder.<String, String>get().put(INPUT, param.getInput()).build();
        String template = this.aippSystemConfigRepository.find(TEMPLATE_GROUP, param.getTemplateType())
                .map(c -> c.getValueAttributeString(TEMPLATE_ATTRIBUTE))
                .orElseThrow(() -> new IllegalStateException(
                        StringUtils.format("Template not exist.type: {0}", param.getTemplateType())));
        String prompt = new DefaultStringTemplate(template).render(values);
        ModelAccessInfo modelAccessInfo = this.aippModelCenter.getDefaultModel(AippConst.CHAT_MODEL_TYPE, context);
        try {
            return ContentProcessUtils.filterReasoningContent(this.chat(modelAccessInfo.getServiceName(),
                    modelAccessInfo.getTag(),
                    0.3,
                    prompt));
        } catch (ClientException e) {
            // 模型生成内容超时的情况下，提醒用户更换默认模型
            log.error("Failed to generate prompt.", e);
            throw new AippException(AippErrCode.GENERATE_CONTENT_FAILED, "prompt", e.getMessage());
        }
    }
}
