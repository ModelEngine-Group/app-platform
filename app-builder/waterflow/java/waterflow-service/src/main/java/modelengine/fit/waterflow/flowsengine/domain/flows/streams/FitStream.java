/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.streams;

import modelengine.fit.waterflow.flowsengine.domain.flows.InterStream;
import modelengine.fit.waterflow.flowsengine.domain.flows.InterStreamHandler;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowOfferId;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowTrans;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.ParallelMode;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.ProcessType;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.callbacks.PreSendCallbackInfo;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.Blocks;

import java.util.List;
import java.util.function.Consumer;

/**
 * FIT STREAM基本响应式模型
 * 主要由标准的：publisher,subscriber,subscription三个组成
 * 辉子 2019-10-31
 *
 * @author 高诗意
 * @since 2023/08/14
 */
public final class FitStream {
    /**
     * StreamIdentity
     *
     * @since 2023-09-15
     */
    public interface StreamIdentity extends Identity {
        /**
         * getStreamId
         *
         * @return String
         */
        String getStreamId();
    }

    /**
     * 数据生产者
     *
     * @param <T> 待生产数据类型
     */
    public interface Source<T> extends Identity {
        /**
         * mono
         *
         * @param invoker invoker
         * @return Publisher<T>
         */
        Publisher<T> mono(Processors.Invoke<T> invoker);

        /**
         * flux
         *
         * @param invoker invoker
         * @return Publisher<T>
         */
        Publisher<T> flux(Processors.ArrayInvoke<T> invoker);

        /**
         * produce
         */
        void produce();
    }

    /**
     * 数据发布者
     * 发布数据的时候同时确定接收者的数据处理方式
     * map: 1:1数据处理方式
     * reduce: n:1数据处理方式
     * produce: m:n数据处理方式
     *
     * @param <I> 要发布的数据类型
     */
    public interface Publisher<I> extends StreamIdentity, InterStreamHandler<I> {
        /**
         * handle
         *
         * @param data data
         */
        default void handle(I data) {
            this.offer(data);
        }

        /**
         * handle
         *
         * @param data data
         * @param token token
         */
        default void handle(I data, String token) {
            this.offer(data, new FlowTrans(token));
        }

        /**
         * handle
         *
         * @param data data
         * @param token token
         */
        default void handle(I[] data, String token) {
            this.offer(data, new FlowTrans(token));
        }

        /**
         * conditions
         *
         * @param processor processor
         * @param convert convert
         * @param whether whether
         * @return Processor<O, O>
         */
        <O> Processor<O, O> conditions(Processors.Just<FlowContext<O>> processor, Processors.Map<I, O> convert,
                Processors.Whether<I> whether);

        /**
         * parallel
         *
         * @param processor processor
         * @param mode mode
         * @param convert convert
         * @param whether whether
         * @return Processor<O, O>
         */
        <O> Processor<O, O> parallel(Processors.Just<FlowContext<O>> processor, ParallelMode mode,
                Processors.Map<I, O> convert, Processors.Whether<I> whether);

        /**
         * join
         *
         * @param processor processor
         * @param convert convert
         * @param whether whether
         * @return Processor<M, O>
         */
        <M, O> Processor<M, O> join(Processors.Reduce<FlowContext<M>, O> processor, Processors.Map<I, M> convert,
                Processors.Whether<I> whether);

        /**
         * just
         *
         * @param processor processor
         * @param convert convert
         * @param whether whether
         * @return Processor<O, O>
         */
        <O> Processor<O, O> just(Processors.Just<FlowContext<O>> processor, Processors.Map<I, O> convert,
                Processors.Whether<I> whether);

        /**
         * map
         *
         * @param processor processor
         * @param convert convert
         * @param whether whether
         * @return Processor<M, O>
         */
        <M, O> Processor<M, O> map(Processors.Map<FlowContext<M>, O> processor, Processors.Map<I, M> convert,
                Processors.Whether<I> whether);

