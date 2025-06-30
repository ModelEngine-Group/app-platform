/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

/**
 * 整数类型的转换测试
 *
 * @author 宋永坦
 * @since 2024/4/20
 */
public class IntegerMappingProcessorTest {
    @Test
    @DisplayName("测试整数的值转换的成功场景")
    void shouldReturnValueWhenGenerateGivenIntegerValue() {
        int expectValue = 666;
        MappingNode mappingConfig = new MappingNode("key", MappingNodeType.INTEGER, MappingFromType.INPUT, expectValue,
                "");

        MappingProcessor target = new IntegerMappingProcessor();
        Object result = target.generate(mappingConfig, new HashMap<>());

        assertEquals(expectValue, result);
    }

    @Test
    @DisplayName("测试字符串整数的值转换的成功场景")
    void shouldReturnValueWhenGenerateGivenStringIntegerValue() {
        int expectValue = 666;
        MappingNode mappingConfig = new MappingNode("key", MappingNodeType.INTEGER, MappingFromType.INPUT,
                String.valueOf(expectValue), "");

        MappingProcessor target = new IntegerMappingProcessor();
        Object result = target.generate(mappingConfig, new HashMap<>());

        assertEquals(expectValue, result);
    }

    @Test
    @DisplayName("测试无法转换的字符串的值转换的失败场景")
    void shouldThrowJoberExceptionWhenGenerateGivenInvalidStringValue() {
        MappingNode mappingConfig = new MappingNode("key", MappingNodeType.INTEGER, MappingFromType.INPUT, "xxx", "");

        MappingProcessor target = new IntegerMappingProcessor();
        assertThrows(WaterflowParamException.class, () -> target.generate(mappingConfig, new HashMap<>()));
    }

    @Test
    @DisplayName("测试其它无法转换的类型抛异常的失败场景")
    void shouldThrowJoberExceptionWhenGenerateGivenInvalidTypeValue() {
        MappingNode mappingConfig = new MappingNode("key", MappingNodeType.BOOLEAN, MappingFromType.INPUT,
                new HashMap<>(), "");

        MappingProcessor target = new IntegerMappingProcessor();
        assertThrows(WaterflowParamException.class, () -> target.generate(mappingConfig, new HashMap<>()));
    }
}
