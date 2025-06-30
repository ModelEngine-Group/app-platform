/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.extract.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.memory.support.CacheMemory;
import modelengine.fit.jade.aipp.extract.command.impl.ExtractCommandHandlerImpl;
import modelengine.fit.jade.aipp.extract.domain.entity.ContentExtractor;
import modelengine.fit.jade.aipp.extract.util.TestUtils;
import modelengine.fit.jade.aipp.extract.utils.Constant;
import modelengine.fit.jade.aipp.memory.AippMemoryFactory;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

/**
 * 表示 {@link ExtractCommandHandler} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-10-28
 */
@DisplayName("测试 ExtractCommandHandler")
@FitTestWithJunit(includeClasses = ExtractCommandHandlerImpl.class)
public class ExtractorCommandHandlerTest {
    @Fit
    private ExtractCommandHandler commandHandler;

    @Mock
    private AippMemoryFactory memoryFactory;

    @Mock
    private AippModelCenter aippModelCenter;

    @Mock
    private ContentExtractor extractor;

    @BeforeEach
    void setUp() {
        String histories = "Q: q1\nA: a1";
        when(this.memoryFactory.create(any(), any())).thenReturn(new CacheMemory() {
            @Override
            public String text() {
                return histories;
            }
        });
        when(this.aippModelCenter.getModelAccessInfo(any(), any(), any())).thenReturn(
                ModelAccessInfo.builder().baseUrl("/model").tag("tag").build());
    }

    @Test
    @DisplayName("测试执行信息提取命令成功")
    void shouldOkWhenHandleExtractCommand() {
        String result = "{}";
        when(this.extractor.run(anyMap(), anyString(), any())).thenReturn(result);
        assertThat(this.commandHandler.handle(TestUtils.getExtractCommand())).isEqualTo(result);
        ArgumentCaptor<Map<String, String>> variablesCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<String> outputSchemaCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ChatOption> chatOptionCaptor = ArgumentCaptor.forClass(ChatOption.class);
        verify(this.extractor).run(variablesCaptor.capture(), outputSchemaCaptor.capture(), chatOptionCaptor.capture());
        Map<String, String> variables = variablesCaptor.getValue();
        assertThat(variables).extracting(Constant.TEXT_KEY, Constant.DESC_KEY, Constant.HISTORY_KEY)
                .containsExactly("text", "desc", "Q: q1\nA: a1");
        String outputSchema = outputSchemaCaptor.getValue();
        assertThat(outputSchema).isEqualTo("{}");
        ChatOption chatOption = chatOptionCaptor.getValue();
        assertThat(chatOption).extracting(ChatOption::model, ChatOption::temperature)
                .containsExactly("model", 0.1);
    }
}