/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.uid.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MybatisTest;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.fitframework.test.domain.db.DatabaseModel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link IdGeneratorMapper} 的测试用例。
 *
 * @author 何嘉斌
 * @since 2024-07-29
 */
@MybatisTest(classes = {IdGeneratorMapper.class}, model = DatabaseModel.POSTGRESQL)
@Sql(before = "sql/test_create_table.sql")
@DisplayName("测试 WorkerGeneratorMapper")
public class IdGeneratorMapperTest {
    @Fit
    private IdGeneratorMapper idGeneratorMapper;

    @Test
    @Sql(before = "sql/test_create_table.sql")
    @DisplayName("获取机器ID成功")
    void shouldOkWhenGetWorkerId() {
        for (int i = 1; i <= 3; i++) {
            assertThat(this.idGeneratorMapper.getNextId()).isEqualTo(i);
        }
    }
}