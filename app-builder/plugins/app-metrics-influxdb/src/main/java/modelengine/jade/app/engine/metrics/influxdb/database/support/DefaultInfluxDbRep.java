/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.database.support;

import modelengine.jade.app.engine.metrics.influxdb.bo.BasicMetricsBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.DepartmentBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.RequestSourceBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserAccessTrendBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserNumBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserSourceBo;
import modelengine.jade.app.engine.metrics.influxdb.database.InfluxDbRep;
import modelengine.jade.app.engine.metrics.influxdb.utils.DepartmentLevel;
import modelengine.jade.app.engine.metrics.influxdb.utils.SampleLevel;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.util.StringUtils;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;

import java.util.List;

/**
 * InfluxDb 数据交互实现。
 *
 * @author 高嘉乐
 * @since 2024-12-30
 */
@Component
public class DefaultInfluxDbRep implements InfluxDbRep {
    private final InfluxDB influxDb;
    private final InfluxDBResultMapper resultMapper;

    public DefaultInfluxDbRep(@Fit InfluxDB influxDb) {
        this.influxDb = influxDb;
        this.resultMapper = new InfluxDBResultMapper();
    }

    @Override
    public BasicMetricsBo getBasicMetricBo(String appId, long start, long end) {
        return this.executeQuery(StringUtils.format("select sum(sum) as sum,"
                                + "sum(count) as count,"
                                + "sum(bucket0) as bucket0,"
                                + "sum(bucket1) as bucket1,"
                                + "sum(bucket2) as bucket2,"
                                + "sum(bucket3) as bucket3 "
                                + "from request "
                                + "where app_id = '{0}' "
                                + "and time >= {1}ms "
                                + "and time <= {2}ms", appId, start, end),
                BasicMetricsBo.class).stream().findFirst().orElse(new BasicMetricsBo());
    }

    @Override
    public List<UserBo> getTopUsers(String appId, long start, long end, int num) {
        return this.executeQuery(StringUtils.format("select top(count, user_name, {0}) as count "
                + "from (select sum(count) as count "
                + "from request "
                + "where app_id = '{1}' "
                + "and time >= {2}ms "
                + "and time <= {3}ms "
                + "group by user_name)", num, appId, start, end), UserBo.class);
    }

    @Override
    public List<DepartmentBo> getTopDepartments(String appId, long start, long end, int num) {
        return this.executeQuery(StringUtils.format("select top(count, {0}) as count,"
                + "l4_name as dep_name "
                + "from (select sum(count) as count "
                + "from request "
                + "where app_id = '{1}' "
                + "and time >= {2}ms "
                + "and time <= {3}ms "
                + "group by l4_name)", num, appId, start, end), DepartmentBo.class);
    }

    @Override
    public List<UserSourceBo> getUserSource(String appId, long start, long end, DepartmentLevel departmentLevel) {
        return this.executeQuery(StringUtils.format("select top(count, 10) as count,"
                + "{3} as dep_name "
                + "from (select count(last) "
                + "from (select last(sum) "
                + "from request "
                + "where app_id = '{0}' "
                + "and time >= {1}ms "
                + "and time <= {2}ms "
                + "group by user_name, {3}) "
                + "group by {3})", appId, start, end, departmentLevel.getLevelName()), UserSourceBo.class);
    }

    @Override
    public List<RequestSourceBo> getRequestSource(String appId, long start, long end, DepartmentLevel departmentLevel) {
        return this.executeQuery(StringUtils.format("select top(count, 10) as count,"
                + "{3} as dep_name "
                + "from (select sum(count) as count "
                + "from request where app_id = '{0}' "
                + "and time >= {1}ms "
                + "and time <= {2}ms "
                + "group by {3})", appId, start, end, departmentLevel.getLevelName()), RequestSourceBo.class);
    }

    @Override
    public List<UserAccessTrendBo> getUserAccessTrends(String appId, long start, long end, SampleLevel sampleLevel) {
        return this.executeQuery(StringUtils.format("select sum(count) as count "
                + "from request "
                + "where app_id = '{0}' "
                + "and time >= {1}ms "
                + "and time <= {2}ms "
                + "group by time({3}m) "
                + "fill(0)", appId, start, end, sampleLevel.getInterval()), UserAccessTrendBo.class);
    }

    @Override
    public UserNumBo getUserNumBo(String appId, long start, long end) {
        return this.executeQuery(StringUtils.format("select count(sum) "
                + "from (select sum(count) "
                + "from request "
                + "where app_id = '{0}' "
                + "and time >= {1}ms "
                + "and time <= {2}ms "
                + "group by user_name)", appId, start, end), UserNumBo.class)
                .stream().findFirst().orElse(new UserNumBo());
    }

    private <T> List<T> executeQuery(String sql, Class<T> clazz) {
        QueryResult result = this.influxDb.query(new Query(sql));
        return this.resultMapper.toPOJO(result, clazz, "request");
    }
}