/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes;

import static modelengine.fitframework.util.ObjectUtils.cast;

import com.alibaba.fastjson.JSON;

import lombok.Getter;
import modelengine.fit.waterflow.common.Constant;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.FitStream;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.Blocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.Node;
import modelengine.fitframework.log.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 流程定义状态节点核心类
 * 流程实例流转时需要构建该对象
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Getter
public class FlowStateNode extends FlowNode {
    private static final Logger log = Logger.get(FlowStateNode.class);

    private static final String VARIABLE_REGEX = "\\{\\{(.+?)}}";

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
            Node<FlowData, FlowData> node = new Node<>(streamId, this.metaId, this::stateProduce, repo, messenger,
                    locks, this.type);
            if (!Objects.isNull(this.jober)) {
                node.setIsAsyncJob(this.jober.isAsync());
            }
            this.processor = node;
            this.processor.onError(errorHandler(streamId));
            if (!triggerMode.isAuto()) {
                this.processor.block(new Blocks.FilterBlock<>());
                Optional.ofNullable(this.taskFilter).ifPresent(f -> this.processor.preFilter(f.filter()));
            }
            Optional.ofNullable(this.joberFilter).ifPresent(f -> this.processor.postFilter(f.filter()));
            setCallback(this.processor, messenger);
            setGlobalTrace(this.processor, messenger);
        }
        return this.processor;
    }

    private List<FlowData> stateProduce(List<FlowContext<FlowData>> inputs) {
        addContextData(inputs);
        List<FlowData> flowDataList = inputs.stream().map(FlowContext::getData).collect(Collectors.toList());
        if (Objects.isNull(this.jober)) {
            return setFlowMetaToContextData(flowDataList);
        }
        log.warn("stateProduce before");
        List<FlowData> executeResult = this.jober.execute(flowDataList);
        log.warn("stateProduce after");
        return setFlowMetaToContextData(executeResult);
    }

    private List<FlowData> setFlowMetaToContextData(List<FlowData> inputs) {
        String output = cast(properties.get("flowContext"));

        Optional.ofNullable(output).ifPresent(o -> {
            String outputKey = output.replaceAll(VARIABLE_REGEX, "$1");
            inputs.forEach(input -> {
                Map<String, String> returnMeta = new HashMap<>();
                returnMeta.put(Constant.OPERATOR_KEY, input.getOperator());
                String metaString = JSON.toJSONString(returnMeta);
                input.getContextData().put(outputKey, metaString);
            });
        });

        return inputs;
    }
}
