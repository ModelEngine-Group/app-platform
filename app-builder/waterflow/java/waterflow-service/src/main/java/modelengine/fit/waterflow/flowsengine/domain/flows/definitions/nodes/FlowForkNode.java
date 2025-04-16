/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.definitions.nodes;

import lombok.Getter;
import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.FitStream;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.Node;
import modelengine.fitframework.log.Logger;

import java.util.Optional;

import static modelengine.fit.waterflow.ErrorCodes.FLOW_NODE_OPERATOR_NOT_SUPPORT;

/**
 * 流程定义结束节点核心类
 * 流程实例流转时需要构建该对象
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Getter
public class FlowForkNode extends FlowNode {
    private static final Logger log = Logger.get(FlowForkNode.class);

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
        if (!Optional.ofNullable(processor).isPresent()) {
            this.processor = new Node<>(streamId, this.metaId, this::forkJuster, repo, messenger, locks, this.type);
            this.processor.onError(errorHandler(streamId));
        }
        return this.processor;
    }

    private FlowData forkJuster(FlowContext<FlowData> input) { // fork节点是特殊的mapper，本质是juster，处理完input并将input返回
        throw new WaterflowException(FLOW_NODE_OPERATOR_NOT_SUPPORT, this.metaId, this.type, "forkJuster");
    }
}
