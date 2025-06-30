/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers;

import com.alibaba.fastjson.JSONObject;

import lombok.RequiredArgsConstructor;
import modelengine.fit.waterflow.ErrorCodes;
import modelengine.fit.waterflow.exceptions.WaterflowParamException;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.FlowNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.callbacks.FlowCallback;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowCallbackType;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowDefinitionStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.NodeParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.callbacks.CallbackParser;
import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.nodes.events.EventParser;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.inspection.Validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static modelengine.fit.waterflow.ErrorCodes.INPUT_PARAM_IS_INVALID;

/**
 * 流程定义JSON解析器
 * 负责解析JSON类型的流程数据
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Component
@RequiredArgsConstructor
public class FlowParser implements Parser {
    private final BrokerClient brokerClient;

    @Override
    public FlowDefinition parse(String flowDefinition) {
        Validation.notBlank(flowDefinition, () -> new WaterflowParamException(INPUT_PARAM_IS_INVALID, "flowDefinition"));
        FlowGraphData flowGraphData = new FlowGraphData(flowDefinition);
        Map<String, FlowNode> allNodeMap = parseFlowNode(flowGraphData);

        EventParser.INSTANCE.parse(flowGraphData, allNodeMap);

        return FlowDefinition.builder()
                .name(flowGraphData.getFlowName())
                .metaId(flowGraphData.getFlowMetaId())
                .version(flowGraphData.getFlowVersion())
                .status(FlowDefinitionStatus.getFlowDefinitionStatus(flowGraphData.getFlowStatus()))
                .description(flowGraphData.getFlowDescription())
                .properties(flowGraphData.getFlowProperties())
                .callback(this.parseFlowCallback(flowGraphData).orElse(null))
                .exceptionFitables(flowGraphData.getFlowExceptionFitables())
                .nodeMap(allNodeMap)
                .finishedCallbackFitables(flowGraphData.getFinishedCallbackFitables())
                .build();
    }

    private Map<String, FlowNode> parseFlowNode(FlowGraphData flowGraphData) {
        Map<String, FlowNode> allNodeMap = new HashMap<>();
        IntStream.range(0, flowGraphData.getNodes()).forEach(nodeIndex -> {
            FlowNodeType nodeType = FlowNodeType.getNodeType(flowGraphData.getNodeType(nodeIndex));
            NodeParser nodeParser = nodeType.getNodeParser();
            Validation.notNull(nodeParser,
                    () -> new WaterflowParamException(INPUT_PARAM_IS_INVALID, "flow node type " + nodeType.getCode()));
            FlowNode flowNode = nodeParser.parseNode(flowGraphData, nodeIndex);
            flowNode.setBrokerClient(brokerClient);
            if (Optional.ofNullable(flowNode.getJober()).isPresent()) {
                flowNode.getJober().setBrokerClient(brokerClient);
            }
            if (Optional.ofNullable(flowNode.getCallback()).isPresent()) {
                flowNode.getCallback().setBrokerClient(brokerClient);
            }
            if (allNodeMap.containsKey(flowNode.getMetaId())) {
                throw new WaterflowParamException(INPUT_PARAM_IS_INVALID, "flow node metaId not allow same");
            }
            allNodeMap.put(flowNode.getMetaId(), flowNode);
        });
        return allNodeMap;
    }

    private Optional<FlowCallback> parseFlowCallback(FlowGraphData flowGraphData) {
        JSONObject flowCallback = flowGraphData.getFlowCallback();
        if (flowCallback == null) {
            return Optional.empty();
        }

        Optional<String> flowCallbackTypeOption = flowGraphData.getFlowCallbackType();
        if (!flowCallbackTypeOption.isPresent()) {
            return Optional.empty();
        }
        FlowCallbackType callbackType = FlowCallbackType.getCallbackType(flowCallbackTypeOption.get());
        CallbackParser callbackParser = callbackType.getCallbackParser();
        Validation.notNull(callbackParser,
                () -> new WaterflowParamException(INPUT_PARAM_IS_INVALID, "flow callback type " + callbackType.getCode()));
        FlowCallback result = callbackParser.parseCallback(flowGraphData);
        result.setBrokerClient(this.brokerClient);
        return Optional.of(result);
    }
}
