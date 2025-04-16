/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.callbacks;

import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowCallbackType;
import modelengine.fitframework.inspection.Validation;

/**
 * 节点通用型回调函数校验规则
 *
 * @author 李哲峰
 * @since 2023/12/11
 */
public class GeneralCallbackRule implements CallbackRule {
    /**
     * 校验不同流程节点通用型回调函数类型的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowCallback 流程节点回调函数
     */
    @Override
    public void apply(FlowCallback flowCallback) {
        Validation.notNull(flowCallback.getType(), exception("flow callback type"));
        Validation.equals(FlowCallbackType.GENERAL_CALLBACK, flowCallback.getType(), exception("flow callback type"));
        Validation.equals(1, flowCallback.getFitables().size(), exception("flow callback fitables"));
    }
}
