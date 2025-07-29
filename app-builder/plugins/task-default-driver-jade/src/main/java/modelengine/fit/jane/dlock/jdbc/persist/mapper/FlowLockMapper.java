/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.dlock.jdbc.persist.mapper;

import modelengine.fit.jane.dlock.jdbc.persist.po.FlowLockPO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

/**
 * flow lock对应MybatisMapper类
 *
 * @author 李哲峰
 * @since 2023/11/29
 */
@Mapper
public interface FlowLockMapper {
    /**
     * 保存flowLock对象
     *
     * @param flowLock flowLock对象实体 {@link FlowLockPO}
     * @return 保存结果
     */
    int create(@Param("flowLock") FlowLockPO flowLock);

    /**
     * 更新flowLock对象
     *
     * @param flowLock flowLock对象实体 {@link FlowLockPO}
     * @param expiredAt 过期时间 {@link LocalDateTime}
     * @return 更新结果
     */
    int update(@Param("flowLock") FlowLockPO flowLock, @Param("expiredAt") LocalDateTime expiredAt);

    /**
     * 更新flowLock到期时间
     *
     * @param flowLock flowLock对象实体 {@link FlowLockPO}
     * @return 更新结果
     */
    int updateExpiredAt(@Param("flowLock") FlowLockPO flowLock);

    /**
     * 根据lockKey标识查询flowLock对象
     *
     * @param lockKey flowLock对象lockKey标识 {@link String}
     * @return flowLock对象实体 {@link FlowLockPO}
     */
    FlowLockPO find(@Param("lockKey") String lockKey);

    /**
     * 检查flowLock对象是否存在
     *
     * @param flowLock flowLock对象实体 {@link FlowLockPO}
     * @return 查找结果
     */
    boolean isExists(@Param("flowLock") FlowLockPO flowLock);

    /**
     * 删除对应lockKey和lockedClient的lock对象
     *
     * @param lockKey flowLock对象lockKey标识 {@link String}
     * @param lockedClient flowLock对象lockedClient标识 {@link String}
     * @return 删除结果
     */
    int delete(@Param("lockKey") String lockKey, @Param("lockedClient") String lockedClient);

    /**
     * 删除对应flowLock的过期lock对象
     *
     * @param flowLock flowLock对象实体 {@link FlowLockPO}
     * @return 删除结果
     */
    int deleteExpired(@Param("flowLock") FlowLockPO flowLock);

    /**
     * 获取数据库当前时间
     *
     * @return 毫秒时间戳
     */
    Long now();
}
