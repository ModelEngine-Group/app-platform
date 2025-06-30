/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.biz.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import modelengine.fit.waterflow.ErrorCodes;
import modelengine.fit.waterflow.exceptions.ServerInternalException;
import modelengine.fit.waterflow.exceptions.WaterflowException;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowlock.FlowLocks;
import modelengine.fit.waterflow.spi.lock.InvalidDistributedLockNotify;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.ThreadUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * 提供trace的归属服务
 *
 * @author 夏斐
 * @since 2024/2/29
 */
@Component
public class DefaultTraceOwnerService implements TraceOwnerService {
    private static final Logger log = Logger.get(DefaultTraceOwnerService.class);

    private static final int PROTECT_TIME_MS = 300000;

    private static final int DEFAULT_WAIT_TIME_MS = 100;

    private static final int MAX_TRY_COUNT = 50;

    private static final int TRY_SLEEP_MS = 10;

    private final FlowLocks locks;

    private final Map<String, OwnInfo> traceMap = new ConcurrentHashMap<>();

    /**
     * 构造TraceOwnerService
     *
     * @param locks 锁
     * @param invalidDistributedLockNotify 锁的失效通知服务
     */
    public DefaultTraceOwnerService(FlowLocks locks, InvalidDistributedLockNotify invalidDistributedLockNotify) {
        this.locks = locks;
        invalidDistributedLockNotify.subscribe(this::removeInvalidTrace);
    }

    @Override
    public void own(String traceId, String transId) {
        log.info("Start to own trace. traceId={}.", traceId);
        Lock lock = this.locks.getDistributedLock(this.locks.traceLockKey(traceId));
        for (int i = 0; i < MAX_TRY_COUNT; ++i) {
            if (OwnLockHelper.tryLock(lock, traceId, DEFAULT_WAIT_TIME_MS)) {
                log.info("Trace is owned. traceId={}, times={}.", traceId, i);
                this.traceMap.put(traceId, new OwnInfo(traceId, transId, lock, Instant.now()));
                return;
            }
            ThreadUtils.sleep(TRY_SLEEP_MS);
        }
        log.error("Can not own the trace, traceId={}.", traceId);
        throw new WaterflowException(ErrorCodes.UN_EXCEPTED_ERROR, "can not own trace");
    }

    @Override
    public boolean tryOwn(String traceId, String transId) {
        log.info("Start to try own trace. traceId={}.", traceId);
        Lock lock = this.locks.getDistributedLock(this.locks.traceLockKey(traceId));
        boolean tryLock = OwnLockHelper.tryLock(lock, traceId, DEFAULT_WAIT_TIME_MS);
        if (tryLock) {
            log.info("Trace is owned. traceId={}.", traceId);
            this.traceMap.put(traceId, new OwnInfo(traceId, transId, lock, Instant.now()));
        }
        return tryLock;
    }

    @Override
    public void release(String traceId) {
        log.info("Start to release trace. traceId={}.", traceId);
        Lock lock = this.locks.getDistributedLock(this.locks.traceLockKey(traceId));
        OwnLockHelper.unlock(lock, traceId);
        log.info("Trace is released, traceId={}.", traceId);
        this.traceMap.remove(traceId);
    }

    @Override
    public boolean isOwn(String traceId) {
        return this.traceMap.containsKey(traceId);
    }

    @Override
    public boolean isAnyOwn(Set<String> traceIds) {
        return traceIds.stream().anyMatch(this::isOwn);
    }

    @Override
    public List<String> getTraces() {
        return new ArrayList<>(this.traceMap.keySet());
    }

