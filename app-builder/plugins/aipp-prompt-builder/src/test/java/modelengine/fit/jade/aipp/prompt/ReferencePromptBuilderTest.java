/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.prompt;

import static modelengine.fit.jade.aipp.prompt.code.PromptBuilderRetCode.PROMPT_BUILDER_KNOWLEDGE_CONTENT_LIMIT;
import static modelengine.fit.jade.aipp.prompt.code.PromptBuilderRetCode.PROMPT_BUILDER_KNOWLEDGE_COUNT_LIMIT;
import static modelengine.fit.jade.aipp.prompt.constant.Constant.KNOWLEDGE_CONTEXT_KEY;
import static modelengine.fit.jade.aipp.prompt.constant.Constant.PROMPT_METADATA_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.aipp.prompt.builder.ReferencePromptBuilder;
import modelengine.fit.jade.aipp.prompt.constant.InternalConstant;
import modelengine.fit.jade.aipp.prompt.util.ReferenceUtil;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.common.globalization.LocaleService;
import modelengine.jade.schema.SchemaValidator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * {@link ReferencePromptBuilder} 的测试。
 *
 * @author 刘信宏
 * @since 2024-12-04
 */
@FitTestWithJunit(includeClasses = {ReferencePromptBuilder.class})
public class ReferencePromptBuilderTest {
    @Fit
    private PromptBuilder promptBuilder;
    private final ObjectSerializer jsonSerializer = new JacksonObjectSerializer(null, null, null);

    @Mock
    private LocaleService localeService;
    @Mock
    private SchemaValidator validator;

    private Map<String, Object> context;
    private UserAdvice defaultUserAdvice;

    @BeforeEach
    void setup() throws IOException {
        assertThat(this.promptBuilder.strategy()).isEqualTo(PromptStrategy.REFERENCE);
        String content = IoUtils.content(ReferencePromptBuilderTest.class, "/knowledge_dedupe.json");
        Object knowledgeList = this.jsonSerializer.deserialize(content, List.class);
        this.context = MapBuilder.<String, Object>get().put(KNOWLEDGE_CONTEXT_KEY, knowledgeList).build();
        Map<String, String> variables = MapBuilder.<String, String>get().put("key0", "value0").build();
        this.defaultUserAdvice = new UserAdvice("background", "template {{key0}}", variables);
    }

    @Test
    void shouldOkWhenReferenceBuilderMatchWithNormalContext() {
        when(this.localeService.localize(eq(InternalConstant.BACKGROUND_KEY))).thenReturn("# 人设与回复逻辑");
        when(this.localeService.localize(eq(InternalConstant.TEMPLATE_LOCALE_KEY)))
                .thenReturn(InternalConstant.REFERENCE_TEMPLATE_ZH);

        assertThat(this.promptBuilder.build(this.defaultUserAdvice, this.context)).isPresent();
        List<List<?>> knowledgeListWithEmpty = ObjectUtils.cast(this.context.get(KNOWLEDGE_CONTEXT_KEY));
        knowledgeListWithEmpty.add(Collections.emptyList());
        Map<String, Object> newContext =
                MapBuilder.<String, Object>get().put(KNOWLEDGE_CONTEXT_KEY, knowledgeListWithEmpty).build();
        assertThat(this.promptBuilder.build(this.defaultUserAdvice, newContext)).isPresent();
    }

    @Test
    void shouldFailedWhenReferenceBuilderMatchWithInvalidContext() {
        List<List<List<Object>>> emptyKnowledge = Collections.singletonList(Collections.emptyList());
        assertThat(this.promptBuilder.build(this.defaultUserAdvice, null)).isNotPresent();
        assertThat(this.promptBuilder.build(this.defaultUserAdvice, Collections.emptyMap())).isNotPresent();
        Map<String, Object> newContext =
                MapBuilder.<String, Object>get().put(KNOWLEDGE_CONTEXT_KEY, emptyKnowledge).build();
        assertThat(this.promptBuilder.build(this.defaultUserAdvice, newContext)).isNotPresent();
    }

    @Test
    void shouldOkWhenReferenceBuilderWithNormalUserAdviceInLocaleZh() {
        when(this.localeService.localize(eq(InternalConstant.BACKGROUND_KEY))).thenReturn("# 人设与回复逻辑");
        when(this.localeService.localize(eq(InternalConstant.TEMPLATE_LOCALE_KEY)))
                .thenReturn(InternalConstant.REFERENCE_TEMPLATE_ZH);

        Optional<PromptMessage> promptOptional = this.promptBuilder.build(this.defaultUserAdvice, this.context);
        Assertions.assertThat(promptOptional).isPresent();
        PromptMessage promptMessage = promptOptional.get();
        List<String> refIds = ReferenceUtil.getReferenceIds(promptMessage);
        assertThat(refIds).hasSize(3);
        assertThat(promptMessage.getSystemMessage()).contains("# 人设与回复逻辑",
                "<ref>引用ID</ref>",
                StringUtils.format("参考文献:\n[{0}] text0\n[{1}] text1\n[{2}] text2\n",
                        refIds.get(0), refIds.get(1), refIds.get(2)));
        assertThat(promptMessage.getHumanMessage()).isEqualTo("template value0");
        assertThat(promptMessage.getMetadata()).containsKey(PROMPT_METADATA_KEY);
    }

