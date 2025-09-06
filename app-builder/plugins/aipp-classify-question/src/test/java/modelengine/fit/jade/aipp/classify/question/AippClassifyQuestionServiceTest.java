/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.classify.question;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.aipp.classify.question.command.ClassifyQuestionCommand;
import modelengine.fit.jade.aipp.classify.question.command.ClassifyQuestionCommandHandler;
import modelengine.fit.jade.aipp.classify.question.utils.TestUtils;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.enums.ModelType;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * {@link AippClassifyQuestionService}测试集。
 *
 * @author 张越
 * @since 2024-11-18
 */
@DisplayName("测试问题分类算子对外暴露端口")
public class AippClassifyQuestionServiceTest {
    @Nested
    @DisplayName("测试 Fit 端口")
    class Fit {
        private ClassifyQuestionCommandHandler commandService;

        private ClassifyQuestionService classifyQuestionService;

        @BeforeEach
        void setUp() {
            this.commandService = mock(ClassifyQuestionCommandHandler.class);
            this.classifyQuestionService = new AippClassifyQuestionService(this.commandService);
        }

        @AfterEach
        void tearDown() {
            clearInvocations(this.commandService);
        }

        @Test
        @DisplayName("测试问题分类服务调用正常")
        void shouldOkWhenInvokeClassifyService() {
            String expect = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
            when(this.commandService.handle(any())).thenReturn(expect);
            ClassifyQuestionCommand command = TestUtils.getCommand();
            ClassifyQuestionParam param = new ClassifyQuestionParam();
            param.setQuestionTypeList(command.getQuestionTypes());
            param.setTemplate(command.getTemplate());
            param.setArgs(command.getArgs());
            param.setQuestionTypeList(command.getQuestionTypes());
            param.setAccessInfo(new ModelAccessInfo(command.getModel(),
                    command.getModelTag(), null, null, ModelType.CHAT_COMPLETIONS.value()));
            param.setTemperature(command.getTemperature());
            String result = this.classifyQuestionService.classifyQuestion(param, command.getMemoryConfig(),
                    command.getHistories());
            Assertions.assertEquals(result, expect);

            ArgumentCaptor<ClassifyQuestionCommand> argument = ArgumentCaptor.forClass(ClassifyQuestionCommand.class);
            verify(this.commandService).handle(argument.capture());
            assertThat(argument.getValue()).isEqualTo(command);
        }
    }
}