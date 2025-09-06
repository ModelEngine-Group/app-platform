/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.task.dto.EvalReportQueryParam;
import modelengine.jade.app.engine.task.entity.EvalReportEntity;
import modelengine.jade.app.engine.task.mapper.EvalReportMapper;
import modelengine.jade.app.engine.task.service.EvalReportService;
import modelengine.jade.app.engine.task.vo.EvalReportVo;
import modelengine.jade.common.vo.PageVo;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalReportServiceImpl} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-14
 */
@FitTestWithJunit(includeClasses = EvalReportServiceImpl.class)
public class EvalReportServiceImplTest {
    @Fit
    private EvalReportService evalReportService;

    @Mock
    private EvalReportMapper evalReportMapper;

    @AfterEach
    void teardown() {
        clearInvocations(this.evalReportMapper);
    }

    @Test
    @DisplayName("插入评估任务报告成功")
    void shouldOkWhenCreateEvalReport() {
        doNothing().when(this.evalReportMapper).create(any());
        EvalReportEntity entity = new EvalReportEntity();
        entity.setNodeId("Accuracy");
        entity.setAverageScore(100.0);
        entity.setHistogram("{}");
        entity.setInstanceId(1L);

        this.evalReportService.createEvalReport(Collections.singletonList(entity));
        verify(this.evalReportMapper, times(1)).create((any()));
    }

    @Test
    @DisplayName("查询评估任务报告元数据成功")
    void shouldOkWhenListEvalReport() {
        long reportId = 1L;
        String nodeName = "Accuracy";
        String nodeId = "node1";
        String schema = "{}";
        Double passScore = 80.0;
        Double averageScore = 100.0;
        int[] histogram = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

        EvalReportEntity entity = new EvalReportEntity();
        entity.setId(reportId);
        entity.setNodeId(nodeId);
        entity.setNodeName(nodeName);
        entity.setAverageScore(averageScore);
        entity.setHistogram(Arrays.toString(histogram));
        entity.setPassScore(passScore);
        entity.setAlgorithmSchema(schema);
        List<EvalReportEntity> entities = Collections.singletonList(entity);

        when(this.evalReportMapper.listEvalReport(any())).thenReturn(entities);
        when(this.evalReportMapper.countEvalReport(any())).thenReturn(1);

        EvalReportQueryParam queryParam = new EvalReportQueryParam();
        PageVo<EvalReportVo> response = this.evalReportService.listEvalReport(queryParam);

        EvalReportVo firstEntity = response.getItems().get(0);
        assertThat(response).extracting(PageVo::getTotal, r -> r.getItems().size()).containsExactly(1, 1);
        assertThat(firstEntity).extracting(EvalReportVo::getId,
                        EvalReportVo::getNodeId,
                        EvalReportVo::getNodeName,
                        EvalReportVo::getAlgorithmSchema,
                        EvalReportVo::getPassScore,
                        EvalReportVo::getAverageScore,
                        EvalReportVo::getHistogram)
                .containsExactly(reportId, nodeId, nodeName, schema, passScore, averageScore, histogram);
    }
}