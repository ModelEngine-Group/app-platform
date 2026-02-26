/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.filter.support;

import static modelengine.jade.common.code.CommonRetCode.BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.util.TypeUtils;
import modelengine.jade.common.code.CommonRetCode;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.common.globalization.LocaleService;
import modelengine.jade.common.localemessage.ExceptionLocaleService;
import modelengine.jade.common.test.TestController;
import modelengine.jade.common.vo.Result;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.stream.Stream;

/**
 * 表示 {@link DefaultHttpExceptionAdvice} 的测试套。
 *
 * @author 易文渊
 * @since 2024-07-18
 */
@MvcTest(classes = {TestController.class, DefaultHttpExceptionAdvice.class})
public class DefaultHttpExceptionAdviceTest {
    private static final int DUMMY_CODE = 1;

    @Fit
    private MockMvc mockMvc;
    @Fit
    private ExceptionLocaleService exceptionLocaleService;

    @Mock
    private LocaleService localeService;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void tearDown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
        clearInvocations(this.localeService);
    }

    @Test
    @DisplayName("测试拦截 Throwable")
    void shouldOkWhenInterceptException() {
        String systemDefaultMessage = "system default message";
        Mockito.when(this.localeService.localize(String.valueOf(CommonRetCode.INTERNAL_ERROR.getCode())))
                .thenReturn(systemDefaultMessage);
        DefaultHttpExceptionAdvice mockedDefaultHttpExceptionAdvice =
                new DefaultHttpExceptionAdvice(this.localeService);
        Result<Void> voidHttpResult = mockedDefaultHttpExceptionAdvice.handleException(new Throwable());
        assertThat(voidHttpResult.getMsg()).isEqualTo(systemDefaultMessage);
    }

    static class TextEventStringProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(Arguments.of(new FitException(DUMMY_CODE, "Fit"), DUMMY_CODE, "Fit"),
                    Arguments.of(new ModelEngineException(CommonRetCode.INTERNAL_ERROR),
                            CommonRetCode.INTERNAL_ERROR.getCode(),
                            CommonRetCode.INTERNAL_ERROR.getMsg()),
                    Arguments.of(new IllegalArgumentException("arg"), BAD_REQUEST.getCode(), "arg"),
                    Arguments.of(new Exception("Exception"), CommonRetCode.INTERNAL_ERROR.getCode(), "Exception"));
        }
    }

    @DisplayName("测试获取异常对象的国际化提示信息")
    @ParameterizedTest
    @ArgumentsSource(TextEventStringProvider.class)
    public void shouldOkWhenGetLocaleMessageWithFitException(Throwable throwable, int code, String expected) {
        Mockito.when(this.localeService.localize(anyString())).thenReturn(throwable.getMessage());

        String message = this.exceptionLocaleService.localizeMessage(throwable);
        assertThat(message).isEqualTo(expected);
        verify(this.localeService).localize(eq(String.valueOf(code)));
    }

    @Test
    @DisplayName("测试拦截 FitException")
    public void shouldOkWhenInterceptFitException() {
        Mockito.when(this.localeService.localize(Mockito.anyString(), Mockito.any())).thenReturn("test error");
        String url = "/nonsupport/exception";
        this.response = this.mockMvc.perform(MockMvcRequestBuilders.get(url)
                .responseType(TypeUtils.parameterized(Result.class, new Type[] {Void.class})));
        assertThat(this.response.objectEntity()).isNotEmpty()
                .get()
                .satisfies(objectEntity -> assertThat(objectEntity.object()).hasFieldOrPropertyWithValue("code", 404)
                        .hasFieldOrPropertyWithValue("msg", "test error")
                        .hasFieldOrPropertyWithValue("data", null));
    }

    @Test
    @DisplayName("测试拦截 ConstraintViolationException")
    @Disabled("框架校验注解无法生效")
    public void shouldOkWhenConstraintViolationExceptionWithHibernate() {
        Mockito.when(this.localeService.localize(Mockito.any(), Mockito.any())).thenReturn("test error");
        this.response = this.mockMvc.perform(MockMvcRequestBuilders.post("/hibernate/blank")
                .param("name", "")
                .responseType(TypeUtils.parameterized(Result.class, new Type[] {Void.class})));
        assertThat(this.response.objectEntity()).isNotEmpty()
                .get()
                .satisfies(objectEntity -> assertThat(objectEntity.object()).hasFieldOrPropertyWithValue("code",
                                CommonRetCode.BAD_REQUEST.getCode())
                        .hasFieldOrPropertyWithValue("msg", "test error")
                        .hasFieldOrPropertyWithValue("data", null));
    }
}