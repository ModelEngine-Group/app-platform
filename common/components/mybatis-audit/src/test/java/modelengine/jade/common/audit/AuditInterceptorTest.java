/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MybatisTest;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.fitframework.transaction.DataAccessException;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;
import modelengine.jade.common.audit.stub.TestMapper;
import modelengine.jade.common.audit.stub.TestPo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * 表示 {@link AuditInterceptor} 的测试集。
 *
 * @author 易文渊
 * @since 2024-08-13
 */
@DisplayName("测试 AuditInterceptor")
@MybatisTest(classes = {TestMapper.class, AuditInterceptor.class})
@Sql(before = "sql/test_create_table.sql")
public class AuditInterceptorTest {
    @Fit
    private TestMapper testMapper;

    @Test
    @Sql(before = "sql/test_create_table.sql")
    @DisplayName("测试插入单条数据，自动审计生效")
    void shouldOkWhenInsertSingleEntity() {
        UserContext userContext = new UserContext("sky", "", "");
        TestPo testPo = new TestPo();
        UserContextHolder.apply(userContext, () -> testMapper.insert(testPo));
        assertThat(testPo).extracting(TestPo::getCreatedBy, TestPo::getUpdatedBy).containsExactly("sky", "sky");
    }

    @Test
    @Sql(before = "sql/test_create_table.sql")
    @DisplayName("测试批量插入数据，自动审计生效")
    void shouldOkWhenInsertCollection() {
        UserContext userContext = new UserContext("sky", "", "");
        TestPo testPo = new TestPo();
        UserContextHolder.apply(userContext, () -> testMapper.insertAll(Collections.singletonList(testPo)));
        assertThat(testPo).extracting(TestPo::getCreatedBy, TestPo::getUpdatedBy).containsExactly("sky", "sky");
    }

    @Test
    @Sql(before = "sql/test_create_table.sql")
    @DisplayName("测试多个参数时，自动审计生效")
    void shouldOkWhenInsertMap() {
        UserContext userContext = new UserContext("sky", "", "");
        TestPo testPo = new TestPo();
        UserContextHolder.apply(userContext, () -> testMapper.insertMultiParm(testPo, 1L));
        assertThat(testPo).extracting(TestPo::getCreatedBy, TestPo::getUpdatedBy).containsExactly("sky", "sky");
    }

    @Test
    @Sql(before = "sql/test_create_table.sql")
    @DisplayName("测试用户上下文为空时，插入数据失败")
    void shouldFailWhenUserContextMissing() {
        assertThatThrownBy(() -> testMapper.insert(new TestPo())).isInstanceOf(DataAccessException.class);
    }
}