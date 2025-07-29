/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.database;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.jade.app.engine.metrics.influxdb.UserDepartmentInfo;
import modelengine.jade.app.engine.metrics.influxdb.service.UserInfoService;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.metrics.InstrumentType;
import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
import io.opentelemetry.sdk.metrics.data.HistogramPointData;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBException;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * InfluxDb 指标上报。
 *
 * @author 高嘉乐
 * @since 2024-12-18
 */
@Component
public class InfluxMetricExporter implements MetricExporter {
    private static final Logger LOG = Logger.get(InfluxMetricExporter.class);

    private final InfluxDB influxdb;

    private final UserInfoService userInfoService;

    public InfluxMetricExporter(InfluxDB influxdb, UserInfoService userInfoService) {
        this.influxdb = influxdb;
        this.userInfoService = userInfoService;
    }

    @Override
    public CompletableResultCode export(Collection<MetricData> metrics) {
        BatchPoints batchPoints = metrics.stream()
                .flatMap(metric -> metric.getHistogramData()
                        .getPoints()
                        .stream()
                        .filter(data -> data.getCount() != 0)
                        .map(this::getPoint))
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        (points -> BatchPoints.builder().points(points).build())));
        if (batchPoints.getPoints().isEmpty()) {
            return CompletableResultCode.ofSuccess();
        }
        try {
            this.influxdb.write(batchPoints);
        } catch (InfluxDBException e) {
            LOG.warn("Writing metrics to influxdb failed.", e);
            return CompletableResultCode.ofFailure();
        }
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode flush() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public CompletableResultCode shutdown() {
        return CompletableResultCode.ofSuccess();
    }

    @Override
    public AggregationTemporality getAggregationTemporality(InstrumentType instrumentType) {
        return AggregationTemporality.DELTA;
    }

    private Point getPoint(HistogramPointData data) {
        Point.Builder measurementBuilder =
                Point.measurement("request").addField("count", data.getCount()).addField("sum", data.getSum());
        for (int i = 0; i < data.getCounts().size(); i++) {
            measurementBuilder.addField(StringUtils.format("bucket{0}", i), data.getCounts().get(i));
        }
        data.getAttributes().forEach((k, v) -> {
            measurementBuilder.tag(k.getKey(), v.toString());
        });
        this.addUserDepartmentInfo(measurementBuilder, data.getAttributes().get(AttributeKey.stringKey("user_name")));
        return measurementBuilder.build();
    }

    private void addUserDepartmentInfo(Point.Builder builder, String userName) {
        UserDepartmentInfo userInfo = userInfoService.getUserDepartmentInfoByName(userName);
        notNull(userInfo, "The user info cannot be null. [username={0}]", userName);
        builder.tag("l1_name", userInfo.getDepName1())
                .tag("l2_name", userInfo.getDepName2())
                .tag("l3_name", userInfo.getDepName3())
                .tag("l4_name", userInfo.getDepName4())
                .tag("l5_name", userInfo.getDepName5())
                .tag("l6_name", userInfo.getDepName6());
    }
}