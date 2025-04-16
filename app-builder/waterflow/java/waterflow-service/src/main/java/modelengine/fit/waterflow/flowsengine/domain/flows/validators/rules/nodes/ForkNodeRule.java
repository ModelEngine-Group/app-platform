/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.nodes;

import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;

/**
 * Fork节点校验规则
 *
 * @author 高诗意
 * @since 2023/08/14
 */
public class ForkNodeRule implements NodeRule {
    /**
     * 校验不同流程节点类型的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowNode 流程节点
     */
    @Override
    public void apply(FlowNode flowNode) {
        validateEmpty(flowNode.getEvents(), "fork node event size");
    }
}
