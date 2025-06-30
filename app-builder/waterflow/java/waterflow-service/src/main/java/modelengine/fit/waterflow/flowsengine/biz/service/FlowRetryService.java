/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.biz.service;

import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.common.utils.SleepUtil;
import modelengine.fit.waterflow.flowsengine.biz.service.cache.FlowDefinitionQueryService;
import modelengine.fit.waterflow.flowsengine.biz.service.cache.FlowQueryService;
import modelengine.fit.waterflow.flowsengine.biz.service.entity.FlowRetryInfo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowRetry;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowcontext.FlowContextRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowNodeStatus;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.From;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.IdGenerator;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.To;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import static modelengine.fit.waterflow.ErrorCodes.FLOW_NODE_MAX_TASK;
import static modelengine.fit.waterflow.ErrorCodes.FLOW_RETRY_JOBER_UPDATE_DATABASE_FAILED;
import static modelengine.fit.waterflow.common.Constant.STREAM_ID_SEPARATOR;
import static modelengine.fit.waterflow.common.Constant.TO_BATCH_KEY;
import static modelengine.fit.waterflow.flowsengine.domain.flows.enums.ProcessType.PROCESS;

/**
 * 流程重试逻辑服务
 *
 * @author yangxiangyu
 * @since 2025/2/24
 */
@Component
public class FlowRetryService {
    private static final Logger LOG = Logger.get(FlowRetryService.class);

    /**
     * 重试自动任务的等待间隔时间，防止同一耗时重试对象在短时间内被多次尝试重试
     */
    private static final long RETRY_WAITING_INTERVAL = 15000L;

    /**
     * 重试任务周期
     */
    private static final long RETRY_INTERVAL = 30000L;

    private final Map<String, FlowRetryInfo> flowRetryMap = new HashMap<>();
    private volatile boolean isRetryRunning = false;

    private final FlowContextRepo repo;

    private final FlowRetryRepo retryRepo;

    private final FlowLocks locks;

    private final TraceOwnerService traceOwnerService;

    private final FlowDefinitionQueryService definitionQueryService;

    private final FlowQueryService flowQueryService;

    public FlowRetryService(FlowContextRepo repo, FlowRetryRepo retryRepo, FlowLocks locks,
                            TraceOwnerService traceOwnerService, FlowDefinitionQueryService definitionQueryService,
                            FlowQueryService flowQueryService) {
        this.repo = repo;
        this.retryRepo = retryRepo;
        this.locks = locks;
        this.traceOwnerService = traceOwnerService;
        this.definitionQueryService = definitionQueryService;
        this.flowQueryService = flowQueryService;
    }

    /**
     * 是否在执行重试任务
     *
     * @return 重试任务状态
     */
    public synchronized boolean isRetryRunning() {
        return this.isRetryRunning;
    }

    /**
     * 重试执行所有状态为RETRYABLE的上下文
     */
    public void retryTask() {
        while (true) {
            try {
                if (!popRetryTask()) {
                    return;
                }
            } catch (Exception ex) {
                LOG.error("retry failed, exception: ", ex);
            } finally {
                SleepUtil.sleep(RETRY_INTERVAL);
            }
        }
    }

    /**
     * 是否拿到重试任务并成功执行
     *
     * @return 是否成功执行
     */
    public boolean popRetryTask() {
        LOG.info("Start retry task.");
        isRetryRunning = true;
        updateFlowRetryMap();
        if (flowRetryMap.isEmpty()) {
            LOG.info("Retry list is empty, retry end.");
            isRetryRunning = false;
            return false;
        }
        startRetry();
        return true;
    }

    private void updateFlowRetryMap() {
        LocalDateTime now = LocalDateTime.now();
        List<FlowRetry> flowRetryList = retryRepo.filterByNextRetryTime(now, new ArrayList<>(flowRetryMap.keySet()))
                .stream()
                .filter(flowRetry -> Objects.equals(flowRetry.getEntityType(), TO_BATCH_KEY))
                .collect(Collectors.toList());

        List<String> toBatchIds = flowRetryList.stream().map(FlowRetry::getEntityId).collect(Collectors.toList());
        List<FlowContext<String>> contexts = repo.getWithoutFlowDataByToBatch(toBatchIds);
        cleanFlowRetry(toBatchIds, contexts);

        Map<String, List<FlowContext<String>>> retryContexts = contexts.stream()
                .filter(context -> traceOwnerService.isAnyOwn(context.getTraceId()))
                .collect(Collectors.groupingBy(FlowContext::getToBatch));
        flowRetryList.stream()
                .filter(flowRetry -> retryContexts.containsKey(flowRetry.getEntityId()))
                .forEach(flowRetry -> {
                    Optional<FlowRetryInfo> retryInfo = getFlowRetryInfo(retryContexts, flowRetry);
                    if (!retryInfo.isPresent()) {
                        return;
                    }
                    flowRetryMap.put(flowRetry.getEntityId(), retryInfo.get());
                });
    }

