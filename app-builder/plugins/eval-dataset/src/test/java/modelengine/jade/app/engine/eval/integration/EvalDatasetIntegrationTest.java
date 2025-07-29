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
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;
import modelengine.jade.app.engine.eval.dto.EvalDataCreateDto;
import modelengine.jade.app.engine.eval.dto.EvalDatasetCreateDto;
import modelengine.jade.app.engine.eval.dto.EvalDatasetUpdateDto;
import modelengine.jade.app.engine.eval.entity.EvalDataQueryParam;
import modelengine.jade.app.engine.eval.entity.EvalDatasetQueryParam;
import modelengine.jade.app.engine.eval.mapper.EvalDataMapper;
import modelengine.jade.app.engine.eval.mapper.EvalDatasetMapper;
import modelengine.jade.app.engine.eval.vo.EvalDatasetVo;
import modelengine.jade.app.engine.uid.UidGenerator;
import modelengine.jade.authentication.AuthenticationService;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.common.code.CommonRetCode;
import modelengine.jade.common.globalization.LocaleService;
import modelengine.jade.common.vo.PageVo;
import modelengine.jade.common.vo.Result;
import modelengine.jade.schema.SchemaValidator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表示评估数据集的集成测试用例集。
 *
 * @author 何嘉斌
 * @since 2024-08-05
 */
@IntegrationTest(scanPackages = {
        "modelengine.jade.app.engine.eval", "modelengine.jade.common.filter", "modelengine.jade.common.audit"
})
@Sql(before = "sql/test_create_table.sql")
@DisplayName("评估数据集集成测试")
@Disabled
public class EvalDatasetIntegrationTest {
    private final UserContext userContext = new UserContext("agent", "", "");

    @Fit
    private MockMvc mockMvc;

    @Spy
    private EvalDatasetMapper datasetMapper;

    @Spy
    private EvalDataMapper evalDataMapper;

    @Mock
    private AuthenticationService authenticationServiceMock;

    @Mock
    private UidGenerator versionGenerator;

    @Mock
    private LocaleService localeService;

    @Mock
    private SchemaValidator schemaValidator;

    private HttpClassicClientResponse<?> response;

    @BeforeEach
    void setUp() {
        doNothing().when(this.schemaValidator).validate(anyString(), anyList());
        when(this.versionGenerator.getUid()).thenReturn(1L, 2L, 3L, 4L, 5L, 6L);
        when(this.datasetMapper.getSchema(anyLong())).thenReturn("");
        when(this.authenticationServiceMock.getUserName(any())).thenReturn(userContext.getName());
        when(localeService.localize(Mockito.any(), Mockito.any())).thenReturn("test error");
    }

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @Sql(before = "sql/test_create_table.sql")
    @DisplayName("创建评估数据集接口成功后，插入新数据成功")
    void shouldOkWhenCreateEvalDataset() throws IOException {
        // 创建评估数据集。
        EvalDatasetCreateDto evalDatasetCreateDto = new EvalDatasetCreateDto();
        evalDatasetCreateDto.setName("ds1");
        evalDatasetCreateDto.setDescription("Test dataset");
        evalDatasetCreateDto.setContents(Collections.singletonList("{}"));
        evalDatasetCreateDto.setSchema("{}");
        evalDatasetCreateDto.setAppId("1");

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/dataset").jsonEntity(evalDatasetCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        this.response.close();

        // 查询数据集数量为 1。
        EvalDatasetQueryParam datasetQueryParam = new EvalDatasetQueryParam();
        datasetQueryParam.setAppId("1");
        datasetQueryParam.setPageIndex(1);
        datasetQueryParam.setPageSize(2);

        List<EvalDatasetVo> datasetVos = this.datasetMapper.listEvalDataset(datasetQueryParam);
        assertThat(datasetVos.size()).isEqualTo(1);

        // 查询评估数据集元数据。
        EvalDatasetVo vo = datasetVos.get(0);
        assertThat(vo).extracting(EvalDatasetVo::getId,
                        EvalDatasetVo::getName,
                        EvalDatasetVo::getDescription,
                        EvalDatasetVo::getCreatedBy,
                        EvalDatasetVo::getUpdatedBy)
                .containsExactly(1L, "ds1", "Test dataset", userContext.getName(), userContext.getName());
        assertThat(vo.getCreatedAt()).isEqualTo(vo.getUpdatedAt());
        // 查询随数据集创建插入数据量为 1。
        EvalDataQueryParam dataQueryParam = new EvalDataQueryParam();
        dataQueryParam.setDatasetId(1L);
        dataQueryParam.setVersion(1L);
        assertThat(this.evalDataMapper.countEvalData(dataQueryParam)).isEqualTo(1);

        // 插入
        EvalDataCreateDto evalDataCreateDto = new EvalDataCreateDto();
        evalDataCreateDto.setDatasetId(1L);
        evalDataCreateDto.setContents(Collections.singletonList("{}"));

        requestBuilder =
                MockMvcRequestBuilders.post("/eval/data").jsonEntity(evalDataCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);

        // 查询插入新数据后，数据量为 2。
        EvalDataQueryParam queryParam = new EvalDataQueryParam();
        queryParam.setDatasetId(1L);
        queryParam.setVersion(5L);
        assertThat(this.evalDataMapper.countEvalData(queryParam)).isEqualTo(2);
    }

