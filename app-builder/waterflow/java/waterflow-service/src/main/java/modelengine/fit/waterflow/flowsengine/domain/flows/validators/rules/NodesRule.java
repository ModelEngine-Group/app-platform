/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules;

import static modelengine.fit.waterflow.ErrorCodes.INPUT_PARAM_IS_INVALID;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 流程节点校验规则
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Component
public class NodesRule implements FlowRule {
    private static final String SPECIAL_CHAR_REG = "[^a-zA-Z0-9 ]";

    private static final Pattern BRACES_PATTERN = Pattern.compile("^\\{\\{(.+?)\\}\\}$");

    /**
     * 校验流程定义的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowDefinition 流程定义实体
     */
    @Override
    public void apply(FlowDefinition flowDefinition) {
        flowDefinition.getNodeIdSet().stream().map(flowDefinition::getFlowNode).forEach(this::apply);
    }

    private void apply(FlowNode flowNode) {
        validateMetaId(flowNode.getMetaId());
        validateType(flowNode.getType());
        validateTriggerMode(flowNode);
        Validation.notBlank(flowNode.getName(), exception("flow node name, name can not be blank"));
        Optional.ofNullable(flowNode.getJoberFilter())
                .ifPresent(flowFilter -> Optional.ofNullable(flowFilter.getFilterType().getFilterRule())
                        .ifPresent((filterRule) -> filterRule.apply(flowFilter)));
        Optional.ofNullable(flowNode.getTaskFilter())
                .ifPresent(flowFilter -> Optional.ofNullable(flowFilter.getFilterType().getFilterRule())
                        .ifPresent((filterRule) -> filterRule.apply(flowFilter)));
        Optional.ofNullable(flowNode.getType().getNodeRule()).ifPresent(nodeRule -> nodeRule.apply(flowNode));
        String flowContext = cast(flowNode.getProperties().get("flowContext"));
        Optional.ofNullable(flowContext).ifPresent(this::checkFlowContext);
    }

    private void checkFlowContext(String flowContext) {
        Matcher matcher = BRACES_PATTERN.matcher(flowContext);
        if (!matcher.find() || Objects.equals(matcher.group(1), "")) {
            throw new WaterflowParamException(INPUT_PARAM_IS_INVALID,
                    "flowContext has been config, but the output key is empty");
        }
    }

    private void validateType(FlowNodeType type) {
        Validation.notNull(type, exception("flow node type, node type can not be null"));
    }

    private void validateMetaId(String metaId) {
        Validation.notBlank(metaId, exception("flow node metaId, metaId can not be blank"));
        Pattern pattern = Pattern.compile(SPECIAL_CHAR_REG);
        Validation.isFalse(pattern.matcher(metaId).find(), exception("flow node metaId not allow special char"));
    }

    private void validateTriggerMode(FlowNode flowNode) {
        Validation.notNull(flowNode.getTriggerMode(), exception("flow node trigger mode, can not be null"));
    }
}
