/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.dlock.jdbc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.dlock.DatabaseBaseTest;

import modelengine.fit.waterflow.spi.lock.InvalidDistributedLockNotify;
import modelengine.fitframework.transaction.DataAccessException;
import modelengine.fitframework.transaction.TransactionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 分布式锁中的本地锁应用测试类
 *
 * @author 李哲峰
 * @since 2023/12/01
 */
@DisplayName("分布式锁中的本地锁应用测试集合")
@Disabled("测试无法跑通")
public class LocalLockTest extends DatabaseBaseTest {
    private DistributedLockRepo repo;

    private DistributedLockClient client;

    @BeforeEach
    void before() {
        repo = mock(DistributedLockRepo.class);
        client = new DistributedLockClient(10_000, 5_000, repo, mock(InvalidDistributedLockNotify.class));

        when(repo.create("test", 10_000)).thenReturn(true);
    }

    @Override
    protected void cleanTable() {
        executeSqlInFile("handler/flowlock/cleanData.sql");
    }

    @Test
    @DisplayName("解锁次数小于上锁次数场景")
    public void testLessAmountOfUnlockTimesThanLock() throws Exception {
        final Random random = new Random();
        final int lockTimes = random.nextInt(5) + 1;
        final int unlockTimes = random.nextInt(lockTimes);

        final Lock lock = client.getLock("test");
        for (int i = 0; i < lockTimes; i++) {
            lock.tryLock();
        }
        for (int i = 0; i < unlockTimes; i++) {
            lock.unlock();
        }

        assertTrue(getLocalLock(lock).isLocked());
    }

    @Test
    @DisplayName("解锁次数等于上锁次数场景")
    public void testSameAmountOfUnlockAsLock() throws Exception {
        final Random random = new Random();
        final int lockTimes = random.nextInt(5) + 1;

        final Lock lock = client.getLock("test");
        for (int i = 0; i < lockTimes; i++) {
            lock.tryLock();
        }
        for (int i = 0; i < lockTimes; i++) {
            lock.unlock();
        }

        assertFalse(getLocalLock(lock).isLocked());
    }

    @Test
    @DisplayName("DataAccessException场景下的锁状态")
    public void testLockStateWithDataAccessException() throws Exception {
        final Lock lock = client.getLock("test");
        lock.tryLock();

        final AtomicBoolean shouldThrow = new AtomicBoolean(true);
        doAnswer(invocation -> {
            if (shouldThrow.getAndSet(false)) {
                throw mock(DataAccessException.class);
            }
            return null;
        }).when(repo).delete(anyString());

        lock.unlock();

        assertFalse(getLocalLock(lock).isLocked());
    }

    @Test
    @DisplayName("TransactionException场景下的锁状态")
    public void testLockStateWithTransactionException() throws Exception {
        final Lock lock = client.getLock("test");
        lock.tryLock();

        final AtomicBoolean shouldThrow = new AtomicBoolean(true);
        doAnswer(invocation -> {
            if (shouldThrow.getAndSet(false)) {
                throw mock(TransactionException.class);
            }
            return null;
        }).when(repo).delete(anyString());

        lock.unlock();

        assertFalse(getLocalLock(lock).isLocked());
    }

    private ReentrantLock getLocalLock(Lock distributedLock) throws Exception {
        Field lockField = DistributedLock.class.getDeclaredField("lock");
        lockField.setAccessible(true);
        if (lockField.get(distributedLock) instanceof ReentrantLock) {
            return (ReentrantLock) lockField.get(distributedLock);
        }
        return new ReentrantLock();
    }
}