    private Optional<FlowRetryInfo> getFlowRetryInfo(Map<String, List<FlowContext<String>>> retryContexts,
                                                     FlowRetry flowRetry) {
        FlowRetryInfo retryInfo = FlowRetryInfo
                .builder()
                .flowRetry(flowRetry)
                .flowContexts(retryContexts.get(flowRetry.getEntityId()))
                .build();

        FlowContext<String> context = retryInfo.getFlowContexts().get(0);
        String streamId = context.getStreamId();
        From<FlowData> from = ObjectUtils.cast(flowQueryService.getPublisher(streamId));
        // 小的并发概率下没有获取到
        if (from == null) {
            flowDefinitionCheck(context.getToBatch(), streamId);
            return Optional.empty();
        }
        String position = context.getPosition();
        To<FlowData, Object> to = from.getSubscriber(position);
        retryInfo.setTo(to);
        return Optional.of(retryInfo);
    }

    private boolean isFinishedContext(List<FlowContext<String>> contexts) {
        return contexts.stream().anyMatch(context -> FlowNodeStatus.isEndStatus(context.getStatus()));
    }

    private void startRetry() {
        LOG.info("Retry task size: {}.", flowRetryMap.size());
        Iterator<FlowRetryInfo> iterator = flowRetryMap.values().iterator();
        while (iterator.hasNext()) {
            FlowRetryInfo flowRetryInfo = iterator.next();
            if (flowRetryInfo.getTo().isMaxConcurrency()) {
                continue;
            }
            retryByToBatch(flowRetryInfo);
            iterator.remove();
        }
    }

    private void retryByToBatch(FlowRetryInfo flowRetry) {
        String lockKey = StringUtils.join(STREAM_ID_SEPARATOR, "retry", flowRetry.getFlowRetry().getEntityId());
        Lock lock = locks.getDistributedLock(lockKey);
        boolean isLockAcquired = lock.tryLock();
        if (!isLockAcquired) {
            LOG.warn("[retryByToBatch] Acquire distributed lock {} failed", lockKey);
            return;
        }
        LOG.debug("[retryByToBatch] Acquire distributed lock {} succeeded", lockKey);
        try {
            executeRetry(flowRetry);
        } catch (Throwable ex) {
            LOG.error("[retryByToBatch] Caught a throwable during the retry. Caused by {}", ex.getMessage());
        } finally {
            lock.unlock();
            LOG.debug("[retryByToBatch] Release distributed lock {} succeeded", lockKey);
        }
    }

    private void executeRetry(FlowRetryInfo flowRetryInfo) {
        String toBatch = flowRetryInfo.getFlowRetry().getEntityId();
        List<FlowContext<String>> contexts = flowRetryInfo.getFlowContexts();
        List<String> traces = this.traceOwnerService.getTraces();
        List<FlowContext<String>> finalContexts = contexts.stream()
                .filter(context -> traces.stream().anyMatch(trace -> context.getTraceId().contains(trace)))
                .collect(Collectors.toList());
        if (unNeedRetry(toBatch, finalContexts)) {
            return;
        }

        String streamId = finalContexts.get(0).getStreamId();
        String position = finalContexts.get(0).getPosition();
        To<FlowData, Object> to = flowRetryInfo.getTo();

        // 节点流量已经超出，则直接返回
        if (to.isMaxConcurrency()) {
            return;
        }
        List<String> traceIds = finalContexts.stream()
                .flatMap(c -> c.getTraceId().stream())
                .distinct()
                .collect(Collectors.toList());
        List<String> contextIds = finalContexts.stream().map(IdGenerator::getId).collect(Collectors.toList());
        LOG.info("Start retrying the jober, toBatch: {}, trace ids: {}, stream id: {}, context ids: {}, "
                + "position: {}", toBatch, traceIds.toString(), streamId, contextIds.toString(), position);
        List<FlowContext<FlowData>> retryContexts = updateRetryStatus(flowRetryInfo, streamId, position,
                finalContexts, to);
        to.getProcessMode().submit(to, retryContexts);
        LOG.info("Retry jober succeeded, toBatch: {}, trace ids: {}, stream id: {}, context ids: {}, " + "position: {}",
                toBatch, traceIds.toString(), streamId, contextIds.toString(), position);
    }

