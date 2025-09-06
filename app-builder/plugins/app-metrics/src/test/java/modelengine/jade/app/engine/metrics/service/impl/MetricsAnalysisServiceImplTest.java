/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jober.common.RangeResult;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.util.MapBuilder;
import modelengine.jade.app.engine.metrics.mapper.ConversationRecordMapper;
import modelengine.jade.app.engine.metrics.mapper.MetricsAccessMapper;
import modelengine.jade.app.engine.metrics.po.ConversationRecordPo;
import modelengine.jade.app.engine.metrics.po.TimeType;
import modelengine.jade.app.engine.metrics.vo.MetricsAnalysisVo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * {@link MetricsAnalysisServiceImpl} 的测试。
 *
 * @author 刘信宏
 * @since 2024-08-21
 */
@FitTestWithJunit(includeClasses = MetricsAnalysisServiceImpl.class)
class MetricsAnalysisServiceImplTest {
    @Mock
    private ConversationRecordMapper conversationRecordMapper;

    @Mock
    private MetaService metaService;

    @Mock
    private MetricsAccessMapper metricsAccessMapper;

    @Fit
    private MetricsAnalysisServiceImpl metricsAnalysisServiceImpl;

    static class TimeTypeProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Arrays.stream(TimeType.values()).map(Arguments::of);
        }
    }

    @AfterEach
    void tearDown() {
        clearInvocations(this.conversationRecordMapper, this.metricsAccessMapper, this.conversationRecordMapper);
    }

    @Test
    void test_collectAccessData_should_void_when_test_data_combination() {
        List<ConversationRecordPo> metricMessages = new ArrayList<>();
        ConversationRecordPo conversationRecordPo = ConversationRecordPo.builder().id(1L).appId("id").build();
        metricMessages.add(conversationRecordPo);
        when(this.conversationRecordMapper.getRecordByTime(any(LocalDateTime.class),
                any(LocalDateTime.class))).thenReturn(metricMessages);

        this.metricsAnalysisServiceImpl.collectAccessData();

        verify(this.conversationRecordMapper).getRecordByTime(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @ParameterizedTest
    @ArgumentsSource(TimeTypeProvider.class)
    void test_findMetricsData_should_return_not_null_when_objects_is_null(TimeType type) {
        Meta meta = this.buildMeta();
        RangeResult rangeResult = new RangeResult(0, 0, 1);
        RangedResultSet set = new RangedResultSet(Collections.singletonList(meta), rangeResult);
        when(metaService.list(any(), anyBoolean(), anyLong(), anyInt(), any())).thenReturn(set);
        Map<String, Map<String, Object>> basicMetrics = MapBuilder.<String, Map<String, Object>>get()
                .put("average_response_time",
                        MapBuilder.<String, Object>get().put("value", new BigDecimal(500.46)).build())
                .build();
        when(conversationRecordMapper.getBasicMetrics(any(), any(), any())).thenReturn(basicMetrics);
        this.metricsAnalysisServiceImpl.findMetricsData("id", type);
        verify(this.conversationRecordMapper).getBasicMetrics(anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class));
        verify(this.conversationRecordMapper).getAvgResponseRange(anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class));
        verify(this.conversationRecordMapper).getTopUsers(anyList(),
                any(LocalDateTime.class),
                any(LocalDateTime.class));
        if (type == TimeType.TODAY || type == TimeType.YESTERDAY) {
            verify(this.metricsAccessMapper).getHourlyAccessData(anyList(),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class));
        } else {
            verify(this.metricsAccessMapper).getDailyAccessData(anyList(),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class));
        }
    }

    @ParameterizedTest
    @ArgumentsSource(TimeTypeProvider.class)
    @DisplayName("测试返回的平均响应时间是否为整数")
    void test_findAvgResponseTime_should_return_int(TimeType type) {
        Meta meta = this.buildMeta();
        RangeResult rangeResult = new RangeResult(0, 0, 1);
        RangedResultSet set = new RangedResultSet(Collections.singletonList(meta), rangeResult);
        when(metaService.list(any(), anyBoolean(), anyLong(), anyInt(), any())).thenReturn(set);
        Map<String, Map<String, Object>> basicMetrics = MapBuilder.<String, Map<String, Object>>get()
                .put("average_response_time",
                        MapBuilder.<String, Object>get().put("value", new BigDecimal(500.46)).build())
                .build();
        when(conversationRecordMapper.getBasicMetrics(any(), any(), any())).thenReturn(basicMetrics);
        MetricsAnalysisVo metricsAnalysisVo = this.metricsAnalysisServiceImpl.findMetricsData("id", type);
        assertThat(metricsAnalysisVo.getBasicMetrics().get("average_response_time").get("value")).isEqualTo(500L);
    }

    Meta buildMeta() {
        Meta meta = new Meta();
        meta.setId("appId1");
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("meta_status", "active");
        attributes.put("app_id", "appId1");
        meta.setAttributes(attributes);
        return meta;
    }
}