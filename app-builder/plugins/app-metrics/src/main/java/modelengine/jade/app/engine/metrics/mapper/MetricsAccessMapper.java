/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.mapper;

import modelengine.jade.app.engine.metrics.po.MetricsAccessPo;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * MetricsAccessMapper类消息处理策略
 *
 * @author 陈霄宇
 * @since 2024/05/24
 */
@Mapper
public interface MetricsAccessMapper {
    /**
     * insert to metrics_access in batch
     *
     * @param metricsAccessList metrics_access list
     */
    void insertMetricAccessBatch(@Param("metricsAccessList") List<MetricsAccessPo> metricsAccessList);

    /**
     * get hourly data
     *
     * @param appIds 应用id
     * @param startTime 根据时间类型计算起始时间
     * @param endTime 根据时间类型计算结束时间
     * @return 获取以小时为单位的数据
     */
    @MapKey("time_unit")
    List<Map<String, Object>> getHourlyAccessData(
            @Param("appIds") List<String> appIds,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * get daily data
     *
     * @param appIds 应用id
     * @param startTime 根据时间类型计算起始时间
     * @param endTime 根据时间类型计算结束时间
     * @return 获取以天为单位的数据
     */
    @MapKey("time_unit")
    List<Map<String, Object>> getDailyAccessData(
            @Param("appIds") List<String> appIds,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
