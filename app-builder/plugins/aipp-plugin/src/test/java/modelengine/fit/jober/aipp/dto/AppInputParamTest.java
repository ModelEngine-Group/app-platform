package modelengine.fit.jober.aipp.dto;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import modelengine.fit.jober.aipp.common.exception.AippParamException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * {@link AppBuilderSaveConfigDto} 的测试类。
 *
 * @author 孙怡菲
 * @since 2025-09-28
 */
class AppInputParamTest {
    @Nested
    @DisplayName("from() 方法测试")
    class FromMethodTest {
        @Test
        @DisplayName("应该成功创建基本的 AppInputParam 对象")
        void shouldCreateBasicAppInputParam() {
            // Given
            Map<String, Object> rawParam = new HashMap<>();
            rawParam.put("name", "testParam");
            rawParam.put("type", "string");
            rawParam.put("displayName", "测试参数");
            rawParam.put("isRequired", true);
            rawParam.put("isVisible", false);

            // When
            AppInputParam result = AppInputParam.from(rawParam);

            // Then
            assertAll(() -> assertEquals("testParam", result.getName()),
                    () -> assertEquals("string", result.getType()),
                    () -> assertEquals("测试参数", result.getDisplayName()),
                    () -> assertTrue(result.isRequired()),
                    () -> assertFalse(result.isVisible()),
                    () -> assertNotNull(result.getValidator()));
        }

        @Test
        @DisplayName("应该使用默认值创建 AppInputParam 对象")
        void shouldCreateAppInputParamWithDefaults() {
            // Given
            Map<String, Object> rawParam = new HashMap<>();
            rawParam.put("name", "testParam");
            rawParam.put("type", "string");

            // When
            AppInputParam result = AppInputParam.from(rawParam);

            // Then
            assertAll(() -> assertTrue(result.isRequired()),
                    () -> assertTrue(result.isVisible()),
                    () -> assertNull(result.getStringMaxLength()));
        }

        @Test
        @DisplayName("当 rawParam 为 null 时应该抛出异常")
        void shouldThrowExceptionWhenRawParamIsNull() {
            // When & Then
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> AppInputParam.from(null));
            assertEquals("rawParam cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("应该正确解析 input 类型的外观配置")
        void shouldParseInputAppearanceConfig() {
            // Given
            Map<String, Object> appearance = new HashMap<>();
            appearance.put("displayType", "input");
            appearance.put("maxLength", 100);

            Map<String, Object> rawParam = new HashMap<>();
            rawParam.put("name", "inputParam");
            rawParam.put("type", "string");
            rawParam.put("appearance", appearance);

            // When
            AppInputParam result = AppInputParam.from(rawParam);

            // Then
            assertEquals(100, result.getStringMaxLength());
        }

        @Test
        @DisplayName("应该正确解析 number 类型的外观配置")
        void shouldParseNumberAppearanceConfig() {
            // Given
            Map<String, Object> appearance = new HashMap<>();
            appearance.put("displayType", "number");
            appearance.put("minValue", "10.5");
            appearance.put("maxValue", "100.99");

            Map<String, Object> rawParam = new HashMap<>();
            rawParam.put("name", "numberParam");
            rawParam.put("type", "number");
            rawParam.put("appearance", appearance);

            // When
            AppInputParam result = AppInputParam.from(rawParam);

            // Then
            assertNotNull(result.getValidator());
        }

        @Test
        @DisplayName("应该正确解析 dropdown 类型的外观配置")
        void shouldParseDropdownAppearanceConfig() {
            // Given
            Map<String, Object> appearance = new HashMap<>();
            appearance.put("displayType", "dropdown");
            appearance.put("options", Arrays.asList("option1", "option2", "option3"));

            Map<String, Object> rawParam = new HashMap<>();
            rawParam.put("name", "dropdownParam");
            rawParam.put("type", "string");
            rawParam.put("appearance", appearance);

            // When
            AppInputParam result = AppInputParam.from(rawParam);

            // Then
            assertNotNull(result.getValidator());
        }
    }

