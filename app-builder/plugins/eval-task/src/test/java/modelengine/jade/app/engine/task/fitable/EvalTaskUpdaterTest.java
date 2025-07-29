/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.fitable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import modelengine.fit.waterflow.entity.FlowTransCompletionInfo;
import modelengine.fit.waterflow.flowsengine.domain.flows.enums.FlowTraceStatus;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.IntegrationTest;
import modelengine.fitframework.test.annotation.Spy;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.jade.app.engine.task.dto.EvalInstanceQueryParam;
import modelengine.jade.app.engine.task.dto.EvalReportQueryParam;
import modelengine.jade.app.engine.task.entity.EvalInstanceEntity;
import modelengine.jade.app.engine.task.entity.EvalInstanceStatusEnum;
import modelengine.jade.app.engine.task.mapper.EvalInstanceMapper;
import modelengine.jade.app.engine.task.mapper.EvalReportMapper;
import modelengine.jade.app.engine.task.service.EvalReportService;
import modelengine.jade.app.engine.task.vo.EvalReportVo;
import modelengine.jade.common.vo.PageVo;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * {@link EvalTaskUpdater} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-22
 */
@IntegrationTest(scanPackages = {
        "modelengine.jade.app.engine.task"
})
@Sql(before = "sql/test_create_table.sql")
public class EvalTaskUpdaterTest {
    @Spy
    private EvalTaskUpdater taskUpdater;

    @Fit
    private EvalReportMapper evalReportMapper;

    @Fit
    private EvalReportService evalReportService;

    @Fit
    private EvalInstanceMapper evalInstanceMapper;

    private static FlowTransCompletionInfo buildTestData(String traceId) {
        return new FlowTransCompletionInfo("flowMetaId",
                "versionId",
                "123",
                Collections.singletonList(traceId),
                FlowTraceStatus.ARCHIVED.toString());
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_data.sql"})
    void shouldOkWhenFlowTransCompleted() {
        this.taskUpdater.callback(buildTestData("trace1"));

        EvalReportQueryParam reportQueryParam = new EvalReportQueryParam();
        reportQueryParam.setInstanceId(1L);

        PageVo<EvalReportVo> reportEntities = this.evalReportService.listEvalReport(reportQueryParam);
        assertThat(reportEntities.getTotal()).isEqualTo(2);

        assertThat(reportEntities.getItems()).extracting(EvalReportVo::getNodeId,
                        EvalReportVo::getNodeName,
                        EvalReportVo::getPassScore,
                        EvalReportVo::getAverageScore,
                        EvalReportVo::getAlgorithmSchema,
                        EvalReportVo::getHistogram)
                .containsExactly(tuple("nodeId2",
                                "nodeName2",
                                20.0,
                                0.0,
                                "{}",
                                new int[] {1, 0, 0, 0, 0, 0, 0, 0, 0, 0}),
                        tuple("nodeId1", "nodeName1", 10.0, 100.0, "{}", new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 1}));

        EvalInstanceQueryParam instanceQueryParam = new EvalInstanceQueryParam();
        instanceQueryParam.setTaskId(1L);
        instanceQueryParam.setPageSize(1);
        instanceQueryParam.setPageIndex(1);
        List<EvalInstanceEntity> instanceEntities = this.evalInstanceMapper.listEvalInstance(instanceQueryParam);
        assertThat(instanceEntities.size()).isEqualTo(1);
        assertThat(instanceEntities).extracting(EvalInstanceEntity::getId,
                EvalInstanceEntity::getStatus,
                EvalInstanceEntity::getPassRate).containsExactly(tuple(1L, EvalInstanceStatusEnum.SUCCESS, 1.0));
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_data.sql"})
    void shouldFailWhenFlowTransCompleted() {
        this.taskUpdater.callback(buildTestData("trace2"));
        EvalInstanceQueryParam instanceQueryParam = new EvalInstanceQueryParam();
        instanceQueryParam.setTaskId(2L);
        instanceQueryParam.setPageSize(1);
        instanceQueryParam.setPageIndex(1);
        List<EvalInstanceEntity> instanceEntities = this.evalInstanceMapper.listEvalInstance(instanceQueryParam);
        assertThat(instanceEntities.size()).isEqualTo(1);
        assertThat(instanceEntities).extracting(EvalInstanceEntity::getId, EvalInstanceEntity::getStatus)
                .containsExactly(tuple(3L, EvalInstanceStatusEnum.FAILED));
    }
}