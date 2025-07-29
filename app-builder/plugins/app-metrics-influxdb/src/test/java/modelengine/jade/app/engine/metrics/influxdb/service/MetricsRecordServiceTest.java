/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.jober.aipp.entity.AippFlowData;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.jade.app.engine.metrics.influxdb.database.InfluxMetricExporter;
import modelengine.jade.app.engine.metrics.influxdb.service.support.DefaultMetricsRecordService;

import org.influxdb.InfluxDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link MetricsRecordService} 的测试。
 *
 * @author 高嘉乐
 * @since 2024-12-10
 */
@FitTestWithJunit(includeClasses = {DefaultMetricsRecordService.class, InfluxMetricExporter.class})
@DisplayName("测试数据上报服务接口")
public class MetricsRecordServiceTest {
    @Fit
    private MetricsRecordService metricsRecordService;

    @Mock
    private InfluxDB influxDb;

    @Mock
    private UserInfoService userInfoService;

    @Mock
    private MetaService metaService;

    @BeforeEach
    void setUp() {
        RangedResultSet<Meta> resultSet = new RangedResultSet<>();
        resultSet.setResults(new ArrayList<>());
        when(metaService.list(any(MetaFilter.class),
                anyBoolean(),
                anyLong(),
                anyInt(),
                any(OperationContext.class))).thenReturn(resultSet);
    }

    @Test
    @DisplayName("传入空时应抛出非法参数异常")
    void shouldThrowWhenRecordMetricWithNullRecord() {
        assertThatIllegalArgumentException().isThrownBy(() -> {
            metricsRecordService.recordMetrics(null);
        });
    }

    @Test
    @DisplayName("传入带用户标签的数据后不应抛出异常")
    void shouldNotThrowWhenRecordMetricsWithUserTags() {
        Exception exception = catchException(() -> {
            metricsRecordService.recordMetrics(getRecord(), getUserTags());
        });

        assertThat(exception).isNull();
    }

    private AippFlowData getRecord() {
        return AippFlowData.builder()
                .appId("test_app")
                .username("test_user")
                .createTime(LocalDateTime.now().minusSeconds(5))
                .finishTime(LocalDateTime.now())
                .build();
    }

    private Map<String, String> getUserTags() {
        Map<String, String> userTags = new HashMap<>();
        userTags.put("myTag", "myValue");
        return userTags;
    }
}