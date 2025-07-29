/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.dlock.jdbc;

import modelengine.fit.jane.dlock.jdbc.utils.DistributedLockStatus;

/**
 * 分布式锁 repo核心类
 *
 * @author 李哲峰
 * @since 2024/1/31
 */
public interface DistributedLockRepo {
    /**
     * 创建锁对象
     *
     * @param key 锁的key值
     * @param ttl 锁的生命周期 单位: ms
     * @return 创建锁对象的结果
     */
    boolean create(String key, long ttl);

    /**
     * 当且仅当lockedClient为本地客户端或锁对象过期时保留并更新锁对象
     *
     * @param key 锁的key值
     * @param ttl 锁的生命周期 单位: ms
     * @return 创建锁对象的结果
     */
    boolean update(String key, long ttl);

    /**
     * 更新锁对象过期时间
     *
     * @param key 锁的key值
     * @param ttl 锁的生命周期 单位: ms
     * @return 更新的结果
     */
    boolean updateExpiredAt(String key, long ttl);

    /**
     * 检查锁对象是否存在
     *
     * @param key 锁的key值
     * @return key值对应的锁是否已经被获取
     */
    boolean isExists(String key);

    /**
     * 删除锁对象
     *
     * @param key 锁的key值
     * @return 删除的结果
     */
    boolean delete(String key);

    /**
     * 删除过期锁对象
     *
     * @param key 锁的key值
     * @return 删除的结果
     */
    boolean deleteExpired(String key);

    /**
     * 获取锁状态
     *
     * @param key 锁的key值
     * @return 锁状态
     */
    DistributedLockStatus getStatus(String key);

    /**
     * 从repo获取当前时间戳
     *
     * @return 时间戳
     */
    long now();
}
