/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.jobers;

import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowJoberType;
import modelengine.fitframework.inspection.Validation;

/**
 * 节点自动任务校验规则
 *
 * @author 高诗意
 * @since 2023/08/14
 */
public class GeneralJoberRule implements JoberRule {
    /**
     * 校验不同流程节点自动任务类型的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowJober 流程节点自动任务
     */
    @Override
    public void apply(FlowJober flowJober) {
        Validation.notNull(flowJober.getType(), exception("flow jober type"));
        Validation.equals(FlowJoberType.GENERAL_JOBER, flowJober.getType(), exception("flow jober type"));
        Validation.equals(1, flowJober.getFitables().size(), exception("flow jober fitables"));
    }
}
