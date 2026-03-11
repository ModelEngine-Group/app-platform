/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes;

import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowOfferId;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowTrans;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextMessenger;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeType;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.ParallelMode;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.FitStream;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.From;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Identity;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.To;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.callbacks.PreSendCallbackInfo;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 中间节点，既是数据发送者，也是数据接受者
 * 由于没有多重继承，node直接继承自To，并生成一个From，引用所有From的实现
 *
 * @param <T>
 * @param <R>
 * @author 高诗意
 * @since 2023/08/14
 */
public class Node<T, R> extends To<T, R> implements FitStream.Processor<T, R>, Identity {
    private final FitStream.Publisher<R> publisher;

    /**
     * 1->1处理节点
     *
     * @param streamId stream流程ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param locks 流程锁
     */
    public Node(String streamId, Processors.Map<FlowContext<T>, R> processor, FlowContextRepo repo, FlowContextMessenger messenger,
            FlowLocks locks) {
        super(streamId, processor, repo, messenger, locks);
        this.publisher = this.initFrom(repo, messenger, locks);
    }

    public Node(String streamId, Processors.FlatMap<FlowContext<T>, R> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        super(streamId, processor, repo, messenger, locks);
        this.publisher = this.initFrom(repo, messenger, locks);
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
    public Node(String streamId, String nodeId, Processors.Map<FlowContext<T>, R> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks, FlowNodeType nodeType) {
        super(streamId, nodeId, processor, repo, messenger, locks, nodeType);
        this.publisher = this.initFrom(repo, messenger, locks);
    }

    /**
     * m->n处理节点
     *
     * @param streamId stream流程ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文发送器，默认在内存
     * @param locks 流程锁
     */
    public Node(String streamId, Processors.Produce<FlowContext<T>, R> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        super(streamId, processor, repo, messenger, locks);
        this.publisher = this.initFrom(repo, messenger, locks);
    }

    /**
     * m->n处理节点
     *
     * @param streamId stream流程ID
     * @param nodeId stream流程节点ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文发送器，默认在内存
     * @param locks 流程锁
     * @param nodeType 节点类型
     */
    public Node(String streamId, String nodeId, Processors.Produce<FlowContext<T>, R> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks, FlowNodeType nodeType) {
        super(streamId, nodeId, processor, repo, messenger, locks, nodeType);
        this.publisher = this.initFrom(repo, messenger, locks);
    }

    /**
     * n->1 处理节点
     *
     * @param streamId stream流程ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文发送器，默认在内存
     * @param locks 流程锁
     */
    public Node(String streamId, Processors.Reduce<FlowContext<T>, R> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks) {
        super(streamId, processor, repo, messenger, locks);
        this.publisher = this.initFrom(repo, messenger, locks);
    }

    /**
     * n->1 处理节点
     *
     * @param streamId stream流程ID
     * @param nodeId stream流程节点ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文发送器，默认在内存
     * @param locks 流程锁
     * @param nodeType 节点类型
     */
    public Node(String streamId, String nodeId, Processors.Reduce<FlowContext<T>, R> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks, FlowNodeType nodeType) {
        super(streamId, nodeId, processor, repo, messenger, locks, nodeType);
        this.publisher = this.initFrom(repo, messenger, locks);
    }

    /**
     * 1->1处理节点
     *
     * @param streamId stream流程ID
     * @param processor 对应处理器
     * @param repo 上下文持久化repo，默认在内存
     * @param messenger 上下文事件发送器，默认在内存
     * @param publisherSupplier 由子类提供构建publisher的方法
     * @param locks 流程锁
     */
    protected Node(String streamId, Processors.Map<FlowContext<T>, R> processor, FlowContextRepo repo,
            FlowContextMessenger messenger, FlowLocks locks, Supplier<FitStream.Publisher<R>> publisherSupplier) {
        super(streamId, processor, repo, messenger, locks);
        this.publisher = publisherSupplier.get();
    }

    /**
     * initFrom
     *
     * @param repo contextRepo
     * @param messenger messenger
     * @param locks 流程锁
     * @return From<R>
     */
    protected From<R> initFrom(FlowContextRepo repo, FlowContextMessenger messenger, FlowLocks locks) {
        From<R> from = new From<>(this.getStreamId(), repo, messenger, locks); // node里的from跟随subscriber的streamId
        from.setId(this.getId());
        return from;
    }

    @Override
    public <O> FitStream.Processor<O, O> conditions(
            Processors.Just<FlowContext<O>> processor, Processors.Map<R, O> convert, Processors.Whether<R> whether) {
        return this.publisher.conditions(processor, convert, whether);
    }

    @Override
    public <O> FitStream.Processor<O, O> parallel(Processors.Just<FlowContext<O>> processor, ParallelMode mode, Processors.Map<R, O> convert,
            Processors.Whether<R> whether) {
        return this.publisher.parallel(processor, mode, convert, whether);
    }

    @Override
    public <M, O> FitStream.Processor<M, O> join(
            Processors.Reduce<FlowContext<M>, O> processor, Processors.Map<R, M> convert, Processors.Whether<R> whether) {
        return this.publisher.join(processor, convert, whether);
    }

    @Override
    public <O> FitStream.Processor<O, O> just(Processors.Just<FlowContext<O>> processor, Processors.Map<R, O> convert, Processors.Whether<R> whether) {
        return this.publisher.just(processor, convert, whether);
    }

    @Override
    public <M, O> FitStream.Processor<M, O> map(
            Processors.Map<FlowContext<M>, O> processor, Processors.Map<R, M> convert, Processors.Whether<R> whether) {
        return this.publisher.map(processor, convert, whether);
    }

    @Override
    public <M, O> FitStream.Processor<M, O> flatMap(
            Processors.FlatMap<FlowContext<M>, O> processor, Processors.Map<R, M> convert, Processors.Whether<R> whether) {
        return this.publisher.flatMap(processor, convert, whether);
    }

    @Override
    public <M, O> FitStream.Processor<M, O> reduce(
            Processors.Reduce<FlowContext<M>, O> processor, Processors.Map<R, M> convert, Processors.Whether<R> whether) {
        return this.publisher.reduce(processor, convert, whether);
    }

    @Override
    public <M, O> FitStream.Processor<M, O> produce(
            Processors.Produce<FlowContext<M>, O> processor, Processors.Map<R, M> convert, Processors.Whether<R> whether) {
        return this.publisher.produce(processor, convert, whether);
    }

    @Override
    public <O> void subscribe(FitStream.Subscriber<R, O> subscriber) {
        this.publisher.subscribe(subscriber);
    }

    @Override
    public <M, O> void subscribe(
            FitStream.Subscriber<M, O> subscriber, Processors.Map<R, M> convert, Processors.Whether<R> whether) {
        this.publisher.subscribe(subscriber, convert, whether);
    }

    @Override
    public <O> void subscribe(String eventId, FitStream.Subscriber<R, O> subscriber) {
        this.publisher.subscribe(eventId, subscriber);
    }

    @Override
    public <M, O> void subscribe(String eventId, FitStream.Subscriber<M, O> subscriber, Processors.Map<R, M> convert, Processors.Whether<R> whether) {
        this.publisher.subscribe(eventId, subscriber, convert, whether);
    }

    @Override
    public void offer(List<FlowContext<R>> contexts, Consumer<PreSendCallbackInfo<R>> preSendCallback) {
        this.publisher.offer(contexts, preSendCallback);
    }

    @Override
    public FlowOfferId offer(R data) {
        return this.publisher.offer(data);
    }

    @Override
    public FlowOfferId offer(R... data) {
        return this.publisher.offer(data);
    }

    @Override
    public FlowOfferId offer(R data, FlowTrans trans) {
        return this.publisher.offer(data, trans);
    }

    @Override
    public FlowOfferId offer(R[] data, FlowTrans trans) {
        return this.publisher.offer(data, trans);
    }

    @Override
    public boolean subscribed() {
        return this.publisher.subscribed();
    }

    @Override
    public List<FitStream.Subscription<R, ?>> getSubscriptions() {
        return this.publisher.getSubscriptions();
    }

    /**
     * 开始处理数据
     * 把该publisher里所有的数据都publish到subscription
     *
     * @param batchId batchId
     * @param preSendCallback 在数据发送到下一个节点前触发当前节点完成回调操作
     */
    @Override
    public void onNext(String batchId, Consumer<PreSendCallbackInfo<R>> preSendCallback) {
        this.publisher.offer(this.nextContexts(batchId), preSendCallback);
    }

    /**
     * 关闭到最终subscriber
     *
     * @return subscriber
     */
    @Override
    public FitStream.Subscriber<R, R> close() {
        FitStream.Subscriber<R, R> end = new To<>(this.getStreamId(), this.getId(), (Processors.Map<FlowContext<R>, R>) i -> i.getData(),
                getRepo(), getMessenger(), getLocks(), FlowNodeType.END);
        this.subscribe(end);
        return end;
    }
}
