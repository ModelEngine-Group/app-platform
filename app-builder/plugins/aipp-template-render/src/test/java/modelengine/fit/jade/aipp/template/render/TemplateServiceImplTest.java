/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.template.render;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link TemplateServiceImpl} 的测试类。
 *
 * @author 孙怡菲
 * @since 2025-08-28
 */
class TemplateServiceImplTest {
    private TemplateServiceImpl textTool;

    @BeforeEach
    void setUp() {
        this.textTool = new TemplateServiceImpl();
    }

    @Test
    @DisplayName("基础模板变量替换成功")
    void shouldReplaceBasicVariables() {
        String template = "Hello {{name}}, your score is {{score}}.";
        Map<String, Object> args = new HashMap<>();
        args.put("name", "Tom");
        args.put("score", 95);

        String result = this.textTool.renderTemplate(template, args);
        assertEquals("Hello Tom, your score is 95.", result);
    }

    @Test
    @DisplayName("缺失变量时置为空字符串")
    void shouldReplaceMissingVariableWithEmpty() {
        String template = "Hello {{name}}, your score is {{score}}.";
        Map<String, Object> args = new HashMap<>();
        args.put("name", "Tom");

        String result = this.textTool.renderTemplate(template, args);
        assertEquals("Hello Tom, your score is .", result);
    }

    @Test
    @DisplayName("空参数Map时模板变量置为空")
    void shouldHandleEmptyArgsMap() {
        String template = "Hello {{name}}!";
        Map<String, Object> args = new HashMap<>();

        String result = this.textTool.renderTemplate(template, args);
        assertEquals("Hello !", result);
    }

    @Test
    @DisplayName("参数为null时模板变量置为空")
    void shouldHandleNullArgs() {
        String template = "Hello {{name}}!";

        String result = this.textTool.renderTemplate(template, null);
        assertEquals("Hello !", result);
    }

    @Test
    @DisplayName("模板无占位符时内容保持不变")
    void shouldHandleTemplateWithoutPlaceholders() {
        String template = "Hello world!";

        String result = this.textTool.renderTemplate(template, Map.of("name", "Tom"));
        assertEquals("Hello world!", result);
    }

    @Test
    @DisplayName("变量中包含占位符内容保持不变")
    void shouldHandleVariableWithPlaceholders() {
        String template = "Hello {{name}}!";

        String result = this.textTool.renderTemplate(template, Map.of("name", "{{Tom}}"));
        assertEquals("Hello {{Tom}}!", result);
    }

    @Test
    @DisplayName("变量为 List 时正常替换")
    void shouldReplaceListVariableCorrectly() {
        String template = "Items: {{items}}";
        Map<String, Object> args = new HashMap<>();
        args.put("items", Arrays.asList("apple", "banana", "cherry"));

        String result = this.textTool.renderTemplate(template, args);
        assertEquals("Items: [apple, banana, cherry]", result);
    }

    @Test
    @DisplayName("变量为 Map 时正常替换")
    void shouldReplaceMapVariableCorrectly() {
        String template = "Map data: {{data}}";
        Map<String, Object> args = new HashMap<>();
        Map<String, Object> mapValue = new LinkedHashMap<>();
        mapValue.put("a", 1);
        mapValue.put("b", 2);
        args.put("data", mapValue);

        String result = this.textTool.renderTemplate(template, args);
        assertEquals("Map data: {a=1, b=2}", result);
    }
}