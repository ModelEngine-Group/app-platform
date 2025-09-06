/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.jade.knowledge.KnowledgeOption;
import modelengine.jade.knowledge.ReferenceLimit;
import modelengine.jade.knowledge.enums.IndexType;
import modelengine.jade.knowledge.enums.ReferenceType;
import modelengine.jade.knowledge.support.FlatKnowledgeOption;

import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * {@link KnowledgeOption} 的测试。
 *
 * @author 刘信宏
 * @since 2024-09-23
 */
public class KnowledgeOptionTest {
    @Test
    void shouldOkWhenFilledWithRequiredOption() {
        FlatKnowledgeOption flatOption = new FlatKnowledgeOption(KnowledgeOption.custom().query("query")
                .repoIds(Collections.emptyList())
                .indexType(IndexType.SEMANTIC)
                .referenceLimit(new ReferenceLimit(ReferenceType.TOP_K, 1))
                .similarityThreshold(0.5F)
                .build());
        assertThat(flatOption).extracting(KnowledgeOption::query, KnowledgeOption::repoIds)
                .containsSequence("query", Collections.emptyList());
    }

    @Test
    void shouldFailedWhenFilledWithoutRequiredOption() {
        assertThatThrownBy(() -> KnowledgeOption.custom().query("query")
                .indexType(IndexType.SEMANTIC)
                .referenceLimit(new ReferenceLimit(ReferenceType.TOP_TOKEN, 1))
                .similarityThreshold(0.5F).build()).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> KnowledgeOption.custom()
                .repoIds(Collections.emptyList())
                .indexType(IndexType.SEMANTIC)
                .referenceLimit(new ReferenceLimit(ReferenceType.TOP_K, 1))
                .similarityThreshold(0.5F).build()).isInstanceOf(IllegalArgumentException.class);
    }
}
