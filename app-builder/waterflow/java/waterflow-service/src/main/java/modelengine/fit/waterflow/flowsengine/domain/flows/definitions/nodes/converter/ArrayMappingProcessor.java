/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import static modelengine.fit.waterflow.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fitframework.log.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 列表类型的数据生成，对应List<Object>类型
 *
 * @author 宋永坦
 * @since 2024/4/19
 */
public class ArrayMappingProcessor extends AbstractMappingProcessor {
    private static final Logger LOG = Logger.get(ArrayMappingProcessor.class);

    @Override
    protected Object generateInput(MappingNode mappingConfig, Map<String, Object> businessData) {
        if (!(mappingConfig.getValue() instanceof List)) {
            LOG.error("The value can not be converted to array, name={}, value={}, valueType={}.",
                    mappingConfig.getName(), mappingConfig.getValue(), mappingConfig.getValue().getClass().getName());
            throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, mappingConfig.getName());
        }
        return mappingConfig.getValue();
    }

    @Override
    protected Object generateExpand(MappingNode mappingConfig, Map<String, Object> businessData) {
        if (!(mappingConfig.getValue() instanceof List)) {
            LOG.error("The value can not be converted to array, name={}, value={}, valueType={}.",
                    mappingConfig.getName(), mappingConfig.getValue(), mappingConfig.getValue().getClass().getName());
            throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, mappingConfig.getName());
        }
        List<MappingNode> rawConfig = cast(mappingConfig.getValue());
        return rawConfig.stream()
                .map(config -> MappingProcessorFactory.get(config).generate(config, businessData))
                .collect(Collectors.toList());
    }
}
