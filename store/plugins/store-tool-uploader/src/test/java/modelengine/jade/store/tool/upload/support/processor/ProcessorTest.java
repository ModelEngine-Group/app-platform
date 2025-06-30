/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.upload.support.processor;

import static modelengine.fel.tool.ToolSchema.PARAMETERS_PROPERTIES;
import static modelengine.fel.tool.ToolSchema.PROPERTIES_TYPE;
import static modelengine.fel.tool.info.schema.ToolsSchema.ARRAY;
import static modelengine.fel.tool.info.schema.ToolsSchema.ITEMS;
import static modelengine.fel.tool.info.schema.ToolsSchema.OBJECT;
import static modelengine.jade.store.tool.upload.utils.FormatFileUtils.getFileInfo;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;

import modelengine.fel.tool.info.entity.SchemaEntity;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.jade.common.exception.ModelEngineException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示 {@link Processor} 的测试类。
 *
 * @author 李金绪
 * @since 2024-10-29
 */
@DisplayName("测试 Processor")
public class ProcessorTest {
    private static final String FILENAME = "fileName";
    private static final String SCHEMA_JSON = "src/test/resources/schema.json";
    private static final String SCHEMA_CUBE_JSON = "src/test/resources/schema_cube.json";
    private static final int MAX_NAME_LENGTH = 256;

    private JacksonObjectSerializer serializer = new JacksonObjectSerializer(null, null, null, true);

    private Processor processor = mock(Processor.class, CALLS_REAL_METHODS);

    @Nested
    @DisplayName("测试 校验名字")
    class ValidateNameTest {
        @Test
        @DisplayName("当名字为空时，抛出异常")
        void shouldExWhenValidateBlankName() {
            assertThatThrownBy(() -> {
                processor.validateName("", "obejct", "field");
            }).isInstanceOf(ModelEngineException.class).hasMessageContaining("and cannot be blank.");
        }

        @Test
        @DisplayName("当名字超长时，抛出异常")
        void shouldExWhenValidateMaxLengthName() {
            assertThatThrownBy(() -> {
                String longName = createLongString(MAX_NAME_LENGTH + 1);
                processor.validateName(longName, "obejct", "field");
            }).isInstanceOf(ModelEngineException.class).hasMessageContaining("The field length exceeds the limit.");
        }

        private String createLongString(int length) {
            return Stream.generate(() -> "A").limit(length).collect(Collectors.joining());
        }

        @Test
        @DisplayName("当名字不符合格式时，抛出异常")
        void shouldExWhenValidateFormatName() {
            assertThatThrownBy(() -> {
                String invalidName = "+*/-*";
                processor.validateName(invalidName, "obejct", "field");
            }).isInstanceOf(ModelEngineException.class).hasMessageContaining("The name format is incorrect.");
        }
    }

    @Nested
    @DisplayName("校验 schema 字段")
    class ValidateSchema {
        private SchemaEntity schema;

        @BeforeEach
        void setup() {
            this.schema = getFileInfo(new File(SCHEMA_JSON), serializer, SchemaEntity.class);
        }

        @Test
        @DisplayName("schema 校验无异常")
        void shouldOkWhenValidateGoodSchema() {
            assertDoesNotThrow(() -> {
                processor.validateSchemaStrictly(FILENAME, schema);
                SchemaEntity cube = getFileInfo(new File(SCHEMA_CUBE_JSON), serializer, SchemaEntity.class);
                processor.validateSchemaStrictly(FILENAME, cube);
            });
        }

        @Test
        @DisplayName("当 schema 为空时，抛出异常")
        void shouldExWhenSchemaNull() {
            assertThatThrownBy(() -> {
                processor.validateSchemaStrictly(FILENAME, null);
            }).isInstanceOf(ModelEngineException.class)
                    .hasMessageContaining("The file must contain the property. [file='fileName', property='schema']");
        }

        @Test
        @DisplayName("校验 schema.name, 当不符合要求时，抛出异常")
        void shouldExWhenNameInValidate() {
            assertThatThrownBy(() -> {
                this.schema.setName("*-/");
                processor.validateSchemaStrictly(FILENAME, this.schema);
            }).isInstanceOf(ModelEngineException.class).hasMessageContaining("The name format is incorrect.");
        }

        @Test
        @DisplayName("校验 schema.description, 当为空时，抛出异常")
        void shouldExWhenDescBlank() {
            assertThatThrownBy(() -> {
                this.schema.setDescription("");
                processor.validateSchemaStrictly(FILENAME, this.schema);
            }).isInstanceOf(ModelEngineException.class)
                    .hasMessageContaining("The file must contain the property and cannot be blank. [file='fileName', "
                            + "property='schema.description']");
        }

        @Test
        @DisplayName("校验 schema.order, 当值或长度不合法时，抛出异常")
        void shouldExWhenOrderInValidate() {
            assertThatThrownBy(() -> {
                this.schema.setOrder(new ArrayList<>());
                processor.validateSchemaStrictly(FILENAME, this.schema);
            }).isInstanceOf(ModelEngineException.class)
                    .hasMessageContaining(
                            "The values in 'order' must exactly match those in 'parameters', with the same quantity, "
                                    + "differing only in order.");
        }

        @Nested
        @DisplayName("校验 schema.return")
        class ValidateSchemaRet {
            @Test
            @DisplayName("当 schema.return 为空时，抛出异常")
            void shouldExWhenReturnNull() {
                assertThatThrownBy(() -> {
                    schema.setRet(null);
                    processor.validateSchemaStrictly(FILENAME, schema);
                }).isInstanceOf(ModelEngineException.class)
                        .hasMessageContaining(
                                "reason=The file must contain the property. [file='tools.json', property='schema"
                                        + ".return']");
            }

