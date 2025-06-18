/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter;

import static modelengine.fitframework.util.IoUtils.content;
import static org.assertj.core.api.Assertions.assertThat;

import modelengine.jade.common.schema.validator.SchemaValidatorImpl;

import modelengine.fit.jade.aipp.formatter.chain.DefaultOutputFormatterChain;
import modelengine.fit.jade.aipp.formatter.constant.Constant;
import modelengine.fit.jade.aipp.formatter.support.ResponsibilityResult;
import modelengine.fit.jade.aipp.formatter.testcase.FormatterTestCase;
import modelengine.fit.jade.aipp.formatter.testcase.entity.FlatMessageItem;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.TypeUtils;
import modelengine.jade.common.schema.validator.SchemaValidatorImpl;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 应用响应的格式化器的集成测试。
 *
 * @author 刘信宏
 * @since 2024-11-22
 */
@FitTestWithJunit(includeClasses = {
        DefaultOutputFormatterChain.class, DefaultOutputFormatter.class, LlmOutputFormatter.class,
        SchemaValidatorImpl.class
})
public class OutputFormatterIntegrationTest {
    private static final ObjectSerializer SERIALIZER = new JacksonObjectSerializer(null, null, null, true);

    @Fit
    private OutputFormatterChain chain;

    static class FormatterIntegrationTestCaseProvider implements ArgumentsProvider {
        static final Map<String, String> TEST_CASE = MapBuilder.<String, String>get()
                .put("shouldOkWhenDefaultFormatterWithNormalData", "/default_formatter_test_case.json")
                .put("shouldOkWhenLlmFormatterWithNormalData", "/valid_llm_output_formatter_test_case.json")
                .put("shouldFailWhenLlmFormatterWithInvalidData", "/invalid_llm_output_formatter_test_case.json")
                .build();

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws IOException {
            String resourceName = TEST_CASE.get(extensionContext.getTestMethod().get().getName());
            String jsonContent = content(FormatterIntegrationTestCaseProvider.class, resourceName);
            List<FormatterTestCase> testCase = SERIALIZER.deserialize(jsonContent,
                    TypeUtils.parameterized(List.class, new Type[] {FormatterTestCase.class}));

            return testCase.stream()
                    .map(test -> Arguments.of(test.getData(), test.getExpected(), test.getFinalOutput()));
        }
    }

    @ArgumentsSource(FormatterIntegrationTestCaseProvider.class)
    @ParameterizedTest
    void shouldOkWhenDefaultFormatterWithNormalData(Object data, String expected) {
        Optional<ResponsibilityResult> result = this.chain.handle(data);
        assertThat(result).isPresent();
        assertThat(result.get()).extracting(ResponsibilityResult::owner, ResponsibilityResult::text)
                .containsSequence(Constant.DEFAULT, expected);
    }

    @ArgumentsSource(FormatterIntegrationTestCaseProvider.class)
    @ParameterizedTest
    void shouldOkWhenLlmFormatterWithNormalData(Object data, String expected, String finalOutput) {
        Optional<ResponsibilityResult> result = this.chain.handle(data);
        assertThat(result).isPresent();
        assertThat(result.get().owner()).isEqualTo(Constant.LLM_OUTPUT);

        List<FlatMessageItem> expectedMsg = SERIALIZER.deserialize(expected,
                TypeUtils.parameterized(List.class, new Type[] {FlatMessageItem.class}));
        Assertions.assertThat(result.get().items()).hasSize(expectedMsg.size())
                .flatExtracting(MessageItem::type, MessageItem::data, MessageItem::reference)
                .containsSequence(expectedMsg.stream()
                        .flatMap(item -> Arrays.stream(Tuple.tuple(item.type(), item.data(), item.reference())
                                .toArray()))
                        .toArray());

        assertThat(result.get().text()).isEqualTo(finalOutput);
    }

    @ArgumentsSource(FormatterIntegrationTestCaseProvider.class)
    @ParameterizedTest
    void shouldFailWhenLlmFormatterWithInvalidData(Object data) {
        OutputFormatter llmFormatter = this.chain.get()
                .stream()
                .filter(formatter -> formatter.name().equals(Constant.LLM_OUTPUT))
                .findFirst()
                .get();
        assertThat(llmFormatter.format(data)).isNotPresent();
    }
}
