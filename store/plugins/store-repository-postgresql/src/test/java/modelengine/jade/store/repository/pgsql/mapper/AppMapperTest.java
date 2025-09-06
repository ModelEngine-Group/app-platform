/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MybatisTest;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.jade.store.entity.query.AppQuery;
import modelengine.jade.store.repository.pgsql.entity.AppDo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 表示 {@link AppMapper} 的单元测试。
 *
 * @author 孙怡菲
 * @since 2025-07-07
 */
@MybatisTest(classes = {AppMapper.class})
@Sql(before = {"sql/create/app.sql", "sql/create/tag.sql"})
@DisplayName("测试 AppMapper")
public class AppMapperTest {
    @Fit
    private AppMapper appMapper;

    @Test
    @Sql(before = {"sql/create/app.sql", "sql/create/tag.sql", "sql/insert/app.sql", "sql/insert/tag.sql"})
    @DisplayName("测试查询 app 列表")
    void shouldReturnSystemCreatedAppFirst() {
        AppQuery appQuery = new AppQuery.Builder().appCategory("chatbot").build();
        List<AppDo> result = this.appMapper.getApps(appQuery);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCreator()).isEqualTo("system");
        assertThat(result.get(1).getCreator()).isEqualTo("Jade");
    }
}
