/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.mapper;

import modelengine.fit.jober.aipp.po.EndNodeStatusPo;

import java.util.List;

/**
 * END节点状态相关的数据库操作.
 *
 * @author 张越
 * @since 2026-04-01
 */
public interface EndNodeStatusMapper {
    /**
     * 根据traceId查询所有的END节点状态信息.
     *
     * @param traceId 追踪实例运行的唯一标识.
     * @return END节点状态列表.
     */
    List<EndNodeStatusPo> selectByTraceId(String traceId);

    /**
     * 根据instanceId查询所有的END节点状态信息.
     *
     * @param instanceId 实例ID.
     * @return END节点状态列表.
     */
    List<EndNodeStatusPo> selectByInstanceId(String instanceId);

    /**
     * 插入一条数据.
     *
     * @param endNodeStatusPo END节点状态对象.
     */
    void insertOne(EndNodeStatusPo endNodeStatusPo);

    /**
     * 获取超期的END节点状态信息唯一标识列表。
     *
     * @param expiredDays 表示超期时间的 int。
     * @param limit 表示查询条数的 int。
     * @return END节点状态唯一标识列表.
     */
    List<Long> getExpiredEndNodeStatuses(int expiredDays, int limit);

    /**
     * 根据END节点状态唯一标识列表删除记录。
     *
     * @param endNodeStatusIds END节点状态唯一标识列表.
     */
    void deleteEndNodeStatuses(List<Long> endNodeStatusIds);
}
