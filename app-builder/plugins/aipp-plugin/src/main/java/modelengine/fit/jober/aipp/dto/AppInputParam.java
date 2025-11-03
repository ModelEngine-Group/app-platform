/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import static modelengine.fit.jober.aipp.common.exception.AippErrCode.INPUT_PARAM_IS_INVALID;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.StringUtils.lengthBetween;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fitframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 表示应用开始节点配置参数
 *
 * @author 孙怡菲
 * @since 2024-11-25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppInputParam {
    private static final int DEFAULT_STRING_MAX_LENGTH = 500;
    private static final BigDecimal DEFAULT_MIN_NUMBER = new BigDecimal("-999999999.99");
    private static final BigDecimal DEFAULT_MAX_NUMBER = new BigDecimal("999999999.99");
    private static final int MAX_DECIMAL_PLACES = 2;

    private String name;
    private String type;
    private String displayName;
    private boolean isRequired;
    private boolean isVisible;
    private Integer  stringMaxLength;
    private Predicate<Object> validator;

    /**
     * 通过键值对构建一个 {@link AppInputParam} 对象
     *
     * @param rawParam 原始参数
     * @return {@link AppInputParam} 对象
     */
    public static AppInputParam from(Map<String, Object> rawParam) {
        if (rawParam == null) {
            throw new IllegalArgumentException("rawParam cannot be null");
        }

        AppearanceConfig appearance = AppearanceConfig.from(rawParam);
        String type = cast(rawParam.get("type"));

        return AppInputParam.builder()
                .name(cast(rawParam.get("name")))
                .type(type)
                .displayName(cast(rawParam.get("displayName")))
                .isRequired(cast(rawParam.getOrDefault("isRequired", true)))
                .isVisible(cast(rawParam.getOrDefault("isVisible", true)))
                .stringMaxLength(appearance.getStringMaxLength())
                .validator(createValidator(appearance, type, rawParam))
                .build();
    }

    /**
     * 校验数据
     *
     * @param dataMap 数据集合
     * @throws AippParamException 当参数无效时抛出
     */
    public void validate(Map<String, Object> dataMap) {
        if (dataMap == null) {
            throw new IllegalArgumentException("dataMap cannot be null");
        }

        Object value = dataMap.get(this.name);

        // 校验必填项
        if (this.isRequired && value == null) {
            throw new AippParamException(INPUT_PARAM_IS_INVALID, this.displayName);
        }

        // 如果值为null且非必填，则跳过后续校验
        if (value == null) {
            return;
        }

        // 使用预构建的校验器
        if (this.validator != null && !this.validator.test(value)) {
            throw new AippParamException(INPUT_PARAM_IS_INVALID, this.displayName);
        }
    }

    /**
     * 根据外观配置创建校验器
     */
    private static Predicate<Object> createValidator(AppearanceConfig config, String type, Map<String, Object> rawParam) {
        String displayType = config.getDisplayType();

        if (displayType == null) {
            return createDefaultValidator(type, rawParam);
        }

        return switch (displayType) {
            case "textInput" -> createStringValidator(config.getStringMaxLength());
            case "numberInput" -> createNumberValidator(config.getMinValue(), config.getMaxValue());
            case "dropdown" -> createDropdownValidator(config.getOptions());
            case "switch" -> createBooleanValidator();
            case "multiselect" -> createArrayValidator(config.getOptions());
            default -> createDefaultValidator(type, rawParam);
        };
    }

    private static Predicate<Object> createStringValidator(Integer maxLength) {
        return value -> {
            if (!(value instanceof String str)) {
                return false;
            }
            // 如果没有设置 maxLength，则不限制长度
            return maxLength == null || lengthBetween(str, 0, maxLength, true, true);
        };
    }

    private static Predicate<Object> createNumberValidator(BigDecimal minValue, BigDecimal maxValue) {
        return value -> {
            if (!(value instanceof Number)) {
                return false;
            }

            try {
                BigDecimal numberValue = new BigDecimal(value.toString());

                if (minValue != null && numberValue.compareTo(minValue) < 0) {
                    return false;
                }

                if (maxValue != null && numberValue.compareTo(maxValue) > 0) {
                    return false;
                }

                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        };
    }

    private static Predicate<Object> createDropdownValidator(List<String> options) {
        return value -> options != null &&
                !options.isEmpty() &&
                options.contains(String.valueOf(value));
    }

    private static Predicate<Object> createArrayValidator(List<String> options) {
        return value -> {
            if (options == null || options.isEmpty()) {
                return false;
            }

            if (!(value instanceof Collection<?> valueCollection)) {
                return false;
            }

            return valueCollection.stream()
                    .map(String::valueOf)
                    .allMatch(options::contains);
        };
    }

    private static Predicate<Object> createBooleanValidator() {
        return value -> value instanceof Boolean;
    }

    private static Predicate<Object> createDefaultValidator(String type, Map<String, Object> rawParam) {
        if (type == null) {
            return value -> false; // 如果类型未定义，则校验失败
        }

        return switch (type.toLowerCase()) {
            case "string" -> value -> {
                if (!(value instanceof String str)) {
                    return false;
                }
                return lengthBetween(str,
                        1,
                        cast(rawParam.getOrDefault("stringMaxLength", DEFAULT_STRING_MAX_LENGTH)),
                        true,
                        true);
            };

            case "integer"-> value -> {
                if (value instanceof Integer) {
                    return ObjectUtils.between((int) value, -999999999, 999999999);
                }
                return false;
            };

            case "number" -> value -> {
                if (value instanceof Number) {
                    return isValidDecimalNumber(value);
                }
                return false;
            };

            case "boolean" -> value -> value instanceof Boolean;

            default -> value -> true;
        };
    }

    private static boolean isValidDecimalNumber(Object value) {
        if (!(value instanceof Number)) {
            return false;
        }

        try {
            BigDecimal numberValue = new BigDecimal(value.toString());

            // 检查数值范围
            if (numberValue.compareTo(DEFAULT_MIN_NUMBER) < 0 ||
                    numberValue.compareTo(DEFAULT_MAX_NUMBER) > 0) {
                return false;
            }

            // 检查小数位数
            return numberValue.scale() <= MAX_DECIMAL_PLACES;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 外观配置内部类，用于封装外观相关参数
     */
    @Getter
    private static class AppearanceConfig {
        private final String displayType;
        private final Integer stringMaxLength;
        private final BigDecimal minValue;
        private final BigDecimal maxValue;
        private final List<String> options;

        private AppearanceConfig(String displayType, Integer stringMaxLength,
                BigDecimal minValue, BigDecimal maxValue, List<String> options) {
            this.displayType = displayType;
            this.stringMaxLength = stringMaxLength;
            this.minValue = minValue;
            this.maxValue = maxValue;
            this.options = options;
        }

        public static AppearanceConfig from(Map<String, Object> rawParam) {
            Map<String, Object> appearance = cast(rawParam.get("appearance"));

            if (appearance == null) {
                return new AppearanceConfig(null, null, null, null, null);
            }

            String displayType = cast(appearance.get("displayType"));
            Integer stringMaxLength = cast(appearance.get("maxLength"));

            BigDecimal minValue = parseNumber(appearance.get("minValue"));
            BigDecimal maxValue = parseNumber(appearance.get("maxValue"));
            List<String> options = cast(appearance.get("options"));

            return new AppearanceConfig(displayType, stringMaxLength, minValue, maxValue, options);
        }

        private static BigDecimal parseNumber(Object value) {
            if (value == null) {
                return null;
            }
            try {
                return new BigDecimal(value.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
}
