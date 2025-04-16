/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.filters;

import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.filters.FlowFilter;
import modelengine.fitframework.inspection.Validation;

import java.util.Objects;

import static modelengine.fit.waterflow.ErrorCodes.INPUT_PARAM_IS_INVALID;

/**
 * 最小Size过滤器校验规则
 *
 * @author 高诗意
 * @since 2023/09/25
 */
public class ThresholdFilterRule implements FilterRule {
    private static final String NUMBER_REGEX = "\\d+";

    private static final String THRESHOLD = "threshold";

    /**
     * 校验不同过滤器的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowFilter 流程过滤器
     */
    @Override
    public void apply(FlowFilter flowFilter) {
        Validation.notNull(flowFilter.getFilterType(), exception("flow filter type"));
        String thresholdValue = flowFilter.getProperties().get(THRESHOLD);
        if (Objects.isNull(thresholdValue) || !isNumeric(thresholdValue)) {
            throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, "flow filter threshold");
        }
    }

    private boolean isNumeric(String threshold) {
        return threshold.matches(NUMBER_REGEX);
    }
}
