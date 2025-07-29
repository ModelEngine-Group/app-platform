/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.database;

import modelengine.jade.app.engine.metrics.influxdb.bo.BasicMetricsBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.DepartmentBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.RequestSourceBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserAccessTrendBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserNumBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserSourceBo;
import modelengine.jade.app.engine.metrics.influxdb.utils.DepartmentLevel;
import modelengine.jade.app.engine.metrics.influxdb.utils.SampleLevel;

import java.util.List;

/**
 * InfluxDb 数据交互接口。
 *
 * @author 高嘉乐
 * @since 2024-12-30
 */
public interface InfluxDbRep {
    /**
     * 获取基本指标。
     *
     * @param appId 表示应用唯一标识符的 {@link String}。
     * @param start 表示查询开始时间的 {@code long}。
     * @param end 表示查询结束时间的 {@code long}。
     * @return 表示基本指标数据的 {@link BasicMetricsBo}。
     */
    BasicMetricsBo getBasicMetricBo(String appId, long start, long end);

    /**
     * 获取请求数量最多的 {@code num} 个用户。
     *
     * @param appId 表示应用唯一标识符的 {@link String}。
     * @param start 表示查询开始时间的 {@code long}。
     * @param end 表示查询结束时间的 {@code long}。
     * @param num 表示取请求数最多的用户数量的 {@code int}。
     * @return 表示请求数量最多的用户的 {@link List}{@code <}{@link UserBo}{@code >}。
     */
    List<UserBo> getTopUsers(String appId, long start, long end, int num);

    /**
     * 获取请求数量最多的 {@code num} 个部门。
     *
     * @param appId 表示应用唯一标识符的 {@link String}。
     * @param start 表示查询开始时间的 {@code long}。
     * @param end 表示查询结束时间的 {@code long}。
     * @param num 表示取请求数最多的部门数量的 {@code int}。
     * @return 表示请求数量最多的部门的 {@link List}{@code <}{@link DepartmentBo}{@code >}。
     */
    List<DepartmentBo> getTopDepartments(String appId, long start, long end, int num);

    /**
     * 获取用户来源。
     *
     * @param appId 表示应用唯一标识符的 {@link String}。
     * @param start 表示查询开始时间的 {@code long}。
     * @param end 表示查询结束时间的 {@code long}。
     * @param departmentLevel 表示部门级别的 {@link DepartmentLevel}。
     * @return 表示用户来源的 {@link List}{@code <}{@link UserSourceBo}{@code >}。
     */
    List<UserSourceBo> getUserSource(String appId, long start, long end, DepartmentLevel departmentLevel);

    /**
     * 获取请求来源。
     *
     * @param appId 表示应用唯一标识符的 {@link String}。
     * @param start 表示查询开始时间的 {@code long}。
     * @param end 表示查询结束时间的 {@code long}。
     * @param departmentLevel 表示部门级别的 {@link String}。
     * @return 表示请求来源的 {@link List}{@code <}{@link RequestSourceBo}{@code >}。
     */
    List<RequestSourceBo> getRequestSource(String appId, long start, long end, DepartmentLevel departmentLevel);

    /**
     * 获取用户访问趋势。
     *
     * @param appId 表示应用唯一标识符的 {@link String}。
     * @param start 表示查询开始时间的 {@code long}。
     * @param end 表示查询结束时间的 {@code long}。
     * @param sampleLevel 表示查询聚合时间精度的 {@link SampleLevel}。
     * @return 表示用户访问趋势的 {@link List}{@code <}{@link UserAccessTrendBo}{@code >}。
     */
    List<UserAccessTrendBo> getUserAccessTrends(String appId, long start, long end, SampleLevel sampleLevel);

    /**
     * 获取用户数量。
     *
     * @param appId 表示应用唯一标识符的 {@link String}。
     * @param start 表示查询开始时间的 {@code long}。
     * @param end 表示查询结束时间的 {@code long}。
     * @return 表示用户数量的 {@link UserNumBo}。
     */
    UserNumBo getUserNumBo(String appId, long start, long end);
}