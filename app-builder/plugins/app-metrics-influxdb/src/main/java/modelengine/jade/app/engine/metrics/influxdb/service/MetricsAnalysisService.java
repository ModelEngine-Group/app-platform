/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.service;

import modelengine.jade.app.engine.metrics.influxdb.utils.DepartmentLevel;
import modelengine.jade.app.engine.metrics.influxdb.vo.MetricsVo;
import modelengine.jade.app.engine.metrics.influxdb.vo.RequestSourceVo;
import modelengine.jade.app.engine.metrics.influxdb.vo.UserSourceVo;

import java.util.List;

/**
 * 指标数据查询服务。
 *
 * @author 高嘉乐
 * @since 2024-12-18
 */
public interface MetricsAnalysisService {
    /**
     * 获取指标。
     *
     * @param appId 表示应用唯一标识符的 {@link String}。
     * @param startTimestamp 表示开始时间戳的 {@code long}。
     * @param endTimestamp 表示结束时间戳的 {@code long}。
     * @return 表示指标数据的 {@link MetricsVo}。
     */
    MetricsVo getMetrics(String appId, long startTimestamp, long endTimestamp);

    /**
     * 获取用户来源。
     *
     * @param appId 表示应用唯一标识符的 {@link String}。
     * @param startTimestamp 表示开始时间戳的 {@code long}。
     * @param endTimestamp 表示结束时间戳的 {@code long}。
     * @param departmentLevel 表示部门级别的 {@link DepartmentLevel}。
     * @return 表示用户来源的 {@link List}{@code <}{@link UserSourceVo}{@code >}。
     */
    List<UserSourceVo> getUserSource(String appId, long startTimestamp, long endTimestamp,
            DepartmentLevel departmentLevel);

    /**
     * 获取请求来源。
     *
     * @param appId 表示应用唯一标识符的 {@link String}。
     * @param startTimestamp 表示开始时间戳的 {@code long}。
     * @param endTimestamp 表示结束时间戳的 {@code long}。
     * @param departmentLevel 表示部门级别的 {@link DepartmentLevel}。
     * @return 表示请求来源的 {@link List}{@code <}{@link RequestSourceVo}{@code >}。
     */
    List<RequestSourceVo> getRequestSource(String appId, long startTimestamp, long endTimestamp,
            DepartmentLevel departmentLevel);
}