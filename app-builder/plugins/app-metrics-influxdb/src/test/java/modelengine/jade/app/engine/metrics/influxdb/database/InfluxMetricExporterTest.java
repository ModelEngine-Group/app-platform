/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.database;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.metrics.influxdb.UserDepartmentInfo;
import modelengine.jade.app.engine.metrics.influxdb.service.UserInfoService;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * {@link InfluxMetricExporter} 的测试。
 *
 * @author 高嘉乐
 * @since 2024-12-31
 */
@FitTestWithJunit(includeClasses = InfluxMetricExporter.class)
@DisplayName("测试 InfluxMetricExporter")
class InfluxMetricExporterTest {
    @Fit
    private MetricExporter exporter;

    @Mock
    private InfluxDB influxDB;

    @Mock
    private UserInfoService userInfoService;

    private SdkMeterProvider meterProvider;
    private LongHistogram histogram;

    @BeforeEach
    void setUp() {
        this.meterProvider = SdkMeterProvider.builder()
                .registerMetricReader(PeriodicMetricReader.builder(exporter).build()).build();
        histogram = meterProvider.get("request").histogramBuilder("test").ofLongs().build();
    }

    @Test
    @DisplayName("指标为空时不应抛出异常")
    void ShouldNotThrowWhenExportWithEmptyList() {
        assertDoesNotThrow(() -> {
            exporter.export(new ArrayList<>());
        });
    }

    @Test
    @DisplayName("上报数据时应调用一次 write")
    void shouldInvokeWriteOnceWhenExport() {
        UserDepartmentInfo userInfoPo = new UserDepartmentInfo();
        userInfoPo.setDepName1("dep1");
        userInfoPo.setDepName2("dep2");
        userInfoPo.setDepName3("dep3");
        userInfoPo.setDepName4("dep4");
        userInfoPo.setDepName5("dep5");
        userInfoPo.setDepName6("dep6");
        when(userInfoService.getUserDepartmentInfoByName(any())).thenReturn(userInfoPo);

        Attributes attributes = Attributes.builder()
                .put("app_id", "test_app")
                .put("user_name", "test_user")
                .put("L1_name", "dep1")
                .put("user_tag", "test_user_tag").build();

        histogram.record(20, attributes);
        histogram.record(2000);

        meterProvider.forceFlush();

        verify(influxDB, times(1)).write(any(BatchPoints.class));
    }
}