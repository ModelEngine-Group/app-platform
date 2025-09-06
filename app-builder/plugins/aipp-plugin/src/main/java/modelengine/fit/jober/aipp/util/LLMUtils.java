/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fit.jober.aipp.common.exception.AippJsonDecodeException;
import modelengine.fit.jober.aipp.enums.LlmModelNameEnum;
import modelengine.fit.jober.aipp.service.LLMService;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.support.ChatMessages;
import modelengine.fel.core.chat.support.HumanMessage;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

/**
 * 为大模型提供一些附加功能
 *
 * @author 熊以可
 * @since 2024-01-26
 */
public class LLMUtils {
    private static final Logger log = Logger.get(LLMUtils.class);

    /**
     * 向LLM生成json
     *
     * @param llmService 表示提供LLM服务的{@link LLMService}
     * @param prompt 表示LLM提示词的{@link String}
     * @param maxToken 表示LLM的最大token数量
     * @param model 表示使用的模型类型的{@link LlmModelNameEnum}
     * @return Json结果
     * @throws AippJsonDecodeException 从LLM获得json结果异常
     */
    public static String askModelForJson(LLMService llmService, String prompt, int maxToken, LlmModelNameEnum model)
            throws AippJsonDecodeException {
        try {
            String answer = llmService.askModelWithText(prompt, maxToken, 0.2d, model);
            return tryFixLlmJsonString(answer);
        } catch (IOException e) {
            log.error("fail on llm service. reason: {}", e.getMessage());
            throw new AippJsonDecodeException(e.getMessage());
        }
    }

    /**
     * 尝试修复大模型json字符串
     *
     * @param answer 回答
     * @return json字符串
     * @throws IOException IO异常
     */
    public static String tryFixLlmJsonString(String answer) throws IOException {
        int startIndex = answer.indexOf('{');
        int finalIndex = answer.lastIndexOf('}');
        String jsonString;
        try {
            jsonString = answer.substring(startIndex, finalIndex + 1);  // get rid of ```json at head and ``` at tail
        } catch (IndexOutOfBoundsException e1) {
            try {
                jsonString = answer.substring(answer.indexOf('['), answer.lastIndexOf(']') + 1);
            } catch (IndexOutOfBoundsException e2) {
                throw new IOException(String.format("bad json: no root object or array found in answer: %s", answer));
            }
        }
        if (JsonUtils.isValidJson(jsonString)) {
            return jsonString;
        }

        jsonString = tryCompleteJson(jsonString);
        if (JsonUtils.isValidJson(jsonString)) {
            return jsonString;
        }
        jsonString = "[" + jsonString + "]";
        // JSONArray的场景可能会有问题
        if (JsonUtils.isValidJson(jsonString)) {
            return jsonString;
        }
        return "{}";
    }

    private static String tryCompleteJson(String jsonString) {
        StringBuilder result = new StringBuilder();
        boolean isInString = false;
        boolean isEscaped = false;
        Stack<Character> symbolStack = new Stack<>();
        for (char currentChar : jsonString.toCharArray()) {
            if (isInString) {
                if (currentChar == '\"' && !isEscaped) {
                    isInString = false;
                } else if (currentChar == '\n' && !isEscaped) {
                    result.append("\\n");
                    continue;
                } else if (currentChar == '\\') {
                    isEscaped = !isEscaped;
                } else {
                    isEscaped = false;
                }
            } else {
                if (currentChar == '\"') {
                    isInString = true;
                }
                String dealParentThesisResult = dealParentThesis(symbolStack, currentChar);
                if (!StringUtils.isBlank(dealParentThesisResult)) {
                    return "{}";
                }
            }
            result.append(currentChar);
        }
        if (isInString) {  // string is not closed at the end
            result.append('\"');
        }
        while (!symbolStack.isEmpty()) {
            result.append(symbolStack.pop());
        }
        return result.toString();
    }

    private static String dealParentThesis(Stack<Character> symbolStack, char currentChar) {
        if (currentChar == '{') {
            symbolStack.add('}');
            return StringUtils.EMPTY;
        }
        if (currentChar == '[') {
            symbolStack.add(']');
            return StringUtils.EMPTY;
        }
        if (currentChar == '}' || currentChar == ']') {
            if (!symbolStack.isEmpty() && symbolStack.peek() == currentChar) {
                symbolStack.pop();
            } else {
                return "{}";
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 使用大模型来获得总结
     *
     * @param openAiClient 表示modelio客户端接口{@link ChatModel}
     * @param prompt 表示待大模型进行总结的内容的{@link String}
     * @param model 表示大模型类型的{@link LlmModelNameEnum}
     * @param maxTokens 表示大模型的最大tokens的参数的{@link int}
     * @return 大模型的返回结果
     * @throws IOException 大模型处理异常
     */
    public static String askModelForSummary(ChatModel openAiClient, String prompt, LlmModelNameEnum model,
            int maxTokens) throws IOException {
        log.info("askModelForSummary with prompt: {}", prompt);
        ChatMessages chatMessages = ChatMessages.from(new HumanMessage(prompt));
        ChatOption option = ChatOption.custom().model(model.getValue()).maxTokens(maxTokens).stream(false).build();
        List<ChatMessage> chatCompletion = openAiClient.generate(chatMessages, option).blockAll();
        if (CollectionUtils.isEmpty(chatCompletion)) {
            log.error("openAiClient response is empty.");
            return StringUtils.EMPTY;
        }
        return chatCompletion.get(0).text();
    }
}