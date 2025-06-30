/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.mapper;

import static modelengine.fitframework.util.IoUtils.content;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import modelengine.jade.app.engine.eval.entity.EvalDataEntity;
import modelengine.jade.app.engine.eval.entity.EvalDataQueryParam;
import modelengine.jade.app.engine.eval.entity.EvalVersionEntity;
import modelengine.jade.app.engine.eval.po.EvalDataPo;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;
import modelengine.jade.common.audit.AuditInterceptor;

import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.test.annotation.MybatisTest;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.fitframework.test.domain.db.DatabaseModel;
import modelengine.fitframework.util.TypeUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * 表示 {@link EvalDataMapper} 的测试用例。
 *
 * @author 易文渊
 * @since 2024-07-22
 */
@MybatisTest(classes = {EvalDataMapper.class, AuditInterceptor.class}, model = DatabaseModel.POSTGRESQL)
@Sql(scripts = "sql/test_create_table.sql")
@DisplayName("测试 EvalDataMapper")
public class EvalDataMapperTest {
    private final UserContext userContext = new UserContext("agent", "", "");

    @Fit
    private EvalDataMapper evalDataMapper;

    private MockedStatic<UserContextHolder> mockedStatic;

    @BeforeEach
    void setUp() {
        mockedStatic = mockStatic(UserContextHolder.class);
        mockedStatic.when(UserContextHolder::get).thenReturn(userContext);
    }

    @AfterEach
    void teardown() {
        mockedStatic.close();
    }

    @Test
    @Sql(scripts = "sql/test_insert_data.sql")
    @DisplayName("插入数据后，回填主键成功")
    void shouldOkWhenInsert() {
        EvalDataPo evalDataPo = new EvalDataPo();
        evalDataPo.setContent("{}");
        evalDataPo.setCreatedVersion(1L);
        evalDataPo.setDatasetId(1L);
        this.evalDataMapper.insertAll(Collections.singletonList(evalDataPo));
        assertThat(evalDataPo.getId()).isNotEqualTo(null);
    }

    static class QueryTestCaseProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws IOException {
            ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null, true);

            String resourceName = "/test/query_eval_data_test_case.json";
            String jsonContent = content(QueryTestCaseProvider.class, resourceName);

            List<EvalDataQueryTestCaseParam> testCase = serializer.deserialize(jsonContent,
                    TypeUtils.parameterized(List.class, new Type[] {EvalDataQueryTestCaseParam.class}));

