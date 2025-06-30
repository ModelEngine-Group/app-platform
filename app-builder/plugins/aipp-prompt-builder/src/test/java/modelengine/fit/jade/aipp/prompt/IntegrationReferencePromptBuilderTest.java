/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.prompt;

import static modelengine.fit.jade.aipp.prompt.constant.Constant.KNOWLEDGE_CONTEXT_KEY;
import static org.assertj.core.api.Assertions.assertThat;

import modelengine.jade.common.globalization.impl.LocaleServiceImpl;
import modelengine.jade.common.schema.validator.SchemaValidatorImpl;

import modelengine.fit.jade.aipp.prompt.builder.ReferencePromptBuilder;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.jade.common.schema.validator.SchemaValidatorImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * {@link ReferencePromptBuilder} 匹配方法的集成测试。
 *
 * @author 刘信宏
 * @since 2024-12-09
 */
@Nested
@FitTestWithJunit(includeClasses = {
        ReferencePromptBuilder.class, LocaleServiceImpl.class, SchemaValidatorImpl.class
})
class IntegrationReferencePromptBuilderTest {
    @Fit
    private PromptBuilder promptBuilder;
    private final ObjectSerializer jsonSerializer = new JacksonObjectSerializer(null, null, null, true);
    private UserAdvice defaultUserAdvice;

    @BeforeEach
    void setup() {
        Map<String, String> variables = MapBuilder.<String, String>get().put("key0", "value0").build();
        this.defaultUserAdvice = new UserAdvice("background", "template {{key0}}", variables);
    }

    @Test
    void shouldOkWhenMatchKnowledgeWithHybridEmptyItem() throws IOException {
        String content =
                IoUtils.content(IntegrationReferencePromptBuilderTest.class, "/knowledge_valid_with_hybrid_empty.json");
        Object knowledgeList = this.jsonSerializer.deserialize(content, List.class);
        Map<String, Object> context =
                MapBuilder.<String, Object>get().put(KNOWLEDGE_CONTEXT_KEY, knowledgeList).build();
        assertThat(this.promptBuilder.build(this.defaultUserAdvice, context)).isPresent();
    }

    @Test
    void shouldFailedWhenMatchKnowledgeWithAllEmptyItem() throws IOException {
        String content =
                IoUtils.content(IntegrationReferencePromptBuilderTest.class, "/knowledge_invalid_with_all_empty.json");
        Object knowledgeList = this.jsonSerializer.deserialize(content, List.class);
        Map<String, Object> context =
                MapBuilder.<String, Object>get().put(KNOWLEDGE_CONTEXT_KEY, knowledgeList).build();
        assertThat(this.promptBuilder.build(this.defaultUserAdvice, context)).isNotPresent();
    }
}
