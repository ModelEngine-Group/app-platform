/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.telemetry.aop.parsers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.util.MapBuilder;
import modelengine.jade.carver.telemetry.aop.SpanAttrParser;
import modelengine.jade.carver.telemetry.aop.stub.CarverSpanObjectParse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.Map;

/**
 * {@link ComplexSpanAttrParser} 的测试。
 *
 * @author 马朝阳
 * @since 2024-07-30
 */
@FitTestWithJunit(includeClasses = {ComplexSpanAttrParser.class})
public class ComplexSpanAttrParserTest {
    private Object intObj;
    private Object kvObj;
    private Object obj;
    private Object listObj;

    @Fit
    private SpanAttrParser parser;

    @BeforeEach
    void setup() {
        this.intObj = 10;
        this.kvObj = MapBuilder.<String, Object>get()
                .put("k1", MapBuilder.<String, Object>get().put("k2", "v").build())
                .put("k11", MapBuilder.<String, Object>get().put("k22", "v").build())
                .build();
        this.obj = new CarverSpanObjectParse.Outer(new CarverSpanObjectParse.Inner("v"));
        this.listObj =
                Collections.singletonList(new CarverSpanObjectParse.Outer(new CarverSpanObjectParse.Inner("v1")));
    }

    @Test
    @DisplayName("匹配解析器，表达式为null")
    void shouldFailWhenNullMatchParser() {
        assertThat(this.parser.match(null)).isEqualTo(false);
    }

    @ParameterizedTest
    @ValueSource(strings = {"k1:v1", "k1:$[0].v1", "k1:$.v1", "k1:[1].v1", "k1_2_3:v1"})
    @DisplayName("匹配解析器，单组键值对正常匹配")
    void shouldOkWhenMatchSimpleParser(String expression) {
        assertThat(this.parser.match(expression)).isEqualTo(true);
    }

    @ParameterizedTest
    @ValueSource(strings = {"k1:v1,", "k1:v1;k2:v2", "k1|v1", "k1,$[0].v1", "k1?v1"})
    @DisplayName("匹配解析器，分隔符异常")
    void shouldFailWhenDelimiterWrongParser(String expression) {
        assertThat(this.parser.match(expression)).isEqualTo(false);
    }

    @ParameterizedTest
    @ValueSource(strings = {"k*:$[0].v1", "#$:v1.v1", "&:v1", "$[1].v1", "k1:[[1]].v1", "k1:v1..v2", "k1:$..v1.v2"})
    @DisplayName("匹配解析器，键或值符号异常")
    void shouldFailWhenKVWrongParser(String expression) {
        assertThat(this.parser.match(expression)).isEqualTo(false);
    }

