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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 对象类型的数据生成，对应Map<String, Object>类型
 *
 * @author 宋永坦
 * @since 2024/4/19
 */
public class ObjectMappingProcessor extends AbstractMappingProcessor {
    private static final Logger LOG = Logger.get(ObjectMappingProcessor.class);

    @Override
    protected Object generateInput(MappingNode mappingConfig, Map<String, Object> businessData) {
        if (!(mappingConfig.getValue() instanceof Map)) {
            LOG.error("The value can not be converted to object, name={}, value={}, valueType={}.",
                    mappingConfig.getName(), mappingConfig.getValue(), mappingConfig.getValue().getClass().getName());
            throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, mappingConfig.getName());
        }
        return mappingConfig.getValue();
    }

    @Override
    protected Object generateExpand(MappingNode mappingConfig, Map<String, Object> businessData) {
        if (!(mappingConfig.getValue() instanceof List)) {
            LOG.error("The value can not be converted to object, name={}, value={}, valueType={}.",
                    mappingConfig.getName(), mappingConfig.getValue(), mappingConfig.getValue().getClass().getName());
            throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, mappingConfig.getName());
        }
        List<MappingNode> rawConfig = cast(mappingConfig.getValue());
        Map<String, Object> result = new LinkedHashMap<>();
        rawConfig.forEach(config -> result.put(config.getName(),
                MappingProcessorFactory.get(config).generate(config, businessData)));
        return result;
    }
}
