/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.rewrite.ports;

import static modelengine.fit.jade.aipp.rewrite.util.TestUtils.getQueryCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.enums.ModelType;
import modelengine.fit.jade.aipp.rewrite.RewriteQueryParam;
import modelengine.fit.jade.aipp.rewrite.RewriteService;
import modelengine.fit.jade.aipp.rewrite.command.RewriteCommandHandler;
import modelengine.fit.jade.aipp.rewrite.command.RewriteQueryCommand;
import modelengine.fit.jade.aipp.rewrite.ports.fit.AippRewriteService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

/**
 * 表示重写算子对外暴露端口的测试集。
 *
 * @author 易文渊
 * @since 2024-09-25
 */
@DisplayName("测试重写算子对外暴露端口")
public class PortsTest {
    @Nested
    @DisplayName("测试 Fit 端口")
    class Fit {
        private RewriteCommandHandler commandService;
        private RewriteService rewriteService;

        @BeforeEach
        void setUp() {
            this.commandService = mock(RewriteCommandHandler.class);
            this.rewriteService = new AippRewriteService(this.commandService);
        }

        @AfterEach
        void tearDown() {
            clearInvocations(this.commandService);
        }

        @Test
        @DisplayName("测试重写服务调用正常")
        void shouldOkWhenInvokeRewriteService() {
            List<String> expect = Arrays.asList("1", "2", "3");
            when(this.commandService.handle(any())).thenReturn(expect);
            RewriteQueryCommand testCommand = getQueryCommand();
            RewriteQueryParam queryParam = constructRewriteQueryParam(testCommand);
            List<String> result = this.rewriteService.rewriteQuery(queryParam,
                    testCommand.getMemoryConfig(),
                    testCommand.getHistories());
            assertThat(result).isEqualTo(expect);
            ArgumentCaptor<RewriteQueryCommand> argument = ArgumentCaptor.forClass(RewriteQueryCommand.class);
            verify(this.commandService).handle(argument.capture());
            assertThat(argument.getValue()).isEqualTo(testCommand);
        }

        private static RewriteQueryParam constructRewriteQueryParam(RewriteQueryCommand testCommand) {
            RewriteQueryParam queryParam = new RewriteQueryParam();
            queryParam.setStrategy("builtin");
            queryParam.setArgs(testCommand.getArgs());
            queryParam.setTemplate(testCommand.getTemplate());
            queryParam.setAccessInfo(new ModelAccessInfo(testCommand.getModel(),
                    testCommand.getModelTag(), null, null, ModelType.CHAT_COMPLETIONS.value()));
            queryParam.setTemperature(testCommand.getTemperature());
            return queryParam;
        }
    }
}