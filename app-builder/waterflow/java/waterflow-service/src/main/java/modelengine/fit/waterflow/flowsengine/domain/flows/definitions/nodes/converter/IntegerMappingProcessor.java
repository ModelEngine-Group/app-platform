/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import static modelengine.fit.waterflow.ErrorCodes.INPUT_PARAM_IS_INVALID;

import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fitframework.log.Logger;

import java.util.Map;
/**
 * 整数类型的数据生成
 *
 * @author 宋永坦
 * @since 2024/4/19
 */
public class IntegerMappingProcessor extends AbstractMappingProcessor {
    private static final Logger LOG = Logger.get(IntegerMappingProcessor.class);

    @Override
    protected Object generateInput(MappingNode mappingConfig, Map<String, Object> businessData) {
        if (mappingConfig.getValue() instanceof Integer) {
            return mappingConfig.getValue();
        }
        if (mappingConfig.getValue() instanceof String) {
            try {
                return Integer.valueOf((String) mappingConfig.getValue());
            } catch (NumberFormatException e) {
                LOG.error("The value can not be converted to integer.", e);
            }
        }
        LOG.error("The value can not be converted to integer, name={}, value={}, valueType={}.",
                mappingConfig.getName(), mappingConfig.getValue(), mappingConfig.getValue().getClass().getName());

        throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, mappingConfig.getName());
    }
}
