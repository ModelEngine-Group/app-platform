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
import modelengine.jade.app.engine.task.dto.EvalCaseQueryParam;
import modelengine.jade.app.engine.task.entity.EvalCaseEntity;
import modelengine.jade.app.engine.task.po.EvalCasePo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 表示 {@link EvalCaseMapper} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@MybatisTest(classes = {EvalCaseMapper.class}, model = DatabaseModel.POSTGRESQL)
@Sql(before = "sql/test_create_table.sql")
@DisplayName("测试 EvalTaskCaseMapper")
public class EvalCaseMapperTest {
    @Fit
    private EvalCaseMapper mapper;

    @Test
    @Sql(before = "sql/test_create_table.sql")
    @DisplayName("插入评估任务用例后，回填主键成功")
    void shouldOkWhenCreateEvalTaskCase() {
        EvalCasePo evalCasePo = new EvalCasePo();
        evalCasePo.setPass(true);
        evalCasePo.setInstanceId(1L);

        this.mapper.create(evalCasePo);
        assertThat(evalCasePo.getId()).isNotEqualTo(null);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_data.sql"})
    @DisplayName("分页查询评估用例成功")
    void shouldOkWhenListEvalCase() {
        EvalCaseQueryParam queryParam = new EvalCaseQueryParam();
        queryParam.setInstanceId(1L);
        queryParam.setPageIndex(1);
        queryParam.setPageSize(10);

        List<EvalCaseEntity> caseEntities = this.mapper.listEvalCase(queryParam);
        assertThat(caseEntities.size()).isEqualTo(1);
        assertThat(this.mapper.countEvalCase(1L)).isEqualTo(1);

        EvalCaseEntity entity = caseEntities.get(0);
        assertThat(entity).extracting(EvalCaseEntity::getId, EvalCaseEntity::getPass).containsExactly(1L, true);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_data.sql"})
    @DisplayName("查询评估用例成功")
    void shouldOkWhenListEvalCaseById() {
        List<EvalCaseEntity> caseEntities = this.mapper.getCaseByInstanceId(1L);
        assertThat(caseEntities.size()).isEqualTo(1);

        EvalCaseEntity entity = caseEntities.get(0);
        assertThat(entity).extracting(EvalCaseEntity::getId, EvalCaseEntity::getPass).containsExactly(1L, true);
    }
}