            return testCase.stream().map(test -> {
                return Arguments.of(test.getQueryParam(), test.getExpectedSize(), test.getExpectedContent());
            });
        }
    }

    @ParameterizedTest
    @ArgumentsSource(QueryTestCaseProvider.class)
    @DisplayName("分页查询数据成功")
    @Sql(scripts = "sql/insert_data.sql")
    void TestListEvalData(EvalDataQueryParam queryParam, int expectedSize, String expectedContent) {
        List<EvalDataEntity> response = this.evalDataMapper.listEvalData(queryParam);
        assertThat(response.get(0).getContent()).isEqualTo(expectedContent);
        assertThat(response.size()).isEqualTo(expectedSize);
        for (EvalDataEntity entity : response) {
            assertThat(entity.getId()).isNotEqualTo(null);
            assertThat(entity.getId()).isGreaterThan(0);
            assertThat(entity.getContent()).isNotNull();
            assertThat(entity.getCreatedAt()).isNotNull();
            assertThat(entity.getUpdatedAt()).isNotNull();
        }
    }

    @Test
    @DisplayName("统计评估数据数量成功")
    @Sql(scripts = "sql/insert_data.sql")
    void TestCountEvalData() {
        EvalDataQueryParam queryParam = new EvalDataQueryParam();
        queryParam.setDatasetId(1L);
        queryParam.setVersion(3L);
        int evalDataCount = this.evalDataMapper.countEvalData(queryParam);

        assertThat(evalDataCount).isEqualTo(5);
    }

    @Test
    @Sql(scripts = "sql/test_insert_data.sql")
    @DisplayName("软删除指定数据后，更新过期时间成功")
    void shouldOkWhenSoftDelete() {
        int effectRows = this.evalDataMapper.updateExpiredVersion(Collections.singletonList(1L),
                2L,
                LocalDateTime.now(),
                "user");
        assertThat(effectRows).isEqualTo(1);
    }

    @Test
    @DisplayName("软删除不存在数据时，更新行数为0")
    void shouldFailWhenSoftDeleteInvalidData() {
        int effectRows = this.evalDataMapper.updateExpiredVersion(Collections.singletonList(1L),
                2L,
                LocalDateTime.now(),
                "user");
        assertThat(effectRows).isEqualTo(0);
    }

    @Test
    @Sql(scripts = "sql/test_insert_data.sql")
    @DisplayName("软删除已被删除数据数据时，更新行数为0")
    void shouldFailWhenSoftDeleteDeletedData() {
        int effectRows = this.evalDataMapper.updateExpiredVersion(Collections.singletonList(2L),
                2L,
                LocalDateTime.now(),
                "user");
        assertThat(effectRows).isEqualTo(0);
    }

    @Test
    @Sql(scripts = "sql/test_insert_data.sql")
    @DisplayName("修改指定数据后，原更新过期时间成功，插入回填主键成功")
    void shouldOkWhenUpdate() {
        EvalDataPo evalDataPo = new EvalDataPo();
        evalDataPo.setContent("{}");
        evalDataPo.setCreatedVersion(1L);
        evalDataPo.setDatasetId(1L);
        evalDataPo.setId(1L);
        int effectRows = this.evalDataMapper.updateExpiredVersion(Collections.singletonList(1L),
                2L,
                LocalDateTime.now(),
                "user");
        assertThat(effectRows).isEqualTo(1);
        this.evalDataMapper.insertAll(Collections.singletonList(evalDataPo));
        assertThat(evalDataPo.getId()).isEqualTo(3L);
    }

    @Test
    @Sql(scripts = "sql/test_insert_data.sql")
    @DisplayName("硬删除指定数据集全部数据成功")
    void shouldOkWhenHardDelete() {
        int effectRows = this.evalDataMapper.deleteAll(Collections.singletonList(1L));
        assertThat(effectRows).isEqualTo(2);
    }

    @Test
    @Sql(scripts = "sql/test_insert_data.sql")
    @DisplayName("硬删除指定数据集没有数据时，删除行数为 0")
    void shouldOkWhenHardDeleteWithNoRecord() {
        int effectRows = this.evalDataMapper.deleteAll(Collections.singletonList(2L));
        assertThat(effectRows).isEqualTo(0);
    }

    @Test
    @Sql(scripts = "sql/test_insert_data.sql")
    @DisplayName("查询数据集最新版本")
    void shouldOKWhenGetVersions() {
        EvalVersionEntity entity = this.evalDataMapper.getLatestVersion(1L);
        assertThat(entity.getVersion()).isEqualTo(2L);
        assertThat(entity.getCreatedTime()).isNotNull();
    }

    @Test
    @Sql(scripts = "sql/test_insert_data.sql")
    @DisplayName("插入数据时，自动插入用户信息")
    void shouldAutoUpdateWhenInsert() {
        EvalDataPo evalDataPo = new EvalDataPo();
        evalDataPo.setContent("{}");
        evalDataPo.setCreatedVersion(1L);
        evalDataPo.setDatasetId(2L);
        this.evalDataMapper.insertAll(Collections.singletonList(evalDataPo));
        assertThat(evalDataPo.getId()).isNotEqualTo(null);

        EvalDataQueryParam queryParam = new EvalDataQueryParam();
        queryParam.setDatasetId(2L);
        queryParam.setVersion(1L);
        queryParam.setPageIndex(1);
        queryParam.setPageSize(10);

        List<EvalDataEntity> dataEntities = this.evalDataMapper.listEvalData(queryParam);
        EvalDataEntity entity = dataEntities.get(0);
        assertThat(entity.getCreatedAt()).isEqualTo(entity.getUpdatedAt());
    }

    @Test
    @Sql(scripts = "sql/test_insert_data.sql")
    @DisplayName("软删除指定数据时，自动插入用户信息")
    void shouldAutoUpdateWhenSoftDelete() {
        EvalDataQueryParam queryParam = new EvalDataQueryParam();
        queryParam.setDatasetId(1L);
        queryParam.setVersion(1L);
        queryParam.setPageIndex(1);
        queryParam.setPageSize(10);

        List<EvalDataEntity> dataEntities = this.evalDataMapper.listEvalData(queryParam);
        EvalDataEntity entity = dataEntities.get(0);
        assertThat(entity.getCreatedAt()).isEqualTo(entity.getUpdatedAt());

        this.evalDataMapper.updateExpiredVersion(Collections.singletonList(1L), 2L, LocalDateTime.now(), "user");

        dataEntities = this.evalDataMapper.listEvalData(queryParam);
        entity = dataEntities.get(0);
        assertThat(entity.getCreatedAt()).isNotEqualTo(entity.getUpdatedAt());
    }
}