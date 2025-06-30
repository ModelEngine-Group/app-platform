/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.formatter;

import static modelengine.fitframework.util.IoUtils.content;
import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.jade.aipp.formatter.testcase.FormatterTestCase;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.TypeUtils;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * {@link DefaultOutputFormatter} 的测试。
 *
 * @author 刘信宏
 * @since 2024-11-22
 */
@FitTestWithJunit(
        includeClasses = {DefaultOutputFormatter.class})
public class DefaultOutputFormatterTest {
    @Fit
    private OutputFormatter formatter;

    static class DefaultFormatterTestCaseProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws IOException {
            ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null, true);

            String resourceName = "/default_formatter_test_case.json";
            String jsonContent = content(DefaultFormatterTestCaseProvider.class, resourceName);

            List<FormatterTestCase> testCase = serializer.deserialize(jsonContent,
                    TypeUtils.parameterized(List.class, new Type[] {FormatterTestCase.class}));

            return testCase.stream().map(test -> Arguments.of(test.getData(), test.getExpected()));
        }
    }

    @ArgumentsSource(DefaultFormatterTestCaseProvider.class)
    @ParameterizedTest
    void GivenNormalDataWhenFormatWithDefaultFormatterThenOk(Object data, String expected) {
        Optional<OutputMessage> outputMessage = this.formatter.format(data);
        Assertions.assertThat(outputMessage).isPresent();
        assertThat(outputMessage.get().items()).hasSize(1).map(MessageItem::data, MessageItem::reference)
                .containsSequence(Tuple.tuple(expected, Collections.emptyMap()));
    }

    @Test
    void GivenInvalidDataWhenFormatWithDefaultFormatterThenFail() {
        assertThat(this.formatter.format(null)).isNotPresent();
        assertThat(this.formatter.format(MapBuilder.get().put("ignore", null).build())).isNotPresent();
    }
}
