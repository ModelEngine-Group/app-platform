/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows;

import modelengine.fit.waterflow.MethodNameLoggerExtension;
import modelengine.fit.waterflow.common.utils.SleepUtil;

import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.function.Supplier;

/**
 * ClassName
 * 简述
 *
 * @author 高诗意
 * @since 2023/10/31
 */
@ExtendWith(MethodNameLoggerExtension.class)
public final class FlowsTestUtil {
    /**
     * 最大等待时间
     */
    public static final int MAX_WAIT_TIME_MS = 5000;

    /**
     * waitFortyMillis
     *
     * @param supplier supplier
     * @return list
     */
    public static <T> List<T> waitFortyMillis(Supplier<List<T>> supplier) {
        return waitMillis(supplier, 40);
    }

    /**
     * 等待时间
     *
     * @param supplier supplier
     * @param millis 时间
     * @return 等待时间
     */
    public static <T> List<T> waitMillis(Supplier<List<T>> supplier, long millis) {
        SleepUtil.sleep(millis);
        return supplier.get();
    }

    /**
     * 等待结果为空
     *
     * @param supplier supplier
     * @return list
     * @throws IllegalStateException 如果在超时时间内未获取到期望的结果
     */
    public static <T> List<T> waitEmpty(Supplier<List<T>> supplier) {
        return waitSize(supplier, 0, MAX_WAIT_TIME_MS);
    }

    /**
     * waitSingle
     *
     * @param supplier supplier
     * @return list
     * @throws IllegalStateException 如果在超时时间内未获取到结果
     */
    public static <T> List<T> waitSingle(Supplier<List<T>> supplier) {
        return waitSingle(supplier, MAX_WAIT_TIME_MS);
    }

    /**
     * waitSingle with timeout
     *
     * @param supplier supplier
     * @param maxWaitMs 最大等待时间
     * @return list
     * @throws IllegalStateException 如果在超时时间内未获取到结果
     */
    public static <T> List<T> waitSingle(Supplier<List<T>> supplier, int maxWaitMs) {
        return waitSize(supplier, 1, maxWaitMs);
    }

    /**
     * 等待大小
     *
     * @param supplier supplier
     * @param size 大小
     * @return list
     * @throws IllegalStateException 如果在超时时间内未获取到期望的结果
     */
    public static <T> List<T> waitSize(Supplier<List<T>> supplier, int size) {
        return waitSize(supplier, size, MAX_WAIT_TIME_MS);
    }

    /**
     * 等待大小, 同时增加最大等待时间
     *
     * @param supplier supplier
     * @param size 大小
     * @param maxWaitMs 最大等待时间
     * @return list
     * @throws IllegalStateException 如果在超时时间内未获取到期望的结果
     */
    public static <T> List<T> waitSize(Supplier<List<T>> supplier, int size, int maxWaitMs) {
        int time = 0;
        int step = 5;
        while (time < maxWaitMs) {
            List<T> ts = supplier.get();
            if (ts.size() == size) {
                return ts;
            }
            SleepUtil.sleep(step);
            time += step;
        }
        throw new IllegalStateException(
            "等待超时：在 " + maxWaitMs + "ms 内未获取到期望的 " + size + " 条结果，当前结果数=" + supplier.get().size());
    }

    /**
     * 等待时间
     *
     * @param stop stop
     * @param most most
     */
    public static void waitUntil(Supplier<Boolean> stop, int most) {
        int time = 0;
        int step = 5;
        while (!stop.get() && time < most) {
            SleepUtil.sleep(step);
            time += step;
        }
    }

    /**
     * 等待时间
     *
     * @param stop stop时间
     */
    public static void waitUntil(Supplier<Boolean> stop) {
        waitUntil(stop, 500);
    }
}