        /**
         * flat map 1到m expression，包装一个flatMap processor
         *
         * @param processor processor
         * @param convert convert
         * @param whether whether
         * @return Processor<M, O>
         */
        <M, O> Processor<M, O> flatMap(Processors.FlatMap<FlowContext<M>, O> processor, Processors.Map<I, M> convert,
                Processors.Whether<I> whether);

        /**
         * reduce
         *
         * @param processor processor
         * @param convert convert
         * @param whether whether
         * @return Processor<M, O>
         */
        <M, O> Processor<M, O> reduce(Processors.Reduce<FlowContext<M>, O> processor, Processors.Map<I, M> convert,
                Processors.Whether<I> whether);

        /**
         * produce
         *
         * @param processor processor
         * @param convert convert
         * @param whether whether
         * @return Processor<M, O>
         */
        <M, O> Processor<M, O> produce(Processors.Produce<FlowContext<M>, O> processor, Processors.Map<I, M> convert,
                Processors.Whether<I> whether);

        /**
         * subscribe
         *
         * @param subscriber subscriber
         */
        <O> void subscribe(Subscriber<I, O> subscriber);

        /**
         * subscribe
         *
         * @param subscriber subscriber
         * @param convert convert
         * @param whether whether
         */
        <M, O> void subscribe(Subscriber<M, O> subscriber, Processors.Map<I, M> convert, Processors.Whether<I> whether);

        /**
         * subscribe
         *
         * @param eventId eventId
         * @param subscriber subscriber
         */
        <O> void subscribe(String eventId, Subscriber<I, O> subscriber);

        /**
         * subscribe
         *
         * @param eventId eventId
         * @param subscriber subscriber
         * @param convert convert
         * @param whether whether
         */
        <M, O> void subscribe(String eventId, Subscriber<M, O> subscriber, Processors.Map<I, M> convert,
                Processors.Whether<I> whether);

        /**
         * offer
         *
         * @param contexts contexts
         * @param preSendCallback 在数据发送到下一个节点前触发当前节点完成回调操作
         */
        void offer(List<FlowContext<I>> contexts, Consumer<PreSendCallbackInfo<I>> preSendCallback);

        /**
         * offer
         *
         * @param data data
         * @return String
         */
        FlowOfferId offer(I data);

        /**
         * offer
         *
         * @param data data
         * @param trans trans
         * @return FlowOfferId
         */
        FlowOfferId offer(I data, FlowTrans trans);

        /**
         * offer
         *
         * @param data data
         * @return String
         */
        FlowOfferId offer(I... data);

        /**
         * offer
         *
         * @param data data
         * @param trans trans
         * @return {@link FlowOfferId}
         */
        FlowOfferId offer(I[] data, FlowTrans trans);

        /**
         * subscribed
         *
         * @return boolean
         */
        boolean subscribed();

        /**
         * getSubscriptions
         *
         * @return List<Subscription < I, ?>>
         */
        List<Subscription<I, ?>> getSubscriptions();
    }

    /**
     * 数据接受者，processor数据在接收者处的处理方式
     *
     * @param <I> 接收的数据类型
     * @param <O> 处理后的数据类型
     */
    public interface Subscriber<I, O> extends StreamIdentity, InterStream<O> {
        /**
         * 节点接收请求事件入口
         *
         * @param type 处理类型
         * @param contexts 待处理的上下文
         */
        void accept(ProcessType type, List<FlowContext<I>> contexts);

        /**
         * 设置节点block
         *
         * @param block block
         */
        void block(Blocks.Block<I> block);

        /**
         * 获取节点block
         *
         * @return block
         */
        Blocks.Block<I> block();

        /**
         * 设置节点preFilter
         *
         * @param filter filter
         */
        void preFilter(Processors.Filter<I> filter);

        /**
         * 获取节点preFilter
         *
         * @return preFilter
         */
        Processors.Filter<I> preFilter();

