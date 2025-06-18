/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter;

import static modelengine.fitframework.util.IoUtils.content;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;

import modelengine.jade.schema.SchemaValidator;

import modelengine.fit.jade.aipp.formatter.testcase.FormatterTestCase;
import modelengine.fit.jade.aipp.formatter.testcase.entity.FlatMessageItem;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.util.TypeUtils;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * {@link LlmOutputFormatter} 的测试。
 *
 * @author 刘信宏
 * @since 2024-11-22
 */
@FitTestWithJunit(includeClasses = {LlmOutputFormatter.class})
public class LlmOutputFormatterTest {
    private static final ObjectSerializer SERIALIZER = new JacksonObjectSerializer(null, null, null, true);

    @Fit
    private OutputFormatter formatter;
    @Mock
    private SchemaValidator validator;

    @BeforeEach
    void setup() {
        // mock 字符串类型不匹配 llmOutput 的 schema
        doThrow(new FitException("Invalid data")).when(this.validator).validate(any(), anyString());
    }

    static class LlmFormatterTestCaseProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws IOException {
            String jsonContent =
                    content(LlmFormatterTestCaseProvider.class, "/valid_llm_output_formatter_test_case.json");
            List<FormatterTestCase> testCase = SERIALIZER.deserialize(jsonContent,
                    TypeUtils.parameterized(List.class, new Type[] {FormatterTestCase.class}));

            return testCase.stream().map(test -> Arguments.of(test.getData(), test.getExpected()));
        }
    }

    @ArgumentsSource(LlmFormatterTestCaseProvider.class)
    @ParameterizedTest
    void GivenNormalDataWhenFormatWithLlmFormatterThenOk(Object data, String expected) {
        List<FlatMessageItem> expectedMsg = SERIALIZER.deserialize(expected,
                TypeUtils.parameterized(List.class, new Type[] {FlatMessageItem.class}));

        Optional<OutputMessage> outputMessage = this.formatter.format(data);
        Assertions.assertThat(outputMessage).isPresent();
        assertThat(outputMessage.get().items()).hasSize(expectedMsg.size())
                .flatExtracting(MessageItem::type, MessageItem::data, MessageItem::reference)
                .containsSequence(expectedMsg.stream()
                        .flatMap(item -> Arrays.stream(Tuple.tuple(item.type(), item.data(), item.reference())
                                .toArray()))
                        .toArray());
    }
}
