/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import modelengine.fit.waterflow.ErrorCodes;
import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fitframework.log.Logger;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 数字类型的数据生成，对应Double类型
 *
 * @author 宋永坦
 * @since 2024/4/19
 */
public class NumberMappingProcessor extends AbstractMappingProcessor {
    private static final Logger LOG = Logger.get(NumberMappingProcessor.class);

    @Override
    protected Object generateInput(MappingNode mappingConfig, Map<String, Object> businessData) {
        if (mappingConfig.getValue() instanceof BigDecimal) {
            return ((BigDecimal) mappingConfig.getValue()).doubleValue();
        }
        if (mappingConfig.getValue() instanceof Double) {
            return mappingConfig.getValue();
        }
        if (mappingConfig.getValue() instanceof Integer) {
            return ((Integer) mappingConfig.getValue()).doubleValue();
        }
        if (mappingConfig.getValue() instanceof String) {
            try {
                return Double.valueOf((String) mappingConfig.getValue());
            } catch (NumberFormatException e) {
                LOG.error("The value can not be converted to number.", e);
            }
        }
        LOG.error("The value can not be converted to number, name={}, value={}, valueType={}.", mappingConfig.getName(),
                mappingConfig.getValue(), mappingConfig.getValue().getClass().getName());

        throw new WaterflowParamException(ErrorCodes.INPUT_PARAM_IS_INVALID, mappingConfig.getName());
    }
}
