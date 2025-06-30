/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules;

import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.events.FlowEvent;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;

import java.util.regex.Pattern;

import static modelengine.fit.waterflow.ErrorCodes.INVALID_EVENT_CONFIG;

/**
 * 节点事件校验规则
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Component
public class EventsRule implements FlowRule {
    private static final Logger log = Logger.get(EventsRule.class);

    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^a-zA-Z0-9 ]");

    /**
     * 校验流程定义的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowDefinition 流程定义实体
     */
    @Override
    public void apply(FlowDefinition flowDefinition) {
        flowDefinition.getNodeIdSet()
                .forEach(nodeId -> flowDefinition.getFlowNode(nodeId)
                        .getEvents()
                        .forEach(flowEvent -> this.apply(flowDefinition, flowEvent)));
    }

    private void apply(FlowDefinition flowDefinition, FlowEvent flowEvent) {
        try {
            validateMetaId(flowEvent.getMetaId());
            Validation.notBlank(flowEvent.getFrom(), exception("flow event from metaId empty"));
            Validation.notNull(flowDefinition.getFlowNode(flowEvent.getFrom()),
                    exception("flow event from metaId invalid"));
            Validation.notBlank(flowEvent.getTo(), exception("flow event to metaId empty"));
            Validation.notNull(flowDefinition.getFlowNode(flowEvent.getTo()),
                    exception("flow event to metaId invalid"));
            Validation.isFalse(flowEvent.getFrom().equals(flowEvent.getTo()),
                    exception("flow event from equal to metaId"));
            applyConditionRule(flowDefinition, flowEvent);
        } catch (WaterflowException ex) {
            log.error("Exception: ", ex);
            throw new WaterflowParamException(INVALID_EVENT_CONFIG, flowEvent.getMetaId());
        }
    }

    private void applyConditionRule(FlowDefinition flowDefinition, FlowEvent flowEvent) {
        FlowNode flowNode = flowDefinition.getFlowNode(flowEvent.getFrom());
        if (!flowNode.belongTo(FlowNodeType.CONDITION)) {
            validateBlank(flowEvent.getConditionRule(), "normal node condition rule");
            return;
        }
        Validation.notBlank(flowEvent.getConditionRule(), exception("condition node condition rule"));
    }

    private void validateMetaId(String metaId) {
        Validation.notBlank(metaId, exception("node event metaId"));
        Validation.isFalse(SPECIAL_CHAR_PATTERN.matcher(metaId).find(), exception("node event metaId"));
    }
}
