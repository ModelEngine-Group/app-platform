/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.prompt;

import static modelengine.fit.jade.aipp.prompt.constant.Constant.KNOWLEDGE_CONTEXT_KEY;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import modelengine.jade.common.globalization.LocaleService;
import modelengine.jade.common.globalization.impl.LocaleServiceImpl;
import modelengine.jade.common.locale.LocaleUtil;
import modelengine.jade.common.schema.validator.SchemaValidatorImpl;

import modelengine.fit.jade.aipp.prompt.builder.CustomPromptBuilder;
import modelengine.fit.jade.aipp.prompt.builder.ReferencePromptBuilder;
import modelengine.fit.jade.aipp.prompt.chain.DefaultPromptBuilderChain;
import modelengine.fit.jade.aipp.prompt.repository.PromptBuilderChain;
import modelengine.fit.jade.aipp.prompt.util.ReferenceUtil;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ReflectionUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.common.schema.validator.SchemaValidatorImpl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * {@link DefaultPromptBuilderChain} 的测试。
 *
 * @author 刘信宏
 * @since 2024-12-04
 */
public class PromptBuilderChainTest {
    @Nested
    @FitTestWithJunit(includeClasses = {
            CustomPromptBuilder.class, ReferencePromptBuilder.class, DefaultPromptBuilderChain.class
    })
    class DefaultChainNormalTest {
        @Fit
        private PromptBuilderChain chain;

        @Mock
        private LocaleService localeService;

        @Test
        void shouldOkWhenCustomBuilderWithNormalUserAdvice() {
            Field field = ReflectionUtils.getDeclaredField(DefaultPromptBuilderChain.class, "chain");
            LazyLoader<List<PromptBuilder>> builders = cast(ReflectionUtils.getField(this.chain, field));
            Assertions.assertThat(builders.get()).hasSize(2)
                    .extracting(PromptBuilder::strategy)
                    .containsExactly(PromptStrategy.REFERENCE, PromptStrategy.CUSTOM);
        }
    }

    @Nested
    @FitTestWithJunit(includeClasses = {
            CustomPromptBuilder.class, ReferencePromptBuilder.class, LocaleServiceImpl.class,
            DefaultPromptBuilderChain.class, SchemaValidatorImpl.class
    })
    class IntegrationPromptBuilderTest {
        @Fit
        private PromptBuilderChain chain;
        private List<PromptBuilder> builders;

        private MockedStatic<LocaleUtil> localeUtilMock;
        private Map<String, Object> context;
        private final ObjectSerializer jsonSerializer = new JacksonObjectSerializer(null, null, null);
        private UserAdvice defaultUserAdvice;

        @BeforeEach
        void setup() throws IOException {
            this.localeUtilMock = mockStatic(LocaleUtil.class);

            Field field = ReflectionUtils.getDeclaredField(DefaultPromptBuilderChain.class, "chain");
            LazyLoader<List<PromptBuilder>> buildersLazy = cast(ReflectionUtils.getField(this.chain, field));
            this.builders = buildersLazy.get();
            Assertions.assertThat(this.builders).hasSize(2)
                    .extracting(PromptBuilder::strategy)
                    .containsExactly(PromptStrategy.REFERENCE, PromptStrategy.CUSTOM);

            String content = IoUtils.content(IntegrationPromptBuilderTest.class, "/knowledge_dedupe.json");
            Object knowledgeList = this.jsonSerializer.deserialize(content, List.class);
            this.context = MapBuilder.<String, Object>get().put(KNOWLEDGE_CONTEXT_KEY, knowledgeList).build();
            Map<String, String> variables = MapBuilder.<String, String>get().put("key0", "value0").build();
            this.defaultUserAdvice = new UserAdvice("background", "template {{key0}}", variables);
        }

        @AfterEach
        void tearDown() {
            this.localeUtilMock.close();
        }