    @Test
    void shouldOkWhenReferenceBuilderWithNormalUserAdviceInLocaleEn() {
        when(this.localeService.localize(eq(InternalConstant.BACKGROUND_KEY))).thenReturn(
                "# Personal setting and recovering Logic");
        when(this.localeService.localize(eq(InternalConstant.TEMPLATE_LOCALE_KEY)))
                .thenReturn(InternalConstant.REFERENCE_TEMPLATE_EN);

        Optional<PromptMessage> promptOptional = this.promptBuilder.build(this.defaultUserAdvice, this.context);
        Assertions.assertThat(promptOptional).isPresent();
        PromptMessage promptMessage = promptOptional.get();
        List<String> refIds = ReferenceUtil.getReferenceIds(promptMessage);
        assertThat(refIds).hasSize(3);
        assertThat(promptMessage.getSystemMessage()).contains("# Personal setting and recovering Logic",
                "<ref>reference ID</ref>",
                StringUtils.format("Reference:\n[{0}] text0\n[{1}] text1\n[{2}] text2\n",
                        refIds.get(0), refIds.get(1), refIds.get(2)));
        assertThat(promptMessage.getHumanMessage()).isEqualTo("template value0");
    }

    @Test
    void shouldFailWhenReferenceBuilderWithInvalidUserAdvice() {
        when(this.localeService.localize(eq(InternalConstant.BACKGROUND_KEY))).thenReturn("# 人设与回复逻辑");
        when(this.localeService.localize(eq(InternalConstant.TEMPLATE_LOCALE_KEY)))
                .thenReturn(InternalConstant.REFERENCE_TEMPLATE_ZH);

        assertThatThrownBy(() -> this.promptBuilder.build(new UserAdvice("background", "template {{key0}}", null),
                Collections.emptyMap())).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> this.promptBuilder.build(null, Collections.emptyMap())).isInstanceOf(
                IllegalArgumentException.class);

        Optional<PromptMessage> promptMessage =
                this.promptBuilder.build(new UserAdvice("background", "template {{key0}}", Collections.emptyMap()),
                        Collections.emptyMap());
        Assertions.assertThat(promptMessage).isNotPresent();
    }

    @Test
    void shouldFailWhenReferenceBuilderWithInvalidNodeCount() throws IOException {
        when(this.localeService.localize(eq(InternalConstant.TEMPLATE_LOCALE_KEY)))
                .thenReturn(InternalConstant.REFERENCE_TEMPLATE_EN);

        String content = IoUtils.content(ReferencePromptBuilderTest.class, "/knowledge_invalid_node_count.json");
        Object knowledgeList = this.jsonSerializer.deserialize(content, List.class);
        Map<String, Object> invalidContext =
                MapBuilder.<String, Object>get().put(KNOWLEDGE_CONTEXT_KEY, knowledgeList).build();
        assertThatThrownBy(() -> this.promptBuilder.build(this.defaultUserAdvice, invalidContext)).isInstanceOf(
                        ModelEngineException.class).extracting("code")
                .isEqualTo(PROMPT_BUILDER_KNOWLEDGE_COUNT_LIMIT.getCode());
    }

    @Test
    void shouldFailWhenReferenceBuilderWithInvalidContentLength() {
        when(this.localeService.localize(eq(InternalConstant.TEMPLATE_LOCALE_KEY)))
                .thenReturn(InternalConstant.REFERENCE_TEMPLATE_EN);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < InternalConstant.KNOWLEDGE_CONTENT_LIMIT + 1; i++) {
            sb.append('文');
        }
        Map<String, Object> knowledge =
                MapBuilder.<String, Object>get().put("id", "id").put("text", sb.toString()).put("score", 0.5).build();
        Object knowledgeList = Collections.singletonList(Collections.singletonList(knowledge));
        Map<String, Object> invalidContext =
                MapBuilder.<String, Object>get().put(KNOWLEDGE_CONTEXT_KEY, knowledgeList).build();
        assertThatThrownBy(() -> this.promptBuilder.build(this.defaultUserAdvice, invalidContext)).isInstanceOf(
                        ModelEngineException.class)
                .extracting("code")
                .isEqualTo(PROMPT_BUILDER_KNOWLEDGE_CONTENT_LIMIT.getCode());
    }
}
