/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.classify.question.command.impl;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.support.ChatMessages;
import modelengine.fel.core.chat.support.HumanMessage;
import modelengine.fel.core.memory.Memory;
import modelengine.fel.core.model.http.SecureConfig;
import modelengine.fel.core.template.support.DefaultStringTemplate;
import modelengine.fit.jade.aipp.classify.question.QuestionType;
import modelengine.fit.jade.aipp.classify.question.command.ClassifyQuestionCommand;
import modelengine.fit.jade.aipp.classify.question.command.ClassifyQuestionCommandHandler;
import modelengine.fit.jade.aipp.classify.question.util.Constant;
import modelengine.fit.jade.aipp.memory.AippMemoryFactory;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jober.aipp.common.utils.ContentProcessUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表示 {@link ClassifyQuestionCommand} 的默认实现。
 *
 * @author 张越
 * @since 2024-11-18
 */
@Component
public class ClassifyQuestionCommandHandlerImpl implements ClassifyQuestionCommandHandler {
    private final AippMemoryFactory memoryFactory;
    private final AippModelCenter aippModelCenter;
    private final String builtinPrompt;
    private final ChatModel modelService;

    private static final String TYPE_REGEX =
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";

    /**
     * 创建 {@link ClassifyQuestionCommandHandlerImpl} 的实例。
     *
     * @param memoryFactory 表示用于创建内存对象的 {@link AippMemoryFactory}。
     * @param aippModelCenter 表示用于获取模型的 {@link AippModelCenter}。
     * @param modelService 大模型服务 {@link ChatModel}。
     * @throws IOException IO异常.
     */
    public ClassifyQuestionCommandHandlerImpl(AippMemoryFactory memoryFactory, AippModelCenter aippModelCenter,
            ChatModel modelService) throws IOException {
        this.memoryFactory = notNull(memoryFactory, "The memory factory cannot be null.");
        this.aippModelCenter = notNull(aippModelCenter, "The model center cannot be null.");
        this.modelService = notNull(modelService, "The model service cannot be null.");
        this.builtinPrompt = IoUtils.content(ClassifyQuestionCommandHandlerImpl.class, Constant.BUILTIN_PROMPT);
    }

    @Override
    public String handle(ClassifyQuestionCommand command) {
        notNull(command, "The command cannot be null.");
        Map<String, String> args = notNull(command.getArgs(), "The command args cannot be null.");
        Memory memory = this.memoryFactory.create(command.getMemoryConfig(), command.getHistories());
        HashMap<String, String> variables = new HashMap<>(args);
        variables.put(Constant.HISTORY_KEY, memory.text());
        variables.put(Constant.TYPE_LIST_KEY, command.getTypeList());
        ModelAccessInfo modelAccessInfo =
                this.aippModelCenter.getModelAccessInfo(command.getModelTag(), command.getModel(), null);
        ChatOption chatOption = ChatOption.custom()
                .model(command.getModel())
                .baseUrl(modelAccessInfo.getBaseUrl())
                .secureConfig(modelAccessInfo.isSystemModel() ? null : SecureConfig.custom().ignoreTrust(true).build())
                .apiKey(modelAccessInfo.getAccessKey())
                .temperature(command.getTemperature())
                .stream(false)
                .build();

        String prompt = new DefaultStringTemplate(this.getTemplate(command)).render(variables);
        ChatMessages chatMessages = new ChatMessages();
        chatMessages.add(new HumanMessage(prompt));
        Choir<ChatMessage> answer = this.modelService.generate(chatMessages, chatOption);
        String textAnswer = ContentProcessUtils.filterReasoningContent(answer.blockAll().get(0).text());
        String extractedType = "";
        Pattern pattern = Pattern.compile(TYPE_REGEX);
        Matcher matcher = pattern.matcher(textAnswer);
        if (matcher.find()) {
            extractedType = matcher.group();
        }
        return command.getQuestionType(extractedType)
                .map(QuestionType::getId)
                .orElseGet(() -> command.getLastQuestionType().getId());
    }

    private String getTemplate(ClassifyQuestionCommand command) {
        return StringUtils.isNotBlank(command.getTemplate()) ? this.builtinPrompt + "\n\n## 辅助描述\n\n"
                + command.getTemplate() : this.builtinPrompt;
    }
}