    @Nested
    @DisplayName("validate() 方法测试")
    class ValidateMethodTest {
        private AppInputParam requiredParam;
        private AppInputParam optionalParam;

        @BeforeEach
        void setUp() {
            requiredParam = AppInputParam.builder()
                    .name("requiredParam")
                    .displayName("必填参数")
                    .type("string")
                    .isRequired(true)
                    .validator(value -> value instanceof String && ((String) value).length() <= 100)
                    .build();

            optionalParam = AppInputParam.builder()
                    .name("optionalParam")
                    .displayName("可选参数")
                    .type("string")
                    .isRequired(false)
                    .validator(value -> value instanceof String)
                    .build();
        }

        @Test
        @DisplayName("当 dataMap 为 null 时应该抛出异常")
        void shouldThrowExceptionWhenDataMapIsNull() {
            // When & Then
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> requiredParam.validate(null));
            assertEquals("dataMap cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("必填参数为 null 时应该抛出 AippParamException")
        void shouldThrowAippParamExceptionWhenRequiredParamIsNull() {
            // Given
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("requiredParam", null);

            // When & Then
            AippParamException exception =
                    assertThrows(AippParamException.class, () -> requiredParam.validate(dataMap));
            // 这里假设异常信息包含参数展示名
            assertTrue(exception.getMessage().contains("必填参数") || exception.getLocalizedMessage()
                    .contains("必填参数"));
        }

        @Test
        @DisplayName("必填参数不存在时应该抛出 AippParamException")
        void shouldThrowAippParamExceptionWhenRequiredParamNotExists() {
            // Given
            Map<String, Object> dataMap = new HashMap<>();
            // 不添加 requiredParam

            // When & Then
            assertThrows(AippParamException.class, () -> requiredParam.validate(dataMap));
        }

        @Test
        @DisplayName("可选参数为 null 时应该通过校验")
        void shouldPassValidationWhenOptionalParamIsNull() {
            // Given
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("optionalParam", null);

            // When & Then
            assertDoesNotThrow(() -> optionalParam.validate(dataMap));
        }

        @Test
        @DisplayName("参数值通过校验器校验时应该成功")
        void shouldPassWhenValuePassesValidator() {
            // Given
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("requiredParam", "valid string");

            // When & Then
            assertDoesNotThrow(() -> requiredParam.validate(dataMap));
        }

        @Test
        @DisplayName("参数值未通过校验器校验时应该抛出异常")
        void shouldThrowExceptionWhenValueFailsValidator() {
            // Given
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("requiredParam", 123); // 数字类型，但校验器期望字符串

            // When & Then
            assertThrows(AippParamException.class, () -> requiredParam.validate(dataMap));
        }
    }

    @Nested
    @DisplayName("校验器功能测试")
    class ValidatorTest {
        @Nested
        @DisplayName("字符串校验器测试")
        class StringValidatorTest {
            private AppInputParam stringParam;

            @BeforeEach
            void setUp() {
                Map<String, Object> appearance = new HashMap<>();
                appearance.put("displayType", "input");
                appearance.put("maxLength", 10);

                Map<String, Object> rawParam = new HashMap<>();
                rawParam.put("name", "stringParam");
                rawParam.put("type", "string");
                rawParam.put("appearance", appearance);
                rawParam.put("isRequired", true);

                this.stringParam = AppInputParam.from(rawParam);
            }

            @ParameterizedTest
            @ValueSource(strings = {"", "hello", "1234567890"})
            @DisplayName("有效字符串应该通过校验")
            void shouldPassValidStrings(String value) {
                // Given
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("stringParam", value);

                // When & Then
                assertDoesNotThrow(() -> this.stringParam.validate(dataMap));
            }

            @ParameterizedTest
            @ValueSource(strings = {"12345678901", "this is too long"})
            @DisplayName("超长字符串应该校验失败")
            void shouldFailTooLongStrings(String value) {
                // Given
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("stringParam", value);

                // When & Then
                assertThrows(AippParamException.class, () -> this.stringParam.validate(dataMap));
            }

