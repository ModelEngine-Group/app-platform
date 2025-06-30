/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.enums;

import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippParamException;

import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;

/**
 * 表单属性类型枚举类
 *
 * @author 邬涨财
 * @since 2024-04-27
 */
public enum FormPropertyTypeEnum {
    INTEGER("Integer", Integer.class),
    STRING("String", String.class),
    BOOLEAN("Boolean", Boolean.class),
    NUMBER("Number", Number.class),
    FLOAT("Float", Float.class),
    BYTE("Byte", Byte.class),
    DOUBLE("Double", Double.class),
    CHARACTER("Character", Character.class),
    OBJECT("Object", Object.class),
    MAP("Map", java.util.Map.class),
    LIST("List", java.util.List.class);

    private static final Logger LOGGER = Logger.get(FormPropertyTypeEnum.class);

    private final String code;
    private final Class<?> clazz;

    FormPropertyTypeEnum(String code, Class<?> clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    public String code() {
        return this.code;
    }

    public Class<?> clazz() {
        return this.clazz;
    }

    public static Class<?> getClazz(String code) {
        return Arrays.stream(values())
                .filter(item -> StringUtils.startsWithIgnoreCase(code, item.code()))
                .findFirst()
                .orElseThrow(() -> {
                    LOGGER.error("The form property type is invalid. [code={}]", code);
                    return new AippParamException(AippErrCode.FORM_PROPERTY_TYPE_IS_INVALID);
                })
                .clazz();
    }
}
