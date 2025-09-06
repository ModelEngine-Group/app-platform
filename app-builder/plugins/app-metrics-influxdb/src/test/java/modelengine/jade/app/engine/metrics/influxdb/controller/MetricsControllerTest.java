/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.metrics.influxdb.service.MetricsAnalysisService;
import modelengine.jade.app.engine.metrics.influxdb.vo.MetricsVo;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.test.domain.mvc.request.MockRequestBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

/**
 * {@link MetricsController} 的测试。
 *
 * @author 高嘉乐
 * @since 2025-01-07
 */
@MvcTest(classes = MetricsController.class)
@DisplayName("测试 MetricsController")
class MetricsControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Mock
    private MetricsAnalysisService metricsAnalysisService;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("当部门级别非法时应失败")
    void shouldFailWhenDepartmentLevelNameInvalid() {
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/metrics/v2/analysis_user")
                .param("appId", "AaBb200")
                .param("startTimestamp", "100")
                .param("endTimestamp", "200")
                .param("departmentLevelName", "invalid");

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("当部门级别不存在时应失败")
    void shouldFailWhenDepartmentLevelNameNotExist() {
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/metrics/v2/analysis_user")
                .param("appId", "AaBb200")
                .param("startTimestamp", "100")
                .param("endTimestamp", "200");

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("当开始时间大于结束时间时应失败")
    void shouldFailWhenStartTimeLargerThanEndTime() {
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/metrics/v2/analysis_request")
                .param("appId", "AaBb200")
                .param("startTimestamp", "200")
                .param("endTimestamp", "100")
                .param("departmentLevelName", "l1_name");

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("当查询基本信息时不包含部门级别，应返回正常值")
    void shouldSuccessWhenGetMetricsWithoutDepartmentLevelName() {
        when(metricsAnalysisService.getMetrics(any(), anyLong(), anyLong())).thenReturn(new MetricsVo());

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/metrics/v2/analysis")
                .param("appId", "AaBb200")
                .param("startTimestamp", "100")
                .param("endTimestamp", "200");

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("查询用户来源信息成功")
    void shouldSuccessWhenGetUserSource() {
        when(metricsAnalysisService.getUserSource(any(), anyLong(), anyLong(), any())).thenReturn(new ArrayList<>());

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/metrics/v2/analysis_user")
                .param("appId", "AaBb200")
                .param("startTimestamp", "100")
                .param("endTimestamp", "200")
                .param("departmentLevelName", "l1_name");

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("查询请求来源信息成功")
    void shouldSuccessWhenGetRequestSource() {
        when(metricsAnalysisService.getUserSource(any(), anyLong(), anyLong(), any())).thenReturn(new ArrayList<>());

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/metrics/v2/analysis_request")
                .param("appId", "AaBb200")
                .param("startTimestamp", "100")
                .param("endTimestamp", "200")
                .param("departmentLevelName", "l1_name");

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("当输入非法应用唯一标识符时应失败")
    void shouldFailWithInvalidAppId() {
        when(metricsAnalysisService.getUserSource(any(), anyLong(), anyLong(), any())).thenReturn(new ArrayList<>());

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/metrics/v2/analysis_request")
                .param("appId", "xxxxxxx")
                .param("startTimestamp", "100")
                .param("endTimestamp", "200")
                .param("departmentLevelName", "l1_name");

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }
}