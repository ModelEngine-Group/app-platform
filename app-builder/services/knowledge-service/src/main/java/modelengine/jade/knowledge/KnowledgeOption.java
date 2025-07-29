/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import modelengine.jade.knowledge.enums.IndexType;
import modelengine.jade.knowledge.support.FlatKnowledgeOption;

import modelengine.fitframework.inspection.Nonnull;

import java.util.List;

/**
 * 知识检索参数实体。
 *
 * @author 刘信宏
 * @since 2024-09-23
 */
public interface KnowledgeOption {
    /**
     * 获取检索的用户问题。
     *
     * @return 表示用户问题的 {@link String}。
     */
    @Nonnull
    String query();

    /**
     * 获取检索的知识库标识列表。
     *
     * @return 表示知识库标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    @Nonnull
    List<String> repoIds();

    /**
     * 获取检索方式。
     *
     * @return 表示检索方式的 {@link String}。
     */
    String indexType();

    /**
     * 获取检索相似度阈值。
     *
     * @return 表示相似度阈值的 {@link Float}。
     */
    Float similarityThreshold();

    /**
     * 获取引用上限。
     *
     * @return 表示引用上限的 {@link ReferenceLimit}。
     */
    ReferenceLimit referenceLimit();

    /**
     * {@link KnowledgeOption} 的构建器。
     */
    interface Builder {
        /**
         * 设置检索的用户问题。
         *
         * @param query 表示用户问题的 {@link String}。
         * @return 表示当前构建器的 {@link FilterConfig.Builder}。
         */
        Builder query(String query);

        /**
         * 设置知识库标识列表。
         *
         * @param repoIds 表示知识库标识列表的 {@link List}{@code <}{@link String}{@code >}。
         * @return 表示当前构建器的 {@link FilterConfig.Builder}。
         */
        Builder repoIds(List<String> repoIds);

        /**
         * 设置检索方式。
         *
         * @param indexType 表示检索方式的 {@link IndexType}。
         * @return 表示当前构建器的 {@link FilterConfig.Builder}。
         */
        Builder indexType(IndexType indexType);

        /**
         * 设置引用上限，如最大召回知识条数、最大召回 token 数。
         *
         * @param referenceLimit 表示引用上限的 {@link ReferenceLimit}。
         * @return 表示当前构建器的 {@link FilterConfig.Builder}。
         */
        Builder referenceLimit(ReferenceLimit referenceLimit);

        /**
         * 设置检索相似度阈值。
         *
         * @param similarityThreshold 表示检索相似度阈值的 {@link Float}。
         * @return 表示当前构建器的 {@link FilterConfig.Builder}。
         */
        Builder similarityThreshold(Float similarityThreshold);

        /**
         * 构建对象。
         *
         * @return 表示构建出来的对象的 {@link KnowledgeOption}。
         */
        KnowledgeOption build();
    }

    /**
     * 返回一个构建器，用以构建检索参数的新实例。
     *
     * @return 表示用以构建检索参数的构建器的 {@link KnowledgeOption.Builder}。
     */
    static Builder custom() {
        return new FlatKnowledgeOption.Builder();
    }
}