            @Test
            @DisplayName("非字符串类型应该校验失败")
            void shouldFailNonStringTypes() {
                // Given
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("stringParam", 123);

                // When & Then
                assertThrows(AippParamException.class, () -> this.stringParam.validate(dataMap));
            }

            @Test
            @DisplayName("没有设置最大长度可以校验成功")
            void shouldSuccessWithoutMaxLength() {
                // Given
                Map<String, Object> appearance = new HashMap<>();
                appearance.put("displayType", "input");

                Map<String, Object> rawParam = new HashMap<>();
                rawParam.put("name", "stringParam");
                rawParam.put("type", "String");
                rawParam.put("appearance", appearance);
                AppInputParam param = AppInputParam.from(rawParam);

                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("stringParam", "test");

                // When & Then
                assertDoesNotThrow(() -> param.validate(dataMap));
            }
        }

        @Nested
        @DisplayName("数字校验器测试")
        class NumberValidatorTest {
            private AppInputParam numberParam;

            @BeforeEach
            void setUp() {
                Map<String, Object> appearance = new HashMap<>();
                appearance.put("displayType", "number");
                appearance.put("minValue", "10");
                appearance.put("maxValue", "100");

                Map<String, Object> rawParam = new HashMap<>();
                rawParam.put("name", "numberParam");
                rawParam.put("type", "number");
                rawParam.put("appearance", appearance);
                rawParam.put("isRequired", true);

                this.numberParam = AppInputParam.from(rawParam);
            }

            @ParameterizedTest
            @ValueSource(doubles = {10.0, 50.5, 100.0})
            @DisplayName("范围内的数字应该通过校验")
            void shouldPassValidNumbers(double value) {
                // Given
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("numberParam", value);

                // When & Then
                assertDoesNotThrow(() -> this.numberParam.validate(dataMap));
            }

            @ParameterizedTest
            @ValueSource(doubles = {9.9, 100.1, -5, 200})
            @DisplayName("超出范围的数字应该校验失败")
            void shouldFailOutOfRangeNumbers(double value) {
                // Given
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("numberParam", value);

                // When & Then
                assertThrows(AippParamException.class, () -> this.numberParam.validate(dataMap));
            }

            @Test
            @DisplayName("非数字类型应该校验失败")
            void shouldFailNonNumberTypes() {
                // Given
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("numberParam", "not a number");

                // When & Then
                assertThrows(AippParamException.class, () -> this.numberParam.validate(dataMap));
            }

            @Test
            @DisplayName("没有设置数字范围可以校验成功")
            void shouldSuccessWithoutMaxLength() {
                // Given
                Map<String, Object> appearance = new HashMap<>();
                appearance.put("displayType", "number");

                Map<String, Object> rawParam = new HashMap<>();
                rawParam.put("name", "numberParam");
                rawParam.put("type", "Number");
                rawParam.put("appearance", appearance);
                AppInputParam param = AppInputParam.from(rawParam);

                // When
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("numberParam", -1.2);

                // then
                assertDoesNotThrow(() -> param.validate(dataMap));
            }

        }

        @Nested
        @DisplayName("下拉框校验器测试")
        class DropdownValidatorTest {
            private AppInputParam dropdownParam;

            @BeforeEach
            void setUp() {
                Map<String, Object> appearance = new HashMap<>();
                appearance.put("displayType", "dropdown");
                appearance.put("options", Arrays.asList("option1", "option2", "option3"));

                Map<String, Object> rawParam = new HashMap<>();
                rawParam.put("name", "dropdownParam");
                rawParam.put("type", "string");
                rawParam.put("appearance", appearance);
                rawParam.put("isRequired", true);

                this.dropdownParam = AppInputParam.from(rawParam);
            }

