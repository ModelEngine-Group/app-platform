/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.IntegrationTest;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.Spy;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.test.domain.mvc.request.MockRequestBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.TypeUtils;
import modelengine.jade.app.engine.eval.dto.EvalDataCreateDto;
import modelengine.jade.app.engine.eval.dto.EvalDataUpdateDto;
import modelengine.jade.app.engine.eval.entity.EvalDataEntity;
import modelengine.jade.app.engine.eval.entity.EvalDataQueryParam;
import modelengine.jade.app.engine.eval.manager.EvalDatasetVersionManager;
import modelengine.jade.app.engine.eval.mapper.EvalDataMapper;
import modelengine.jade.app.engine.eval.mapper.EvalDatasetMapper;
import modelengine.jade.app.engine.eval.service.EvalDataService;
import modelengine.jade.app.engine.uid.UidGenerator;
import modelengine.jade.authentication.AuthenticationService;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.common.code.CommonRetCode;
import modelengine.jade.common.globalization.LocaleService;
import modelengine.jade.common.vo.PageVo;
import modelengine.jade.common.vo.Result;
import modelengine.jade.schema.SchemaValidator;

import org.apache.ibatis.session.SqlSessionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * 表示评估数据的集成测试用例集。
 *
 * @author 易文渊
 * @author 何嘉斌
 * @since 2024-07-26
 */
@IntegrationTest(scanPackages = {
        "modelengine.jade.app.engine.eval", "modelengine.jade.common.filter", "modelengine.jade.common.audit"
})
@Sql(before = "sql/test_create_table.sql")
@Disabled
@DisplayName("评估数据集成测试")
public class EvalDataIntegrationTest {
    private final UserContext userContext = new UserContext("agent", "", "");

    @Fit
    private MockMvc mockMvc;

    @Fit
    private EvalDataService evalDataService;

    @Spy
    private EvalDataMapper evalDataMapper;

    @Spy
    private EvalDatasetVersionManager evalDatasetVersionManager;

    @Mock
    private AuthenticationService authenticationServiceMock;

    @Mock
    private UidGenerator versionGenerator;

    @Mock
    private SchemaValidator schemaValidator;

    @Mock
    private LocaleService localeService;

    @Spy
    private EvalDatasetMapper datasetMapper;

    private HttpClassicClientResponse<?> response;

