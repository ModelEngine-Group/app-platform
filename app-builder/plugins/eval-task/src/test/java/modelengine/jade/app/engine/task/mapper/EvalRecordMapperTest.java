/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MybatisTest;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.fitframework.test.domain.db.DatabaseModel;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.app.engine.task.dto.EvalRecordQueryParam;
import modelengine.jade.app.engine.task.entity.EvalRecordEntity;
import modelengine.jade.app.engine.task.po.EvalRecordPo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalRecordMapper} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@MybatisTest(classes = {EvalRecordMapper.class}, model = DatabaseModel.POSTGRESQL)
@Sql(before = "sql/test_create_table.sql")
@DisplayName("测试 EvalTaskCaseResultMapper")
public class EvalRecordMapperTest {
    private static final List<String> INPUTS =
            Arrays.asList("{\"input\":\"1+1\", \"output\":\"2\", \"expected\":\"2\"}",
                    "{\"input\":\"3+1\", \"output\":\"3\", \"expected\":\"4\"}");

    @Fit
    private EvalRecordMapper mapper;

    @Test
    @Sql(before = "sql/test_create_table.sql")
    @DisplayName("插入单个评估记录后，回填主键成功")
    void shouldOkWhenInsertSingleEvalRecord() {
        EvalRecordPo resultPo = new EvalRecordPo();
        resultPo.setInput(INPUTS.get(0));
        resultPo.setNodeName("nodeName1");
        resultPo.setNodeId("nodeId1");
        resultPo.setScore(1.0);
        resultPo.setTaskCaseId(1L);

        this.mapper.create(Collections.singletonList(resultPo));
        assertThat(resultPo.getId()).isNotEqualTo(null);
    }

    @Test
    @Sql(before = "sql/test_create_table.sql")
    @DisplayName("插入评估记录后，回填主键成功")
    void shouldOkWhenInsertEvalRecord() {
        EvalRecordPo recordPo1 = new EvalRecordPo();
        recordPo1.setInput(INPUTS.get(0));
        recordPo1.setNodeName("nodeName1");
        recordPo1.setNodeId("nodeId1");
        recordPo1.setScore(1.0);
        recordPo1.setTaskCaseId(1L);

        EvalRecordPo recordPo2 = new EvalRecordPo();
        recordPo2.setInput(INPUTS.get(1));
        recordPo2.setNodeName("nodeName2");
        recordPo2.setNodeId("nodeId1");
        recordPo2.setScore(1.0);
        recordPo2.setTaskCaseId(1L);

        this.mapper.create(Arrays.asList(recordPo1, recordPo2));
        assertThat(recordPo1.getId()).isNotEqualTo(null);
        assertThat(recordPo2.getId()).isNotEqualTo(null);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_record.sql"})
    @DisplayName("分页查询评估任务用例结果成功")
    void shouldOkWhenQueryEvalRecord() {
        EvalRecordQueryParam queryParam = new EvalRecordQueryParam();
        queryParam.setNodeIds(Arrays.asList("nodeId1", "nodeId2"));
        queryParam.setPageSize(5);
        queryParam.setPageIndex(1);
        List<EvalRecordEntity> entities = this.mapper.listEvalRecord(queryParam);
        assertThat(entities.size()).isEqualTo(2);
        assertThat(entities).extracting(EvalRecordEntity::getId,
                        EvalRecordEntity::getInput,
                        EvalRecordEntity::getNodeName,
                        EvalRecordEntity::getNodeId,
                        EvalRecordEntity::getScore)
                .containsExactly(tuple(1L,
                                "{\"input\":\"1+1\", \"output\":\"2\", \"expected\":\"2\"}",
                                "nodeName1",
                                "nodeId1",
                                100.0),
                        tuple(2L,
                                "{\"input\":\"3+1\", \"output\":\"3\", \"expected\":\"4\"}",
                                "nodeName2",
                                "nodeId2",
                                0.0));
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_record.sql"})
    @DisplayName("统计评估任务用例结果成功")
    void shouldOkWhenCountEvalRecord() {
        EvalRecordQueryParam queryParam = new EvalRecordQueryParam();
        queryParam.setNodeIds(Arrays.asList("nodeId1", "nodeId2"));
        queryParam.setPageSize(5);
        queryParam.setPageIndex(1);
        int count = this.mapper.countEvalRecord(queryParam);
        assertThat(count).isEqualTo(2);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_data.sql"})
    @DisplayName("查询评估任务用例结果成功")
    void shouldOkWhenListEvalRecord() {
        List<EvalRecordEntity> recordEntities = this.mapper.getEntityByCaseIds(Collections.singletonList(1L));
        assertThat(recordEntities.size()).isEqualTo(2);
        for (int i = 0; i < recordEntities.size(); i++) {
            EvalRecordEntity entity = recordEntities.get(i);
            assertThat(entity).extracting(EvalRecordEntity::getId,
                            EvalRecordEntity::getInput,
                            EvalRecordEntity::getNodeName,
                            EvalRecordEntity::getNodeId,
                            EvalRecordEntity::getScore)
                    .containsExactly(Long.valueOf(i + 1),
                            INPUTS.get(i),
                            StringUtils.format("nodeName{0}", i + 1),
                            StringUtils.format("nodeId{0}", i + 1),
                            (double) 100 * (1 - i));
        }
    }
}