/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes;

import static modelengine.fitframework.util.ObjectUtils.cast;

import lombok.Getter;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.MappingFromType;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.MappingNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.MappingNodeType;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter.MappingProcessorFactory;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.events.FlowEvent;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.FitStream;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.ConditionsNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.utils.FlowExecuteInfoUtil;
import modelengine.fit.waterflow.flowsengine.utils.OhScriptExecutor;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 流程定义条件节点核心类
 * 流程实例流转时需要构建该对象
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Getter
public class FlowConditionNode extends FlowNode {
    private static final Logger log = Logger.get(FlowConditionNode.class);
    private static final String CONDITIONS_KEY = "conditions";
    private static final String CONDITION_EXECUTE_INFO_TYPE = "condition";
    private static final String CONDITION_KEY = "condition";
    private static final String CONDITION_RELATION_KEY = "conditionRelation";
    private static final String BRANCHES_KEY = "branches";
    private static final String VALUE_KEY = "value";
    private static final String NAME_KEY = "name";
    private static final String TYPE_KEY = "type";

    /**
     * 获取节点内的processor
     * 只有普通节点需要实现
     *
     * @param streamId stream流程Id
     * @param repo stream流程上下文repo
     * @param messenger stream流程事件发送器
     * @param locks 流程锁
     * @return {@link FitStream.Processor}
     */
    @Override
    public FitStream.Processor<FlowData, FlowData> getProcessor(String streamId, FlowContextRepo<FlowData> repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        if (!Optional.ofNullable(this.processor).isPresent()) {
            this.processor = new ConditionsNode<>(streamId, this.metaId, this::conditionalJuster, repo, messenger,
                    locks, this.type);
            this.processor.onError(errorHandler(streamId));
            setCallback(this.processor, messenger);
            setGlobalTrace(this.processor, messenger);
        }
        return this.processor;
    }

    @Override
    protected void subscribe(
            FitStream.Publisher<FlowData> from, FitStream.Subscriber<FlowData, FlowData> to, FlowEvent event) {
        from.subscribe(event.getMetaId(), to, null, this.getWhether(from.getStreamId(), event));
            to.setAllMode();
    }

    private Processors.Whether<FlowData> getWhether(String streamId, FlowEvent event) {
        log.info("[flowEngines] stream {} condition node {} with origin rule {}", streamId, this.metaId,
                event.getConditionRule());

        return (input) -> {
            String conditionRule = event.getConditionRule();
            log.info("[flowEngines] stream {} condition node {} with rule {}", streamId, this.metaId, conditionRule);
            return OhScriptExecutor.evaluateConditionRule(input.getData(), conditionRule);
        };
    }

    private void conditionalJuster(FlowContext<FlowData> input) {
        Optional.ofNullable(this.jober)
                .ifPresent(flowJober -> flowJober.execute(Collections.singletonList(input.getData())));

        Map<String, Object> businessData = input.getData().getBusinessData();
        Map<String, Object> conditionParams = cast(
                Optional.ofNullable(this.properties.get("conditionParams")).orElse(new HashMap<>()));

        Map<String, Object> resultMap = generateResultMap(conditionParams, businessData);

        FlowExecuteInfoUtil.addInputMap2ExecuteInfoMap(input.getData(), resultMap, this.getMetaId(),
                CONDITION_EXECUTE_INFO_TYPE);
    }

    private Map<String, Object> generateResultMap(Map<String, Object> conditionParams,
            Map<String, Object> businessData) {
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> branches = cast(
                Optional.ofNullable(conditionParams.get(BRANCHES_KEY)).orElse(new ArrayList<>()));
        List<Map<String, Object>> resultBranches = new ArrayList<>();

        for (Map<String, Object> branch : branches) {
            resultBranches.add(generateResultBranch(branch, businessData));
        }

        resultMap.put(BRANCHES_KEY, resultBranches);
        return resultMap;
    }

    private Map<String, Object> generateResultBranch(Map<String, Object> branch, Map<String, Object> businessData) {
        Map<String, Object> resultBranch = new HashMap<>();
        List<Map<String, Object>> conditions = cast(
                Optional.ofNullable(branch.get(CONDITIONS_KEY)).orElse(new ArrayList<>()));
        List<Map<String, Object>> resultConditions = new ArrayList<>();

        for (Map<String, Object> condition : conditions) {
            resultConditions.add(generateResultCondition(condition, businessData));
        }

        resultBranch.put(CONDITIONS_KEY, resultConditions);
        resultBranch.put(CONDITION_RELATION_KEY, branch.get(CONDITION_RELATION_KEY));
        return resultBranch;
    }

    private Map<String, Object> generateResultCondition(Map<String, Object> condition,
            Map<String, Object> businessData) {
        Map<String, Object> resultCondition = new HashMap<>();
        resultCondition.put(CONDITION_KEY, condition.get(CONDITION_KEY));

        List<Map<String, Object>> conditionValueList = cast(
                Optional.ofNullable(condition.get("value")).orElse(new ArrayList<>()));

        for (Map<String, Object> conditionValue : conditionValueList) {
            processConditionValue(resultCondition, conditionValue, businessData);
        }

        return resultCondition;
    }

    private void processConditionValue(Map<String, Object> resultCondition, Map<String, Object> conditionValue,
            Map<String, Object> businessData) {
        String referenceNode = cast(Optional.ofNullable(conditionValue.get("referenceNode")).orElse(""));

        if (!StringUtils.isBlank(referenceNode)) {
            List<String> nameList = new ArrayList<>(cast(conditionValue.get(VALUE_KEY)));
            nameList.add(0, referenceNode);
            String newConditionValueName = StringUtils.join(".", nameList);

            MappingNode result = MappingNode.builder()
                    .name(cast(conditionValue.get(NAME_KEY)))
                    .type(MappingNodeType.get(cast(conditionValue.get(TYPE_KEY))))
                    .from(MappingFromType.get(cast(conditionValue.get("from"))))
                    .value(conditionValue.get(VALUE_KEY))
                    .referenceNode(referenceNode)
                    .build();

            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("key", newConditionValueName);
            conditionMap.put(TYPE_KEY, conditionValue.get(TYPE_KEY));
            conditionMap.put(VALUE_KEY, MappingProcessorFactory.get(result).generate(result, businessData));
            resultCondition.put(cast(conditionValue.get(NAME_KEY)), conditionMap);
        } else {
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put(TYPE_KEY, conditionValue.get(TYPE_KEY));
            conditionMap.put(VALUE_KEY, conditionValue.get(VALUE_KEY));
            resultCondition.put(cast(conditionValue.get(NAME_KEY)), conditionMap);
        }
    }
}