        /**
         * 设置节点postFilter
         *
         * @param filter filter
         */
        void postFilter(Processors.Filter<I> filter);

        /**
         * 获取节点postFilter
         *
         * @return postFilter
         */
        Processors.Filter<I> postFilter();

        /**
         * onSubscribe
         *
         * @param subscription subscription
         */
        void onSubscribe(Subscription<?, I> subscription);

        /**
         * 节点真正处理context方法onProcess
         *
         * @param contexts contexts
         */
        void onProcess(List<FlowContext<I>> contexts);

        /**
         * onNext
         *
         * @param batchId batchId
         * @param preSendCallback 在数据发送到下一个节点前触发当前节点完成回调操作
         */
        void onNext(String batchId, Consumer<PreSendCallbackInfo<O>> preSendCallback);

        /**
         * afterProcess
         *
         * @param pre pre
         * @param after after
         */
        void afterProcess(List<FlowContext<I>> pre, List<FlowContext<O>> after);

        /**
         * onComplete
         *
         * @param callback callback
         */
        void onComplete(Processors.Just<Callback<FlowContext<O>>> callback);

        /**
         * isAuto
         *
         * @return Boolean
         */
        Boolean isAuto();

        /**
         * nextContexts
         *
         * @param batchId batchId
         * @return List<FlowContext < O>>
         */
        List<FlowContext<O>> nextContexts(String batchId);

        /**
         * onError
         *
         * @param handler handler
         */
        void onError(Processors.Error<I> handler);

        /**
         * onGlobalError
         *
         * @param handler handler
         */
        void onGlobalError(Processors.Error handler);

        /**
         * 设置获取节点执行前信息的回调
         *
         * @param handler handler
         */
        void onGlobalBefore(Processors.Just<Callback<FlowContext<I>>> handler);

        /**
         * 设置获取节点执行后信息的回调
         *
         * @param handler handler
         */
        void onGlobalAfter(Processors.Just<Callback<FlowContext<O>>> handler);

        /**
         * 设置节点优先级
         *
         * @param order 优先级
         */
        void setOrder(int order);

        /**
         * 设置FanIn模式
         *
         * @param fanInMode FanIn模式
         */
        void setFanInMode(To.FanInMode fanInMode);

        /**
         * 设置为ALL模式，强制等待所有输入数据到齐后再处理
         */
        default void setAllMode() {
        }

        /**
         * 设置为ANY模式，有数据到达即处理
         */
        default void setAnyMode() {
        }
    }

    /**
     * publisher与subscriber之间的连接器
     * 其中包含了两者传递的一些要求信息：处理多少数据，如何过滤数据
     * subscriber来不及处理的数据或者block的数据在subscription中缓存
     *
     * @param <I> 接收的数据类型
     * @param <O> 转换后数据类型
     */
    public interface Subscription<I, O> extends StreamIdentity {
        /**
         * cache
         *
         * @param contexts contexts
         */
        void cache(List<FlowContext<I>> contexts);

        /**
         * getWhether
         *
         * @return Whether<I>
         */
        Processors.Whether<I> getWhether();

        /**
         * getTo
         *
         * @return Subscriber<O, R>
         */
        <R> Subscriber<O, R> getTo();
    }

    /**
     * 既是发布者，也是接收者：处理完数据后再发给下一个接受者
     *
     * @param <T> 接收的数据类型
     * @param <R> 处理后的数据类型
     */
    public interface Processor<T, R> extends Publisher<R>, Subscriber<T, R> {
        /**
         * close
         *
         * @return Subscriber<R, R>
         */
        Subscriber<R, R> close();
    }

    /**
     * 用于流结束后的返回对象处理
     *
     * @param <O> 处理的对象类型
     */
    public interface Callback<O> {
        /**
         * getAll
         *
         * @return List<O>
         */
        List<O> getAll();

        /**
         * get
         *
         * @return O
         */
        O get();
    }
}
