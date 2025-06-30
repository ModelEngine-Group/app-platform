/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules;

import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.tasks.FlowTask;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowTaskType;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.tasks.TaskCenterTaskRule;
import modelengine.fit.waterflow.flowsengine.domain.flows.validators.rules.tasks.TaskRule;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 节点手动任务校验规则
 *
 * @author 杨祥宇
 * @since 2023/9/21
 */
@Component
public class TasksRule implements FlowRule {
    private final BrokerClient brokerClient;

    private Map<FlowTaskType, TaskRule> taskRuleMap;

    public TasksRule(BrokerClient brokerClient) {
        this.brokerClient = brokerClient;
        initialTaskRuleMap();
    }

    private void initialTaskRuleMap() {
        taskRuleMap = new HashMap<FlowTaskType, TaskRule>() {
            {
                put(FlowTaskType.APPROVING_TASK, new TaskCenterTaskRule(brokerClient));
                put(FlowTaskType.TASK_CENTER, new TaskCenterTaskRule(brokerClient));
            }
        };
    }

    /**
     * 校验流程定义的合法性
     * 当校验不通过时，抛出运行时异常{@link WaterflowParamException}
     *
     * @param flowDefinition 流程定义实体
     */
    @Override
    public void apply(FlowDefinition flowDefinition) {
        flowDefinition.getNodeIdSet()
                .forEach(nodeId -> Optional.ofNullable(flowDefinition.getFlowNode(nodeId).getTask())
                        .ifPresent(this::apply));
    }

    private void apply(FlowTask flowTask) {
        Validation.notNull(flowTask.getTaskType(), exception("flow task type"));
        Validation.notBlank(flowTask.getTaskId(),
                exception(StringUtils.format("flow {0} task id", flowTask.getTaskType().getCode())));
        Optional.ofNullable(taskRuleMap.get(flowTask.getTaskType())).ifPresent(taskRule -> taskRule.apply(flowTask));
    }
}