    @Test
    @Sql(before = "sql/test_create_table.sql")
    @DisplayName("创建评估数据集接口失败")
    void shouldNotOkWhenCreateEvalDatasetWithoutApplicationId() {
        EvalDatasetCreateDto evalDatasetCreateDto = new EvalDatasetCreateDto();
        evalDatasetCreateDto.setName("ds1");
        evalDatasetCreateDto.setDescription("Test dataset");
        evalDatasetCreateDto.setContents(Collections.singletonList("{}"));
        evalDatasetCreateDto.setSchema("{}");

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/eval/dataset")
                .jsonEntity(evalDatasetCreateDto)
                .responseType(Result.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(400);
        assertThat(this.response.objectEntity()).isNotEmpty()
                .get()
                .satisfies(objectEntity -> assertThat(objectEntity.object()).hasFieldOrPropertyWithValue("code",
                        CommonRetCode.BAD_REQUEST.getCode()));
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_insert_data.sql"})
    @DisplayName("删除评估数据集接口成功")
    void shouldOkWhenDeleteEvalDataset() {
        EvalDatasetQueryParam datasetQueryParam = new EvalDatasetQueryParam();
        datasetQueryParam.setAppId("1");
        datasetQueryParam.setPageIndex(1);
        datasetQueryParam.setPageSize(10);

        List<EvalDatasetVo> datasetVos = this.datasetMapper.listEvalDataset(datasetQueryParam);
        assertThat(datasetVos.size()).isEqualTo(3);

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/eval/dataset")
                .param("datasetIds", Arrays.asList("1", "2"))
                .responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);

        datasetQueryParam = new EvalDatasetQueryParam();
        datasetQueryParam.setAppId("1");
        datasetQueryParam.setPageIndex(1);
        datasetQueryParam.setPageSize(10);

        datasetVos = this.datasetMapper.listEvalDataset(datasetQueryParam);
        assertThat(datasetVos.size()).isEqualTo(1);
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_insert_data.sql"})
    @DisplayName("修改数据集信息接口成功")
    void shouldOkWhenUpdateDataset() {
        EvalDatasetUpdateDto updateDto = new EvalDatasetUpdateDto();
        updateDto.setId(1L);
        updateDto.setName("name1");
        updateDto.setDescription("desc1");

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("/eval/dataset").jsonEntity(updateDto).responseType(Void.class);

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);

        EvalDatasetQueryParam datasetQueryParam = new EvalDatasetQueryParam();
        datasetQueryParam.setAppId("1");
        datasetQueryParam.setPageIndex(1);
        datasetQueryParam.setPageSize(5);

        List<EvalDatasetVo> datasetVos = this.datasetMapper.listEvalDataset(datasetQueryParam);
        assertThat(datasetVos.size()).isEqualTo(3);

        EvalDatasetVo vo = datasetVos.get(0);
        assertThat(vo).extracting(EvalDatasetVo::getId, EvalDatasetVo::getName, EvalDatasetVo::getDescription)
                .containsExactly(1L, "name1", "desc1");
    }

    @Test
    @Sql(before = {"sql/test_create_table.sql", "sql/test_create_dataset.sql"})
    @DisplayName("分页查询评估数据集接口成功")
    void shouldOkWhenQueryEvalDataset() {
        for (int i = 0; i < 3; i++) {
            MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/dataset")
                    .param("appId", "123456")
                    .param("pageIndex", String.valueOf(i + 1))
                    .param("pageSize", "1")
                    .responseType(TypeUtils.parameterized(Result.class,
                            new Type[] {TypeUtils.parameterized(PageVo.class, new Type[] {EvalDatasetVo.class})}));

            this.response = this.mockMvc.perform(requestBuilder);
            assertThat(this.response.statusCode()).isEqualTo(200);
            assertThat(this.response.objectEntity()).isPresent();

            Result<PageVo<EvalDatasetVo>> rawTarget = ObjectUtils.cast(this.response.objectEntity().get().object());
            PageVo<EvalDatasetVo> target = rawTarget.getData();

            EvalDatasetVo vo = target.getItems().get(0);
            assertThat(vo).extracting(EvalDatasetVo::getId,
                            EvalDatasetVo::getName,
                            EvalDatasetVo::getDescription,
                            EvalDatasetVo::getCreatedBy,
                            EvalDatasetVo::getUpdatedBy)
                    .containsExactly(i + 1L,
                            StringUtils.format("name{0}", i + 1),
                            StringUtils.format("desc{0}", i + 1),
                            StringUtils.format("Sky{0}", i + 1),
                            StringUtils.format("Fang{0}", i + 1));
        }
    }

    @Test
    @Sql(before = "sql/test_create_table.sql")
    @DisplayName("评估数据集增删改查成功")
    void shouldOkWhenCrudEvalDataset() throws IOException {
        // 创建评估数据集。
        EvalDatasetCreateDto evalDatasetCreateDto = new EvalDatasetCreateDto();
        evalDatasetCreateDto.setName("ds1");
        evalDatasetCreateDto.setDescription("Test dataset");
        evalDatasetCreateDto.setContents(Collections.singletonList("{}"));
        evalDatasetCreateDto.setSchema("{}");
        evalDatasetCreateDto.setAppId("1");

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/dataset").jsonEntity(evalDatasetCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        this.response.close();

        // 查询数据集数量为 1。
        EvalDatasetQueryParam datasetQueryParam = new EvalDatasetQueryParam();
        datasetQueryParam.setAppId("1");
        datasetQueryParam.setPageIndex(1);
        datasetQueryParam.setPageSize(10);

        List<EvalDatasetVo> datasetVos = this.datasetMapper.listEvalDataset(datasetQueryParam);
        assertThat(datasetVos.size()).isEqualTo(1);

        EvalDatasetVo vo = datasetVos.get(0);
        assertThat(vo).extracting(EvalDatasetVo::getId, EvalDatasetVo::getName, EvalDatasetVo::getDescription)
                .containsExactly(1L, "ds1", "Test dataset");
        assertThat(vo.getCreatedAt()).isEqualTo(vo.getUpdatedAt());

        // 更新数据集元数据。
        EvalDatasetUpdateDto updateDto = new EvalDatasetUpdateDto();
        updateDto.setId(1L);
        updateDto.setName("name1");
        updateDto.setDescription("desc1");

        requestBuilder = MockMvcRequestBuilders.put("/eval/dataset").jsonEntity(updateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        this.response.close();

        datasetVos = this.datasetMapper.listEvalDataset(datasetQueryParam);
        assertThat(datasetVos.size()).isEqualTo(1);
        vo = datasetVos.get(0);
        assertThat(vo).extracting(EvalDatasetVo::getId, EvalDatasetVo::getName, EvalDatasetVo::getDescription)
                .containsExactly(1L, "name1", "desc1");
        assertThat(vo.getCreatedAt()).isNotEqualTo(vo.getUpdatedAt());

        // 删除评估数据集。
        requestBuilder = MockMvcRequestBuilders.delete("/eval/dataset")
                .param("datasetIds", Arrays.asList("1", "2"))
                .responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.datasetMapper.listEvalDataset(datasetQueryParam).size()).isEqualTo(0);
    }
}