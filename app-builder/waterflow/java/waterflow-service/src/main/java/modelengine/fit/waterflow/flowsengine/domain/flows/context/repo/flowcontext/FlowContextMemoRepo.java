/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext;

import modelengine.fit.waterflow.flowsengine.biz.service.DefaultTraceOwnerService;
import modelengine.fit.waterflow.flowsengine.biz.service.TraceOwnerService;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowTrace;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocksMemo;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors.Filter;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.Processors.Validator;
import modelengine.fit.waterflow.spi.lock.InvalidDistributedLockNotify;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 持久化{@link FlowContext}对象到内存中核心类
 * 与{@link FlowContextPersistRepo}组成{@link FlowContextRepo}的不同实现
 *
 * @author 高诗意
 * @since 2023/08/14
 */
public class FlowContextMemoRepo<T> implements FlowContextRepo<T> {
    private final List<FlowContext<T>> contexts = new CopyOnWriteArrayList<>();

    private final TraceOwnerService traceOwnerService = new DefaultTraceOwnerService(new FlowLocksMemo(),
            new InvalidDistributedLockNotify() {
                @Override
                public void subscribe(Consumer<Lock> consumer) {
                }

                @Override
                public void notify(Lock invalidLock) {
                }
            });

    @Override
    public TraceOwnerService getTraceOwnerService() {
        return this.traceOwnerService;
    }

    @Override
    public List<FlowContext<T>> getContextsByPosition(String streamId, List<String> posIds, String status) {
        return this.contexts.stream()
                .filter(c -> c.getStreamId().equals(streamId))
                .filter(c -> posIds.contains(c.getPosition()))
                .filter(c -> c.getStatus().toString().equals(status))
                .filter(c -> !c.isSent())
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<T>> getContextsByPosition(String streamId, String posId, String batchId, String status) {
        return this.contexts.stream()
                .filter(c -> c.getStreamId().equals(streamId))
                .filter(c -> c.getPosition().equals(posId))
                .filter(c -> c.getBatchId().equals(batchId))
                .filter(c -> c.getStatus().toString().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public <T1> List<FlowContext<T1>> findWithoutFlowDataByTraceId(String traceId) {
        return null;
    }

    @Override
    public synchronized List<FlowContext<T>> getContextsByTrace(String traceId, String status) {
        return this.getContextsByTrace(traceId).stream()
                .filter(context -> context.getStatus().name().equals(status))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<FlowContext<T>> getContextsByTrace(String traceId) {
        return this.contexts.stream().filter(c -> c.getTraceId().contains(traceId)).collect(Collectors.toList());
    }

    @Override
    public synchronized void save(List<FlowContext<T>> contexts) {
        this.contexts.removeIf(c -> contexts.stream().anyMatch(c1 -> c.getId().equals(c1.getId())));
        this.contexts.addAll(contexts);
    }

    @Override
    public void updateToSent(List<FlowContext<T>> contexts) {
        contexts.forEach(context -> context.setSent(true));
        save(contexts);
    }

    @Override
    public void updateToReady(List<FlowContext<T>> flowContexts) {

    }

    @Override
    public void save(FlowContext<T> context) {

    }

    @Override
    public List<FlowContext<T>> getContextsByParallel(String parallelId) {
        return this.contexts.stream().filter(c -> c.getParallel().equals(parallelId)).collect(Collectors.toList());
    }

    @Override
    public FlowContext<T> getById(String id) {
        return contexts.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<FlowContext<T>> getByToBatch(List<String> toBatchIds) {
        return this.contexts.stream().filter(c -> toBatchIds.contains(c.getToBatch())).collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<T>> getPendingAndSentByIds(List<String> ids) {
        return contexts.stream()
                .filter(c -> FlowNodeStatus.PENDING.equals(c.getStatus()))
                .filter(FlowContext::isSent)
                .filter(c -> ids.contains(c.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<FlowContext<T>> getByIds(List<String> ids) {
        return contexts.stream().filter(c -> ids.contains(c.getId())).collect(Collectors.toList());
    }

    @Override
    public synchronized List<FlowContext<T>> requestMappingContext(String streamId, List<String> subscriptions,
        Filter<T> filter, Validator<T> validator) {
        List<FlowContext<T>> all = this.contexts.stream()
                .filter(c -> c.getStreamId().equals(streamId))
                .filter(c -> subscriptions.contains(c.getPosition()))
                .filter(c -> c.getStatus() == FlowNodeStatus.PENDING || c.getStatus() == FlowNodeStatus.SKIPPED)
                .collect(Collectors.toList());
        List<FlowContext<T>> filters = filter.process(all);
        return filters.stream().filter(c -> validator.check(c, filters)).collect(Collectors.toList());
    }

    @Override
    public synchronized List<FlowContext<T>> requestProducingContext(String streamId, List<String> subscriptions,
        Filter<T> filter) {
        List<FlowContext<T>> all = this.contexts.stream()
                .filter(c -> c.getStreamId().equals(streamId))
                .filter(c -> subscriptions.contains(c.getPosition()))
                .filter(c -> c.getStatus() == FlowNodeStatus.PENDING || c.getStatus() == FlowNodeStatus.SKIPPED)
                .collect(Collectors.toList());
        return filter.process(all);
    }

    @Override
    public void save(FlowTrace trace, FlowContext<T> flowContext) {

    }

    @Override
    public void updateFlowDataAndToBatch(List<FlowContext<T>> contexts) {
        save(contexts);
    }

    @Override
    public boolean hasContextWithStatus(List<String> status, String traceId) {
        return false;
    }

    @Override
    public boolean isAllContextStatus(List<String> statusList, String traceId) {
        return false;
    }

    @Override
    public boolean hasContextWithStatusAtPosition(List<String> status, String traceId, String position) {
        return false;
    }

    @Override
    public String getTransIdByTrace(String id) {
        return null;
    }

    @Override
    public void updateProcessStatus(List<FlowContext<T>> contexts) {
        save(contexts);
    }
}
