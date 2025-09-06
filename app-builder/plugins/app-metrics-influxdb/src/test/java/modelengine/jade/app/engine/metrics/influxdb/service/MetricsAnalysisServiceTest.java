/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.jade.app.engine.metrics.influxdb.bo.BasicMetricsBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.DepartmentBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.RequestSourceBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserAccessTrendBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserNumBo;
import modelengine.jade.app.engine.metrics.influxdb.bo.UserSourceBo;
import modelengine.jade.app.engine.metrics.influxdb.database.InfluxDbRep;
import modelengine.jade.app.engine.metrics.influxdb.service.support.DefaultMetricsAnalysisService;
import modelengine.jade.app.engine.metrics.influxdb.utils.DepartmentLevel;
import modelengine.jade.app.engine.metrics.influxdb.utils.SampleLevel;
import modelengine.jade.app.engine.metrics.influxdb.vo.MetricsVo;
import modelengine.jade.app.engine.metrics.influxdb.vo.RequestSourceVo;
import modelengine.jade.app.engine.metrics.influxdb.vo.UserSourceVo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link DefaultMetricsAnalysisService} 的测试。
 *
 * @author 高嘉乐
 * @since 2025-01-02
 */
@FitTestWithJunit(includeClasses = DefaultMetricsAnalysisService.class)
@DisplayName("测试 MetricsAnalysisServiceImpl")
class MetricsAnalysisServiceTest {
    private static final Instant MOCK_TIME;
    private static final BasicMetricsBo MOCK_BASIC_METRIC;
    private static final List<UserBo> MOCK_TOP_USERS;
    private static final List<DepartmentBo> MOCK_TOP_DEPARTMENTS;
    private static final UserNumBo MOCK_USER_NUM;
    private static final List<UserAccessTrendBo> MOCK_USER_ACCESS_TRENDS;
    private static final List<UserSourceBo> MOCK_USER_SOURCES;
    private static final List<RequestSourceBo> MOCK_REQUEST_SOURCES;

    static {
        MOCK_TIME = Instant.now();
        MOCK_BASIC_METRIC = getMockBasicMetric();
        MOCK_TOP_USERS = getMockTopUsers();
        MOCK_TOP_DEPARTMENTS = getMockTopDepartments();
        MOCK_USER_NUM = getMockUserNum();
        MOCK_USER_ACCESS_TRENDS = getMockUserAccessTrends();
        MOCK_USER_SOURCES = getMockUserSources();
        MOCK_REQUEST_SOURCES = getMockRequestSources();
    }

    @Fit
    private DefaultMetricsAnalysisService metricsAnalysisService;

    @Mock
    private InfluxDbRep influxDbRep;

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

    @AfterEach
    void tearDown() {
        clearInvocations(influxDbRep);
    }

    @Test
    @DisplayName("测试 getMetrics，有数据时，返回正常结果")
    void shouldOkWhenGetMetricsSuccess() {
        when(influxDbRep.getBasicMetricBo(any(), anyLong(), anyLong())).thenReturn(MOCK_BASIC_METRIC);
        when(influxDbRep.getTopUsers(any(), anyLong(), anyLong(), anyInt())).thenReturn(MOCK_TOP_USERS);
        when(influxDbRep.getTopDepartments(any(), anyLong(), anyLong(), anyInt())).thenReturn(MOCK_TOP_DEPARTMENTS);
        when(influxDbRep.getUserNumBo(any(), anyLong(), anyLong())).thenReturn(MOCK_USER_NUM);
        when(influxDbRep.getUserAccessTrends(any(), anyLong(), anyLong(), any(SampleLevel.class))).thenReturn(
                MOCK_USER_ACCESS_TRENDS);

        MetricsVo metrics = metricsAnalysisService.getMetrics("123", 0, 100);

        assertThat(metrics).extracting(MetricsVo::getUserNum,
                        MetricsVo::getRequestNum,
                        MetricsVo::getAvgLatency,
                        o -> o.getTopUsers().size(),
                        o -> o.getTopDepartments().size(),
                        o -> o.getUserAccessTrends().size(),
                        MetricsVo::getAvgLatencyDistribution)
                .containsExactly(MOCK_USER_NUM.getCount(),
                        MOCK_BASIC_METRIC.getCount(),
                        MOCK_BASIC_METRIC.getSum() / MOCK_BASIC_METRIC.getCount(),
                        MOCK_TOP_USERS.size(),
                        MOCK_TOP_DEPARTMENTS.size(),
                        MOCK_USER_ACCESS_TRENDS.size(),
                        Arrays.asList(MOCK_BASIC_METRIC.getBucket0(),
                                MOCK_BASIC_METRIC.getBucket1(),
                                MOCK_BASIC_METRIC.getBucket2(),
                                MOCK_BASIC_METRIC.getBucket3()));
    }

