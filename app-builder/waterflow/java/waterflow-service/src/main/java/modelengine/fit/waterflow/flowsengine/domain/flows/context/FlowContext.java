/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.context;

import lombok.Getter;
import lombok.Setter;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.IdGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * stream里承载数据上下文
 *
 * @since 2023/08/14
 */
public final class FlowContext<T> extends IdGenerator {
    /**
     * 通过from.offer(data)而不是.offer(context)发起的数据会新增一个trace，这个trace会延续到flow end
     */

    @Getter
    private final Set<String> traceId;

    /**
     * 数据发起的起始节点:上任何一个publisher都可以发起数据，不一定是stream的起始节点
     * 只要通过from.offer(data)而不是.offer(context)的都是数据发起节点
     */
    @Getter
    private final String rootId;

    /**
     * 得到所处stream id
     */
    @Getter
    private final String streamId;

    /**
     * 上下文里带的数据，这是关键属性
     */
    @Getter
    @Setter
    private T data;

    /**
     * 上下文当前所处的位置:那个处理节点上
     */
    @Getter
    private String position;

    /**
     * 如果是parallel节点，是否join了
     */
    @Getter
    private boolean joined;

    /**
     * 上下文当前的状态值
     */
    @Getter
    private FlowNodeStatus status = FlowNodeStatus.NEW;

    /**
     * 上下文所有的transaction
     * 当数据是一个数据的时候，这个数组在一个FlowTransId里，在reduce，produce时默认统一处理
     */
    @Getter
    @Setter
    private FlowTrans trans;

    /**
     * 上下文所处的平行节点id：如果该上下文在平行节点才有效
     */
    @Getter
    private String parallel;

    /**
     * 所处平行节点模式，是either还是all
     */
    @Getter
    private String parallelMode;

    /**
     * 来源于哪个context
     */
    @Getter
    @Setter
    private String previous;

    /**
     * context的生产批次ID
     */
    @Getter
    private String batchId;

    /**
     * 转向哪个context批次
     */
    @Getter
    private String toBatch;

    /**
     * 标记该context是否已经发出事件
     */
    @Getter
    @Setter
    private boolean sent;

    /**
     * 当前context创建时间
     */
    @Getter
    @Setter
    private LocalDateTime createAt;

    /**
     * 当前context更新时间
     */
    @Getter
    @Setter
    private LocalDateTime updateAt;

    /**
     * 当前context处理完成时间
     */
    @Getter
    @Setter
    private LocalDateTime archivedAt;

    /**
     * 当前context接下来要走到位置：可以是连线或者节点id
     */
    @Setter
    @Getter
    private String nextPositionId;

    public FlowContext(String streamId, String rootId, T data, Set<String> traceId, String position) {
        this(streamId, rootId, data, traceId, position, "", "", LocalDateTime.now());
    }

    public FlowContext(String streamId, String rootId, T data, Set<String> traceId, String position, String parallel,
            String parallelMode, LocalDateTime createAt) {
        this.streamId = streamId;
        this.rootId = rootId;
        this.data = data;
        this.traceId = new HashSet<>();
        this.traceId.addAll(traceId);
        this.position = position;
        this.parallel = parallel;
        this.parallelMode = parallelMode;
        this.trans = new FlowTrans();
        this.createAt = createAt;
    }

    /**
     * setPosition
     *
     * @param position position
     * @return FlowContext<T>
     */
    public FlowContext<T> setPosition(String position) {
        this.position = position;
        return this;
    }

    /**
     * setStatus
     *
     * @param status status
     * @return FlowContext<T>
     */
    public FlowContext<T> setStatus(FlowNodeStatus status) {
        this.status = status;
        return this;
    }

    /**
     * setParallel
     *
     * @param parallel parallel
     * @return FlowContext<T>
     */
    public FlowContext<T> setParallel(String parallel) {
        this.parallel = parallel;
        return this;
    }

    /**
     * setParallelMode
     *
     * @param parallelMode parallelMode
     * @return FlowContext<T>
     */
    public FlowContext<T> setParallelMode(String parallelMode) {
        this.parallelMode = parallelMode;
        return this;
    }

    /**
     * batchId
     *
     * @param batchId batchId
     * @return FlowContext<T>
     */
    public FlowContext<T> batchId(String batchId) {
        this.batchId = batchId;
        return this;
    }

    /**
     * toBatch
     *
     * @param toBatch toBatch
     * @return FlowContext<T>
     */
    public FlowContext<T> toBatch(String toBatch) {
        this.toBatch = toBatch;
        return this;
    }

    /**
     * inFlowTrans
     *
     * @param flowTrans flowTrans
     * @return FlowContext<T>
     */
    public FlowContext<T> inFlowTrans(FlowTrans flowTrans) {
        this.trans = flowTrans;
        return this;
    }

    /**
     * generate是在map，reduce，produce的过程中把大多数上一个context的内容复制给下一个
     *
     * @param data 处理后的数据
     * @param position 处理后所处的节点
     * @param createAt 创建时间.
     * @param <R> 处理后数据类型
     * @return 新的上下文
     */
    public <R> FlowContext<R> generate(R data, String position, LocalDateTime createAt) {
        FlowContext<R> context = new FlowContext<>(this.streamId, this.rootId, data, this.traceId, this.position,
                this.parallel, this.parallelMode, createAt);
        context.position = position;
        context.previous = this.id;
        context.trans = this.trans;
        return context;
    }

    /**
     * generate
     *
     * @param data data
     * @param position position
     * @return List<FlowContext < R>>
     */
    public <R> List<FlowContext<R>> generate(List<R> data, String position) {
        return data.stream().map(d -> this.generate(d, position, LocalDateTime.now())).collect(Collectors.toList());
    }

    /**
     * fork一个新的context用于一拖多分支，继承当前context的运行元数据，但生成新的contextId。
     *
     * @return 新的分支context
     */
    public FlowContext<T> fork() {
        return this.convertData(this.data);
    }

    /**
     * convertData
     *
     * @param <R> 转换后的数据类型
     * @param data 转换后的数据
     * @return 转换后的context
     */
    public <R> FlowContext<R> convertData(R data) {
        FlowContext<R> context = this.copyContext(data);
        context.previous = this.id;
        context.nextPositionId = this.nextPositionId;
        return context;
    }

    /**
     * 用于when.convert数据时候的转换context，除了包裹的数据类型不一样，所有其他信息都一样
     *
     * @param <R> 转换后的数据类型
     * @param data 转换后的数据
     * @param id contextId
     * @return 转换后的context
     */
    public <R> FlowContext<R> convertData(R data, String id) {
        FlowContext<R> context = this.copyContext(data);
        context.previous = this.previous;
        context.id = id;
        return context;
    }

    private <R> FlowContext<R> copyContext(R data) {
        FlowContext<R> context = new FlowContext<>(this.streamId, this.rootId, data, this.traceId, this.position,
                this.parallel, this.parallelMode, LocalDateTime.now());
        context.status = this.status;
        context.trans = this.trans;
        context.batchId = this.batchId;
        context.toBatch = this.toBatch;
        context.createAt = this.createAt;
        context.updateAt = this.updateAt;
        context.archivedAt = this.archivedAt;
        return context;
    }

    /**
     * joined
     *
     * @param joined joined
     */
    public void join(boolean joined) {
        this.joined = joined;
    }
}
