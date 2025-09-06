/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.dlock.jdbc;

import lombok.RequiredArgsConstructor;
import modelengine.fit.jane.dlock.jdbc.persist.mapper.FlowLockMapper;
import modelengine.fit.jane.dlock.jdbc.persist.po.FlowLockPO;
import modelengine.fit.jane.dlock.jdbc.utils.DistributedLockStatus;
import modelengine.fit.jane.dlock.jdbc.utils.HostUtil;
import modelengine.fitframework.annotation.Component;

import org.apache.ibatis.exceptions.PersistenceException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * {@link DistributedLockRepo} 默认实现类
 *
 * @author 李哲峰
 * @since 2023/11/30
 */
@Component
@RequiredArgsConstructor
public class DefaultDistributedLockRepo implements DistributedLockRepo {
    private final FlowLockMapper flowLockMapper;

    private String lockedClient = HostUtil.getHostAddress();

    /**
     * 设置上锁客户端
     *
     * @param lockedClient 客户端IP地址
     */
    public void setLockedClient(String lockedClient) {
        this.lockedClient = lockedClient;
    }

    /**
     * 创建锁对象
     *
     * @param key 锁的key值
     * @param ttl 锁的生命周期 单位: ms
     * @return 创建锁对象的结果
     */
    @Override
    public boolean create(String key, long ttl) {
        try {
            Timestamp expireTime = new Timestamp(this.now() + ttl);
            FlowLockPO flowLockPO = FlowLockPO.builder()
                    .lockKey(key)
                    .expiredAt(expireTime.toLocalDateTime())
                    .lockedClient(this.lockedClient)
                    .build();
            return this.flowLockMapper.create(flowLockPO) > 0;
        } catch (PersistenceException e) {
            return false;
        }
    }

    /**
     * 当且仅当lockedClient为本地客户端或锁对象过期时保留并更新锁对象
     *
     * @param key 锁的key值
     * @param ttl 锁的生命周期 单位: ms
     * @return 更新锁对象的结果
     */
    @Override
    public boolean update(String key, long ttl) {
        long curTime = this.now();
        Timestamp expireTime = new Timestamp(curTime + ttl);
        FlowLockPO flowLockPO = FlowLockPO.builder()
                .lockKey(key)
                .expiredAt(expireTime.toLocalDateTime())
                .lockedClient(this.lockedClient)
                .build();
        return this.flowLockMapper.update(flowLockPO, new Timestamp(curTime).toLocalDateTime()) > 0;
    }

    /**
     * 更新锁对象过期时间
     *
     * @param key 锁的key值
     * @param ttl 锁的生命周期 单位: ms
     * @return 更新的结果
     */
    @Override
    public boolean updateExpiredAt(String key, long ttl) {
        Timestamp expireTime = new Timestamp(this.now() + ttl);
        FlowLockPO flowLockPO = FlowLockPO.builder()
                .lockKey(key)
                .expiredAt(expireTime.toLocalDateTime())
                .lockedClient(this.lockedClient)
                .build();
        return this.flowLockMapper.updateExpiredAt(flowLockPO) > 0;
    }

    /**
     * 检查锁对象是否存在
     *
     * @param key 锁的key值
     * @return key值对应的锁是否已经被获取
     */
    @Override
    public boolean isExists(String key) {
        FlowLockPO flowLockPO = FlowLockPO.builder()
                .lockKey(key)
                .expiredAt(this.nowLocalDateTime())
                .lockedClient(this.lockedClient)
                .build();
        return this.flowLockMapper.isExists(flowLockPO);
    }

    /**
     * 删除锁对象
     *
     * @param key 锁的key值
     * @return 删除的结果
     */
    @Override
    public boolean delete(String key) {
        return this.flowLockMapper.delete(key, this.lockedClient) > 0;
    }

    /**
     * 删除过期锁对象
     *
     * @param key 锁的key值
     * @return 删除的结果
     */
    @Override
    public boolean deleteExpired(String key) {
        FlowLockPO flowLockPO = FlowLockPO.builder()
                .lockKey(key)
                .expiredAt(this.nowLocalDateTime())
                .lockedClient(this.lockedClient)
                .build();
        return this.flowLockMapper.deleteExpired(flowLockPO) > 0;
    }

    @Override
    public DistributedLockStatus getStatus(String key) {
        LocalDateTime localDateTime = this.nowLocalDateTime();
        FlowLockPO flowLockPO = this.flowLockMapper.find(key);
        if (flowLockPO == null) {
            return DistributedLockStatus.NOT_EXIST;
        }
        boolean isOwner = Objects.equals(flowLockPO.getLockedClient(), this.lockedClient);
        boolean isExpired = localDateTime.isAfter(flowLockPO.getExpiredAt());
        if (isOwner) {
            return isExpired ? DistributedLockStatus.LOCK_BY_ME_EXPIRED : DistributedLockStatus.LOCK_BY_ME;
        }
        return isExpired ? DistributedLockStatus.LOCK_BY_OTHER_EXPIRED : DistributedLockStatus.LOCK_BY_OTHER;
    }

    /**
     * 从repo获取当前时间戳
     *
     * @return 时间戳
     */
    @Override
    public long now() {
        return this.flowLockMapper.now();
    }

    private LocalDateTime nowLocalDateTime() {
        return new Timestamp(this.now()).toLocalDateTime();
    }
}
