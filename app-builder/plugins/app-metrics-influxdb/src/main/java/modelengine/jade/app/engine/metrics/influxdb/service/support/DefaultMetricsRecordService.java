/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.service.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.sdk.metrics.Aggregation;
import io.opentelemetry.sdk.metrics.InstrumentSelector;
import io.opentelemetry.sdk.metrics.InstrumentType;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.View;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jober.aipp.entity.AippFlowData;
import modelengine.fitframework.annotation.Component;
import modelengine.jade.app.engine.metrics.influxdb.config.RecordConfig;
import modelengine.jade.app.engine.metrics.influxdb.service.MetricsRecordService;
import modelengine.jade.app.engine.metrics.influxdb.utils.MetaUtils;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 指标数据上报服务。
 *
 * @author 高嘉乐
 * @since 2024-12-28
 */
@Component
public class DefaultMetricsRecordService implements MetricsRecordService {
    /**
     * 自动上报间隔（单位：秒）。
     */
    private static final int INTERVAL = 30;

    private final LongHistogram histogram;

    private final MetaService metaService;

    /**
     * 指标数据上报服务构造方法。
     *
     * @param exporter 表示指标数据导出器的 {@link MetricExporter}。
     * @param metaService 表示 meta 服务的 {@link MetaService}。
     */
    public DefaultMetricsRecordService(MetricExporter exporter, MetaService metaService) {
        this.metaService = metaService;
        SdkMeterProvider meterProvider = SdkMeterProvider.builder()
                .registerMetricReader(PeriodicMetricReader.builder(exporter)
                        .setInterval(INTERVAL, TimeUnit.SECONDS)
                        .build())
                .registerView(InstrumentSelector.builder().setType(InstrumentType.HISTOGRAM).build(),
                        View.builder()
                                .setAggregation(Aggregation.explicitBucketHistogram(RecordConfig.EXPLICIT_BUCKETS))
                                .build())
                .build();
        this.histogram = meterProvider.get("app-engine").histogramBuilder("request").ofLongs().build();
    }

    @Override
    public void recordMetrics(AippFlowData aippFlowData) {
        this.recordMetrics(aippFlowData, null);
    }

    @Override
    public void recordMetrics(AippFlowData aippFlowData, Map<String, String> userTags) {
        notNull(aippFlowData, "The input aippFlowData cannot be null.");
        String aippId = MetaUtils.getAippIdByAppId(this.metaService, aippFlowData.getAppId());
        AttributesBuilder builder = Attributes.builder()
                .put("app_id", aippId)
                .put("user_name", aippFlowData.getUsername());
        this.addUserTags(builder, userTags);
        long duration =
                Duration.between(aippFlowData.getCreateTime(), aippFlowData.getFinishTime()).toMillis();
        this.histogram.record(duration, builder.build());
    }

    private void addUserTags(AttributesBuilder attributesBuilder, Map<String, String> userTags) {
        if (userTags == null) {
            return;
        }
        for (Map.Entry<String, String> entry : userTags.entrySet()) {
            attributesBuilder.put(entry.getKey(), entry.getValue());
        }
    }
}