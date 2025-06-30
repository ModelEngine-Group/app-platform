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

import java.util.Map;

/**
 * boolean类型的数据生成
 *
 * @author 宋永坦
 * @since 2024/4/18
 */
public class BooleanMappingProcessor extends AbstractMappingProcessor {
    private static final Logger LOG = Logger.get(BooleanMappingProcessor.class);

    @Override
    protected Object generateInput(MappingNode mappingConfig, Map<String, Object> businessData) {
        if (mappingConfig.getValue() instanceof String) {
            return Boolean.parseBoolean(cast(mappingConfig.getValue()));
        }
        if (mappingConfig.getValue() instanceof Boolean) {
            return mappingConfig.getValue();
        }
        LOG.error("The value can not be converted to boolean, name={}, value={}, valueType={}.",
                mappingConfig.getName(), mappingConfig.getValue(), mappingConfig.getValue().getClass().getName());

        throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, mappingConfig.getName());
    }
}
