/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.dlock.jdbc;

import modelengine.fit.jane.dlock.ExpirableDistributedLockHandler;

import modelengine.fit.waterflow.spi.lock.InvalidDistributedLockNotify;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JDBC分布式锁客户端
 *
 * @author 李哲峰
 * @since 2023/11/30
 */
@Component
public final class DistributedLockClient implements ExpirableDistributedLockHandler {
    /**
     * 线程默认休眠时间
     */
    public static final int DEFAULT_IDLE_MS = 100;

    private final DistributedLockRepo repo;

    private final long ttl;

    private final long renewScheduleRate;

    private final Map<String, DistributedLock> locks = new ConcurrentHashMap<>();

    private final InvalidDistributedLockNotify invalidDistributedLockNotify;

    private int idleTime = DEFAULT_IDLE_MS;

    public DistributedLockClient(@Value("${databasedistributedlock.ttl}") long ttl,
            @Value("${databasedistributedlock.renewScheduleRate}") long renewScheduleRate, DistributedLockRepo repo,
            InvalidDistributedLockNotify invalidDistributedLockNotify) {
        this.ttl = ttl;
        this.renewScheduleRate = renewScheduleRate;
        this.repo = repo;
        this.invalidDistributedLockNotify = invalidDistributedLockNotify;
    }

    /**
     * 设置休眠时间
     *
     * @param idleTime 休眠时间
     */
    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }

    /**
     * 获取分布式锁，如果key值存在于锁池中，直接从锁池中获取，否则重新创建。
     *
     * @param key 分布式锁的key值
     * @return 分布式锁
     */
    @Override
    public DistributedLock getLock(String key) {
        String lockKey = DistributedLock.getLockKey(key);
        return this.locks.computeIfAbsent(lockKey,
                k -> new DistributedLock(this.ttl, this.renewScheduleRate, this.repo, this.idleTime, key,
                        invalidDistributedLockNotify));
    }

    /**
     * 从锁池中删除未被使用的于给定timeout时间前过期的锁
     *
     * @param timeout 从上次使用锁到现在所经历的时间
     */
    @Override
    public void deleteExpiredLocks(long timeout) {
        long expiredTime = this.repo.now() - timeout;
        for (Iterator<DistributedLock> iterator = this.locks.values().iterator(); iterator.hasNext(); ) {
            DistributedLock dLock = iterator.next();
            if (dLock.getLastUsed() < expiredTime && !dLock.isInProcess()) {
                dLock.deleteExpired();
                iterator.remove();
            }
        }
    }
}
