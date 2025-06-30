/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import modelengine.fit.waterflow.ErrorCodes;
import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fitframework.util.MapBuilder;

import java.util.Map;
import java.util.Optional;

/**
 * 对应参数生成转换处理器的工厂
 *
 * @author 宋永坦
 * @since 2024/4/18
 */
public class MappingProcessorFactory {
    private static final Map<MappingNodeType, MappingProcessor> mappingProcessors
            = MapBuilder.<MappingNodeType, MappingProcessor>get()
            .put(MappingNodeType.BOOLEAN, new BooleanMappingProcessor())
            .put(MappingNodeType.INTEGER, new IntegerMappingProcessor())
            .put(MappingNodeType.NUMBER, new NumberMappingProcessor())
            .put(MappingNodeType.STRING, new StringMappingProcessor())
            .put(MappingNodeType.OBJECT, new ObjectMappingProcessor())
            .put(MappingNodeType.ARRAY, new ArrayMappingProcessor())
            .build();

    /**
     * 根据给定的映射节点配置获取对应的映射处理器
     *
     * @param mappingConfig 映射节点配置
     * @return 对应的映射处理器
     * @throws WaterflowParamException 如果给定的映射节点配置的类型在映射处理器映射中不存在，则抛出此异常
     */
    public static MappingProcessor get(MappingNode mappingConfig) {
        return Optional.ofNullable(mappingProcessors.get(mappingConfig.getType()))
                .orElseThrow(() -> new WaterflowParamException(ErrorCodes.INPUT_PARAM_IS_INVALID,
                        mappingConfig.getType().getCode()));
    }
}
