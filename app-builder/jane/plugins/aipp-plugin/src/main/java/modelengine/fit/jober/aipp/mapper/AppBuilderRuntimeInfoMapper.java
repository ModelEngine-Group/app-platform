/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.mapper;

import modelengine.fit.jober.aipp.po.AppBuilderRuntimeInfoPo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 运行时信息相关的数据库操作
 *
 * @author 张越
 * @since 2024-07-29
 */
public interface AppBuilderRuntimeInfoMapper {
    /**
     * 根据traceId查询所有的运行时信息.
     *
     * @param traceId 追踪实例运行的唯一标识.
     * @return {@link List}{@code <}{@link AppBuilderRuntimeInfoPo}{@code >} 运行时信息列表.
     */
    List<AppBuilderRuntimeInfoPo> selectByTraceId(String traceId);

    /**
     * 插入一条数据.
     *
     * @param appBuilderRuntimeInfoPO {@link AppBuilderRuntimeInfoPo} 对象.
     */
    void insertOne(AppBuilderRuntimeInfoPo appBuilderRuntimeInfoPO);

    /**
     * 获取超期的运行时信息唯一标识列表。
     *
     * @param expiredDays 表示超期时间的 {@code int}。
     * @param limit 表示查询条数的 {@code int}。
     * @return 表示运行时信息唯一标识列表的 {@link List}{@code <}{@link Long}{@code >}。
     */
    List<Long> getExpiredRuntimeInfos(int expiredDays, int limit);

    /**
     * 根据运行时信息唯一标识列表强制删除会话记录。
     *
     * @param runtimeInfoIds 表示运行时信息唯一标识列表的 {@link List}{@code <}{@link Long}{@code >}。
     */
    void deleteRuntimeInfos(List<Long> runtimeInfoIds);
}