    private void cleanFlowRetry(List<String> toBatchIds, List<FlowContext<String>> contexts) {
        Map<String, List<FlowContext<String>>> allContext = contexts
                .stream()
                .collect(Collectors.groupingBy(FlowContext::getToBatch));
        List<String> cleanedIds = toBatchIds
                .stream()
                .filter(id -> (!allContext.containsKey(id)) || isFinishedContext(allContext.get(id)))
                .collect(Collectors.toList());
        retryRepo.delete(cleanedIds);
    }

    private boolean unNeedRetry(String toBatch, List<FlowContext<String>> finalContexts) {
        if (isFinishedContext(finalContexts)) {
            LOG.warn("[executeRetry] the batch is no need retry, toBatch={}.", toBatch);
            retryRepo.delete(Collections.singletonList(toBatch));
            return true;
        }
        if (finalContexts.stream().anyMatch(c -> !FlowNodeStatus.RETRYABLE.equals(c.getStatus()))) {
            LOG.warn("[executeRetry] Retry failed: the toBatch {} is currently unretryable", toBatch);
            return true;
        }
        return false;
    }

    private List<FlowContext<FlowData>> updateRetryStatus(FlowRetryInfo flowRetryInfo, String streamId, String position,
                                                          List<FlowContext<String>> finalContexts, To<FlowData, Object> to) {
        Lock lock = locks.getDistributedLock(locks.streamNodeLockKey(streamId, position, PROCESS.toString()));
        lock.lock();
        List<FlowContext<FlowData>> retryContext;
        try {
            if (to.isMaxConcurrency()) {
                throw new WaterflowException(FLOW_NODE_MAX_TASK, to.getId());
            }
            String entityId = flowRetryInfo.getFlowRetry().getEntityId();
            retryContext = repo.getByToBatch(Collections.singletonList(entityId));
            if (retryContext.isEmpty()) {
                retryRepo.delete(Collections.singletonList(entityId));
                LOG.warn("not find retry context by batch id: {}.", entityId);
                throw new WaterflowException(FLOW_RETRY_JOBER_UPDATE_DATABASE_FAILED, entityId);
            }
            updateDbForRetry(retryContext, position, flowRetryInfo.getFlowRetry());
            to.updateConcurrency(1);
        } finally {
            lock.unlock();
        }
        return retryContext;
    }

    /**
     * 为retryJober更新相关数据库
     *
     * @param contexts contexts
     * @param position position
     * @param flowRetry flowRetry
     */
    @Transactional
    public void updateDbForRetry(List<FlowContext<FlowData>> contexts, String position, FlowRetry flowRetry) {
        // 当前调换顺序（理论上这样也更合理）
        // 1. 保证retry record更新失败后不会更新context状态，下次还能被重试拉起
        // 2. 目前待排查之前事务未回退context状态原因
        LocalDateTime retryTime = LocalDateTime.now();
        flowRetry.setLastRetryTime(retryTime);
        flowRetry.setRetryCount(flowRetry.getRetryCount() + 1);
        flowRetry.setNextRetryTime(retryTime.plus(RETRY_WAITING_INTERVAL, ChronoUnit.MILLIS));
        if (retryRepo.updateRetryRecord(Collections.singletonList(flowRetry)) == 0) {
            String toBatch = flowRetry.getEntityId();
            LOG.error("[updateDbForRetry] Updating flow_retry database failed for the toBatch {},"
                    + " firstContextId={}, retryContextSize={}.", toBatch, contexts.get(0).getId(), contexts.size());
            throw new WaterflowException(FLOW_RETRY_JOBER_UPDATE_DATABASE_FAILED, toBatch);
        }
        repo.updateStatus(contexts, FlowNodeStatus.READY.toString(), position);
    }

    private void flowDefinitionCheck(String toBatch, String streamId) {
        FlowDefinition flowDefinition = definitionQueryService.findByStreamId(streamId);
        if (flowDefinition == null) {
            retryRepo.delete(Collections.singletonList(toBatch));
            LOG.error("[executeRetry] Retry failed: cannot find flow definition for the toBatch {}", toBatch);
            return;
        }
    }
}
