/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.dlock;

/**
 * 表示支持生命周期管理的分布式锁处理接口
 *
 * @author 李哲峰
 * @since 2023-11-30
 */
public interface ExpirableDistributedLockHandler extends DistributedLockHandler {
    /**
     * 删除未被使用的于给定timeout时间前过期的锁
     *
     * @param timeout 从上次使用锁到现在所经历的时间
     */
    void deleteExpiredLocks(long timeout);
}
