/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.vo;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.jade.app.engine.metrics.influxdb.bo.BasicMetricsBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.DepartmentBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserAccessTrendBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserNumBo;
import modelengine.jade.app.engine.metrics.influxdb.config.RecordConfig;

import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 指标数据视图。
 *
 * @author 高嘉乐
 * @since 2024-12-10
 */
@Data
@NoArgsConstructor
public class MetricsVo {
    private long requestNum;
    private long avgLatency;
    private long userNum;
    private List<UserAccessVo> topUsers;
    private List<DepartmentAccessVo> topDepartments;
    private List<Long> avgLatencyDistribution;
    private List<String> avgLatencyRange;
    private List<UserAccessTrendVo> userAccessTrends;

    /**
     * 指标数据视图构造方法。
     *
     * @param basicMetricsBo 表示基本指标数据的 {@link BasicMetricsBo}。
     * @param topUsers 表示请求数量最多的用户信息的 {@link List}{@code <}{@link UserBo}{@code >}。
     * @param topDepartments 表示请求数量最多的部门信息的 {@link List}{@code <}{@link DepartmentBo}{@code >}。
     * @param userAccessTrends 表示用户访问趋势的 {@link List}{@code <}{@link UserAccessTrendBo}{@code >}。
     * @param userNumBo 表示用户数量的 {@link UserNumBo}。
     */
    public MetricsVo(BasicMetricsBo basicMetricsBo, List<UserBo> topUsers, List<DepartmentBo> topDepartments,
            List<UserAccessTrendBo> userAccessTrends, UserNumBo userNumBo) {
        this.avgLatencyRange =
                Arrays.asList(StringUtils.format("below {0}ms", RecordConfig.EXPLICIT_BUCKETS.get(0).intValue()),
                        StringUtils.format("{0}ms-{1}ms",
                                RecordConfig.EXPLICIT_BUCKETS.get(0).intValue() + 1,
                                RecordConfig.EXPLICIT_BUCKETS.get(1).intValue()),
                        StringUtils.format("{0}ms-{1}ms",
                                RecordConfig.EXPLICIT_BUCKETS.get(1).intValue() + 1,
                                RecordConfig.EXPLICIT_BUCKETS.get(2).intValue()),
                        StringUtils.format("above {0}ms", RecordConfig.EXPLICIT_BUCKETS.get(2).intValue()));
        this.updateBasic(basicMetricsBo);
        this.updateTop(topUsers, topDepartments);
        this.updateUserAccessTrends(userAccessTrends);
        this.updateUserNum(userNumBo);
    }

    private void updateBasic(BasicMetricsBo basicMetricsBo) {
        notNull(basicMetricsBo, "BasicMetricsBo cannot be null.");
        this.requestNum = basicMetricsBo.getCount();
        this.avgLatency = this.requestNum != 0 ? basicMetricsBo.getSum() / this.requestNum : 0;
        this.avgLatencyDistribution = Arrays.asList(basicMetricsBo.getBucket0(),
                basicMetricsBo.getBucket1(),
                basicMetricsBo.getBucket2(),
                basicMetricsBo.getBucket3());
    }

    private void updateTop(List<UserBo> userBoList, List<DepartmentBo> departmentBoList) {
        this.topUsers = userBoList.stream().map(UserAccessVo::from).collect(Collectors.toList());
        this.topDepartments = departmentBoList.stream().map(DepartmentAccessVo::from).collect(Collectors.toList());
    }

    private void updateUserAccessTrends(List<UserAccessTrendBo> userAccessTrends) {
        this.userAccessTrends = userAccessTrends.stream().map(UserAccessTrendVo::from).collect(Collectors.toList());
    }

    private void updateUserNum(UserNumBo userNumBo) {
        notNull(userNumBo, "UserNumBo cannot be null.");
        this.userNum = userNumBo.getCount();
    }
}