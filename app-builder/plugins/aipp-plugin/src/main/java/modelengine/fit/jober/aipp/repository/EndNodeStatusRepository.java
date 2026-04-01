/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository;

import modelengine.fit.jober.aipp.domain.EndNodeStatus;

import java.util.List;

/**
 * END节点状态相关数据库操作对象.
 *
 * @author 张越
 * @since 2026-04-01
 */
public interface EndNodeStatusRepository {
    /**
     * 根据 traceId 获取END节点状态.
     *
     * @param traceId 追踪实例运行情况的唯一标识.
     * @return END节点状态列表.
     */
    List<EndNodeStatus> selectByTraceId(String traceId);

    /**
     * 根据 instanceId 获取END节点状态.
     *
     * @param instanceId 实例ID.
     * @return END节点状态列表.
     */
    List<EndNodeStatus> selectByInstanceId(String instanceId);

    /**
     * 插入一条数据.
     *
     * @param endNodeStatus END节点状态.
     */
    void insertOne(EndNodeStatus endNodeStatus);

    /**
     * 获取END节点状态过期历史记录.
     *
     * @param expiredDays 表示超期天数的 int.
     * @param limit 表示查询条数的 int.
     * @return 超期END节点状态id的列表.
     */
    List<Long> getExpiredEndNodeStatuses(int expiredDays, int limit);

    /**
     * 根据END节点状态id列表删除历史记录.
     *
     * @param endNodeStatusIds 历史记录的id列表.
     */
    void deleteEndNodeStatuses(List<Long> endNodeStatusIds);
}
