/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.jobers;

import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowJoberProperties;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowJoberType;
import modelengine.fitframework.inspection.Validation;

/**
 * HttpJoberRule
 *
 * @author 晏钰坤
 * @since 2023/10/18
 */
public class HttpJoberRule implements JoberRule {
    /**
     * 校验不同流程节点自动任务类型的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowJober 流程节点自动任务
     */
    @Override
    public void apply(FlowJober flowJober) {
        Validation.notNull(flowJober.getType(), exception("flow http jober type"));
        Validation.equals(FlowJoberType.HTTP_JOBER, flowJober.getType(), exception("flow http jober type"));
        Validation.equals(1, flowJober.getFitables().size(), exception("flow http jober fitables"));
        Validation.notNull(flowJober.getProperties().get(FlowJoberProperties.ENTITY.getValue()),
                exception("flow http jober entity"));
    }
}

