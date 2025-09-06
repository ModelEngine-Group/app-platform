/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MybatisTest;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.fitframework.test.domain.db.DatabaseModel;
import modelengine.fitframework.transaction.DataAccessException;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.app.engine.eval.entity.EvalDatasetQueryParam;
import modelengine.jade.app.engine.eval.po.EvalDatasetPo;
import modelengine.jade.app.engine.eval.vo.EvalDatasetVo;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;
import modelengine.jade.common.audit.AuditInterceptor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Arrays;
import java.util.List;

/**
 * 表示 {@link EvalDatasetMapper} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-07-31
 */
@MybatisTest(classes = {EvalDatasetMapper.class, AuditInterceptor.class}, model = DatabaseModel.POSTGRESQL)
@Sql(before = "sql/test_create_table.sql")
@DisplayName("测试 EvalDatasetMapper")
public class EvalDatasetMapperTest {
    private final UserContext userContext = new UserContext("agent", "", "");

    @Fit
    private EvalDatasetMapper evalDatasetMapper;

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
    @Sql(before = "sql/test_create_table.sql")
    @DisplayName("插入数据集后，回填主键成功")
    void shouldOkWhenInsert() {
        EvalDatasetPo evalDatasetPo = new EvalDatasetPo();
        evalDatasetPo.setName("ds1");
        evalDatasetPo.setDescription("Test dataset");
        evalDatasetPo.setSchema("{}");
        evalDatasetPo.setAppId("");

        this.evalDatasetMapper.create(evalDatasetPo);
        assertThat(evalDatasetPo.getId()).isNotEqualTo(null);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_insert_data.sql"})
    @DisplayName("删除单个数据集时，不存在未删除数据，返回正确的删除行数")
    void shouldOkWhenDeleteSingleDataset() {
        assertThat(this.evalDatasetMapper.deleteById(2L)).isEqualTo(1);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_insert_data.sql"})
    @DisplayName("删除单个数据集时，数据集不存在，删除行数为 0")
    void shouldOKWhenDeleteSingleNonexistentDataset() {
        assertThat(this.evalDatasetMapper.deleteById(4L)).isEqualTo(0);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_insert_data.sql"})
    @DisplayName("删除单个数据集时，存在未删除数据，删除失败")
    void shouldFailWhenDeleteSingleDatasetWithRemainingData() {
        assertThatThrownBy(() -> this.evalDatasetMapper.deleteById(1L)).isInstanceOf(DataAccessException.class);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_insert_data.sql"})
    @DisplayName("删除数据集时，不存在未删除数据，返回正确的删除行数")
    void shouldOkWhenDeleteDataset() {
        assertThat(this.evalDatasetMapper.delete(Arrays.asList(2L, 3L))).isEqualTo(2);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_insert_data.sql"})
    @DisplayName("删除数据集时，存在已删除数据集，返回正确的删除行数")
    void shouldOkWhenDeleteDatasetWithNonexistentDataset() {
        assertThat(this.evalDatasetMapper.delete(Arrays.asList(2L, 4L))).isEqualTo(1);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_insert_data.sql"})
    @DisplayName("删除数据集时，存在未删除数据，删除失败")
    void shouldFailWhenDeleteDatasetsWithRemainingData() {
        assertThatThrownBy(() -> this.evalDatasetMapper.delete(Arrays.asList(1L,
                2L))).isInstanceOf(DataAccessException.class);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_dataset.sql"})
    @DisplayName("分页查询数据集元数据成功")
    void shouldOkWhenListEvalDataset() {
        EvalDatasetQueryParam queryParam = new EvalDatasetQueryParam();
        queryParam.setAppId("123456");
        queryParam.setPageIndex(1);
        queryParam.setPageSize(2);
        List<EvalDatasetVo> datasetVos = this.evalDatasetMapper.listEvalDataset(queryParam);
        assertThat(datasetVos.size()).isEqualTo(2);

        for (int i = 0; i < datasetVos.size(); i++) {
            EvalDatasetVo vo = datasetVos.get(i);
            assertThat(vo).extracting(EvalDatasetVo::getId,
                            EvalDatasetVo::getName,
                            EvalDatasetVo::getDescription,
                            EvalDatasetVo::getCreatedBy,
                            EvalDatasetVo::getUpdatedBy)
                    .containsExactly(Long.valueOf(i + 1),
                            StringUtils.format("name{0}", i + 1),
                            StringUtils.format("desc{0}", i + 1),
                            StringUtils.format("Sky{0}", i + 1),
                            StringUtils.format("Fang{0}", i + 1));
        }
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_dataset.sql"})
    @DisplayName("根据数据集唯一标识查询数据集元数据成功")
    void shouldOkWhenGetEvalDatasetById() {
        EvalDatasetVo vo = this.evalDatasetMapper.getEvalDatasetById(3L);
        assertThat(vo).extracting(EvalDatasetVo::getId,
                EvalDatasetVo::getSchema,
                EvalDatasetVo::getName,
                EvalDatasetVo::getDescription,
                EvalDatasetVo::getCreatedBy,
                EvalDatasetVo::getUpdatedBy).containsExactly(3L, "Fake schema 3", "name3", "desc3", "Sky3", "Fang3");
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_dataset.sql"})
    @DisplayName("修改数据集名称成功")
    void shouldOKWhenUpdateEvalDatasetName() {
        String name = "datasetName1";
        Long id = 1L;
        EvalDatasetPo evalDatasetPo = new EvalDatasetPo();
        evalDatasetPo.setName(name);
        evalDatasetPo.setId(id);

        this.evalDatasetMapper.updateEvaldataset(evalDatasetPo);
        EvalDatasetVo vo = this.evalDatasetMapper.getEvalDatasetById(id);
        assertThat(vo).hasFieldOrPropertyWithValue("name", name);
        assertThat(vo).hasFieldOrPropertyWithValue("description", "desc1");
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_dataset.sql"})
    @DisplayName("修改数据集描述成功")
    void shouldOKWhenUpdateEvalDatasetDesc() {
        String desc = "datasetDesc1";
        Long id = 1L;
        EvalDatasetPo evalDatasetPo = new EvalDatasetPo();
        evalDatasetPo.setDescription(desc);
        evalDatasetPo.setId(id);

        this.evalDatasetMapper.updateEvaldataset(evalDatasetPo);
        EvalDatasetVo vo = this.evalDatasetMapper.getEvalDatasetById(id);
        assertThat(vo).hasFieldOrPropertyWithValue("name", "name1");
        assertThat(vo).hasFieldOrPropertyWithValue("description", desc);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_dataset.sql"})
    @DisplayName("修改数据集描述和名字成功")
    void shouldOKWhenUpdateEvalDatasetDescAndName() {
        String name = "datasetName1";
        String desc = "datasetDesc1";
        Long id = 1L;
        EvalDatasetPo evalDatasetPo = new EvalDatasetPo();
        evalDatasetPo.setDescription(desc);
        evalDatasetPo.setName(name);
        evalDatasetPo.setId(id);

        this.evalDatasetMapper.updateEvaldataset(evalDatasetPo);
        EvalDatasetVo vo = this.evalDatasetMapper.getEvalDatasetById(id);
        assertThat(vo).hasFieldOrPropertyWithValue("name", name);
        assertThat(vo).hasFieldOrPropertyWithValue("description", desc);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_dataset.sql"})
    @DisplayName("修改数据集名字成功")
    void shouldOKWhenUpdateEvalDatasetDescAndName1() {
        String name = "datasetName1";
        Long id = 1L;
        EvalDatasetPo evalDatasetPo = new EvalDatasetPo();
        evalDatasetPo.setName(name);
        evalDatasetPo.setId(id);

        this.evalDatasetMapper.updateEvaldataset(evalDatasetPo);
        EvalDatasetVo vo = this.evalDatasetMapper.getEvalDatasetById(id);
        assertThat(vo).hasFieldOrPropertyWithValue("name", name);
        assertThat(vo).hasFieldOrPropertyWithValue("description", "desc1");
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql"})
    @DisplayName("插入数据集时，自动插入用户信息")
    void shouldAutoUpdateWhenInsert() {
        EvalDatasetPo evalDatasetPo = new EvalDatasetPo();
        evalDatasetPo.setName("ds1");
        evalDatasetPo.setDescription("Test dataset");
        evalDatasetPo.setSchema("{}");
        evalDatasetPo.setAppId("");

        this.evalDatasetMapper.create(evalDatasetPo);

        EvalDatasetVo vo = this.evalDatasetMapper.getEvalDatasetById(1L);
        assertThat(vo).hasFieldOrPropertyWithValue("createdBy", "agent");
        assertThat(vo).hasFieldOrPropertyWithValue("updatedBy", "agent");
        assertThat(vo.getCreatedAt()).isEqualTo(vo.getUpdatedAt());
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_dataset.sql"})
    @DisplayName("修改数据集成功成功时，自动插入用户信息")
    void shouldAutoUpdateWhenUpdateEvalDataset() {
        String name = "datasetName1";
        String desc = "datasetDesc1";
        Long id = 1L;
        EvalDatasetPo evalDatasetPo = new EvalDatasetPo();
        evalDatasetPo.setDescription(desc);
        evalDatasetPo.setName(name);
        evalDatasetPo.setId(id);

        EvalDatasetVo vo = this.evalDatasetMapper.getEvalDatasetById(id);
        assertThat(vo).hasFieldOrPropertyWithValue("updatedBy", "Fang1");
        assertThat(vo.getCreatedAt()).isEqualTo(vo.getUpdatedAt());

        this.evalDatasetMapper.updateEvaldataset(evalDatasetPo);
        vo = this.evalDatasetMapper.getEvalDatasetById(id);
        assertThat(vo).hasFieldOrPropertyWithValue("updatedBy", "agent");
        assertThat(vo.getCreatedAt()).isNotEqualTo(vo.getUpdatedAt());
    }
}