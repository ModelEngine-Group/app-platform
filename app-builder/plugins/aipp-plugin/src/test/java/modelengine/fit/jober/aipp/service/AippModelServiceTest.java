/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.chat.support.AiMessage;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jober.aipp.domain.AippSystemConfig;
import modelengine.fit.jober.aipp.dto.model.PromptGenerateDto;
import modelengine.fit.jober.aipp.repository.AippSystemConfigRepository;
import modelengine.fit.jober.aipp.service.impl.AippModelServiceImpl;
import modelengine.fitframework.flowable.Choir;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

/**
 * AippModelServiceTest
 *
 * @author 张越
 * @since 2024-12-02
 */
@DisplayName("模型用户交互接口测试类")
public class AippModelServiceTest {
    private static ChatModel modelService;

    private static AippModelCenter aippModelCenter;

    private static AippSystemConfigRepository aippSystemConfigRepository;

    private static AippModelService aippModelService;

    @BeforeAll
    public static void beforeAll() {
        modelService = mock(ChatModel.class);
        aippModelCenter = mock(AippModelCenter.class);
        aippSystemConfigRepository = mock(AippSystemConfigRepository.class);
        aippModelService = new AippModelServiceImpl(modelService, aippModelCenter, aippSystemConfigRepository);
    }

    @AfterEach
    void tearDown() {
        clearInvocations(modelService, aippModelCenter, aippSystemConfigRepository);
    }

    @Nested
    @DisplayName("测试chat接口")
    class ChatTest {
        @BeforeEach
        void setUp() {
        }

        @Test
        @DisplayName("chat接口正常")
        void shouldOkWhenChat() {
            when(aippModelCenter.getModelAccessInfo(anyString(), any(), any())).thenReturn(
                    ModelAccessInfo.builder().baseUrl("1111").tag("tag").build());
            when(modelService.generate(any(Prompt.class), any(ChatOption.class))).thenReturn(
                    Choir.just(new AiMessage("123")));

            String result = aippModelService.chat("Qwen2-72B-Instruct", "internal", 0.1, "代码专家");
            Assertions.assertEquals(result, "123");
        }
    }

    @Nested
    @DisplayName("测试提示词模板生成")
    class GeneratePrompt {
        @BeforeEach
        void setUp() {
        }

        @Test
        @DisplayName("正常生成提示词")
        void shouldOkWhenGeneratePrompt() {
            ModelAccessInfo model = Mockito.mock(ModelAccessInfo.class);
            when(model.getServiceName()).thenReturn("llm_model");
            when(model.getTag()).thenReturn("llm_tag");
            when(aippModelCenter.getDefaultModel(any(), any())).thenReturn(model);
            when(aippModelCenter.getModelAccessInfo(anyString(), any(), any())).thenReturn(
                    ModelAccessInfo.builder().baseUrl("1111").tag("tag").build());
            when(modelService.generate(any(Prompt.class), any(ChatOption.class))).thenReturn(
                    Choir.just(new AiMessage("123")));
            when(aippSystemConfigRepository.find(anyString(), anyString())).thenReturn(
                    Optional.of(AippSystemConfig.builder().configValue("{\"template\": \"template\" }").build()));

            PromptGenerateDto param = new PromptGenerateDto();
            param.setInput("xx专家");
            param.setModel("Qwen2-72B-Instruct");
            param.setModelTag("internal");
            param.setTemperature(0.2);
            param.setTemplateType("user");

            String result = aippModelService.generatePrompt(param, null);
            Assertions.assertEquals(result, "123");
        }

        @Test
        @DisplayName("系统配置不存在")
        void shouldThrowExceptionWhenConfigNotExists() {
            when(aippModelCenter.getModelAccessInfo(anyString(), any(), any())).thenReturn(
                    ModelAccessInfo.builder().baseUrl("1111").build());
            when(modelService.generate(any(Prompt.class), any(ChatOption.class))).thenReturn(
                    Choir.just(new AiMessage("123")));
            when(aippSystemConfigRepository.find(anyString(), anyString())).thenReturn(Optional.empty());

            PromptGenerateDto param = new PromptGenerateDto();
            param.setInput("xx专家");
            param.setModel("Qwen2-72B-Instruct");
            param.setModelTag("internal");
            param.setTemperature(0.2);
            param.setTemplateType("user");

            Assertions.assertThrows(IllegalStateException.class, () -> aippModelService.generatePrompt(param, null));
        }
    }
}
