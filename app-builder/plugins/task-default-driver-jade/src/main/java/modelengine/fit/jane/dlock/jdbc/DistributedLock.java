/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.dlock.jdbc;

import lombok.Getter;
import modelengine.fit.jane.dlock.jdbc.utils.CustomThreadFactory;
import modelengine.fit.jane.dlock.jdbc.utils.DistributedLockStatus;

import lombok.Getter;
import modelengine.fit.waterflow.spi.lock.InvalidDistributedLockNotify;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.transaction.DataAccessException;
import modelengine.fitframework.transaction.TransactionException;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * JDBC分布式锁
 *
 * @author 李哲峰
 * @since 2023/11/30
 */
public final class DistributedLock implements Lock {
    private static final Logger log = Logger.get(DistributedLock.class);

    private static final ScheduledThreadPoolExecutor RENEW_LOCK_EXECUTOR =
            new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("DistributedLock-renew"));

    private static final Integer KEY_MAX_LENGTH = 100;

    private static final int MAX_CONTINUES_INVALID_COUNT = 3;

    private final ReentrantLock threadLock;

    private final long ttl;

    private final long renewScheduleRate;

    private final String lockKey;

    private final DistributedLockRepo repo;

    private final long idleTime;

    private final InvalidDistributedLockNotify invalidDistributedLockNotify;

    private boolean isValid = true;

    private int continuesInvalidCount = 0;

    /**
     * -- GETTER --
     *
     * @return 分布式锁最后使用时间
     */
    @Getter
    private volatile long lastUsed = System.currentTimeMillis();

    private ScheduledFuture<?> renewLockFuture;

    public DistributedLock(long ttl, long renewScheduleRate, DistributedLockRepo repo, long idleTime, Object key,
            InvalidDistributedLockNotify invalidDistributedLockNotify) {
        this.invalidDistributedLockNotify = invalidDistributedLockNotify;
        this.threadLock = new ReentrantLock(false);
        this.ttl = ttl;
        this.renewScheduleRate = renewScheduleRate;
        this.lockKey = getLockKey(key);
        this.repo = repo;
        this.idleTime = idleTime;
    }

    public static String getLockKey(Object key) {
        String lockKeyStr = String.valueOf(key);
        return lockKeyStr.length() <= KEY_MAX_LENGTH ? lockKeyStr : lockKeyStr.substring(0, KEY_MAX_LENGTH);
    }

    /**
     * 非阻塞获取分布式锁
     *
     * @return 获取结果
     */
    @Override
    public boolean tryLock() {
        try {
            return tryLock(0, TimeUnit.MICROSECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * 在指定时间内阻塞获取分布式锁
     *
     * @param waitingTime 最长等锁时间
     * @param unit 时间单位
     * @return 获取结果
     * @throws InterruptedException 表示线程中断异常
     */
    @Override
    public boolean tryLock(long waitingTime, TimeUnit unit) throws InterruptedException {
        log.debug("tryLock before: {}", this.lockKey);
        long now = System.currentTimeMillis();
        if (!this.threadLock.tryLock(waitingTime, unit)) {
            return false;
        }
        while (true) {
            try {
                boolean isHeld = acquireLock();
                if (!isHeld) {
                    this.threadLock.unlock();
                }
                log.debug("tryLock after: {}", this.lockKey);
                return isHeld;
            } catch (DataAccessException | TransactionException e) {
                // 重试
                log.error("try lock error :{}", this.lockKey);
                log.error("Exception={}.", e);
            } catch (Exception e) {
                this.threadLock.unlock();
                log.warn("tryLock error after: {}", this.lockKey);
                rethrowLockException(e);
            }
        }
    }

    /**
     * 持续阻塞获取分布式锁
     */
    @Override
    public void lock() {
        log.debug("lock before: {}", this.lockKey);
        this.threadLock.lock();
        while (true) {
            try {
                while (!acquireLock()) {
                    log.debug("acquireLock waiting: {}", this.lockKey);
                    Thread.sleep(this.idleTime);
                }
                break;
            } catch (DataAccessException | TransactionException | InterruptedException e) {
                // 重试
                log.error("locked error : {}", this.lockKey);
                log.error("Exception={}.", e);
            } catch (Exception e) {
                this.threadLock.unlock();
                log.warn("lock error after: {}", this.lockKey);
                rethrowLockException(e);
            }
        }
        log.warn("lock after: {}", this.lockKey);
    }

    /**
     * 解锁
     */
    @Override
    public void unlock() {
        log.debug("unlock before: {}", this.lockKey);
        if (!this.threadLock.isHeldByCurrentThread()) {
            log.error("The current thread {} doesn't own the lock at lock key:{}, lock info: {}.",
                    Thread.currentThread().getName(), this.lockKey, this.threadLock.toString());
            return;
        }
        if (this.threadLock.getHoldCount() > 1) {
            this.threadLock.unlock();
            return;
        }
        try {
            if (!this.isValid) {
                log.warn("The lock is invalid, unlock before. lockKey={}.", this.lockKey);
                return;
            }
            while (true) {
                try {
                    this.repo.delete(this.lockKey);
                    return;
                } catch (DataAccessException | TransactionException e) {
                    // 重试
                    log.error("unlocked error : {}", this.lockKey);
                    log.error("Exception={}.", e);
                } catch (Exception e) {
                    log.warn("unlock error after: {}", this.lockKey);
                    throw new DataAccessException("Failed to release the lock at lock key " + this.lockKey, e);
                }
            }
        } finally {
            Optional.ofNullable(renewLockFuture).ifPresent(f -> f.cancel(true));
            this.threadLock.unlock();
            log.debug("unlock after: {}", this.lockKey);
        }
    }

    /**
     * 持续阻塞获取分布式锁直到获取成功或线程被中断
     *
     * @throws InterruptedException 表示线程中断异常
     */
    @Override
    public void lockInterruptibly() throws InterruptedException {
        this.threadLock.lockInterruptibly();
        while (true) {
            try {
                tryLockInterruptibly();
                break;
            } catch (DataAccessException | TransactionException e) {
                // 重试
                log.error("lockInterruptibly error : {}", this.lockKey);
                log.error("Exception={}.", e);
            } catch (InterruptedException ie) {
                this.threadLock.unlock();
                throw ie;
            } catch (Exception e) {
                this.threadLock.unlock();
                rethrowLockException(e);
            }
        }
    }

    /**
     * 未支持的接口方法
     *
     * @return {@link UnsupportedOperationException}
     */
    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("Conditions are not supported");
    }

    /**
     * 分布式锁是否为被获取状态
     *
     * @return 分布式锁当前是否为被获取状态
     */
    public boolean isInProcess() {
        return this.repo.isExists(this.lockKey);
    }

    /**
     * 删除过期分布式锁
     *
     * @return 删除的结果
     */
    public boolean deleteExpired() {
        return this.repo.deleteExpired(this.lockKey);
    }

    private void tryLockInterruptibly() throws InterruptedException {
        while (!acquireLock()) {
            Thread.sleep(this.idleTime);
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
        }
    }

    private boolean acquireLock() {
        log.debug("acquireLock enter");
        DistributedLockStatus lockStatus = this.repo.getStatus(this.lockKey);
        if (lockStatus == DistributedLockStatus.LOCK_BY_ME_EXPIRED) {
            log.warn("This lock by me is expired, lockKey = {}.", this.lockKey);
        }
        if (lockStatus.isOccupied()) {
            return false;
        }
        boolean isAcquired;
        if (lockStatus.isAllowUpdate()) {
            isAcquired = this.repo.update(this.lockKey, this.ttl);
        } else {
            isAcquired = this.repo.create(this.lockKey, this.ttl);
        }
        if (isAcquired) {
            this.lastUsed = System.currentTimeMillis();
            if (renewLockFuture == null || renewLockFuture.isCancelled()) {
                renewLockFuture = RENEW_LOCK_EXECUTOR.scheduleAtFixedRate(this::renewLock,
                        renewScheduleRate,
                        renewScheduleRate,
                        TimeUnit.MILLISECONDS);
            }
            this.isValid = true;
        }
        log.debug("acquireLock end");
        return isAcquired;
    }

    private void renewLock() {
        boolean hasUpdateError = false;
        try {
            if (!repo.updateExpiredAt(lockKey, ttl)) {
                hasUpdateError = true;
                log.warn("Failed to keepalive, lockKey={}.", this.lockKey);
            }
        } catch (FitException e) {
            hasUpdateError = true;
        } catch (Exception e) {
            hasUpdateError = true;
            log.warn("Failed to keepalive, lockKey={}, errorMsg={}.", this.lockKey, e.getMessage());
            log.warn("Exception=", e);
        } finally {
            checkRenewStatus(hasUpdateError);
        }
    }

    private void checkRenewStatus(boolean hasUpdateError) {
        if (!hasUpdateError) {
            this.continuesInvalidCount = 0;
            return;
        }
        ++this.continuesInvalidCount;
        if (this.continuesInvalidCount < MAX_CONTINUES_INVALID_COUNT) {
            return;
        }
        this.isValid = false;
        invalidDistributedLockNotify.notify(this);
        renewLockFuture.cancel(true);
    }

    private void rethrowLockException(Exception e) {
        throw new DataAccessException("Failed to acquire the lock at lock key " + this.lockKey, e);
    }
}