    @Test
    @DisplayName("测试 getMetrics，无数据时，返回默认结果")
    void shouldReturnEmptyWhenGetMetricsEmpty() {
        when(influxDbRep.getBasicMetricBo(any(), anyLong(), anyLong())).thenReturn(new BasicMetricsBo());
        when(influxDbRep.getTopUsers(any(), anyLong(), anyLong(), anyInt())).thenReturn(new ArrayList<>());
        when(influxDbRep.getTopDepartments(any(), anyLong(), anyLong(), anyInt())).thenReturn(new ArrayList<>());
        when(influxDbRep.getUserNumBo(any(), anyLong(), anyLong())).thenReturn(new UserNumBo());
        when(influxDbRep.getUserAccessTrends(any(),
                anyLong(),
                anyLong(),
                any(SampleLevel.class))).thenReturn(new ArrayList<>());

        MetricsVo metrics = metricsAnalysisService.getMetrics("123", 0, 100);

        assertThat(metrics).isNotNull()
                .extracting(MetricsVo::getUserNum,
                        MetricsVo::getRequestNum,
                        MetricsVo::getAvgLatency,
                        o -> o.getUserAccessTrends().size(),
                        o -> o.getTopUsers().size(),
                        o -> o.getTopDepartments().size())
                .containsExactly(0L, 0L, 0L, 0, 0, 0);
    }

    @Test
    @DisplayName("测试获取用户来源，有数据时，返回正常结果")
    void shouldOkWhenGetUserSourceSuccess() {
        when(influxDbRep.getUserSource(any(), anyLong(), anyLong(), any())).thenReturn(MOCK_USER_SOURCES);

        List<UserSourceVo> userSource =
                metricsAnalysisService.getUserSource("123", 0, 100, DepartmentLevel.DEP_LEVEL_1);
        assertThat(userSource.size()).isEqualTo(MOCK_USER_SOURCES.size());
    }

    @Test
    @DisplayName("测试获取用户来源，无数据时，返回空列表")
    void shouldReturnEmptyListWhenGetUserSourceEmpty() {
        when(influxDbRep.getUserSource(any(), anyLong(), anyLong(), any())).thenReturn(new ArrayList<>());

        List<UserSourceVo> userSource =
                metricsAnalysisService.getUserSource("123", 0, 100, DepartmentLevel.DEP_LEVEL_2);
        assertThat(userSource.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("测试获取请求来源，有数据时，返回正常结果")
    void shouldOkWhenGetRequestSourceSuccess() {
        when(influxDbRep.getRequestSource(any(), anyLong(), anyLong(), any())).thenReturn(MOCK_REQUEST_SOURCES);

        List<RequestSourceVo> requestSource =
                metricsAnalysisService.getRequestSource("123", 0, 100, DepartmentLevel.DEP_LEVEL_5);
        assertThat(requestSource.size()).isEqualTo(MOCK_REQUEST_SOURCES.size());
    }

    @Test
    @DisplayName("测试获取请求来源，无数据时，返回空列表")
    void shouldReturnEmptyListWhenGetRequestSourceEmpty() {
        when(influxDbRep.getRequestSource("123", 0, 100, DepartmentLevel.DEP_LEVEL_2)).thenReturn(new ArrayList<>());

        List<RequestSourceVo> requestSource =
                metricsAnalysisService.getRequestSource("123", 0, 100, DepartmentLevel.DEP_LEVEL_1);
        assertThat(requestSource.size()).isEqualTo(0);
    }

    private static BasicMetricsBo getMockBasicMetric() {
        BasicMetricsBo basicMetricsBo = new BasicMetricsBo();
        basicMetricsBo.setCount(1L);
        basicMetricsBo.setSum(1000L);
        basicMetricsBo.setBucket0(0L);
        basicMetricsBo.setBucket1(1L);
        basicMetricsBo.setBucket2(2L);
        basicMetricsBo.setBucket3(3L);
        return basicMetricsBo;
    }

    private static List<UserBo> getMockTopUsers() {
        UserBo userBo = new UserBo();
        userBo.setUserName("mock_user");
        userBo.setCount(5L);
        return Collections.singletonList(userBo);
    }

    private static List<DepartmentBo> getMockTopDepartments() {
        DepartmentBo departmentBo = new DepartmentBo();
        departmentBo.setDepartmentName("mock_department");
        departmentBo.setCount(25L);
        return Collections.singletonList(departmentBo);
    }

    private static UserNumBo getMockUserNum() {
        UserNumBo userNumBo = new UserNumBo();
        userNumBo.setCount(11L);
        return userNumBo;
    }

    private static List<UserAccessTrendBo> getMockUserAccessTrends() {
        UserAccessTrendBo userAccessTrendBo = new UserAccessTrendBo();
        userAccessTrendBo.setCount(18L);
        userAccessTrendBo.setTime(MOCK_TIME);
        return Collections.singletonList(userAccessTrendBo);
    }

    private static List<UserSourceBo> getMockUserSources() {
        UserSourceBo userSourceBo = new UserSourceBo();
        userSourceBo.setDepartmentName("mock_department");
        userSourceBo.setCount(100L);
        return Collections.singletonList(userSourceBo);
    }

    private static List<RequestSourceBo> getMockRequestSources() {
        RequestSourceBo requestSourceBo = new RequestSourceBo();
        requestSourceBo.setDepartmentName("mock_department");
        requestSourceBo.setCount(120L);
        return Collections.singletonList(requestSourceBo);
    }
}