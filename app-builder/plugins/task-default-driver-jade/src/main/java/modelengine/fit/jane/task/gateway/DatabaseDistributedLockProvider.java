/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.task.gateway;

import lombok.AllArgsConstructor;

import modelengine.fit.jane.dlock.jdbc.DistributedLockClient;
import modelengine.fit.waterflow.spi.lock.DistributedLockProvider;
import modelengine.fitframework.annotation.Alias;
import modelengine.fitframework.annotation.Component;

import java.util.concurrent.locks.Lock;

/**
 * 为 {@link DistributedLockProvider} 提供基于SQL数据库的实现。
 *
 * @author 李哲峰
 * @since 2023-12-07
 */
@Component
@AllArgsConstructor
@Alias("databaseDistributedLockProvider")
public class DatabaseDistributedLockProvider implements DistributedLockProvider {
    /**
     * 分布式锁客户端
     */
    private final DistributedLockClient distributedLockClient;

    @Override
    public Lock get(String key) {
        return this.distributedLockClient.getLock(key);
    }
}
