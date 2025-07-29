/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

public class LLMUtilsTest {
    static Stream<JsonTestCase> LLMJsonFixSuccessTestCaseCreator() {
        return Stream.of(new JsonTestCase("```json{}```", "{}"),
                new JsonTestCase("{\"a\":{}", "{\"a\":{}}"),
                new JsonTestCase("{\"a\\\"\":[{}", "{\"a\\\"\":[{}]}"),
                new JsonTestCase("[{\"a\n\": [\"\n\"]", "[{\"a\\n\": [\"\\n\"]}]"),
                new JsonTestCase("[{},{}", "[{},{}]"),
                new JsonTestCase("[\"]", "[\"]\"]"));
    }

    static Stream<String> LLMJsonFixFailTestCaseCreator() {
        return Stream.of("{\"a\":[}", "{[jane}]", "{}{}");
    }

    @ParameterizedTest
    @MethodSource("LLMJsonFixSuccessTestCaseCreator")
    void shouldFixJsonAsExpected(JsonTestCase success) throws IOException {
        String actual = LLMUtils.tryFixLlmJsonString(success.input);
        Assertions.assertEquals(success.expected, actual);
    }

    @ParameterizedTest
    @MethodSource("LLMJsonFixFailTestCaseCreator")
    void shouldReturnEmptyStringWhenFixFailed(String failureInput) throws IOException {
        Assertions.assertEquals("{}", LLMUtils.tryFixLlmJsonString(failureInput));
    }

    @Test
    void shouldThrowWhenNoJsonRootFound() {
        Assertions.assertThrows(IOException.class, () -> LLMUtils.tryFixLlmJsonString("```"));
    }

    static class JsonTestCase {
        public String input;
        public String expected;

        JsonTestCase(String in, String out) {
            input = in;
            expected = out;
        }
    }
}
