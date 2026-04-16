/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.streams;

import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.nodes.Retryable;

import java.util.List;

/**
 * 数据处理器类型
 * 总共有：只处理不改变类型；处理并改变类型；m条数据处理生成n条其他数据；m条数据处理生成1条其他数据
 * 辉子 2019.10.31
 *
 * @author 高诗意
 * @since 2023/08/14
 */
public final class Processors {
    /**
     * 节点处理一个原材料->一个产品的生产过程
     *
     * @param <T>原材料类型
     * @param <R>产品类型
     */
    @FunctionalInterface
    public interface Map<T, R> {
        /**
         * process
         *
         * @param input input
         * @return R
         */
        R process(T input);
    }

    /**
     * 节点处理1个原材料到m个产品的过程
     *
     * @param <T> 原材料类型
     * @param <R> 产品类型
     */
    @FunctionalInterface
    public interface FlatMap<T, R> {
        /**
         * process
         *
         * @param input input
         * @return List<R>
         */
        List<R> process(T input);
    }

    /**
     * 节点处理m个原材料->一个产品的生产过程
     *
     * @param <T>原材料类型
     * @param <R>产品类型
     */
    @FunctionalInterface
    public interface Reduce<T, R> {
        /**
         * process
         *
         * @param input input
         * @return R
         */
        R process(List<T> input);
    }

    /**
     * 节点处理m个原材料到n个产品的过程
     *
     * @param <T> 原材料类型
     * @param <R> 产品类型
     */
    @FunctionalInterface
    public interface Produce<T, R> {
        /**
         * process
         *
         * @param input input
         * @return List<R>
         */
        List<R> process(List<T> input);
    }

    /**
     * 对原材料进行加工
     *
     * @param <T> 需要加工的原材料类型
     */
    @FunctionalInterface
    public interface Just<T> {
        /**
         * process
         *
         * @param input input
         */
        void process(T input);
    }

    /**
     * 单原材料生产
     *
     * @param <T> 返回原材料
     */
    @FunctionalInterface
    public interface Invoke<T> {
        /**
         * invoke
         *
         * @return T
         */
        T invoke();
    }

    /**
     * 多原材料生产
     *
     * @param <T> 返回原材料
     */
    @FunctionalInterface
    public interface ArrayInvoke<T> {
        /**
         * invoke
         *
         * @return T[]
         */
        T[] invoke();
    }

    /**
     * Error
     *
     * @since 2023-09-15
     */
    @FunctionalInterface
    public interface Error<T> {
        /**
         * handle
         *
         * @param exception exception
         * @param retryable retryable
         * @param contexts contexts
         */
        void handle(Exception exception, Retryable<T> retryable, List<FlowContext<T>> contexts);
    }

    /**
     * 用于when的条件判定
     *
     * @param <T>
     */
    @FunctionalInterface
    public interface Whether<T> {
        /**
         * is
         *
         * @param input input
         * @return boolean
         */
        boolean is(FlowContext<T> input);
    }

    /**
     * 根据筛选条件选取符合要求的原材料，用于群体筛选
     *
     * @param <T> 原材料类型
     */
    @FunctionalInterface
    public interface Filter<T> {
        /**
         * process
         *
         * @param input input
         * @return List<FlowContext < T>>
         */
        List<FlowContext<T>> process(List<FlowContext<T>> input);
    }

    /**
     * 用于单条验证原材料是否符合生产标准
     *
     * @param <T>
     */
    @FunctionalInterface
    public interface Validator<T> {
        /**
         * check
         *
         * @param input input
         * @param inputs inputs
         * @return boolean
         */
        boolean check(FlowContext<T> input, List<FlowContext<T>> inputs);
    }

    /**
     * 多输入数据合并器，用于 fan-in 场景下将多条输入合并为单条处理
     *
     * @param <T> 输入数据类型
     */
    @FunctionalInterface
    public interface Merger<T> {
        /**
         * 将多条输入上下文合并为单条输出上下文
         *
         * @param contexts 待合并的上下文列表
         * @return 合并后的单个上下文，如果无需合并则返回 null
         */
        FlowContext<T> merge(List<FlowContext<T>> contexts);
    }
}

