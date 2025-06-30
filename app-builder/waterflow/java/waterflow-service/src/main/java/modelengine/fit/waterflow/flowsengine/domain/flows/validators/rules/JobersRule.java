/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules;

import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.jobers.FlowJober;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.StringUtils;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 节点手动任务校验规则
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Component
public class JobersRule implements FlowRule {
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
        flowDefinition.getNodeIdSet()
                .forEach(nodeId -> Optional.ofNullable(flowDefinition.getFlowNode(nodeId).getJober())
                        .ifPresent(this::apply));
    }

    private void apply(FlowJober flowJober) {
        Validation.notNull(flowJober.getType(), exception("flow jober type"));
        Validation.notNull(flowJober.getFitables(), exception("flow jober fitables"));
        validateName(flowJober.getName());
        Optional.ofNullable(flowJober.getType().getJoberRule()).ifPresent(joberRule -> joberRule.apply(flowJober));
    }

    private void validateName(String name) {
        if (StringUtils.isBlank(name)) {
            return;
        }
        Validation.isFalse(SPECIAL_CHAR_PATTERN.matcher(name).find(),
                exception("flow jober name, name contains special characters"));
        Validation.lessThan(name.length(), MAX_NAME_SIZE, exception("flow jober name, name length over 256"));
    }
}