    @Override
    public List<String> getTraces(String targetTransId) {
        return Optional.ofNullable(targetTransId)
                .map(target -> this.traceMap.values().stream().map(OwnInfo::getTransId)
                        .filter(transId -> transId == null || target.equals(transId)).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public void removeInvalidTrace(Lock invalidLock) {
        log.warn("There is a lock is invalid.");
        Iterator<Map.Entry<String, OwnInfo>> iterator = this.traceMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, OwnInfo> entry = iterator.next();
            if (entry.getValue().getLock() == invalidLock) {
                log.warn("[TraceOwner] The trace is not belong to this node, traceId={}.", entry.getKey());
                OwnLockHelper.unlock(entry.getValue().getLock(), entry.getKey());
                iterator.remove();
                break;
            }
        }
    }

    @Override
    public boolean isInProtectTime(String traceId) {
        OwnInfo ownInfo = this.traceMap.get(traceId);
        if (ownInfo == null) {
            return false;
        }
        return Duration.between(ownInfo.createTime, Instant.now()).toMillis() < PROTECT_TIME_MS;
    }

    @Data
    @AllArgsConstructor
    private static class OwnInfo {
        private String traceId;

        private String transId;

        private final Lock lock;

        private Instant createTime;
    }

    private static class OwnLockHelper {
        /**
         * 线程池大小
         */
        public static final int EXECUTOR_SIZE = 32;

        private static final List<ExecutorInfo> EXECUTOR_SERVICE = new ArrayList<>();

        private static final Map<String, Integer> KEY_EXECUTOR_INDEXES = new HashMap<>();

        static {
            for (int i = 0; i < EXECUTOR_SIZE; ++i) {
                EXECUTOR_SERVICE.add(
                        new ExecutorInfo(Executors.newSingleThreadExecutor(new CustomThreadFactory("own-lock" + i)),
                                i));
            }
        }

        private static class ExecutorInfo {
            private ExecutorService executor;

            private int useCount = 0;

            private int index;

            private ExecutorInfo(ExecutorService executor, int index) {
                this.executor = executor;
                this.index = index;
            }

            /**
             * 获取对应的executor
             *
             * @return executor
             */
            public ExecutorService getExecutor() {
                return this.executor;
            }

            /**
             * 查询使用次数
             *
             * @return 使用次数
             */
            public int getUseCount() {
                return this.useCount;
            }

            /**
             * 增加一个使用次数
             */
            public void incrementUse() {
                ++this.useCount;
            }

            /**
             * 减少一个使用次数
             */
            public void decrementUse() {
                --this.useCount;
            }

            /**
             * 获取对应的index
             *
             * @return index
             */
            public int getIndex() {
                return this.index;
            }
        }

        /**
         * lock
         *
         * @param lock lock
         * @param key key
         */
        public static void lock(Lock lock, String key) {
            Future<?> submit = acquireUse(key).submit(lock::lock);
            boolean isOk = false;
            try {
                submit.get();
                isOk = true;
            } catch (InterruptedException | ExecutionException e) {
                throw new ServerInternalException(e.getMessage(), e);
            } finally {
                if (!isOk) {
                    releaseUse(key);
                }
            }
        }

        /**
         * unlock
         *
         * @param lock lock
         * @param key key
         */
        public static void unlock(Lock lock, String key) {
            Future<?> submit = acquireUse(key).submit(lock::unlock);
            try {
                submit.get();
                releaseUse(key);
            } catch (InterruptedException | ExecutionException e) {
                throw new ServerInternalException(e.getMessage(), e);
            }
        }

        /**
         * tryLock
         *
         * @param lock lock
         * @param key key
         * @param waitMs wait time
         * @return boolean
         */
        public static boolean tryLock(Lock lock, String key, int waitMs) {
            Future<?> submit = acquireUse(key).submit(() -> lock.tryLock(waitMs, TimeUnit.MILLISECONDS));
            boolean isGetLock = false;
            try {
                isGetLock = ObjectUtils.cast(submit.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new ServerInternalException(e.getMessage(), e);
            } finally {
                if (!isGetLock) {
                    releaseUse(key);
                }
            }
            return isGetLock;
        }

        private static synchronized ExecutorService acquireUse(String key) {
            Integer index = KEY_EXECUTOR_INDEXES.get(key);
            if (index != null) {
                return EXECUTOR_SERVICE.get(index).getExecutor();
            }
            ExecutorInfo executorInfo = getAndIncrementLowPayloadExecutor();
            KEY_EXECUTOR_INDEXES.put(key, executorInfo.getIndex());
            return executorInfo.getExecutor();
        }

        private static synchronized void releaseUse(String key) {
            Integer index = KEY_EXECUTOR_INDEXES.get(key);
            if (index != null) {
                EXECUTOR_SERVICE.get(index).decrementUse();
                KEY_EXECUTOR_INDEXES.remove(key);
            }
        }

        private static ExecutorInfo getAndIncrementLowPayloadExecutor() {
            ExecutorInfo executorInfo =
                    EXECUTOR_SERVICE.stream().min(Comparator.comparingInt(ExecutorInfo::getUseCount)).get();
            executorInfo.incrementUse();
            return executorInfo;
        }
    }
}
