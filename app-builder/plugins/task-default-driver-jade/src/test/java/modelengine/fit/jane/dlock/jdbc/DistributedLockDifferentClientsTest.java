/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.dlock.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import modelengine.fit.jane.common.utils.SleepUtil;
import modelengine.fit.jane.dlock.DatabaseBaseTest;
import modelengine.fit.jane.dlock.jdbc.persist.mapper.FlowLockMapper;

import modelengine.fit.jane.common.utils.SleepUtil;
import modelengine.fit.waterflow.spi.lock.InvalidDistributedLockNotify;
import modelengine.fitframework.log.Logger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

/**
 * 分布式锁不同客户端测试类
 *
 * @author 李哲峰
 * @since 2023/12/01
 */
@DisplayName("分布式锁不同客户端测试集合")
public class DistributedLockDifferentClientsTest extends DatabaseBaseTest {
    private static final Logger log = Logger.get(DistributedLockDifferentClientsTest.class);

    @Override
    protected void cleanTable() {
        executeSqlInFile("handler/flowlock/cleanData.sql");
    }

    @Test
    @DisplayName("测试2个客户端的2个线程先后加解同1分布式锁")
    public void testAcquireLockAfterUnlock() throws Exception {
        final DistributedLockClient client1 = createClient(100, 50, "192.168.0.1");
        final DistributedLockClient client2 = createClient(100, 50, "192.168.0.2");

        final Lock lock1 = client1.getLock("test");
        final AtomicBoolean locked = new AtomicBoolean();
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final CountDownLatch latch3 = new CountDownLatch(1);
        lock1.lockInterruptibly();
        Executors.newSingleThreadExecutor().execute(() -> {
            Lock lock2 = client2.getLock("test");
            try {
                latch1.countDown();
                lock2.lockInterruptibly();
                latch2.await(10, TimeUnit.SECONDS);
                locked.set(true);
            } catch (InterruptedException ignored) {
                log.error("testAcquireLockAfterUnlock error");
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

    /**
     * 测试2个客户端的线程同时调用lockInterruptibly
     *
     * @throws Exception Exception
     */
    @Test
    @DisplayName("测试2个客户端的线程同时尝试获取分布式锁")
    public void testAcquireBothLocksWithSuccess() throws Exception {
        final DistributedLockClient client1 = createClient(100, 50, "192.168.0.1");
        final List<String> locked = new ArrayList<>();
        final CountDownLatch latch = new CountDownLatch(2);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.execute(() -> {
            Lock lock = client1.getLock("test");
            try {
                lock.lockInterruptibly();
                locked.add("1");
                latch.countDown();
            } catch (InterruptedException ignored) {
                log.error("testAcquireBothLocksWithSuccess error");
            } finally {
                try {
                    lock.unlock();
                } catch (Exception e2) {
                    // 忽略
                }
            }
        });
        final DistributedLockClient client2 = createClient(100, 50, "192.168.0.2");
        pool.execute(() -> {
            Lock lock = client2.getLock("test");
            try {
                lock.lockInterruptibly();
                locked.add("2");
                latch.countDown();
            } catch (InterruptedException ignored) {
                log.error("testAcquireBothLocksWithSuccess error");
            } finally {
                try {
                    lock.unlock();
                } catch (Exception e2) {
                    // 忽略
                }
            }
        });

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        // 最终两个客户端都成功获取锁以及解锁
        assertTrue(locked.contains("1"));
        assertTrue(locked.contains("2"));
        pool.shutdownNow();
    }

    @Test
    @DisplayName("测试当多个客户端的线程同时调用tryLock时，只有一个客户端获得分布式锁的场景")
    public void testOnlyOneLock() throws Exception {
        final BlockingQueue<String> locked = new LinkedBlockingQueue<>();
        final CountDownLatch latch = new CountDownLatch(20);
        ArrayList<Callable<Boolean>> tasks = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            int suffix = i;
            Callable<Boolean> task = () -> {
                DistributedLockClient client = createClient(100, 50, "192.168.0." + suffix);
                Lock lock = client.getLock("test");
                try {
                    if (lock.tryLock(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                        locked.add("done");
                        return true;
                    }
                } finally {
                    latch.countDown();
                }
                return false;
            };
            tasks.add(task);
        }
        ExecutorService pool = Executors.newFixedThreadPool(20);
        pool.invokeAll(tasks);
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertEquals(1, locked.size());
        assertTrue(locked.contains("done"));
        pool.shutdownNow();
    }

    @Test
    @DisplayName("测试当一个客户端加锁期间另一客户端阻塞的场景")
    public void testLockExclusiveAccess() throws Exception {
        Lock lock1 = createClient(1000, 500, "192.168.0.1").getLock("test");
        final BlockingQueue<Integer> data = new LinkedBlockingQueue<>();
        final CountDownLatch latch1 = new CountDownLatch(1);
        lock1.lockInterruptibly();
        Executors.newSingleThreadExecutor().execute(() -> {
            Lock lock2 = createClient(1000, 500, "192.168.0.2").getLock("test");
            try {
                latch1.countDown();
                lock2.lockInterruptibly();
                data.add(4);
                SleepUtil.sleep(10);
                data.add(5);
                SleepUtil.sleep(10);
                data.add(6);
            } catch (InterruptedException ignored) {
                log.error("testLockExclusiveAccess error");
            } finally {
                lock2.unlock();
            }
        });
        assertTrue(latch1.await(10, TimeUnit.SECONDS));
        data.add(1);
        SleepUtil.sleep(100);
        data.add(2);
        SleepUtil.sleep(100);
        data.add(3);
        lock1.unlock();
        for (int i = 0; i < 6; i++) {
            Integer integer = data.poll(10, TimeUnit.SECONDS);
            assertNotNull(integer);
            assertEquals(i + 1, integer.intValue());
        }
    }


    @Test
    @DisplayName("测试分布式锁过期失效后可以再次被另一个客户端获得的场景")
    public void testAcquireLockAfterExpiry() throws Exception {
        Lock lock1 = createClient(100, 1000, "192.168.0.1").getLock("test");
        final BlockingQueue<Integer> data = new LinkedBlockingQueue<>();
        final CountDownLatch latch = new CountDownLatch(1);
        lock1.lockInterruptibly();
        SleepUtil.sleep(200);
        Executors.newSingleThreadExecutor().execute(() -> {
            Lock lock2 = createClient(100, 1000, "192.168.0.2").getLock("test");
            try {
                lock2.lockInterruptibly();
                data.add(1);
            } catch (InterruptedException ignored) {
                log.error("testAcquireLockAfterExpiry error");
            } finally {
                lock2.unlock();
            }
            latch.countDown();
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        data.add(2);
        lock1.unlock();
        for (int i = 0; i < 2; i++) {
            Integer integer = data.poll(10, TimeUnit.SECONDS);
            assertNotNull(integer);
            assertEquals(i + 1, integer.intValue());
        }
    }

    @Test
    @DisplayName("测试分布式锁被续约导致另一客户端无法获取的场景")
    public void testAcquireLockBlockedByRenewal() throws Exception {
        Lock lock1 = createClient(100, 50, "192.168.0.1").getLock("test");
        final BlockingQueue<Integer> data = new LinkedBlockingQueue<>();
        final CountDownLatch latch = new CountDownLatch(1);
        lock1.lockInterruptibly();
        SleepUtil.sleep(200);
        Executors.newSingleThreadExecutor().execute(() -> {
            Lock lock2 = createClient(100, 50, "192.168.0.2").getLock("test");
            try {
                lock2.lockInterruptibly();
                data.add(1);
            } catch (InterruptedException ignored) {
                log.error("testAcquireLockBlockedByRenewal interruptedException");
            } finally {
                lock2.unlock();
            }
            latch.countDown();
        });
        assertFalse(latch.await(1, TimeUnit.SECONDS));
        lock1.unlock();
        assertTrue(data.isEmpty());
    }

    private DistributedLockClient createClient(long ttl, long renewScheduleRate, String ipAddress) {
        DefaultDistributedLockRepo repository = new DefaultDistributedLockRepo(
                sqlSessionManager.openSession(true).getMapper(FlowLockMapper.class));
        repository.setLockedClient(ipAddress);
        return new DistributedLockClient(ttl, renewScheduleRate, repository, mock(InvalidDistributedLockNotify.class));
    }
}
