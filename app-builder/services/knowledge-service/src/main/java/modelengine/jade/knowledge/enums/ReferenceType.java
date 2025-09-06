/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.enums;

import modelengine.fitframework.inspection.Nonnull;

/**
 * 引用上限枚举类型。
 *
 * @author 刘信宏
 * @since 2024-10-09
 */
public enum ReferenceType implements KnowledgeRetrievalParam {
    /**
     * 引用上限，最大召回知识条数。
     */
    TOP_K("topK"),

    /**
     * 引用上限，最大召回 token 数。
     */
    TOP_TOKEN("topToken");

    private final String value;

    ReferenceType(String value) {
        this.value = value;
    }

    /**
     * 获取过滤参数的名称。
     *
     * @return 表示过滤名称的 {@link String}。
     */
    @Nonnull
    @Override
    public String value() {
        return this.value;
    }
}
