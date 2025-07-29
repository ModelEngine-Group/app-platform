/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.enums;

import modelengine.fitframework.inspection.Nonnull;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;

/**
 * 检索方式枚举类。
 *
 * @author 刘信宏
 * @since 2024-10-08
 */
public enum IndexType implements KnowledgeRetrievalParam {
    /**
     * 语义检索。
     */
    SEMANTIC("semantic"),

    /**
     * 全文检索。
     */
    FULL_TEXT("fullText"),

    /**
     * 混合检索。
     */
    HYBRID("hybrid");

    private final String value;

    IndexType(String value) {
        this.value = value;
    }

    /**
     * 通过字符串构造枚举对象 {@link IndexType}。
     *
     * @param value 表示检索类型的 {@link String}。
     * @return 表示检索方式枚举对象的 {@link IndexType}。
     */
    public static IndexType from(String value) {
        return Arrays.stream(values())
                .filter(item -> item.value().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(StringUtils.format("Unsupported value: [{0}]", value)));
    }

    /**
     * 获取过滤参数的标识。
     *
     * @return 表示过滤参数标识的 {@link String}。
     */
    @Nonnull
    @Override
    public String value() {
        return this.value;
    }
}
