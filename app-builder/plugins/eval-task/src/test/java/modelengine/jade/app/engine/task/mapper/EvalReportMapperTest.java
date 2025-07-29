/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MybatisTest;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.fitframework.test.domain.db.DatabaseModel;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.app.engine.task.dto.EvalReportQueryParam;
import modelengine.jade.app.engine.task.entity.EvalReportEntity;
import modelengine.jade.app.engine.task.po.EvalReportPo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalReportMapper} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-14
 */
@MybatisTest(classes = {EvalReportMapper.class}, model = DatabaseModel.POSTGRESQL)
@Sql(before = "sql/test_create_table.sql")
@DisplayName("测试 EvalReportMapper")
public class EvalReportMapperTest {
    private static final List<String> TEST_ALGORITHMS = Arrays.asList("accuracy", "recall", "precision");

    @Fit
    private EvalReportMapper mapper;

    @Test
    @DisplayName("插入评估任务报告后，回填主键成功")
    void shouldOkWhenCreateEvalTask() {
        EvalReportPo evalReportPo = new EvalReportPo();
        evalReportPo.setNodeId("node1");
        evalReportPo.setAverageScore(100.0);
        evalReportPo.setHistogram("{}");
        evalReportPo.setInstanceId(1L);

        this.mapper.create(Collections.singletonList(evalReportPo));
        assertThat(evalReportPo.getId()).isNotEqualTo(null);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_report.sql"})
    @DisplayName("查询评估任务报告成功")
    void shouldOkWhenListEvalReport() {
        EvalReportQueryParam queryParam = new EvalReportQueryParam();
        queryParam.setInstanceId(1L);

        List<EvalReportEntity> reportEntities = this.mapper.listEvalReport(queryParam);
        assertThat(reportEntities.size()).isEqualTo(3);

        for (int i = 0; i < reportEntities.size(); i++) {
            EvalReportEntity entity = reportEntities.get(i);
            assertThat(entity).extracting(EvalReportEntity::getId,
                            EvalReportEntity::getNodeId,
                            EvalReportEntity::getHistogram,
                            EvalReportEntity::getAverageScore,
                            EvalReportEntity::getPassScore,
                            EvalReportEntity::getAlgorithmSchema)
                    .containsExactly(Long.valueOf(i + 1),
                            StringUtils.format("node{0}", i + 1),
                            "[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]",
                            (i + 1) * 10.0,
                            (i + 1) * 10.0,
                            "{}");
        }
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_report.sql"})
    @DisplayName("查询评估任务报告数量成功")
    void shouldOkWhenCountEvalReport() {
        EvalReportQueryParam queryParam = new EvalReportQueryParam();
        queryParam.setInstanceId(1L);

        int count = this.mapper.countEvalReport(queryParam);
        assertThat(count).isEqualTo(3);
    }
}