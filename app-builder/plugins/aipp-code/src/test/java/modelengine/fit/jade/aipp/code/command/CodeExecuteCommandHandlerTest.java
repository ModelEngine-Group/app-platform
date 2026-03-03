/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import modelengine.jade.common.code.CommonRetCode;

import modelengine.fit.jade.aipp.code.command.impl.CodeExecuteCommandHandlerImpl;
import modelengine.fit.jade.aipp.code.domain.entity.ProgrammingLanguage;
import modelengine.fit.jade.aipp.code.domain.entity.support.PythonCodeExecutorProxy;
import modelengine.fit.jade.aipp.code.domain.factory.CodeExecutorFactory;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import javax.validation.ConstraintViolationException;

/**
 * 表示 @{@link CodeExecuteCommandHandler} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-10-16
 */
@FitTestWithJunit(includeClasses = CodeExecuteCommandHandlerImpl.class)
@DisplayName("测试 CodeExecuteCommandHandler")
public class CodeExecuteCommandHandlerTest {
    @Fit
    private CodeExecuteCommandHandlerImpl commandHandler;

    @Mock
    private PythonCodeExecutorProxy pythonExecutor;

    @Mock
    private CodeExecutorFactory executorFactory;

    @BeforeEach
    void setUp() {
        when(this.executorFactory.create(any())).thenReturn(this.pythonExecutor);
    }

    @Test
    @DisplayName("测试运行成功")
    void shouldOkWhenRunCommand() {
        when(this.pythonExecutor.run(anyMap(), anyString())).thenReturn(1);
        CodeExecuteCommand command = new CodeExecuteCommand(new HashMap<>(), "fake code", ProgrammingLanguage.PYTHON);
        Object result = this.commandHandler.handle(command);
        assertThat(result).isEqualTo(1);
    }

    @Test
    @DisplayName("测试运行失败，抛出异常")
    void shouldOkWhenRunCommandThrowException() {
        when(this.pythonExecutor.run(anyMap(),
                anyString())).thenThrow(new FitException(CommonRetCode.INTERNAL_ERROR.getCode(),
                "Execution timed out"));
        CodeExecuteCommand command = new CodeExecuteCommand(new HashMap<>(), "fake code", ProgrammingLanguage.PYTHON);

        assertThatThrownBy(() -> this.commandHandler.handle(command)).isInstanceOf(FitException.class)
                .extracting("code", "message")
                .containsExactly(CommonRetCode.INTERNAL_ERROR.getCode(), "Execution timed out");
    }

    @Test
    @DisplayName("校验参数值成功")
    // @Disabled("升级fit框架validation机制问题")
    void shouldFailWithInvalidArgs() {
        assertThatThrownBy(() -> this.commandHandler.handle(new CodeExecuteCommand(null,
                "1",
                ProgrammingLanguage.PYTHON))).isInstanceOf(ConstraintViolationException.class)
                .extracting("message")
                .isEqualTo("handle.command.args: Args cannot be null.");
    }

    @Test
    @DisplayName("校验代码值成功")
    // @Disabled("升级fit框架validation机制问题")
    void shouldFailWithInvalidCode() {
        assertThatThrownBy(() -> this.commandHandler.handle(new CodeExecuteCommand(new HashMap<>(),
                "",
                ProgrammingLanguage.PYTHON))).isInstanceOf(ConstraintViolationException.class)
                .extracting("message")
                .isEqualTo("handle.command.code: Code cannot be blank.");
    }

    @Test
    @DisplayName("校验执行语言值成功")
    // @Disabled("升级fit框架validation机制问题")
    void shouldFailWithInvalidLanguage() {
        assertThatThrownBy(() -> this.commandHandler.handle(new CodeExecuteCommand(new HashMap<>(),
                "1",
                null))).isInstanceOf(ConstraintViolationException.class)
                .extracting("message")
                .isEqualTo("handle.command.language: Invalid code language.");
    }

    @Test
    @DisplayName("校验 null 值成功")
    // @Disabled("升级fit框架validation机制问题")
    void shouldFailWithNullInput() {
        assertThatThrownBy(() -> this.commandHandler.handle(null)).isInstanceOf(ConstraintViolationException.class)
                .extracting("message")
                .isEqualTo("handle.command: Command cannot be null.");
    }

    @Test
    @DisplayName("构建CommandHandler入参为 null 时，抛出异常")
    void shouldFailWhenCreateCommandHandlerWithNullFactory() {
        assertThatThrownBy(() -> new CodeExecuteCommandHandlerImpl(null)).isInstanceOf(IllegalArgumentException.class)
                .message()
                .isEqualTo("The executor factory cannot be null.");
    }
}