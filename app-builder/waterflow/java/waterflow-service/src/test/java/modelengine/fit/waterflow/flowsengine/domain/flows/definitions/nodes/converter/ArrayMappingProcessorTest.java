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
import java.util.List;
import java.util.Map;

/**
 * 列表类型转换的测试
 *
 * @author 宋永坦
 * @since 2024/4/20
 */
class ArrayMappingProcessorTest {
    @Test
    @DisplayName("测试列表expand转换的成功场景")
    void shouldReturnValueWhenGenerateGivenElementWithExpand() {
        List<Object> expectValue = new ArrayList<>(Arrays.asList("str", 666));
        ArrayList<MappingNode> arrayValueConfig = new ArrayList<>(
                Arrays.asList(new MappingNode("", MappingNodeType.STRING, MappingFromType.INPUT, "str", ""),
                        new MappingNode("", MappingNodeType.INTEGER, MappingFromType.INPUT, 666, "")));
        MappingNode mappingConfig = new MappingNode("arr", MappingNodeType.ARRAY, MappingFromType.EXPAND,
                arrayValueConfig, "");

        MappingProcessor target = new ArrayMappingProcessor();
        Object result = target.generate(mappingConfig, new HashMap<>());

        assertEquals(expectValue, result);
    }

    @Test
    @DisplayName("测试列表值转换的成功场景")
    void shouldReturnValueWhenGenerateGivenElementWithInput() {
        List<Object> expectValue = new ArrayList<>(Arrays.asList("str", 666));
        MappingNode mappingConfig = new MappingNode("arr", MappingNodeType.ARRAY, MappingFromType.INPUT,
                expectValue, "");

        MappingProcessor target = new ArrayMappingProcessor();
        Object result = target.generate(mappingConfig, new HashMap<>());

        assertEquals(expectValue, result);
    }

    @Test
    @DisplayName("测试列表引用值转换的成功场景")
    void shouldReturnValueWhenGenerateGivenElementWithRefValue() {
        List<Object> expectValue = new ArrayList<>(Arrays.asList("str1", 666));
        ArrayList<MappingNode> arrayValueConfig = new ArrayList<>(Arrays.asList(
                new MappingNode("", MappingNodeType.STRING, MappingFromType.REFERENCE, Arrays.asList("str"), "", true),
                new MappingNode("", MappingNodeType.INTEGER, MappingFromType.REFERENCE,
                        Arrays.asList("level1", "level2"), "", true)));
        MappingNode mappingConfig = new MappingNode("arr", MappingNodeType.ARRAY, MappingFromType.EXPAND,
                arrayValueConfig, "");
        Map<String, Object> businessData = MapBuilder.<String, Object>get()
                .put("level1", MapBuilder.<String, Object>get().put("level2", 666).build())
                .put("str", "str1")
                .build();

        MappingProcessor target = new ArrayMappingProcessor();
        Object result = target.generate(mappingConfig, businessData);

        assertEquals(expectValue, result);
    }

    @Test
    @DisplayName("测试列表嵌套Object类型转换的成功场景")
    void shouldReturnValueWhenGenerateGivenElementWithObjectValue() {
        List<Object> expectValue = new ArrayList<>(
                Arrays.asList(MapBuilder.<String, Object>get().put("int", 666).put("str", "str1").build()));

        ArrayList<MappingNode> objectValueConfig = new ArrayList<>(
                Arrays.asList(new MappingNode("str", MappingNodeType.STRING, MappingFromType.INPUT, "str1", ""),
                        new MappingNode("int", MappingNodeType.INTEGER, MappingFromType.INPUT, 666, "")));
        MappingNode objectMappingConfig = new MappingNode("keyObj", MappingNodeType.OBJECT, MappingFromType.EXPAND,
                objectValueConfig, "");

        ArrayList<MappingNode> arrayValueConfig = new ArrayList<>(Arrays.asList(objectMappingConfig));
        MappingNode mappingConfig = new MappingNode("arr", MappingNodeType.ARRAY, MappingFromType.EXPAND,
                arrayValueConfig, "");

        MappingProcessor target = new ArrayMappingProcessor();
        Object result = target.generate(mappingConfig, new HashMap<>());

        assertEquals(expectValue, result);
    }

    @Test
    @DisplayName("测试列表嵌套Array类型转换的成功场景")
    void shouldReturnValueWhenGenerateGivenElementWithArrayValue() {
        List<Object> expectValue = new ArrayList<>(Arrays.asList(Arrays.asList("str1", 666)));

        ArrayList<MappingNode> subArrayValueConfig = new ArrayList<>(
                Arrays.asList(new MappingNode("", MappingNodeType.STRING, MappingFromType.INPUT, "str1", ""),
                        new MappingNode("", MappingNodeType.INTEGER, MappingFromType.INPUT, 666, "")));
        MappingNode subArrayMappingConfig = new MappingNode("keyObj", MappingNodeType.ARRAY, MappingFromType.EXPAND,
                subArrayValueConfig, "");

        ArrayList<MappingNode> arrayValueConfig = new ArrayList<>(Arrays.asList(subArrayMappingConfig));
        MappingNode mappingConfig = new MappingNode("arr", MappingNodeType.ARRAY, MappingFromType.EXPAND,
                arrayValueConfig, "");

        MappingProcessor target = new ArrayMappingProcessor();
        Object result = target.generate(mappingConfig, new HashMap<>());

        assertEquals(expectValue, result);
    }

    @Test
    @DisplayName("测试Value类型不匹配抛异常的失败场景")
    void shouldThrowJoberExceptionWhenGenerateGivenInvalidTypeValue() {
        MappingNode mappingConfig = new MappingNode("key", MappingNodeType.ARRAY, MappingFromType.INPUT,
                new HashMap<>(), "");

        MappingProcessor target = new ArrayMappingProcessor();
        assertThrows(WaterflowParamException.class, () -> target.generate(mappingConfig, new HashMap<>()));
    }
}