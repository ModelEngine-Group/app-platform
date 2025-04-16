/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.rewrite.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.memory.support.CacheMemory;
import modelengine.fit.jade.aipp.memory.AippMemoryFactory;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jade.aipp.rewrite.command.impl.RewriteCommandHandlerImpl;
import modelengine.fit.jade.aipp.rewrite.domain.entity.Rewriter;
import modelengine.fit.jade.aipp.rewrite.domain.factory.RewriterFactory;
import modelengine.fit.jade.aipp.rewrite.domain.vo.RewriteParam;
import modelengine.fit.jade.aipp.rewrite.util.Constant;
import modelengine.fit.jade.aipp.rewrite.util.TestUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

/**
 * 表示 {@link RewriteCommandHandler} 的测试集。
 *
 * @author 易文渊
 * @since 2024-09-27
 */
@DisplayName("测试 RewriteCommandService")
public class RewriteCommandHandlerTest {
    private AippMemoryFactory memoryFactory;
    private RewriterFactory rewriterFactory;
    private AippModelCenter aippModelCenter;

    private RewriteCommandHandler commandService;

    @BeforeEach
    void setUp() {
        this.memoryFactory = mock(AippMemoryFactory.class);
        this.rewriterFactory = mock(RewriterFactory.class);
        this.aippModelCenter = mock(AippModelCenter.class);
        this.commandService =
                new RewriteCommandHandlerImpl(this.memoryFactory, this.rewriterFactory, this.aippModelCenter);
    }

    @Test
    @DisplayName("测试执行重写问题命令成功")
    void shouldOkWhenHandleQueryCommand() {
        List<String> result = Arrays.asList("1", "2", "3");
        String histories = "Q: q1\nA: a1";
        Rewriter rewriter = mock(Rewriter.class);
        when(this.rewriterFactory.create(any())).thenReturn(rewriter);
        when(this.memoryFactory.create(any(), any())).thenReturn(new CacheMemory() {
            @Override
            public String text() {
                return histories;
            }
        });
        when(this.aippModelCenter.getModelAccessInfo(any(), any(), any())).thenReturn(
                ModelAccessInfo.builder().baseUrl("/model").tag("tag").build());
        when(rewriter.invoke(any())).thenReturn(result);
        assertThat(this.commandService.handle(TestUtils.getQueryCommand())).isEqualTo(result);
        ArgumentCaptor<RewriteParam> argument = ArgumentCaptor.forClass(RewriteParam.class);
        verify(rewriter).invoke(argument.capture());
        RewriteParam param = argument.getValue();
        assertThat(param.getTemplate()).isEqualTo("hello");
        assertThat(param.getVariables()).hasFieldOrPropertyWithValue(Constant.QUERY_KEY, "sky")
                .hasFieldOrPropertyWithValue(Constant.HISTORY_KEY, histories);
        assertThat(param.getChatOption()).extracting(ChatOption::model,
                ChatOption::baseUrl,
                ChatOption::temperature).containsExactly("model", "/model", 0.1);
    }
}