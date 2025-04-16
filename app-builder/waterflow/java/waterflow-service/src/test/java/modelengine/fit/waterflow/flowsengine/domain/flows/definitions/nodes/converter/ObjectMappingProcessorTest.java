/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 对象类型转换的测试
 *
 * @author 宋永坦
 * @since 2024/4/20
 */
class ObjectMappingProcessorTest {
    @Test
    @DisplayName("测试对象值转换的成功场景")
    void shouldReturnValueWhenGenerateGivenElementWithInput() {
        Map<String, Object> expectValue = MapBuilder.<String, Object>get().put("int", 666).put("str", "str1").build();
        MappingNode mappingConfig = new MappingNode("keyObj", MappingNodeType.OBJECT, MappingFromType.INPUT,
                expectValue, "");

        MappingProcessor target = new ObjectMappingProcessor();
        Object result = target.generate(mappingConfig, new HashMap<>());

        assertEquals(expectValue, result);
    }

    @Test
    @DisplayName("测试对象expand转换的成功场景")
    void shouldReturnValueWhenGenerateGivenElementWithExpand() {
        Map<String, Object> expectValue = MapBuilder.<String, Object>get().put("int", 666).put("str", "str1").build();
        ArrayList<MappingNode> objectValueConfig = new ArrayList<>(
                Arrays.asList(new MappingNode("str", MappingNodeType.STRING, MappingFromType.INPUT, "str1", ""),
                        new MappingNode("int", MappingNodeType.INTEGER, MappingFromType.INPUT, 666, "")));
        MappingNode mappingConfig = new MappingNode("keyObj", MappingNodeType.OBJECT, MappingFromType.EXPAND,
                objectValueConfig, "");

        MappingProcessor target = new ObjectMappingProcessor();
        Object result = target.generate(mappingConfig, new HashMap<>());

        assertEquals(expectValue, result);
    }

    @Test
    @DisplayName("测试对象引用值转换的成功场景")
    void shouldReturnValueWhenGenerateGivenElementWithRefValue() {
        Map<String, Object> expectValue = MapBuilder.<String, Object>get().put("int", 666).put("str", "str1").build();
        ArrayList<MappingNode> objectValueConfig = new ArrayList<>(Arrays.asList(
                new MappingNode("str", MappingNodeType.STRING, MappingFromType.REFERENCE, Arrays.asList("str"),
                        "", true),
                new MappingNode("int", MappingNodeType.INTEGER, MappingFromType.REFERENCE,
                        Arrays.asList("level1", "level2"), "", true)));
        MappingNode mappingConfig = new MappingNode("keyObj", MappingNodeType.OBJECT, MappingFromType.EXPAND,
                objectValueConfig, "", true);
        Map<String, Object> businessData = MapBuilder.<String, Object>get()
                .put("level1", MapBuilder.<String, Object>get().put("level2", 666).build())
                .put("str", "str1")
                .build();

        MappingProcessor target = new ObjectMappingProcessor();
        Object result = target.generate(mappingConfig, businessData);

        assertEquals(expectValue, result);
    }

    @Test
    @DisplayName("测试对象嵌套Object类型转换的成功场景")
    void shouldReturnValueWhenGenerateGivenElementWithObjectValue() {
        Map<String, Object> expectValue = MapBuilder.<String, Object>get()
                .put("obj", MapBuilder.<String, Object>get().put("int", 666).put("str", "str1").build())
                .build();

        ArrayList<MappingNode> subObjectValueConfig = new ArrayList<>(
                Arrays.asList(new MappingNode("str", MappingNodeType.STRING, MappingFromType.INPUT, "str1", ""),
                        new MappingNode("int", MappingNodeType.INTEGER, MappingFromType.INPUT, 666, "")));
        MappingNode objectMappingConfig = new MappingNode("obj", MappingNodeType.OBJECT, MappingFromType.EXPAND,
                subObjectValueConfig, "");

        ArrayList<MappingNode> objectValueConfig = new ArrayList<>(Arrays.asList(objectMappingConfig));
        MappingNode mappingConfig = new MappingNode("key", MappingNodeType.OBJECT, MappingFromType.EXPAND,
                objectValueConfig, "");

        MappingProcessor target = new ObjectMappingProcessor();
        Object result = target.generate(mappingConfig, new HashMap<>());

        assertEquals(expectValue, result);
    }

    @Test
    @DisplayName("测试对象嵌套Array类型转换的成功场景")
    void shouldReturnValueWhenGenerateGivenElementWithArrayValue() {
        Map<String, Object> expectValue = MapBuilder.<String, Object>get()
                .put("arr", new ArrayList<>(Arrays.asList("str1", 666)))
                .build();

        ArrayList<MappingNode> subArrayValueConfig = new ArrayList<>(
                Arrays.asList(new MappingNode("", MappingNodeType.STRING, MappingFromType.INPUT, "str1", ""),
                        new MappingNode("", MappingNodeType.INTEGER, MappingFromType.INPUT, 666, "")));
        MappingNode subArrayMappingConfig = new MappingNode("arr", MappingNodeType.ARRAY, MappingFromType.EXPAND,
                subArrayValueConfig, "");

        ArrayList<MappingNode> objectValueConfig = new ArrayList<>(Arrays.asList(subArrayMappingConfig));
        MappingNode mappingConfig = new MappingNode("key", MappingNodeType.OBJECT, MappingFromType.EXPAND,
                objectValueConfig, "");

        MappingProcessor target = new ObjectMappingProcessor();
        Object result = target.generate(mappingConfig, new HashMap<>());

        assertEquals(expectValue, result);
    }

    @Test
    @DisplayName("测试Value类型不匹配抛异常的失败场景")
    void shouldThrowJoberExceptionWhenGenerateGivenInvalidTypeValue() {
        MappingNode mappingConfig = new MappingNode("key", MappingNodeType.OBJECT, MappingFromType.INPUT,
                new LinkedList<>(), "");

        MappingProcessor target = new ObjectMappingProcessor();
        assertThrows(WaterflowParamException.class, () -> target.generate(mappingConfig, new HashMap<>()));
    }
}