/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.task.entity.EvalReportEntity;
import modelengine.jade.app.engine.task.service.EvalReportService;
import modelengine.jade.app.engine.task.vo.EvalReportVo;
import modelengine.jade.common.vo.PageVo;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.test.domain.mvc.request.MockRequestBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.TypeUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalReportController} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-14
 */
@MvcTest(classes = EvalReportController.class)
@DisplayName("测试 EvalReportController")
public class EvalReportControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Mock
    private EvalReportService evalReportService;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("查询评估任务报告接口成功")
    void shouldOkWhenQueryEvalReport() {
        long reportId = 1L;
        String nodeName = "Accuracy";
        String nodeId = "node1";
        String schema = "{}";
        Double passScore = 80.0;
        Double averageScore = 100.0;
        int[] histogram = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

        EvalReportVo vo = new EvalReportVo();
        vo.setId(reportId);
        vo.setNodeName(nodeName);
        vo.setNodeId(nodeId);
        vo.setAlgorithmSchema(schema);
        vo.setAverageScore(averageScore);
        vo.setPassScore(passScore);
        vo.setHistogram(histogram);
        List<EvalReportVo> entities = Collections.singletonList(vo);

        when(this.evalReportService.listEvalReport(any())).thenReturn(PageVo.of(1, entities));
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/task/report")
                .param("instanceId", "1")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {EvalReportVo.class}));

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.response.objectEntity()).isPresent();

        PageVo<EvalReportVo> target = ObjectUtils.cast(this.response.objectEntity().get().object());
        assertThat(target.getTotal()).isEqualTo(1);
        assertThat(target.getItems()).isNotEmpty();
        assertThat(target.getItems().get(0)).extracting(EvalReportVo::getId,
                        EvalReportVo::getNodeId,
                        EvalReportVo::getNodeName,
                        EvalReportVo::getAlgorithmSchema,
                        EvalReportVo::getPassScore,
                        EvalReportVo::getAverageScore,
                        EvalReportVo::getHistogram)
                .containsExactly(reportId, nodeId, nodeName, schema, passScore, averageScore, histogram);
    }

    @Test
    @DisplayName("缺少参数导致查询评估数据集接口失败")
    void shouldFailWhenQueryEvalReport() {
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/task/report")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {EvalReportEntity.class}));

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }
}