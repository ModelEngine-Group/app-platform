/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.eval.dto.EvalDatasetCreateDto;
import modelengine.jade.app.engine.eval.dto.EvalDatasetUpdateDto;
import modelengine.jade.app.engine.eval.entity.EvalDatasetDeleteParam;
import modelengine.jade.app.engine.eval.entity.EvalDatasetEntity;
import modelengine.jade.app.engine.eval.entity.EvalVersionEntity;
import modelengine.jade.app.engine.eval.service.EvalDatasetService;
import modelengine.jade.app.engine.eval.vo.EvalDatasetVo;
import modelengine.jade.common.vo.PageVo;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.test.domain.mvc.request.MockRequestBuilder;
import modelengine.fitframework.transaction.DataAccessException;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.TypeUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalDataController} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-07-31
 */
@MvcTest(classes = {EvalDatasetController.class})
@DisplayName("测试 EvalDatasetController")
public class EvalDatasetControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Mock
    private EvalDatasetService evalDatasetService;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("创建评估数据集接口成功")
    void shouldOkWhenCreateEvalDataset() {
        doNothing().when(this.evalDatasetService).create(any());

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
    }

    @Test
    @DisplayName("创建评估数据集接口失败")
    void shouldNotOkWhenCreateEvalDatasetWithoutApplicationId() {
        doNothing().when(this.evalDatasetService).create(any());

        EvalDatasetCreateDto evalDatasetCreateDto = new EvalDatasetCreateDto();
        evalDatasetCreateDto.setName("ds1");
        evalDatasetCreateDto.setDescription("Test dataset");
        evalDatasetCreateDto.setContents(Collections.singletonList("{}"));
        evalDatasetCreateDto.setSchema("{}");

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/dataset").jsonEntity(evalDatasetCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("删除单个评估数据集接口成功")
    void shouldOkWhenDeleteSingleEvalDataset() {
        doNothing().when(this.evalDatasetService).delete(anyList());

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete("/eval/dataset").param("datasetIds", "1").responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("非法入参删除单个评估数据集接口失败")
    void shouldFailWhenDeleteSingleEvalDatasetWithInvalidId() {
        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete("/eval/dataset").param("datasetIds", "-1").responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("批量删除评估数据集接口成功")
    void shouldOkWhenDeleteEvalDataset() {
        doNothing().when(this.evalDatasetService).delete(anyList());

        EvalDatasetDeleteParam evalDatasetDeleteParam = new EvalDatasetDeleteParam();
        evalDatasetDeleteParam.setDatasetIds(Arrays.asList(1L, 2L));

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/eval/dataset")
                .param("datasetIds", Arrays.asList("1", "2"))
                .responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("删除评估数据集接口失败")
    void shouldFailWhenDeleteEvalDataset() {
        doThrow(new DataAccessException("Fail message")).when(this.evalDatasetService).delete(anyList());

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete("/eval/dataset").param("datasetIds", "1").responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("非法入参批量删除评估数据集接口失败")
    void shouldFailWhenDeleteEvalDatasetWithInvalidInput() {
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/eval/dataset")
                .param("datasetIds", Arrays.asList("-1", "2"))
                .responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("分页查询评估数据集接口成功")
    void shouldOkWhenQueryEvalDataset() {
        long datasetId = 1L;
        String datasetName = "TEST DATASET";
        String description = "A DATASET FOR TEST";
        String schema = "{FAKE SCHEMA}";

        EvalVersionEntity version = new EvalVersionEntity();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime tempTime = LocalDateTime.parse("2024-10-08 17:16:34", formatter);
        version.setVersion(1L);
        version.setCreatedTime(tempTime);
        List<EvalVersionEntity> versions = Collections.singletonList(version);

        EvalDatasetVo vo = new EvalDatasetVo();
        vo.setId(datasetId);
        vo.setName(datasetName);
        vo.setDescription(description);
        vo.setSchema(schema);
        vo.setVersions(versions);
        List<EvalDatasetVo> vos = Collections.singletonList(vo);

        when(this.evalDatasetService.listEvalDataset(any())).thenReturn(PageVo.of(1, vos));
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/dataset")
                .param("appId", "1")
                .param("pageIndex", "1")
                .param("pageSize", "10")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {EvalDatasetVo.class}));

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.response.objectEntity()).isPresent();

        PageVo<EvalDatasetVo> target = ObjectUtils.cast(this.response.objectEntity().get().object());
        assertThat(target.getTotal()).isEqualTo(1);
        assertThat(target.getItems()).isNotEmpty();
        assertThat(target.getItems().get(0)).extracting(EvalDatasetVo::getId,
                EvalDatasetVo::getName,
                EvalDatasetVo::getDescription).containsExactly(datasetId, datasetName, description);
    }

    @Test
    @DisplayName("缺少参数导致分页查询评估数据集接口失败")
    void shouldNotOkWhenQueryEvalDataset() {
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/dataset")
                .param("pageIndex", "1")
                .param("pageSize", "10")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {EvalDatasetEntity.class}));

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("根据 ID 查询评估数据集接口成功")
    void shouldOkWhenQueryEvalDatasetById() {
        long datasetId = 1L;
        String datasetName = "TEST DATASET";
        String description = "A DATASET FOR TEST";
        String schema = "{FAKE SCHEMA}";

        EvalVersionEntity version = new EvalVersionEntity();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime tempTime = LocalDateTime.parse("2024-10-08 17:16:34", formatter);
        version.setVersion(1L);
        version.setCreatedTime(tempTime);
        List<EvalVersionEntity> versions = Collections.singletonList(version);

        EvalDatasetVo vo = new EvalDatasetVo();
        vo.setId(datasetId);
        vo.setName(datasetName);
        vo.setDescription(description);
        vo.setSchema(schema);
        vo.setVersions(versions);

        when(this.evalDatasetService.getEvalDatasetById(any())).thenReturn(vo);
        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get("/eval/dataset/1").responseType(EvalDatasetVo.class);

        this.response = this.mockMvc.perform(requestBuilder);

        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.response.objectEntity()).isPresent();

        EvalDatasetVo target = ObjectUtils.cast(this.response.objectEntity().get().object());
        assertThat(target).extracting(EvalDatasetVo::getSchema,
                EvalDatasetVo::getName,
                EvalDatasetVo::getDescription,
                EvalDatasetVo::getVersions).containsExactly(schema, datasetName, description, versions);
    }

    @Test
    @DisplayName("参数错误导致根据 ID 查询评估数据集接口失败")
    void shouldNotOkWhenQueryEvalDatasetById() {
        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get("/eval/dataset/-1").responseType(EvalDatasetEntity.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("修改数据集信息接口成功")
    void shouldOkWhenUpdateDataset() {
        doNothing().when(this.evalDatasetService).updateEvalDataset(any());
        EvalDatasetUpdateDto updateDto = new EvalDatasetUpdateDto();
        updateDto.setId(1L);
        updateDto.setName("name1");
        updateDto.setDescription("desc1");

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("/eval/dataset").jsonEntity(updateDto).responseType(Void.class);

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("修改数据集信息接口成功")
    void shouldOkWhenUpdateDataset1() {
        doNothing().when(this.evalDatasetService).updateEvalDataset(any());
        EvalDatasetUpdateDto updateDto = new EvalDatasetUpdateDto();
        updateDto.setId(1L);
        updateDto.setName("name1");
        updateDto.setDescription(null);

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("/eval/dataset").jsonEntity(updateDto).responseType(Void.class);

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("缺少参数导致修改数据集信息接口失败")
    void shouldNotOkWhenUpdateDataset() {
        EvalDatasetUpdateDto updateDto = new EvalDatasetUpdateDto();
        updateDto.setName("name1");
        updateDto.setDescription("desc1");
        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("/eval/dataset").jsonEntity(updateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }
}