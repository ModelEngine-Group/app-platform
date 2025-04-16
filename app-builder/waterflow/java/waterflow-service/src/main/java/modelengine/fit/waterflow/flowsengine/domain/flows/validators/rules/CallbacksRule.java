/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules;

import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.StringUtils;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 节点回调函数校验规则
 *
 * @author 李哲峰
 * @since 2023/12/11
 */
@Component
public class CallbacksRule implements FlowRule {
    private static final int MAX_NAME_SIZE = 256;

    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*<?{}|]");

    /**
     * 校验流程定义的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowDefinition 流程定义实体
     */
    @Override
    public void apply(FlowDefinition flowDefinition) {
        flowDefinition.getNodeIdSet().forEach(nodeId -> {
            FlowNode flowNode = flowDefinition.getFlowNode(nodeId);
            Optional.ofNullable(flowNode.getCallback()).ifPresent(c -> {
                Validation.isFalse(flowNode.belongTo(FlowNodeType.START), exception("flow callback node type"));
                apply(c);
            });
        });
    }

    private void apply(FlowCallback flowCallback) {
        Validation.notNull(flowCallback.getType(), exception("flow callback type"));
        Validation.notNull(flowCallback.getFitables(), exception("flow callback fitables"));
        validateName(flowCallback.getName());
        Optional.ofNullable(flowCallback.getType().getCallbackRule())
                .ifPresent(callbackRule -> callbackRule.apply(flowCallback));
    }

    private void validateName(String name) {
        if (StringUtils.isBlank(name)) {
            return;
        }
        Validation.isFalse(SPECIAL_CHAR_PATTERN.matcher(name).find(),
                exception("flow callback name, name contains special characters"));
        Validation.lessThan(name.length(), MAX_NAME_SIZE, exception("flow callback name, name length over 256"));
    }
}