        @Test
        void shouldOkWhenPromptBuilderWithLocaleZh() {
            this.localeUtilMock.when(LocaleUtil::getLocale).thenReturn(Locale.CHINA);

            PromptBuilder promptBuilder = this.builders.get(1);
            assertThat(promptBuilder.strategy()).isEqualTo(PromptStrategy.CUSTOM);
            Optional<PromptMessage> promptMessage = promptBuilder.build(this.defaultUserAdvice, Collections.emptyMap());
            Assertions.assertThat(promptMessage).isPresent();
            assertThat(promptMessage.get()).extracting(PromptMessage::getSystemMessage, PromptMessage::getHumanMessage)
                    .containsExactly("# 人设与回复逻辑\n\nbackground", "template value0");

            promptBuilder = this.builders.get(0);
            assertThat(promptBuilder.strategy()).isEqualTo(PromptStrategy.REFERENCE);
            promptMessage = promptBuilder.build(this.defaultUserAdvice, this.context);
            Assertions.assertThat(promptMessage).isPresent();
            List<String> refIds = ReferenceUtil.getReferenceIds(promptMessage.get());
            assertThat(refIds).hasSize(3);
            assertThat(promptMessage.get().getSystemMessage()).contains("# 人设与回复逻辑",
                    "<ref>引用ID</ref>",
                    StringUtils.format("参考文献:\n[{0}] text0\n[{1}] text1\n[{2}] text2\n",
                            refIds.get(0),
                            refIds.get(1),
                            refIds.get(2)));
        }

        @Test
        void shouldOkWhenPromptBuilderWithLocaleEn() {
            this.localeUtilMock.when(LocaleUtil::getLocale).thenReturn(Locale.ENGLISH);

            PromptBuilder promptBuilder = this.builders.get(1);
            assertThat(promptBuilder.strategy()).isEqualTo(PromptStrategy.CUSTOM);
            Optional<PromptMessage> promptMessage = promptBuilder.build(this.defaultUserAdvice, Collections.emptyMap());
            Assertions.assertThat(promptMessage).isPresent();
            assertThat(promptMessage.get()).extracting(PromptMessage::getSystemMessage, PromptMessage::getHumanMessage)
                    .containsExactly("# Personal setting and recovering Logic\n\nbackground", "template value0");

            promptBuilder = this.builders.get(0);
            assertThat(promptBuilder.strategy()).isEqualTo(PromptStrategy.REFERENCE);
            promptMessage = promptBuilder.build(this.defaultUserAdvice, this.context);
            Assertions.assertThat(promptMessage).isPresent();

            List<String> refIds = ReferenceUtil.getReferenceIds(promptMessage.get());
            assertThat(refIds).hasSize(3);
            assertThat(promptMessage.get().getSystemMessage()).contains("# Personal setting and recovering Logic",
                    "<ref>reference ID</ref>",
                    StringUtils.format("Reference:\n[{0}] text0\n[{1}] text1\n[{2}] text2\n",
                            refIds.get(0),
                            refIds.get(1),
                            refIds.get(2)));
        }

        @Test
        void shouldOkWhenPromptBuilderChainBuildWithNormalData() {
            this.localeUtilMock.when(LocaleUtil::getLocale).thenReturn(Locale.CHINA);

            Optional<PromptMessage> promptMessage = this.chain.build(this.defaultUserAdvice, Collections.emptyMap());
            Assertions.assertThat(promptMessage).isPresent();
            assertThat(promptMessage.get()).extracting(PromptMessage::getSystemMessage, PromptMessage::getHumanMessage)
                    .containsExactly("# 人设与回复逻辑\n\nbackground", "template value0");

            promptMessage = this.chain.build(this.defaultUserAdvice, this.context);
            Assertions.assertThat(promptMessage).isPresent();
            List<String> refIds = ReferenceUtil.getReferenceIds(promptMessage.get());
            assertThat(refIds).hasSize(3);
            assertThat(promptMessage.get().getSystemMessage()).contains("# 人设与回复逻辑",
                    "<ref>引用ID</ref>",
                    StringUtils.format("参考文献:\n[{0}] text0\n[{1}] text1\n[{2}] text2\n",
                            refIds.get(0),
                            refIds.get(1),
                            refIds.get(2)));
        }
    }
}
