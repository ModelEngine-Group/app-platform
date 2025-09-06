/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 大模型名称
 *
 * @author 刘信宏
 * @since 2023/12/26
 */
@Getter
public enum LlmModelNameEnum {
    QWEN_14B("Qwen-14B"),
    QWEN_72B("Qwen-72B"),
    QWEN_VL("qwen-vl"),
    QWEN2_VL("Qwen2-VL"),
    BAICHUAN_13B("BAICHUAN_13B"),
    UNKNOWN("UNKNOWN");

    private final String value;

    LlmModelNameEnum(String value) {
        this.value = value;
    }

    /**
     * 根据给定的值获取对应的大模型名称枚举
     *
     * @param value 大模型名称的字符串表示
     * @return LlmModelNameEnum
     */
    public static LlmModelNameEnum getLlmModelName(String value) {
        return Arrays.stream(values()).filter(item -> item.name().equalsIgnoreCase(value)).findFirst().orElse(UNKNOWN);
    }
}