    @ParameterizedTest
    @ValueSource(strings = {"k1:v1.v2.v3,k2:$[1].v1", "k3:[0].v1.v2,k4:$.v1", "k5:$[10].v1,k6:$.v1.v2,k7:v1.v2.v3"})
    @DisplayName("匹配解析器，多组键值对正常匹配")
    void shouldOkWhenMatchMultipleKVParser(String expression) {
        assertThat(this.parser.match(expression)).isEqualTo(true);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"k1:v1.v2.v3,\tk2:$[1].v1", "k3:[0].v1.v2, k4:$.v1", "k5:$[10].v1,\nk6:$.v1.v2,k7:v1.v2.v3"})
    @DisplayName("匹配解析器，多组键值对带空白字符正常匹配")
    void shouldOkWhenMatchMultipleKVParserWithWhitespace(String expression) {
        assertThat(this.parser.match(expression)).isEqualTo(true);
    }

    @ParameterizedTest
    @ValueSource(strings = {"k1:v1.v2;k2:$[1].v1", "k3:[0].v1.v2  k4:$.v1.v2", "k5;v1#k6"})
    @DisplayName("匹配解析器，多组键值对分隔符异常匹配")
    void shouldFailWhenMatchMultipleKVParser(String expression) {
        assertThat(this.parser.match(expression)).isEqualTo(false);
    }

    @Test
    @DisplayName("解析表达式为空或键值对分割失败")
    void shouldOkWhenParseEmptyExpression() {
        Map<String, String> emptyExp = this.parser.parse("", this.intObj);
        assertThat(emptyExp).isInstanceOf(Map.class).isEmpty();

        Map<String, String> nullExp = this.parser.parse(null, this.intObj);
        assertThat(nullExp).isInstanceOf(Map.class).isEmpty();

        Map<String, String> kvExp = this.parser.parse("k1:", this.intObj);
        assertThat(kvExp).isInstanceOf(Map.class).isEmpty();
    }

    @Test
    @DisplayName("解析基本数据类型")
    void shouldOkWhenParsePrimitiveObj() {
        Map<String, String> actual = this.parser.parse("k:v", this.intObj);
        assertThat(actual).isInstanceOf(Map.class).hasFieldOrPropertyWithValue("k", "");
    }

    @Test
    @DisplayName("解析键值对")
    void shouldOkWhenParseKVObject() {
        Map<String, String> single = this.parser.parse("k:k1.k2", this.kvObj);
        assertThat(single).isInstanceOf(Map.class).hasFieldOrPropertyWithValue("k", "v");

        Map<String, String> multiple = this.parser.parse("k:$.k1.k2,kk:k11", this.kvObj);
        assertThat(multiple).isInstanceOf(Map.class).hasFieldOrPropertyWithValue("k", "v");
        assertThat(multiple).isInstanceOf(Map.class).hasFieldOrPropertyWithValue("kk", "{k22=v}");

        Map<String, String> multiple2 = this.parser.parse("k  :$.k1.k2, kk:k11", this.kvObj);
        assertThat(multiple2).isInstanceOf(Map.class).hasFieldOrPropertyWithValue("k", "v");
        assertThat(multiple2).isInstanceOf(Map.class).hasFieldOrPropertyWithValue("kk", "{k22=v}");

        Map<String, String> multiple3 = this.parser.parse("\tk:\n$.k1.k2,\rkk:\nk11.k22", this.kvObj);
        assertThat(multiple3).isInstanceOf(Map.class).hasFieldOrPropertyWithValue("k", "v");
        assertThat(multiple3).isInstanceOf(Map.class).hasFieldOrPropertyWithValue("kk", "v");

        assertThatThrownBy(() -> this.parser.parse("k:$.k1 .k2,kk:k 11",
                this.kvObj)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> this.parser.parse("k:$.k1.k\t2,kk:k\n11.k22", this.kvObj)).isInstanceOf(
                IllegalArgumentException.class);
        assertThatThrownBy(() -> this.parser.parse("k:$.k1.k2,kk:k11.k\r22",
                this.kvObj)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("解析对象")
    void shouldOkWhenParseObject() {
        Map<String, String> actual = this.parser.parse("kk:k1,kkk:$.k1.k2,kkkk:k1.k2", this.obj);
        assertThat(actual).isInstanceOf(Map.class).hasFieldOrPropertyWithValue("kk", "{k2=v}");
        assertThat(actual).isInstanceOf(Map.class).hasFieldOrPropertyWithValue("kkk", "v");
        assertThat(actual).isInstanceOf(Map.class).hasFieldOrPropertyWithValue("kkkk", "v");
    }

    @Test
    @DisplayName("解析列表")
    void shouldOkWhenParseListObject() {
        Map<String, String> actual = this.parser.parse("kk:[0].k1,kkk:$[0].k1.k2,kkkk:[0].k2", this.listObj);
        assertThat(actual).isInstanceOf(Map.class).hasFieldOrPropertyWithValue("kk", "{k2=v1}");
        assertThat(actual).isInstanceOf(Map.class).hasFieldOrPropertyWithValue("kkk", "v1");
        assertThat(actual).isInstanceOf(Map.class).hasFieldOrPropertyWithValue("kkkk", "");
    }
}