            @ParameterizedTest
            @ValueSource(strings = {"option1", "option2", "option3"})
            @DisplayName("有效选项应该通过校验")
            void shouldPassValidOptions(String value) {
                // Given
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("dropdownParam", value);

                // When & Then
                assertDoesNotThrow(() -> this.dropdownParam.validate(dataMap));
            }

            @ParameterizedTest
            @ValueSource(strings = {"option4", "invalid", ""})
            @DisplayName("无效选项应该校验失败")
            void shouldFailInvalidOptions(String value) {
                // Given
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("dropdownParam", value);

                // When & Then
                assertThrows(AippParamException.class, () -> this.dropdownParam.validate(dataMap));
            }
        }

        @Nested
        @DisplayName("数组校验器测试")
        class ArrayValidatorTest {
            private AppInputParam arrayParam;
            private List<String> options;

            @BeforeEach
            void setUp() {
                this.options = Arrays.asList("option1", "option2", "option3", "option4");

                Map<String, Object> appearance = new HashMap<>();
                appearance.put("displayType", "multiselect");
                appearance.put("options", this.options);

                Map<String, Object> rawParam = new HashMap<>();
                rawParam.put("name", "arrayParam");
                rawParam.put("type", "Array");
                rawParam.put("appearance", appearance);
                rawParam.put("isRequired", true);

                this.arrayParam = AppInputParam.from(rawParam);
            }

            @ParameterizedTest
            @MethodSource("validArrayValues")
            @DisplayName("有效数组值应该通过校验")
            void shouldPassValidArrayValues(List<String> value) {
                // Given
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("arrayParam", value);

                // When & Then
                assertDoesNotThrow(() -> this.arrayParam.validate(dataMap));
            }

            private static Stream<Arguments> validArrayValues() {
                return Stream.of(
                        Arguments.of(Arrays.asList("option1")),
                        Arguments.of(Arrays.asList("option1", "option2")),
                        Arguments.of(Arrays.asList("option3", "option4")),
                        Arguments.of(Arrays.asList("option1", "option2", "option3", "option4")),
                        Arguments.of(Collections.emptyList())
                );
            }

            @ParameterizedTest
            @MethodSource("invalidArrayValues")
            @DisplayName("无效数组值应该校验失败")
            void shouldFailInvalidArrayValues(List<String> value) {
                // Given
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("arrayParam", value);

                // When & Then
                assertThrows(AippParamException.class, () -> this.arrayParam.validate(dataMap));
            }

            private static Stream<Arguments> invalidArrayValues() {
                return Stream.of(
                        Arguments.of(Arrays.asList("invalid")),
                        Arguments.of(Arrays.asList("option1", "invalid")),
                        Arguments.of(Arrays.asList("option1", "option5")),
                        Arguments.of(Arrays.asList("", "option1")),
                        Arguments.of(Arrays.asList("option1", null))
                );
            }

            @Test
            @DisplayName("空选项列表应该校验失败")
            void shouldFailWhenOptionsEmpty() {
                // Given
                Map<String, Object> appearance = new HashMap<>();
                appearance.put("displayType", "multiselect");
                appearance.put("options", Collections.emptyList());

                Map<String, Object> rawParam = new HashMap<>();
                rawParam.put("name", "emptyOptionsParam");
                rawParam.put("type", "Array[String]");
                rawParam.put("appearance", appearance);

                AppInputParam emptyOptionsParam = AppInputParam.from(rawParam);
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("emptyOptionsParam", Arrays.asList("option1"));

                // When & Then
                assertThrows(AippParamException.class, () -> emptyOptionsParam.validate(dataMap));
            }

            @Test
            @DisplayName("null值应该校验失败")
            void shouldFailWhenValueIsNull() {
                // Given
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("arrayParam", null);

                // When & Then
                assertThrows(AippParamException.class, () -> this.arrayParam.validate(dataMap));
            }

            @Test
            @DisplayName("非集合类型应该校验失败")
            void shouldFailWhenValueIsNotCollection() {
                // Given
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("arrayParam", "option1");

                // When & Then
                assertThrows(AippParamException.class, () -> this.arrayParam.validate(dataMap));
            }
        }

