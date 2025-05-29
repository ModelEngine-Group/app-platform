/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import static modelengine.fit.jober.aipp.common.exception.AippErrCode.INPUT_PARAM_IS_INVALID;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.StringUtils.lengthBetween;

import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.enums.InputParamType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 表示应用开始节点配置参数
 *
 * @author 孙怡菲
 * @since 2024-11-25
 */
@Data
@NoArgsConstructor
public class AppInputParam {
    private int stringMaxLength = 500;
    private Map<InputParamType, Predicate<Object>> paramTypePredicateMap;
    private String name;
    private String type;
    private String description;
    private boolean isRequired;
    private boolean isVisible;

    /**
     * 通过键值对构建一个 {@link AppInputParam} 对象.
     *
     * @param rawParam 原始参数.
     * @return {@link AppInputParam} 对象.
     */
    public static AppInputParam from(Map<String, Object> rawParam) {
        AppInputParam appInputParam = new AppInputParam();
        appInputParam.setName(ObjectUtils.cast(rawParam.get("name")));
        appInputParam.setType(ObjectUtils.cast(rawParam.get("type")));
        appInputParam.setDescription(ObjectUtils.cast(rawParam.get("description")));
        appInputParam.setRequired(ObjectUtils.cast(rawParam.getOrDefault("isRequired", true)));
        appInputParam.setVisible(ObjectUtils.cast(rawParam.getOrDefault("isVisible", true)));
        Integer stringMaxLength = cast(rawParam.getOrDefault("stringMaxLength", 500));
        appInputParam.setStringMaxLength(stringMaxLength);
        Map<InputParamType, Predicate<Object>> paramTypePredicateMap
                = MapBuilder.<InputParamType, Predicate<Object>>get()
                .put(InputParamType.STRING_TYPE,
                        v -> v instanceof String && lengthBetween(cast(v), 1, stringMaxLength, true, true))
                .put(InputParamType.BOOLEAN_TYPE, v -> v instanceof Boolean)
                .put(InputParamType.INTEGER_TYPE,
                        v -> v instanceof Integer && ObjectUtils.between((int) v, -999999999, 999999999))
                .put(InputParamType.NUMBER_TYPE, AppInputParam::isValidNumber)
                .build();
        appInputParam.setParamTypePredicateMap(paramTypePredicateMap);
        return appInputParam;
    }

    private static boolean isValidNumber(Object value) {
        if (!(value instanceof Number)) {
            return false;
        }
        BigDecimal numberValue = new BigDecimal(value.toString());
        if (numberValue.compareTo(new BigDecimal("-999999999.99")) < 0
                || numberValue.compareTo(new BigDecimal("999999999.99")) > 0) {
            return false;
        }
        int scale = numberValue.scale();
        return scale <= 2;
    }

    /**
     * 校验数据.
     *
     * @param dataMap 数据集合.
     */
    public void validate(Map<String, Object> dataMap) {
        String paramName = this.getName();
        if (this.isRequired()) {
            Validation.notNull(cast(dataMap.get(paramName)),
                    () -> new AippParamException(INPUT_PARAM_IS_INVALID, paramName));
        }
        if (dataMap.get(paramName) == null) {
            return;
        }

        Object v = dataMap.get(this.getName());
        Predicate<Object> predicate = paramTypePredicateMap.get(InputParamType.getParamType(this.getType()));
        if (!predicate.test(v)) {
            throw new AippParamException(INPUT_PARAM_IS_INVALID, paramName);
        }
    }
}
