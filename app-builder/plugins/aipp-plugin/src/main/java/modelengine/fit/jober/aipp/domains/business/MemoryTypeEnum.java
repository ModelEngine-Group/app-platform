/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.business;

import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * memory类型
 *
 * @author 张越
 * @since 2025/01/09
 */
public enum MemoryTypeEnum {
    /**
     * 通过对话轮数.
     */
    BY_CONVERSATION_TURN("ByConversationTurn"),

    /**
     * 自定义.
     */
    CUSTOMIZING("Customizing"),

    /**
     * 不使用memory.
     */
    NOT_USE_MEMORY("NotUseMemory"),

    /**
     * 用户选择.
     */
    USER_SELECT("UserSelect");

    private final String type;

    MemoryTypeEnum(String type) {
        this.type = type;
    }

    /**
     * 将字符串类型转换为枚举.
     *
     * @param type 字符串类型.
     * @return {@link Optional}{@code <}{@link MemoryTypeEnum}{@code >} 对象.
     */
    public static Optional<MemoryTypeEnum> getType(String type) {
        return Arrays.stream(values())
                .filter(item -> StringUtils.equalsIgnoreCase(type, item.type))
                .findFirst();
    }

    /**
     * 枚举的类型描述.
     *
     * @return {@link String} 类型描述.
     */
    public String type() {
        return this.type;
    }
}
