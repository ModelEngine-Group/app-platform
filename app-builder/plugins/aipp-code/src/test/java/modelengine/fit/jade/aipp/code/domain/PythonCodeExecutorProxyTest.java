/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Answers.RETURNS_SELF;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.aipp.code.config.CodeExecutorAutoConfig;
import modelengine.fit.jade.aipp.code.domain.entity.CodeExecutor;
import modelengine.fit.jade.aipp.code.domain.entity.ProgrammingLanguage;
import modelengine.fit.jade.aipp.code.domain.entity.support.PythonCodeExecutorProxy;
import modelengine.fit.jade.aipp.code.domain.factory.CodeExecutorFactory;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * 表示 {@link PythonCodeExecutorProxy} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-10-16
 */
@DisplayName("测试 PythonCodeExecutorProxy 实现")
public class PythonCodeExecutorProxyTest {
    private static Invoker invoker;
    private static CodeExecutorFactory factory;

    @BeforeAll
    static void beforeAll() {
        BrokerClient brokerClient = mock(BrokerClient.class);
        Router router = mock(Router.class);
        invoker = mock(Invoker.class, RETURNS_SELF);

        when(brokerClient.getRouter(anyString())).thenReturn(router);
        when(router.route(any())).thenReturn(invoker);
        factory = new CodeExecutorAutoConfig().getCodeExecutorFactory(brokerClient,
                new JacksonObjectSerializer(null, null, null, true));
    }

    @Nested
    @DisplayName("测试 Python code executor")
    class Python {
        Map<String, Object> args;
        private CodeExecutor pythonExecutor;

        @BeforeEach
        void setup() {
            this.pythonExecutor = factory.create(ProgrammingLanguage.PYTHON);
            this.args = MapBuilder.<String, Object>get().build();
        }

        @Test
        @DisplayName("节点运行成功")
        void shouldOkWhenRunCode() {
            when(invoker.invoke(anyMap(), anyString())).thenReturn("1");
            String code = "fake code";
            Object result = pythonExecutor.run(this.args, code);
            assertThat(result).isEqualTo(1);
        }

        @Test
        @DisplayName("节点运行失败，抛出报错")
        void shouldOkWhenRunCodeWithException() {
            when(invoker.invoke(anyMap(), anyString())).thenThrow(new FitException("Execution timed out"));
            String code = "fake code";
            assertThatThrownBy(() -> this.pythonExecutor.run(this.args, code)).isInstanceOf(FitException.class)
                    .extracting("code", "message")
                    .containsExactly(0x7F000000, "Execution timed out");
        }
    }
}