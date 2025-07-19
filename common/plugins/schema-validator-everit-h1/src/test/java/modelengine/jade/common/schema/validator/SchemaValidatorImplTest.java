/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.schema.validator;

import static modelengine.fitframework.util.IoUtils.content;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import modelengine.jade.app.engine.schema.SchemaValidatorImplTestCaseParam;
import modelengine.jade.common.schema.validator.SchemaValidatorImpl;
import modelengine.jade.schema.SchemaValidator;
import modelengine.jade.schema.exception.JsonContentInvalidException;
import modelengine.jade.schema.exception.JsonSchemaInvalidException;

import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.serialization.SerializationException;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.TypeUtils;
import modelengine.jade.common.schema.validator.SchemaValidatorImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 {@link SchemaValidatorImpl} 的测试用例。
 *
 * @author 兰宇晨
 * @since 2024-07-29
 */
public class SchemaValidatorImplTest {
    private final ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null, true);

    static class ValidateTestCaseProvider implements ArgumentsProvider {
        static final Map<String, String> testCaseMap = MapBuilder.<String, String>get()
                .put("shouldOkWhenValidStringSchema", "/test/valid_eval_data_and_schema_test_case.json")
                .put("shouldNotOkWhenValidateWithInvalidStringData", "/test/invalid_eval_data_test_case.json")
                .put("shouldNotOkWhenValidateWithInvalidStringSchema", "/test/invalid_schema_fail_test_case.json")
                .put("shouldOkWhenValidMapSchema", "/test/valid_eval_data_and_schema_test_case.json")
                .put("shouldNotOkWhenValidateWithInvalidMapData", "/test/invalid_eval_data_test_case.json")
                .put("shouldNotOkWhenValidateWithInvalidMapSchema", "/test/invalid_schema_fail_test_case.json")
                .put("shouldOkWhenValidListSchema", "/test/valid_list_data_and_schema_test_case.json")
                .build();

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws IOException {
            ObjectSerializer objectSerializer = new JacksonObjectSerializer(null, null, null, true);

            String resourceName = testCaseMap.get(extensionContext.getTestMethod().get().getName());
            String jsonContent = content(ValidateTestCaseProvider.class, resourceName);

            List<SchemaValidatorImplTestCaseParam> testCase = objectSerializer.deserialize(jsonContent,
                    TypeUtils.parameterized(List.class, new Type[] {modelengine.jade.app.engine.schema.SchemaValidatorImplTestCaseParam.class}));

            return testCase.stream().map(test -> Arguments.of(test.getSchema(), test.getContent()));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ValidateTestCaseProvider.class)
    @DisplayName("批量校验字符串数据成功")
    void shouldOkWhenValidStringSchema(String schema, List<String> contents) {
        SchemaValidator validator = new SchemaValidatorImpl(this.serializer);
        assertDoesNotThrow(() -> validator.validate(schema, contents));
    }

    @ParameterizedTest
    @ArgumentsSource(ValidateTestCaseProvider.class)
    @DisplayName("批量校验字符串数据失败[数据非法]")
    void shouldNotOkWhenValidateWithInvalidStringData(String schema, List<String> contents) {
        SchemaValidator validator = new SchemaValidatorImpl(this.serializer);
        assertThatThrownBy(() -> validator.validate(schema, contents)).isInstanceOf(JsonContentInvalidException.class);
    }

    @ParameterizedTest
    @ArgumentsSource(ValidateTestCaseProvider.class)
    @DisplayName("批量校验字符串数据失败[Schema非法]")
    void shouldNotOkWhenValidateWithInvalidStringSchema(String schema, List<String> contents) {
        SchemaValidator validator = new SchemaValidatorImpl(this.serializer);
        assertThatThrownBy(() -> validator.validate(schema, contents)).isInstanceOf(JsonSchemaInvalidException.class);
    }

    @ParameterizedTest
    @ArgumentsSource(ValidateTestCaseProvider.class)
    @DisplayName("批量校验Map形式数据成功")
    void shouldOkWhenValidMapSchema(String schema, List<String> contents) {
        SchemaValidator validator = new SchemaValidatorImpl(this.serializer);
        Map<String, Object> jsonSchema = serializer.deserialize(schema, Map.class);
        List<Map<String, Object>> target = contents.stream()
                .map(content -> serializer.<Map<String, Object>>deserialize(content, Map.class))
                .collect(Collectors.toList());
        assertDoesNotThrow(() -> validator.validate(jsonSchema, target));
    }

    @ParameterizedTest
    @ArgumentsSource(ValidateTestCaseProvider.class)
    @DisplayName("批量校验List形式数据成功")
    void shouldOkWhenValidListSchema(String schema, List<String> contents) {
        SchemaValidator validator = new SchemaValidatorImpl(this.serializer);
        Map<String, Object> jsonSchema = serializer.deserialize(schema, Map.class);
        List<? extends List<?>> target = contents.stream()
                .map(content -> serializer.<List<?>>deserialize(content, List.class))
                .collect(Collectors.toList());
        assertDoesNotThrow(() -> validator.validate(jsonSchema, target));
    }

    @ParameterizedTest
    @ArgumentsSource(ValidateTestCaseProvider.class)
    @DisplayName("批量校验Map形式数据失败[数据非法]")
    void shouldNotOkWhenValidateWithInvalidMapData(String schema, List<String> contents) {
        try {
            SchemaValidator validator = new SchemaValidatorImpl(this.serializer);
            Map<String, Object> jsonSchema = serializer.deserialize(schema, Map.class);
            List<Map<String, Object>> target = contents.stream()
                    .map(content -> serializer.<Map<String, Object>>deserialize(content, Map.class))
                    .collect(Collectors.toList());
            assertThatThrownBy(() -> validator.validate(jsonSchema,
                    target)).isInstanceOf(JsonContentInvalidException.class);
        } catch (SerializationException ex) {
            return;
        }
    }

    @ParameterizedTest
    @ArgumentsSource(ValidateTestCaseProvider.class)
    @DisplayName("批量校验Map形式数据失败[Schema非法]")
    void shouldNotOkWhenValidateWithInvalidMapSchema(String schema, List<String> contents) {
        try {
            SchemaValidator validator = new SchemaValidatorImpl(this.serializer);
            Map<String, Object> jsonSchema = serializer.deserialize(schema, Map.class);
            List<Map<String, Object>> target = contents.stream()
                    .map(content -> serializer.<Map<String, Object>>deserialize(content, Map.class))
                    .collect(Collectors.toList());
            assertThatThrownBy(() -> validator.validate(jsonSchema,
                    target)).isInstanceOf(JsonContentInvalidException.class);
        } catch (SerializationException ex) {
            return;
        }
    }
}