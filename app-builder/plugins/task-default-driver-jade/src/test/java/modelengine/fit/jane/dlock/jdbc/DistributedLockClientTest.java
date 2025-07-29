/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.dlock.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import modelengine.fit.jane.common.utils.SleepUtil;
import modelengine.fit.jane.dlock.DatabaseBaseTest;
import modelengine.fit.jane.dlock.jdbc.persist.mapper.FlowLockMapper;

import modelengine.fit.jane.common.utils.SleepUtil;
import modelengine.fit.waterflow.spi.lock.InvalidDistributedLockNotify;
import modelengine.fitframework.log.Logger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

/**
 * 分布式锁核心测试类
 *
 * @author 李哲峰
 * @since 2023/12/01
 */
@DisplayName("分布式锁中核心测试集合")
public class DistributedLockClientTest extends DatabaseBaseTest {
    private static final Logger log = Logger.get(DistributedLockClientTest.class);

    private final ExecutorService threadExecutor = Executors.newSingleThreadExecutor();

    private DistributedLockRepo repo;

    private DistributedLockClient client;

    @BeforeEach
    void before() {
        repo = new DefaultDistributedLockRepo(sqlSessionManager.openSession(true).getMapper(FlowLockMapper.class));
        client = new DistributedLockClient(10_000, 5_000, repo, mock(InvalidDistributedLockNotify.class));
    }

    @Override
    protected void cleanTable() {
        executeSqlInFile("handler/flowlock/cleanData.sql");
    }

    @Test
    @DisplayName("测试调用lock方法时，客户端的锁池容量大小变化")
    public void testClientLockPoolSize() throws Exception {
        for (int i = 0; i < 10; i++) {
            Lock lock = client.getLock("test");
            lock.lock();
            try {
                assertEquals(1, getLocks().size());
            } finally {
                lock.unlock();
            }
        }
        SleepUtil.sleep(10);
        client.deleteExpiredLocks(0);
        assertEquals(0, getLocks().size());
    }

    @Test
    @DisplayName("测试调用lockInterruptibly方法时，客户端的锁池容量大小变化")
    public void testLockPoolSizeWithLockInterruptibly() throws Exception {
        for (int i = 0; i < 10; i++) {
            Lock lock = client.getLock("test");
            lock.lockInterruptibly();
            try {
                assertEquals(getLocks().size(), 1);
            } finally {
                lock.unlock();
            }
        }
    }

    @Test
    @DisplayName("测试同一客户端中，相同key对应的分布式锁是否被复用")
    public void testSameLockForLock() {
        for (int i = 0; i < 10; i++) {
            Lock lock1 = client.getLock("test");
            lock1.lock();
            try {
                Lock lock2 = client.getLock("test");
                assertSame(lock1, lock2);
                lock2.lock();
                lock2.unlock();
            } finally {
                lock1.unlock();
            }
        }
    }

    @Test
    @DisplayName("测试调用lockInterruptibly方法时，相同key对应的分布式锁是否被复用")
    public void testSameLockForLockInterruptibly() throws Exception {
        for (int i = 0; i < 10; i++) {
            Lock lock1 = client.getLock("test");
            lock1.lockInterruptibly();
            try {
                Lock lock2 = client.getLock("test");
                assertSame(lock1, lock2);
                lock2.lockInterruptibly();
                lock2.unlock();
            } finally {
                lock1.unlock();
            }
        }
    }

    @Test
    @DisplayName("测试key不同的两个锁是否为同一把锁")
    public void testLocksWithDifferentKey() throws Exception {
        for (int i = 0; i < 10; i++) {
            Lock lock1 = client.getLock("test");
            lock1.lockInterruptibly();
            try {
                Lock lock2 = client.getLock("bar");
                assertNotSame(lock1, lock2);
                lock2.lockInterruptibly();
                lock2.unlock();
            } finally {
                lock1.unlock();
            }
        }
    }

    @Test
    @Disabled
    @DisplayName("测试当线程没有成功获取锁的情况下尝试解锁的场景")
    public void testUnlockFailureWithoutAcquiringLockFirst() throws Exception {
        final Lock lock1 = client.getLock("test");
        lock1.lockInterruptibly();
        final AtomicBoolean locked = new AtomicBoolean();
        final CountDownLatch latch = new CountDownLatch(1);
        Future<Object> result = threadExecutor.submit(() -> {
            Lock lock2 = client.getLock("test");
            locked.set(lock2.tryLock(200, TimeUnit.MILLISECONDS));
            latch.countDown();
            try {
                lock2.unlock();
            } catch (Exception e) {
                return e;
            }
            return null;
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertFalse(locked.get());
        lock1.unlock();
        assertTrue(result.get(10, TimeUnit.SECONDS)
                .toString()
                .contains("The current thread doesn't own the lock at lock key test"));
    }

    @Test
    @DisplayName("测试两个线程先后调用lockInterruptibly上锁的场景")
    public void testTwoThreadsLockInterruptibly() throws Exception {
        final Lock lock1 = client.getLock("test");
        final AtomicBoolean locked = new AtomicBoolean();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final CountDownLatch latch3 = new CountDownLatch(1);
        lock1.lockInterruptibly();
        threadExecutor.execute(() -> {
            Lock lock2 = client.getLock("test");
            try {
                latch1.countDown();
                lock2.lockInterruptibly();
                latch2.await(10, TimeUnit.SECONDS);
                locked.set(true);
            } catch (InterruptedException ignored) {
                log.error("testTwoThreadsLockInterruptibly error");
            } finally {
                lock2.unlock();
                latch3.countDown();
            }
        });
        assertTrue(latch1.await(10, TimeUnit.SECONDS));
        assertFalse(locked.get());
        lock1.unlock();
        latch2.countDown();
        assertTrue(latch3.await(10, TimeUnit.SECONDS));
        assertTrue(locked.get());
    }

    @Test
    @Disabled
    @DisplayName("测试线程解锁非本线程的锁的场景")
    public void testUnlockForOtherThread() throws Exception {
        final Lock lock = client.getLock("test");
        lock.lockInterruptibly();
        final CountDownLatch latch = new CountDownLatch(1);
        Future<Object> result = threadExecutor.submit(() -> {
            try {
                lock.unlock();
            } catch (Exception e) {
                latch.countDown();
                return e;
            }
            return null;
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        lock.unlock();
        assertTrue(result.get(10, TimeUnit.SECONDS)
                .toString()
                .contains("The current thread doesn't own the lock at lock key test"));
    }

    private Map<String, DistributedLock> getLocks() throws Exception {
        Field locksField = DistributedLockClient.class.getDeclaredField("locks");
        locksField.setAccessible(true);
        if (!(locksField.get(client) instanceof Map)) {
            return new HashMap<>();
        }
        return (Map) locksField.get(client);
    }
}
