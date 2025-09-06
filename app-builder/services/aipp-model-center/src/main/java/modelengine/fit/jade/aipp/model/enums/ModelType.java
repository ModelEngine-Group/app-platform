/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.enums;

import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;

/**
 * 表示模型类型的枚举类。
 *
 * @author 陈镕希
 * @since 2025-07-10
 */
public enum ModelType {
    /**
     * 对话模型。
     */
    CHAT_COMPLETIONS("chat_completions"),

    /**
     * 嵌入式模型。
     */
    EMBEDDINGS("embeddings"),

    /**
     * 重排序模型。
     */
    RERANK("rerank");

    private final String value;

    ModelType(String value) {
        this.value = value;
    }

    /**
     * 获取提示词构造策略名称。
     *
     * @return 表示提示词构造策略名称的 {@link String}。
     */
    public String value() {
        return this.value;
    }

    /**
     * 通过字符串构造枚举对象 {@link ModelType}。
     *
     * @param value 表示模型类型的 {@link String}。
     * @return 表示模型类型枚举对象的 {@link ModelType}。
     */
    public static ModelType from(String value) {
        return Arrays.stream(values())
                .filter(item -> item.value().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(StringUtils.format("Unsupported value: [{0}]", value)));
    }
}