        @Nested
        @DisplayName("布尔校验器测试")
        class BooleanValidatorTest {
            private AppInputParam booleanParam;

            @BeforeEach
            void setUp() {
                Map<String, Object> appearance = new HashMap<>();
                appearance.put("displayType", "switch");

                Map<String, Object> rawParam = new HashMap<>();
                rawParam.put("name", "booleanParam");
                rawParam.put("type", "boolean");
                rawParam.put("appearance", appearance);
                rawParam.put("isRequired", true);

                this.booleanParam = AppInputParam.from(rawParam);
            }

            @ParameterizedTest
            @ValueSource(booleans = {true, false})
            @DisplayName("布尔值应该通过校验")
            void shouldPassBooleanValues(boolean value) {
                // Given
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("booleanParam", value);

                // When & Then
                assertDoesNotThrow(() -> this.booleanParam.validate(dataMap));
            }

            @ParameterizedTest
            @ValueSource(strings = {"true", "false", "1", "0"})
            @DisplayName("字符串类型应该校验失败")
            void shouldFailStringValues(String value) {
                // Given
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("booleanParam", value);

                // When & Then
                assertThrows(AippParamException.class, () -> this.booleanParam.validate(dataMap));
            }
        }

        @Nested
        @DisplayName("默认校验器测试")
        class DefaultValidatorTest {
            @Test
            @DisplayName("string 类型应该只接受字符串")
            void shouldAcceptOnlyStringForStringType() {
                // Given
                Map<String, Object> rawParam = new HashMap<>();
                rawParam.put("name", "stringParam");
                rawParam.put("type", "String");
                rawParam.put("isRequired", true);

                AppInputParam param = AppInputParam.from(rawParam);

                // When & Then - 有效字符串
                Map<String, Object> validData = new HashMap<>();
                validData.put("stringParam", "valid string");
                assertDoesNotThrow(() -> param.validate(validData));

                // When & Then - 无效类型
                Map<String, Object> invalidData = new HashMap<>();
                invalidData.put("stringParam", 123);
                assertThrows(AippParamException.class, () -> param.validate(invalidData));
            }

            @Test
            @DisplayName("Integer 类型应该只接受整数")
            void shouldAcceptOnlyIntegerForNumberType() {
                // Given
                Map<String, Object> rawParam = new HashMap<>();
                rawParam.put("name", "numberParam");
                rawParam.put("type", "Integer");
                rawParam.put("isRequired", true);

                AppInputParam param = AppInputParam.from(rawParam);

                // When & Then - 有效整数
                Map<String, Object> validData = new HashMap<>();
                validData.put("numberParam", 123);
                assertDoesNotThrow(() -> param.validate(validData));

                // When & Then - 无效类型（字符串）
                Map<String, Object> invalidData1 = new HashMap<>();
                invalidData1.put("numberParam", "123");
                assertThrows(AippParamException.class, () -> param.validate(invalidData1));

                // When & Then - 无效类型（浮点数）
                Map<String, Object> invalidData2 = new HashMap<>();
                invalidData2.put("numberParam", 123.45);
                assertThrows(AippParamException.class, () -> param.validate(invalidData2));
            }

            @Test
            @DisplayName("number 类型应该只接受数字类型")
            void shouldAcceptOnlyFloatForDecimalType() {
                // Given
                Map<String, Object> rawParam = new HashMap<>();
                rawParam.put("name", "decimalParam");
                rawParam.put("type", "Number");
                rawParam.put("isRequired", true);

                AppInputParam param = AppInputParam.from(rawParam);

                // When & Then - 有效浮点数
                Map<String, Object> validData = new HashMap<>();
                validData.put("decimalParam", 123.45);
                assertDoesNotThrow(() -> param.validate(validData));

                // When & Then - 有效整数
                Map<String, Object> invalidData = new HashMap<>();
                invalidData.put("decimalParam", 123);
                assertDoesNotThrow(() -> param.validate(invalidData));

                // When & Then - 超出默认范围的数字
                Map<String, Object> bigNumber = new HashMap<>();
                bigNumber.put("numberParam", 1000000000.0); //
                assertThrows(AippParamException.class, () -> param.validate(bigNumber));
            }

