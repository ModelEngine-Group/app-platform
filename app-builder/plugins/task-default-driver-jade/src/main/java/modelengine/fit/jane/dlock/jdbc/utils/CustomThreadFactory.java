/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.dlock.jdbc.utils;

import modelengine.fitframework.log.Logger;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义的线程工厂
 *
 * @author 夏斐
 * @since 2024/3/1
 */
public class CustomThreadFactory implements ThreadFactory {
    private static final Logger log = Logger.get(CustomThreadFactory.class);

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final String namePrefix;

    public CustomThreadFactory(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, namePrefix + "-" + threadNumber.getAndIncrement());
        t.setUncaughtExceptionHandler((tr, ex) -> log.error(tr.getName() + " : " + ex.getMessage()));
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        return t;
    }
}