    @BeforeEach
    void setUp() {
        doNothing().when(this.schemaValidator).validate(anyString(), anyList());
        when(this.versionGenerator.getUid()).thenReturn(1L, 2L, 3L, 4L, 5L, 6L);
        when(this.datasetMapper.getSchema(anyLong())).thenReturn("");
        when(this.authenticationServiceMock.getUserName(any())).thenReturn(userContext.getName());
    }

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_insert_data.sql"})
    @DisplayName("创建评估数据接口成功")
    void shouldOkWhenCreateEvalData() {
        EvalDataCreateDto evalDataCreateDto = new EvalDataCreateDto();
        evalDataCreateDto.setDatasetId(2L);
        evalDataCreateDto.setContents(Collections.singletonList("{}"));

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/data").jsonEntity(evalDataCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);

        EvalDataQueryParam queryParam = new EvalDataQueryParam();
        queryParam.setDatasetId(2L);
        queryParam.setVersion(5L);
        assertThat(this.evalDataMapper.countEvalData(queryParam)).isEqualTo(1);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql"})
    @DisplayName("不合格数据创建评估数据接口失败")
    void shouldFailWhenCreateEvalDataWithInvalidDataId() {
        EvalDataCreateDto evalDataCreateDto = new EvalDataCreateDto();
        evalDataCreateDto.setDatasetId(-1L);
        evalDataCreateDto.setContents(Collections.singletonList("{}"));

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/data").jsonEntity(evalDataCreateDto).responseType(Result.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(400);
        assertThat(this.response.objectEntity()).isNotEmpty()
                .get()
                .satisfies(objectEntity -> assertThat(objectEntity.object()).hasFieldOrPropertyWithValue("code",
                        CommonRetCode.BAD_REQUEST.getCode()));
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_insert_data.sql"})
    @DisplayName("删除评估数据接口成功")
    void shouldOkWhenDeleteEvalData() {
        EvalDataQueryParam queryParam = new EvalDataQueryParam();
        queryParam.setDatasetId(1L);
        queryParam.setVersion(5L);
        assertThat(this.evalDataMapper.countEvalData(queryParam)).isEqualTo(1);

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete("/eval/data").param("dataIds", "1").responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.evalDataMapper.countEvalData(queryParam)).isEqualTo(0);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_insert_data.sql"})
    @DisplayName("修改评估数据接口成功")
    public void shouldOkWhenUpdateEvalData() {
        when(this.versionGenerator.getUid()).thenReturn(3L);

        EvalDataUpdateDto evalDataUpdateDto = new EvalDataUpdateDto();
        evalDataUpdateDto.setDatasetId(1L);
        evalDataUpdateDto.setDataId(1L);
        evalDataUpdateDto.setContent("{{}}");

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("/eval/data").jsonEntity(evalDataUpdateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);

        EvalDataQueryParam queryParam = new EvalDataQueryParam();
        queryParam.setDatasetId(1L);
        queryParam.setVersion(1L);
        queryParam.setPageIndex(1);
        queryParam.setPageSize(10);

        List<EvalDataEntity> dataEntities = this.evalDataMapper.listEvalData(queryParam);
        assertThat(dataEntities.size()).isEqualTo(2);
        EvalDataEntity entity = dataEntities.get(0);
        assertThat(entity).extracting(EvalDataEntity::getId, EvalDataEntity::getContent).containsExactly(1L, "{}");

        queryParam.setVersion(5L);
        assertThat(this.evalDataMapper.countEvalData(queryParam)).isEqualTo(1);
        dataEntities = this.evalDataMapper.listEvalData(queryParam);
        assertThat(dataEntities.size()).isEqualTo(1);
        entity = dataEntities.get(0);
        assertThat(entity).extracting(EvalDataEntity::getId, EvalDataEntity::getContent).containsExactly(3L, "{{}}");
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_insert_data.sql"})
    @DisplayName("分页查询评估数据接口成功")
    void shouldOkWhenQueryEvalData() {
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/data")
                .param("datasetId", "1")
                .param("version", "1")
                .param("pageIndex", "1")
                .param("pageSize", "10")
                .responseType(TypeUtils.parameterized(Result.class,
                        new Type[] {TypeUtils.parameterized(PageVo.class, new Type[] {EvalDataEntity.class})}));

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.response.objectEntity()).isPresent();
        Result<PageVo<EvalDataEntity>> rawTarget = ObjectUtils.cast(this.response.objectEntity().get().object());
        PageVo<EvalDataEntity> target = rawTarget.getData();
        assertThat(target.getTotal()).isEqualTo(1);
        assertThat(target.getItems()).isNotEmpty().extracting(EvalDataEntity::getContent).contains("{C5: C5}");
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_insert_data.sql"})
    @DisplayName("分页查询多页评估数据接口成功")
    void shouldOkWhenQueryPagedEvalData() {
        for (int i = 0; i <= 1; i++) {
            MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/data")
                    .param("datasetId", "1")
                    .param("version", "3")
                    .param("pageIndex", String.valueOf(i + 1))
                    .param("pageSize", "2")
                    .responseType(TypeUtils.parameterized(Result.class,
                            new Type[] {TypeUtils.parameterized(PageVo.class, new Type[] {EvalDataEntity.class})}));

            this.response = this.mockMvc.perform(requestBuilder);
            assertThat(this.response.statusCode()).isEqualTo(200);
            assertThat(this.response.objectEntity()).isPresent();
            Result<PageVo<EvalDataEntity>> rawTarget = ObjectUtils.cast(this.response.objectEntity().get().object());
            PageVo<EvalDataEntity> target = rawTarget.getData();
            assertThat(target.getTotal()).isEqualTo(5);
            assertThat(target.getItems()).isNotEmpty()
                    .extracting(EvalDataEntity::getContent)
                    .contains(String.format(Locale.ROOT, "{C%d: C%d}", i * 2 + 1, i * 2 + 1))
                    .contains(String.format(Locale.ROOT, "{C%d: C%d}", i * 2 + 2, i * 2 + 2));
        }
    }

    @Disabled("测试使用的 h2 数据库不支持方法级事务回滚")
    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_insert_data.sql"})
    @DisplayName("修改评估数据失败，数据已被删除，事务回滚成功")
    void shouldRollbackWhenUpdateFailed() {
        when(this.evalDatasetVersionManager.applyVersion()).thenReturn(3L);
        doThrow(new SqlSessionException()).when(evalDataMapper).insertAll(anyList());

        EvalDataQueryParam queryParam = new EvalDataQueryParam();
        queryParam.setDatasetId(1L);
        queryParam.setVersion(5L);
        int evalDataCount = this.evalDataMapper.countEvalData(queryParam);

        try {
            evalDataService.update(1L, 1L, "{}}");
        } catch (SqlSessionException e) {
            int evalDataCount2 = this.evalDataMapper.countEvalData(queryParam);
            assertThat(evalDataCount).isEqualTo(evalDataCount2);
        }
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_insert_data.sql"})
    @DisplayName("评估数据增删改查成功")
    void shouldOkWhenCrudData() throws IOException {
        // 初始化数据库后，数据集 id2 的数据量为 0。
        EvalDataQueryParam dataQueryParam = new EvalDataQueryParam();
        dataQueryParam.setDatasetId(2L);
        dataQueryParam.setVersion(1L);
        assertThat(this.evalDataMapper.countEvalData(dataQueryParam)).isEqualTo(0);

        // 插入新数据
        EvalDataCreateDto evalDataCreateDto = new EvalDataCreateDto();
        evalDataCreateDto.setDatasetId(2L);
        evalDataCreateDto.setContents(Collections.singletonList("{}"));

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/data").jsonEntity(evalDataCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        this.response.close();

        // 查询插入新数据后，数据量为 1。
        EvalDataQueryParam queryParam = new EvalDataQueryParam();
        queryParam.setDatasetId(2L);
        queryParam.setVersion(5L);
        queryParam.setPageIndex(1);
        queryParam.setPageSize(10);
        assertThat(this.evalDataMapper.countEvalData(queryParam)).isEqualTo(1);

        // 更新数据 id3
        EvalDataUpdateDto evalDataUpdateDto = new EvalDataUpdateDto();
        evalDataUpdateDto.setDatasetId(2L);
        evalDataUpdateDto.setDataId(3L);
        evalDataUpdateDto.setContent("{{}}");

        requestBuilder =
                MockMvcRequestBuilders.put("/eval/data").jsonEntity(evalDataUpdateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        this.response.close();

        // 查询更新前数据内容
        queryParam.setVersion(1L);
        List<EvalDataEntity> dataEntities = this.evalDataMapper.listEvalData(queryParam);
        assertThat(dataEntities.size()).isEqualTo(1);
        EvalDataEntity entity = dataEntities.get(0);
        assertThat(entity).extracting(EvalDataEntity::getId, EvalDataEntity::getContent).containsExactly(3L, "{}");
        assertThat(entity.getCreatedAt()).isNotEqualTo(entity.getUpdatedAt());

        // 查询更新后数据内容
        queryParam.setVersion(2L);
        assertThat(this.evalDataMapper.countEvalData(queryParam)).isEqualTo(1);
        dataEntities = this.evalDataMapper.listEvalData(queryParam);
        assertThat(dataEntities.size()).isEqualTo(1);
        entity = dataEntities.get(0);
        assertThat(entity).extracting(EvalDataEntity::getId, EvalDataEntity::getContent).containsExactly(4L, "{{}}");
        assertThat(entity.getCreatedAt()).isEqualTo(entity.getUpdatedAt());

        // 删除数据 id3
        requestBuilder = MockMvcRequestBuilders.delete("/eval/data").param("dataIds", "4").responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);

        queryParam.setVersion(3L);
        assertThat(this.evalDataMapper.countEvalData(queryParam)).isEqualTo(0);
    }
}