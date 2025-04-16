/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes;

import static modelengine.fit.waterflow.ErrorCodes.FLOW_NODE_OPERATOR_NOT_SUPPORT;
import static modelengine.fitframework.util.ObjectUtils.cast;

import lombok.Getter;
import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.common.Constant;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes.events.FlowEvent;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.ParallelMode;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.FitStream;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.JoinNode;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.ParallelNode;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 流程定义并行节点核心类
 * 流程实例流转时需要构建该对象
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Getter
public class FlowParallelNode extends FlowNode {
    private static final Logger log = Logger.get(FlowParallelNode.class);

    private static final String PARALLEL_NODE_ID_SUFFIX = "parallel";

    private static final String JOIN_NODE_ID_SUFFIX = "join";

    private static final String PARALLEL_MODE = "mode";

    private final List<FlowNode> forks = new ArrayList<>();

    private FitStream.Processor<FlowData, FlowData> joiner;

    /**
     * 获取节点内的processor
     * 一个parallel节点产生了两个processor，链式链接时入口是processor，出口是joiner
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
            this.processor = new ParallelNode<>(streamId, this.getMetaId(PARALLEL_NODE_ID_SUFFIX), this::parallelJuster,
                    ParallelMode.parseFrom(cast(properties.get(PARALLEL_MODE))), repo, messenger, locks, this.type);
            this.processor.onError(errorHandler(streamId));
            this.joiner = new JoinNode<>(streamId, this.getMetaId(JOIN_NODE_ID_SUFFIX), this::joinReducer, repo,
                    messenger, locks, FlowNodeType.JOIN);
            this.joiner.onError(errorHandler(streamId));
            this.forks.forEach(fork -> {
                FitStream.Processor<FlowData, FlowData> forkProcessor = fork.getProcessor(streamId, repo, messenger,
                        locks);
                this.processor.subscribe(forkProcessor);
                forkProcessor.subscribe(this.joiner);
            });
        }
        return this.processor;
    }

    @Override
    protected void subscribe(FitStream.Publisher<FlowData> from, FitStream.Subscriber<FlowData, FlowData> to,
            FlowEvent event) {
        this.joiner.subscribe(event.getMetaId(), to);
    }

    private void parallelJuster(FlowContext<FlowData> input) {
        throw new WaterflowException(FLOW_NODE_OPERATOR_NOT_SUPPORT, this.metaId, this.type, "parallelJuster");
    }

    private FlowData joinReducer(List<FlowContext<FlowData>> inputs) {
        throw new WaterflowException(FLOW_NODE_OPERATOR_NOT_SUPPORT, this.metaId, this.type, "joinReducer");
    }

    private String getMetaId(String suffix) {
        return StringUtils.join(Constant.STREAM_ID_SEPARATOR, this.metaId, suffix);
    }
}
