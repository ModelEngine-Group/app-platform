/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.enums;

import modelengine.fitframework.inspection.Nonnull;

/**
 * 检索过滤参数的枚举类。
 *
 * @author 刘信宏
 * @since 2024-09-24
 */
public enum FilterType implements KnowledgeRetrievalParam {
    /**
     * 引用上限，最大召回知识条数。
     */
    REFERENCE_TOP_K(ReferenceType.TOP_K.value()),

    /**
     * 引用上限，最大召回 token 数。
     */
    REFERENCE_TOP_TOKEN(ReferenceType.TOP_TOKEN.value()),

    /**
     * 相似度阈值。
     */
    SIMILARITY_THRESHOLD("similarityThreshold");

    private final String value;

    FilterType(String value) {
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
