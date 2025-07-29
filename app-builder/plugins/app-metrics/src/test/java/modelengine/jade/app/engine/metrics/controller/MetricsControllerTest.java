/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.metrics.dto.MetricsFeedbackDto;
import modelengine.jade.app.engine.metrics.po.TimeType;
import modelengine.jade.app.engine.metrics.service.MetricsAnalysisService;
import modelengine.jade.app.engine.metrics.service.MetricsFeedbackService;
import modelengine.jade.app.engine.metrics.vo.MetricsAnalysisVo;
import modelengine.jade.app.engine.metrics.vo.MetricsFeedbackVo;
import modelengine.jade.app.engine.metrics.vo.Page;
import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.test.domain.mvc.request.MockRequestBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * {@link MetricsController} 的测试。
 *
 * @author 刘信宏
 * @since 2024-08-22
 */
@MvcTest(classes = {MetricsController.class})
class MetricsControllerTest {
    @Mock
    private MetricsAnalysisService metricsAnalysisService;

    @Mock
    private MetricsFeedbackService metricsFeedbackService;

    @Fit
    private MetricsController metricsController;

    @Fit
    private MockMvc mockMvc;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    void test_getAnalysis_should_ok_when_test_data_combination() {
        // setup
        MetricsAnalysisVo metricsAnalysisVo = new MetricsAnalysisVo();
        when(metricsAnalysisService.findMetricsData(anyString(), any(TimeType.class))).thenReturn(metricsAnalysisVo);

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/metrics/analysis")
                .param("appId", "1")
                .param("timeType", String.valueOf(TimeType.TODAY))
                .responseType(MetricsAnalysisVo.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    void test_getFeedback_should_ok_when_test_data_combination() {
        Page<MetricsFeedbackVo> page = new Page();
        when(metricsFeedbackService.getMetricsFeedback(any(MetricsFeedbackDto.class))).thenReturn(page);

        MetricsFeedbackDto metricsFeedbackDTO = MetricsFeedbackDto.builder().appId("id").build();
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/metrics/feedback")
                .jsonEntity(metricsFeedbackDTO)
                .responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    void test_export_should_ok_when_test_data_combination() throws Exception {
        // setup
        byte[] buf = new byte[0];
        ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
        when(metricsFeedbackService.export(any(MetricsFeedbackDto.class))).thenReturn(inputStream);

        MetricsFeedbackDto metricsFeedbackDTO = new MetricsFeedbackDto();

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/metrics/export").jsonEntity(metricsFeedbackDTO).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }
}