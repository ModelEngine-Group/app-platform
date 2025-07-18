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
import modelengine.jade.app.engine.task.po.EvalAlgorithmPo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * 表示 {@link EvalAlgorithmMapper} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-09-19
 */
@MybatisTest(classes = {EvalAlgorithmMapper.class}, model = DatabaseModel.POSTGRESQL)
@Sql(before = "sql/test_create_table.sql")
@DisplayName("测试 EvalAlgorithmMapper")
public class EvalAlgorithmMapperTest {
    @Fit
    private EvalAlgorithmMapper mapper;

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_report.sql"})
    @DisplayName("插入评估算法成功")
    void shouldOkWhenInsertEvalAlgorithm() {
        EvalAlgorithmPo po = new EvalAlgorithmPo();
        po.setNodeId("node1");
        po.setNodeName("accuracy");
        po.setAlgorithmSchema("{}");
        po.setPassScore(60.0);
        po.setTaskId(1L);

        mapper.insert(Collections.singletonList(po));
        assertThat(po.getId()).isNotEqualTo(null);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_report.sql"})
    @DisplayName("查询评估算法数量成功")
    void shouldOkWhenCountEvalAlgorithm() {
        int count = this.mapper.countByNodeId("node1");
        assertThat(count).isEqualTo(1);
    }
}