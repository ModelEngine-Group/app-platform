/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.service.support;

import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fitframework.annotation.Component;
import modelengine.jade.app.engine.metrics.influxdb.bo.BasicMetricsBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.DepartmentBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.RequestSourceBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserAccessTrendBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserNumBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserSourceBo;
import modelengine.jade.app.engine.metrics.influxdb.database.InfluxDbRep;
import modelengine.jade.app.engine.metrics.influxdb.service.MetricsAnalysisService;
import modelengine.jade.app.engine.metrics.influxdb.utils.DepartmentLevel;
import modelengine.jade.app.engine.metrics.influxdb.utils.MetaUtils;
import modelengine.jade.app.engine.metrics.influxdb.utils.SampleLevel;
import modelengine.jade.app.engine.metrics.influxdb.vo.MetricsVo;
import modelengine.jade.app.engine.metrics.influxdb.vo.RequestSourceVo;
import modelengine.jade.app.engine.metrics.influxdb.vo.UserSourceVo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 指标数据查询服务。
 *
 * @author 高嘉乐
 * @since 2024-12-21
 */
@Component
public class DefaultMetricsAnalysisService implements MetricsAnalysisService {
    private static final int TOP_USER_NUM = 10;
    private static final int TOP_DEPARTMENT_NUM = 10;

    private final InfluxDbRep influxDbRep;
    private final MetaService metaService;

    public DefaultMetricsAnalysisService(InfluxDbRep influxDbRep, MetaService metaService) {
        this.influxDbRep = influxDbRep;
        this.metaService = metaService;
    }

    @Override
    public MetricsVo getMetrics(String appId, long startTimestamp, long endTimestamp) {
        String aippId = MetaUtils.getAippIdByAppId(this.metaService, appId);
        // 获取指标
        BasicMetricsBo basicMetricsBo = this.influxDbRep.getBasicMetricBo(aippId, startTimestamp, endTimestamp);
        List<UserBo> userBoList = this.influxDbRep.getTopUsers(aippId, startTimestamp, endTimestamp, TOP_USER_NUM);
        List<DepartmentBo> departmentBoList =
                this.influxDbRep.getTopDepartments(aippId, startTimestamp, endTimestamp, TOP_DEPARTMENT_NUM);
        UserNumBo userNum = this.influxDbRep.getUserNumBo(aippId, startTimestamp, endTimestamp);
        List<UserAccessTrendBo> userAccessTrends = this.influxDbRep.getUserAccessTrends(aippId,
                startTimestamp,
                endTimestamp,
                SampleLevel.calLevel(endTimestamp - startTimestamp));

        return new MetricsVo(basicMetricsBo, userBoList, departmentBoList, userAccessTrends, userNum);
    }

    @Override
    public List<UserSourceVo> getUserSource(String appId, long startTimestamp, long endTimestamp,
            DepartmentLevel departmentLevel) {
        String aippId = MetaUtils.getAippIdByAppId(this.metaService, appId);
        List<UserSourceBo> userSourceList =
                this.influxDbRep.getUserSource(aippId, startTimestamp, endTimestamp, departmentLevel);
        return userSourceList.stream().map(UserSourceVo::from).collect(Collectors.toList());
    }

    @Override
    public List<RequestSourceVo> getRequestSource(String appId, long startTimestamp, long endTimestamp,
            DepartmentLevel departmentLevel) {
        String aippId = MetaUtils.getAippIdByAppId(this.metaService, appId);
        List<RequestSourceBo> requestSourceList =
                this.influxDbRep.getRequestSource(aippId, startTimestamp, endTimestamp, departmentLevel);
        return requestSourceList.stream().map(RequestSourceVo::from).collect(Collectors.toList());
    }
}