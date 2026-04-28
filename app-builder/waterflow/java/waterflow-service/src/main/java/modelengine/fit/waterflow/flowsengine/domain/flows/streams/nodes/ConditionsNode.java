/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes;

import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.FitStream;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.From;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.callbacks.PreSendCallbackInfo;
import modelengine.fitframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 条件节点，也是match的起始节点
 * parallel节点覆盖了from的subscribe方法，允许subscribe到多个subscriber
 * parallel只允许map处理器，避免复杂设计
 * 辉子 2019-12-17
 *
 * @param <I>传入数据类型
 * @author 高诗意
 * @since 2023/08/14
 */
public class ConditionsNode<I> extends Node<I, I> {
    /**
     * 是否启用 condition 分支跳过机制
     * 启用后，未匹配的分支也会创建 forked context 并标记为 SKIPPED
     */
    private boolean enableConditionSkip = false;

    /**
     * 1->1处理节点
     *
     * @param streamId stream流程ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     */
    public ConditionsNode(String streamId, Processors.Just<FlowContext<I>> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        this(streamId, processor, repo, messenger, locks, false);
    }

    /**
     * 1->1处理节点
     *
     * @param streamId stream流程ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     * @param enableConditionSkip 是否启用 condition 分支跳过机制
     */
    public ConditionsNode(String streamId, Processors.Just<FlowContext<I>> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks, boolean enableConditionSkip) {
        super(streamId, i -> {
            processor.process(i);
            return i.getData();
        }, repo, messenger, locks, () -> initFrom(streamId, repo, messenger, locks, enableConditionSkip));
        this.enableConditionSkip = enableConditionSkip;
    }

    /**
     * 1->1处理节点
     *
     * @param streamId stream流程ID
     * @param nodeId stream流程节点ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     * @param nodeType 节点类型
     */
    public ConditionsNode(String streamId, String nodeId, Processors.Just<FlowContext<I>> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks, FlowNodeType nodeType) {
        this(streamId, nodeId, processor, repo, messenger, locks, nodeType, false);
    }

    /**
     * 1->1处理节点
     *
     * @param streamId stream流程ID
     * @param nodeId stream流程节点ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     * @param nodeType 节点类型
     * @param enableConditionSkip 是否启用 condition 分支跳过机制
     */
    public ConditionsNode(String streamId, String nodeId, Processors.Just<FlowContext<I>> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks, FlowNodeType nodeType, boolean enableConditionSkip) {
        super(streamId, i -> {
            processor.process(i);
            return i.getData();
        }, repo, messenger, locks, () -> initFrom(streamId, repo, messenger, locks, enableConditionSkip));
        this.id = nodeId;
        this.nodeType = nodeType;
        this.enableConditionSkip = enableConditionSkip;
    }

    /**
     * 设置是否启用 condition 分支跳过机制
     *
     * @param enableConditionSkip 是否启用
     * @return 当前节点实例，支持链式调用
     */
    public ConditionsNode<I> setEnableConditionSkip(boolean enableConditionSkip) {
        this.enableConditionSkip = enableConditionSkip;
        return this;
    }

    /**
     * 初始化from节点，并根据订阅者的匹配规则，将匹配的上下文缓存到订阅者中
     * 只publish给符合条件的subscription
     *
     * @param <I> 传入数据类型
     * @param streamId stream流程ID
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     * @param enableConditionSkip 是否启用 condition 分支跳过机制
     * @return 返回初始化后的from节点
     */
    private static <I> From<I> initFrom(String streamId, FlowContextRepo repo, FlowContextMessenger messenger,
            FlowLocks locks, boolean enableConditionSkip) {
        return new From<I>(streamId, repo, messenger, locks) {
            @Override
            public void offer(List<FlowContext<I>> contexts, Consumer<PreSendCallbackInfo<I>> preSendCallback) {
                if (CollectionUtils.isEmpty(contexts) || CollectionUtils.isEmpty(this.getSubscriptions())) {
                    preSendCallback.accept(new PreSendCallbackInfo<>(new LinkedHashMap<>(), contexts));
                    return;
                }
                Map<FitStream.Subscription<I, ?>, List<FlowContext<I>>> matchedContexts = new LinkedHashMap<>();
                List<FlowContext<I>> forkedContexts = new ArrayList<>();
                Set<String> consumedContextIds = new HashSet<>();
                List<FitStream.Subscription<I, ?>> subscriptions = this.getSubscriptions();
                contexts.forEach(context -> {
                    int matchedIndex = -1;
                    for (int index = 0; index < subscriptions.size(); index++) {
                        if (subscriptions.get(index).getWhether().is(context)) {
                            matchedIndex = index;
                            break;
                        }
                    }
                    consumedContextIds.add(context.getId());
                    for (int index = 0; index < subscriptions.size(); index++) {
                        FitStream.Subscription<I, ?> subscription = subscriptions.get(index);
                        boolean useOriginalContext = index == matchedIndex;
                        boolean isSkippedBranch = index != matchedIndex;
                        // 根据 enableConditionSkip 标记决定是否创建 skipped context
                        // enableConditionSkip=true 时，未匹配分支创建 forked context 并标记 skipped
                        // enableConditionSkip=false 时，非 FlowData 类型的未匹配分支直接忽略
                        if (useOriginalContext) {
                            context.setNextPositionId(subscription.getId());
                            matchedContexts.computeIfAbsent(subscription, key -> new ArrayList<>()).add(context);
                        } else if (isSkippedBranch && enableConditionSkip) {
                            FlowContext<I> branchContext = context.fork();
                            branchContext.setNextPositionId(subscription.getId());
                            branchContext.setStatus(FlowNodeStatus.SKIPPED).markSkippedSignal();
                            matchedContexts.computeIfAbsent(subscription, key -> new ArrayList<>()).add(branchContext);
                            forkedContexts.add(branchContext);
                        }
                        // enableConditionSkip=false 且未匹配分支：直接忽略，不创建任何 context
                    }
                });
                List<FlowContext<I>> unMatchedContexts = contexts.stream()
                        .filter(context -> !consumedContextIds.contains(context.getId()))
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(forkedContexts)) {
                    Set<String> traces = forkedContexts.stream()
                            .flatMap(context -> context.getTraceId().stream())
                            .collect(Collectors.toSet());
                    Lock lock = this.locks.getDistributedLock(this.locks.streamNodeLockKey(this.getStreamId(),
                            this.getId(), "ConditionForkContextPool"));
                    lock.lock();
                    try {
                        this.repo.updateContextPool(forkedContexts, traces);
                        this.repo.save(forkedContexts);
                    } finally {
                        lock.unlock();
                    }
                }
                PreSendCallbackInfo<I> callbackInfo = new PreSendCallbackInfo<>(matchedContexts, unMatchedContexts);
                preSendCallback.accept(callbackInfo);
                matchedContexts.forEach(FitStream.Subscription::cache);
            }
        };
    }
}
