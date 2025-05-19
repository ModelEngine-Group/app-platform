/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.enums;

import lombok.Getter;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;

/**
 * 输入参数类型的枚举
 *
 * @author 孙怡菲
 * @since 2024/12/16
 */
@Getter
public enum InputParamType {
    STRING_TYPE("String"),
    BOOLEAN_TYPE("Boolean"),
    INTEGER_TYPE("Integer"),
    NUMBER_TYPE("Number"),
    UNKNOWN_TYPE("Unknown");

    private final String type;

    InputParamType(String type) {
        this.type = type;
    }

    /**
     * 根据给定的type获取对应的入参类型枚举值
     *
     * @param type 参数type
     * @return InputParamType
     */
    public static InputParamType getParamType(String type) {
        return Arrays.stream(InputParamType.values())
            .filter(paramType -> StringUtils.equals(paramType.type, type))
            .findFirst()
            .orElse(InputParamType.UNKNOWN_TYPE);
    }
}
