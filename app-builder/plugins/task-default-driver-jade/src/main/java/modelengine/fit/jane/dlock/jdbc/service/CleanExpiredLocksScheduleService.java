/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.dlock.jdbc.service;

import modelengine.fit.jane.dlock.jdbc.DistributedLockClient;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.annotation.Scheduled;

/**
 * 分布式锁定时清理服务
 *
 * @author 李哲峰
 * @since 2024/2/23
 */
@Component
public class CleanExpiredLocksScheduleService {
    private static final Logger log = Logger.get(CleanExpiredLocksScheduleService.class);

    /**
     * 锁对象的过期间隔时间
     */
    private final long timeout;

    /**
     * 分布式锁客户端
     */
    private final DistributedLockClient distributedLockClient;

    public CleanExpiredLocksScheduleService(@Value("${databasedistributedlock.timeout}") long timeout,
            DistributedLockClient distributedLockClient) {
        this.timeout = timeout;
        this.distributedLockClient = distributedLockClient;
    }

    /**
     * 定时清理上次使用时间小于当前时间减去timeout时间间隔的锁对象
     */
    @Scheduled(strategy = Scheduled.Strategy.FIXED_RATE, value = "30000")
    public void clean() {
        log.debug("Start cleaning up expired distributed locks");
        this.distributedLockClient.deleteExpiredLocks(this.timeout);
    }
}
