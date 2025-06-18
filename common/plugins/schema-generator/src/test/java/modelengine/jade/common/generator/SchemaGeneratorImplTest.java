/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.generator;

import static modelengine.fitframework.util.IoUtils.content;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.jade.common.generator.SchemaGeneratorImpl;
import modelengine.jade.schema.SchemaGenerator;
import modelengine.jade.schema.exception.JsonSchemaInvalidException;

import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.TypeUtils;
import modelengine.jade.common.generator.SchemaGeneratorImpl;

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
import java.util.stream.Stream;

/**
 * 表示 {@link SchemaGenerator} 的测试用例。
 *
 * @author 兰宇晨
 * @since 2024-08-10
 */
public class SchemaGeneratorImplTest {
    static class ValidateTestCaseProvider implements ArgumentsProvider {
        static final Map<String, String> testCaseMap = MapBuilder.<String, String>get()
            .put("shouldOkWhenGenerateSchema", "/test/generate_schema_success_test_case.json")
            .put("shouldNotOKWhenGenerateSchema", "/test/generate_schema_fail_test_case.json")
            .build();

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext)
            throws IOException {
            ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null, true);

            String resourceName = testCaseMap.get(extensionContext.getTestMethod().get().getName());
            String jsonContent = content(SchemaGeneratorImplTest.ValidateTestCaseProvider.class, resourceName);

            List<SchemaGeneratorImplTestCaseParam> testCase = serializer.deserialize(jsonContent,
                TypeUtils.parameterized(List.class, new Type[] {SchemaGeneratorImplTestCaseParam.class}));

            return testCase.stream().map(test -> Arguments.of(test.getJson(), test.getSchema()));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(SchemaGeneratorImplTest.ValidateTestCaseProvider.class)
    @DisplayName("成功生成数据约束")
    public void shouldOkWhenGenerateSchema(String json, String schema) {
        SchemaGenerator generator = new SchemaGeneratorImpl();
        assertThat(generator.generateSchema(json)).isEqualTo(schema);
    }

    @ParameterizedTest
    @ArgumentsSource(SchemaGeneratorImplTest.ValidateTestCaseProvider.class)
    @DisplayName("无效 Json 导致生成数据约束失败")
    public void shouldNotOKWhenGenerateSchema(String json) {
        SchemaGenerator generator = new SchemaGeneratorImpl();
        assertThatThrownBy(() -> generator.generateSchema(json)).isInstanceOf(JsonSchemaInvalidException.class);
    }
}