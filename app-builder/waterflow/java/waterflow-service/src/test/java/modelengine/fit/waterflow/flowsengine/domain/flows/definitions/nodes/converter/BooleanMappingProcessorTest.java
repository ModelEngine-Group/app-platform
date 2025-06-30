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
 * 布尔类型转换的测试
 *
 * @author 宋永坦
 * @since 2024/4/20
 */
public class BooleanMappingProcessorTest {
    @Test
    @DisplayName("测试将boolean的值转换的成功场景")
    void shouldReturnValueWhenGenerateGivenBooleanValue() {
        MappingNode trueMappingConfig = new MappingNode("true", MappingNodeType.BOOLEAN, MappingFromType.INPUT, true,
                "");
        MappingNode falseMappingConfig = new MappingNode("false", MappingNodeType.BOOLEAN, MappingFromType.INPUT, false,
                "");

        MappingProcessor target = new BooleanMappingProcessor();
        Object trueResult = target.generate(trueMappingConfig, new HashMap<>());
        Object falseResult = target.generate(falseMappingConfig, new HashMap<>());

        assertEquals(true, trueResult);
        assertEquals(false, falseResult);
    }

    @Test
    @DisplayName("测试将字符串的值转换的成功场景")
    void shouldReturnValueWhenGenerateGivenStringBooleanValue() {
        MappingNode trueMappingConfig = new MappingNode("true", MappingNodeType.BOOLEAN, MappingFromType.INPUT, "true",
                "");
        MappingNode falseMappingConfig = new MappingNode("false", MappingNodeType.BOOLEAN, MappingFromType.INPUT,
                "false", "");

        MappingProcessor target = new BooleanMappingProcessor();
        Object trueResult = target.generate(trueMappingConfig, new HashMap<>());
        Object falseResult = target.generate(falseMappingConfig, new HashMap<>());

        assertEquals(true, trueResult);
        assertEquals(false, falseResult);
    }

    @Test
    @DisplayName("测试其它无法转换的类型抛异常的失败场景")
    void shouldThrowJoberExceptionWhenGenerateGivenInvalidTypeValue() {
        MappingNode mappingConfig = new MappingNode("true", MappingNodeType.BOOLEAN, MappingFromType.INPUT,
                new HashMap<>(), "");

        MappingProcessor target = new BooleanMappingProcessor();
        assertThrows(WaterflowParamException.class, () -> target.generate(mappingConfig, new HashMap<>()));
    }
}
