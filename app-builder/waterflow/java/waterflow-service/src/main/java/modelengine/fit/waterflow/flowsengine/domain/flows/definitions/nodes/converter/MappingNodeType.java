/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import static modelengine.fit.waterflow.ErrorCodes.ENUM_CONVERT_FAILED;

import lombok.Getter;
import modelengine.fit.waterflow.exceptions.WaterflowParamException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * 对应节点配置中的类型
 *
 * @author 宋永坦
 * @since 2024/4/18
 */
@Getter
public enum MappingNodeType {
    BOOLEAN("BOOLEAN"),
    INTEGER("INTEGER"),
    NUMBER("NUMBER"),
    STRING("STRING"),
    ARRAY("ARRAY"),
    OBJECT("OBJECT"),
    ;

    private static final Set<MappingNodeType> nestedTypes = new HashSet<>(Arrays.asList(ARRAY, OBJECT));

    private String code;

    MappingNodeType(String code) {
        this.code = code;
    }

    /**
     * 根据code获取枚举值
     *
     * @param code 枚举值对应的code
     * @return 枚举值
     * @throws WaterflowParamException 当找不到对应的枚举值时抛出
     */
    public static MappingNodeType get(String code) {
        return Arrays.stream(values())
                .filter(value -> value.getCode().equals(code.toUpperCase(Locale.ROOT)))
                .findFirst()
                .orElseThrow(() -> new WaterflowParamException(ENUM_CONVERT_FAILED, "ElementType", code));
    }

    /**
     * 判断是否是复杂类型
     *
     * @param type 目标类型
     * @return 是否是复杂类型
     */
    public static boolean isNestedType(MappingNodeType type) {
        return nestedTypes.contains(type);
    }
}
