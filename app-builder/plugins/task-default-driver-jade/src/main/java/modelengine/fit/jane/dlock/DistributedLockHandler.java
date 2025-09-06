/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.dlock;

import java.util.concurrent.locks.Lock;

/**
 * 表示分布式锁的处理接口
 *
 * @author 李哲峰
 * @since 2023-11-30
 */
public interface DistributedLockHandler {
    /**
     * 通过锁key获取分布式锁
     *
     * @param key 分布式锁的key值
     * @return {@link Lock} 分布式锁锁对象
     */
    Lock getLock(String key);
}