            @Test
            @DisplayName("Boolean 类型应该只接受布尔值")
            void shouldAcceptOnlyBooleanForBooleanType() {
                // Given
                Map<String, Object> rawParam = new HashMap<>();
                rawParam.put("name", "boolParam");
                rawParam.put("type", "Boolean");
                rawParam.put("isRequired", true);

                AppInputParam param = AppInputParam.from(rawParam);

                // When & Then - 有效布尔值
                Map<String, Object> validData = new HashMap<>();
                validData.put("boolParam", true);
                assertDoesNotThrow(() -> param.validate(validData));

                // When & Then - 无效类型
                Map<String, Object> invalidData = new HashMap<>();
                invalidData.put("boolParam", "true");
                assertThrows(AippParamException.class, () -> param.validate(invalidData));
            }

            @Test
            @DisplayName("未知类型不进行校验")
            void shouldFallbackToTypeInferenceForUnknownTypes() {
                // Given
                Map<String, Object> rawParam = new HashMap<>();
                rawParam.put("name", "unknownParam");
                rawParam.put("type", "unknown");
                rawParam.put("isRequired", true);

                AppInputParam param = AppInputParam.from(rawParam);

                // When & Then
                Map<String, Object> stringData = new HashMap<>();
                stringData.put("unknownParam", "test");
                assertDoesNotThrow(() -> param.validate(stringData));
            }

            @Test
            @DisplayName("空字符串应该被拒绝")
            void shouldRejectEmptyString() {
                // Given
                Map<String, Object> rawParam = new HashMap<>();
                rawParam.put("name", "stringParam");
                rawParam.put("type", "String");
                rawParam.put("isRequired", true);

                AppInputParam param = AppInputParam.from(rawParam);

                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("stringParam", "");

                // When & Then
                assertThrows(AippParamException.class, () -> param.validate(dataMap));
            }

            @Test
            @DisplayName("null 类型应该导致校验失败")
            void shouldFailForNullType() {
                // Given
                Map<String, Object> rawParam = new HashMap<>();
                rawParam.put("name", "nullTypeParam");
                rawParam.put("type", null);
                rawParam.put("isRequired", true);

                AppInputParam param = AppInputParam.from(rawParam);

                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("nullTypeParam", "anything");

                // When & Then
                assertThrows(AippParamException.class, () -> param.validate(dataMap));
            }
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class EdgeCaseTest {
        @Test
        @DisplayName("无效的数字格式应该被正确处理")
        void shouldHandleInvalidNumberFormat() {
            // Given
            Map<String, Object> appearance = new HashMap<>();
            appearance.put("displayType", "number");
            appearance.put("minValue", "invalid");
            appearance.put("maxValue", "also_invalid");

            Map<String, Object> rawParam = new HashMap<>();
            rawParam.put("name", "numberParam");
            rawParam.put("type", "number");
            rawParam.put("appearance", appearance);

            // When & Then
            assertDoesNotThrow(() -> AppInputParam.from(rawParam));
        }

        @Test
        @DisplayName("空的选项列表应该被正确处理")
        void shouldHandleEmptyOptionsList() {
            // Given
            Map<String, Object> appearance = new HashMap<>();
            appearance.put("displayType", "dropdown");
            appearance.put("options", Collections.emptyList());

            Map<String, Object> rawParam = new HashMap<>();
            rawParam.put("name", "dropdownParam");
            rawParam.put("type", "string");
            rawParam.put("appearance", appearance);
            rawParam.put("isRequired", true);

            AppInputParam param = AppInputParam.from(rawParam);

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("dropdownParam", "anything");

            // When & Then
            assertThrows(AippParamException.class, () -> param.validate(dataMap));
        }
    }
}