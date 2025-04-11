/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.persist.mapper;

import modelengine.fit.waterflow.flowsengine.persist.po.FlowTracePO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * flow trace对应MybatisMapper类
 *
 * @author 杨祥宇
 * @since 2023/8/30
 */
@Mapper
public interface FlowTraceMapper {
    /**
     * 保存flowTrace对象
     *
     * @param flowTrace flowTrace对象实体 {@link FlowTracePO}
     */
    void create(@Param("flowTrace") FlowTracePO flowTrace);

    /**
     * 更新flowTrace对象
     *
     * @param flowTrace flowTrace对象实体 {@link FlowTracePO}
     */
    void update(@Param("flowTrace") FlowTracePO flowTrace);

    /**
     * 根据trace id标识查询flowTrace对象
     *
     * @param traceId flowTrace对象id标识
     * @return flowTrace对象实体 {@link FlowTracePO}
     */
    FlowTracePO find(@Param("traceId") String traceId);

    /**
     * 删除对应stream所有trace对象
     *
     * @param streamId 流程streamId标识 {@link String}
     */
    void delete(@Param("streamId") String streamId);

    /**
     * 更新ContextPool
     *
     * @param traceId traceId
     * @param contextList contextList
     */
    void updateContextPool(@Param("traceId") String traceId, @Param("contextPool") String contextList);

    /**
     * 批量更新trace
     *
     * @param flowTraces flowTraces
     */
    void batchUpdate(List<FlowTracePO> flowTraces);

    /**
     * 批量保存trace
     *
     * @param flowTraces flowTraces
     */
    void batchCreate(List<FlowTracePO> flowTraces);

    /**
     * 根据traceIds批量查询trace
     *
     * @param traceIds trace id列表
     * @return trace列表
     */
    List<FlowTracePO> findByIdList(@Param("traceIds") List<String> traceIds);

    /**
     * 更新trace状态
     *
     * @param ids traceIds
     * @param status status
     * @param endTime endTime
     * @param exclusiveStatus 更新的status对应的互斥状态列表，如果数据库的status在exclusiveStatus中，则不能更新
     */
    void updateStatus(List<String> ids, String status, LocalDateTime endTime, List<String> exclusiveStatus);

    /**
     * 根据ids查询所有的trace对象
     *
     * @param traceIds traceIds
     * @return List<FlowTracePO>
     */
    List<FlowTracePO> findByTraceIdList(List<String> traceIds);

    /**
     * 根据id删除trace
     *
     * @param traceIds trace id列表
     */
    void deleteByIdList(List<String> traceIds);

    /**
     * 查找正在运行的trace
     *
     * @param applications 通过applications筛选
     * @return trace列表
     */
    List<String> findRunningTrace(List<String> applications);

    /**
     * 查询超期并且已完成的链路唯一标识列表。
     *
     * @param expiredDays 表示超期天数的 {@link LocalDateTime}。
     * @param limit 表示查询限制的 {@code int}。
     * @return 表示链路唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> getExpiredTrace(LocalDateTime expiredDays, int limit);
}