            @Test
            @DisplayName("当 schema.return.type 为空时，抛出异常")
            void shouldExWhenTypeNull() {
                assertThatThrownBy(() -> {
                    Map<String, Object> ret = schema.getRet();
                    ret.remove(PROPERTIES_TYPE);
                    schema.setRet(ret);
                    processor.validateSchemaStrictly(FILENAME, schema);
                }).isInstanceOf(ModelEngineException.class).hasMessageContaining("The field has no type defined.");
            }

            @Test
            @DisplayName("当 schema.return.type 不在指定类型范围内时，抛出异常")
            void shouldExWhenBasicTypeInvalid() {
                assertThatThrownBy(() -> {
                    Map<String, Object> ret = schema.getRet();
                    ret.put(PROPERTIES_TYPE, "invalid");
                    schema.setRet(ret);
                    processor.validateSchemaStrictly(FILENAME, schema);
                }).isInstanceOf(ModelEngineException.class)
                        .hasMessageContaining(
                                "The parameter type must comply with the JSON schema parameter type format.");
            }

            @Test
            @DisplayName("当 schema.return.type.array.type 不在指定类型范围内时，抛出异常")
            void shouldExWhenArrayTypeInvalid() {
                assertThatThrownBy(() -> {
                    Map<String, Object> ret = schema.getRet();
                    Map<String, String> items = new HashMap() {{
                        put(PROPERTIES_TYPE, "invalid");
                    }};
                    ret.put(PROPERTIES_TYPE, ARRAY);
                    ret.put(ITEMS, items);
                    schema.setRet(ret);
                    processor.validateSchemaStrictly(FILENAME, schema);
                }).isInstanceOf(ModelEngineException.class)
                        .hasMessageContaining(
                                "The parameter type must comply with the JSON schema parameter type format.");
            }

            @Test
            @DisplayName("当 schema.return.type.array.type 是 object 嵌套时，成功")
            void shouldOkWhenArrayTypeIsObject() {
                assertDoesNotThrow(() -> {
                    Map<String, Object> ret = schema.getRet();
                    Map<String, Object> items = new HashMap<>();
                    items.put(PROPERTIES_TYPE, OBJECT);
                    items.put(PARAMETERS_PROPERTIES, schema.getParameters().getProperties());
                    ret.put(PROPERTIES_TYPE, ARRAY);
                    ret.put(ITEMS, items);
                    schema.setRet(ret);
                    processor.validateSchemaStrictly(FILENAME, schema);
                });
            }

            @Test
            @DisplayName("当 schema.return.type.object 嵌套校验时，成功")
            void shouldOKWhenReturnTypeIsObject() {
                assertDoesNotThrow(() -> {
                    Map<String, Object> ret = schema.getRet();
                    ret.put(PROPERTIES_TYPE, OBJECT);
                    ret.put(PARAMETERS_PROPERTIES, schema.getParameters().getProperties());
                    schema.setRet(ret);
                    processor.validateSchemaStrictly(FILENAME, schema);
                });
            }
        }

        @Nested
        @DisplayName("校验 schema.parameters")
        class ValidateParam {
            @Test
            @DisplayName("当 schema.parameters 为空时，成功")
            void shouldOkWhenParamIsNull() {
                assertDoesNotThrow(() -> {
                    schema.setParameters(null);
                    schema.setOrder(null);
                    processor.validateSchemaStrictly(FILENAME, schema);
                });
            }

            @Test
            @DisplayName("当 schema.parameters 为空时，成功")
            void shouldExWhenTypeIsNotObject() {
                assertThatThrownBy(() -> {
                    schema.getParameters().setType("invalid");
                    processor.validateSchemaStrictly(FILENAME, schema);
                }).isInstanceOf(ModelEngineException.class)
                        .hasMessageContaining("The type of the field 'parameters' in 'tools.json' must is: 'object'.");
            }

            @Test
            @DisplayName("当 schema.required 为空时，抛出异常")
            void shouldExWhenRequiredNull() {
                assertThatThrownBy(() -> {
                    schema.getParameters().setRequired(null);
                    processor.validateSchemaStrictly(FILENAME, schema);
                }).isInstanceOf(ModelEngineException.class)
                        .hasMessageContaining(
                                "The file must contain the property. [file='fileName', property='schema.required']");
            }

            @Test
            @DisplayName("当 schema.required 中的值不存在与 parameters 中，抛出异常")
            void shouldExWhenRequiredNotExist() {
                assertThatThrownBy(() -> {
                    List<String> required = Arrays.asList("location", "error");
                    schema.getParameters().setRequired(required);
                    processor.validateSchemaStrictly(FILENAME, schema);
                }).isInstanceOf(ModelEngineException.class)
                        .hasMessageContaining("The key in 'required' must already exist in the 'parameters'.");
            }

            @Test
            @DisplayName("当 schema.required 超过参数列表长度时，抛出异常")
            void shouldExWhenRequiredTooLong() {
                assertThatThrownBy(() -> {
                    List<String> required = Arrays.asList("location", "date", "date");
                    schema.getParameters().setRequired(required);
                    processor.validateSchemaStrictly(FILENAME, schema);
                }).isInstanceOf(ModelEngineException.class)
                        .hasMessageContaining(
                                "The size of 'required' in 'tools.json' cannot be larger than 'properties' size.");
            }
        }
    }
}
