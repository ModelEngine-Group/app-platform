/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.support;

import modelengine.jade.knowledge.KnowledgeOption;
import modelengine.jade.knowledge.ReferenceLimit;
import modelengine.jade.knowledge.enums.IndexType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelengine.fitframework.inspection.Validation;

import java.util.Collections;
import java.util.List;

/**
 * 可序列化的 {@link KnowledgeOption} 实现。
 *
 * @author 刘信宏
 * @since 2024-09-23
 */
@Getter
@Setter
@NoArgsConstructor
public class FlatKnowledgeOption implements KnowledgeOption {
    private String query;

    private List<String> repoIds;

    private String indexType;

    private ReferenceLimit referenceLimit;

    private Float similarityThreshold;

    /**
     * 使用 {@link KnowledgeOption} 初始化 {@link FlatKnowledgeOption} 对象。
     *
     * @param option 表示知识检索参数实体的 {@link KnowledgeOption}。
     */
    public FlatKnowledgeOption(KnowledgeOption option) {
        this.query = option.query();
        this.repoIds = option.repoIds();
        this.indexType = option.indexType();
        this.referenceLimit = option.referenceLimit();
        this.similarityThreshold = option.similarityThreshold();
    }

    FlatKnowledgeOption(FlatKnowledgeOption.Builder builder) {
        this.query = builder.query;
        this.repoIds = builder.repoIds;
        this.indexType = builder.indexType.value();
        this.referenceLimit = builder.referenceLimit;
        this.similarityThreshold = builder.similarityThreshold;
    }

    @Override
    public String query() {
        return this.query;
    }

    @Override
    public List<String> repoIds() {
        return Collections.unmodifiableList(this.repoIds);
    }

    @Override
    public String indexType() {
        return this.indexType;
    }

    @Override
    public Float similarityThreshold() {
        return this.similarityThreshold;
    }

    @Override
    public ReferenceLimit referenceLimit() {
        return this.referenceLimit;
    }

    /**
     * {@link FlatKnowledgeOption} 的构建器。
     */
    public static class Builder implements KnowledgeOption.Builder {
        private String query;

        private List<String> repoIds;

        private IndexType indexType;

        private ReferenceLimit referenceLimit;

        private Float similarityThreshold;

        @Override
        public KnowledgeOption.Builder query(String query) {
            this.query = query;
            return this;
        }

        @Override
        public KnowledgeOption.Builder repoIds(List<String> repoIds) {
            this.repoIds = repoIds;
            return this;
        }

        @Override
        public KnowledgeOption.Builder indexType(IndexType indexType) {
            this.indexType = indexType;
            return this;
        }

        @Override
        public KnowledgeOption.Builder referenceLimit(ReferenceLimit referenceLimit) {
            this.referenceLimit = referenceLimit;
            return this;
        }

        @Override
        public KnowledgeOption.Builder similarityThreshold(Float similarityThreshold) {
            this.similarityThreshold = similarityThreshold;
            return this;
        }

        @Override
        public KnowledgeOption build() {
            Validation.notNull(this.query, "The query cannot be null.");
            Validation.notNull(this.repoIds, "The knowledge repository ids cannot be null.");
            return new FlatKnowledgeOption(this);
        }
    }
}
