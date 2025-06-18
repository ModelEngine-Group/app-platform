/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.rewrite.domain;

import static modelengine.fit.jade.aipp.rewrite.util.Constant.HISTORY_KEY;
import static modelengine.fit.jade.aipp.rewrite.util.Constant.QUERY_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.chat.support.AiMessage;
import modelengine.fit.jade.aipp.rewrite.domain.entity.RewriteStrategy;
import modelengine.fit.jade.aipp.rewrite.domain.entity.Rewriter;
import modelengine.fit.jade.aipp.rewrite.domain.factory.RewriterFactory;
import modelengine.fit.jade.aipp.rewrite.domain.vo.RewriteParam;
import modelengine.fit.jade.aipp.rewrite.infra.config.RewriteAutoConfig;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link Rewriter} 的测试集。
 *
 * @author 易文渊
 * @since 2024-09-23
 */
@DisplayName("测试 Rewriter 的实现")
@Disabled
public class RewriterTest {
    private static ChatModel modelService;
    private static RewriterFactory factory;

    @BeforeAll
    static void beforeAll() throws IOException {
        modelService = mock(ChatModel.class);
        factory =
                new RewriteAutoConfig().getRewriteFactory(modelService, new JacksonObjectSerializer(null, null, null, true));
    }

    @Nested
    @DisplayName("测试自定义重写算子")
    class Custom {
        private Rewriter customRewriter;

        @BeforeEach
        void setUp() {
            this.customRewriter = factory.create(RewriteStrategy.CUSTOM);
        }

        @Test
        @DisplayName("返回单个查询成功")
        void shouldOkWhenReturnSingleQuery() {
            when(modelService.generate(any(Prompt.class), any(ChatOption.class))).thenReturn(Choir.just(new AiMessage(
                    "123")));
            List<String> result = this.customRewriter.invoke(new RewriteParam("sky",
                    Collections.emptyMap(),
                    ChatOption.custom().build()));
            assertThat(result).hasSize(1).containsExactly("123");
        }
    }

    @Nested
    @DisplayName("测试内置重写算子")
    class Builtin {
        private Rewriter builtinRewriter;

        @BeforeEach
        void setUp() {
            this.builtinRewriter = factory.create(RewriteStrategy.BUILTIN);
        }

        List<String> doRewrite() {
            return this.builtinRewriter.invoke(new RewriteParam("{{sky}}",
                    MapBuilder.<String, String>get()
                            .put("sky", "fang")
                            .put(QUERY_KEY, "query")
                            .put(HISTORY_KEY, "")
                            .build(),
                    ChatOption.custom().build()));
        }

        @Test
        @DisplayName("测试模型正常返回")
        void shouldOkWhenRewriteNormal() {
            when(modelService.generate(any(Prompt.class), any(ChatOption.class))).thenReturn(Choir.just(new AiMessage(
                    "[\"1\", \"2\", \"3\"]")));
            assertThat(this.doRewrite()).hasSize(4).containsExactly("query", "1", "2", "3");
        }

        @Test
        @DisplayName("测试模型返回值异常")
        void shouldOkWhenRewriteFail() {
            when(modelService.generate(any(Prompt.class), any(ChatOption.class))).thenReturn(Choir.just(new AiMessage(
                    "123")));
            assertThat(this.doRewrite()).hasSize(1).containsExactly("query");
        }

        @Test
        @DisplayName("测试过滤重复问题")
        void shouldOkWhenFilterSameQuery() {
            when(modelService.generate(any(Prompt.class), any(ChatOption.class))).thenReturn(Choir.just(new AiMessage(
                    "[\"query!\", \"query \", \" query\"]")));
            assertThat(this.doRewrite()).hasSize(1).containsExactly("query");
        }
    }
}