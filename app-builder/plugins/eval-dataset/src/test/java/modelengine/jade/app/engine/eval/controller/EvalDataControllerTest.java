/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

import modelengine.jade.app.engine.eval.dto.EvalDataCreateDto;
import modelengine.jade.app.engine.eval.dto.EvalDataUpdateDto;
import modelengine.jade.app.engine.eval.entity.EvalDataDeleteParam;
import modelengine.jade.app.engine.eval.entity.EvalDataEntity;
import modelengine.jade.app.engine.eval.service.EvalDataService;
import modelengine.jade.app.engine.eval.service.EvalListDataService;
import modelengine.jade.common.vo.PageVo;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.test.domain.mvc.request.MockRequestBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.TypeUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalDataController} 的测试集。
 *
 * @author 易文渊
 * @since 2024-07-22
 */
@MvcTest(classes = {EvalDataController.class})
@DisplayName("测试 EvalDataController")
public class EvalDataControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Mock
    private EvalDataService evalDataService;

    @Mock
    private EvalListDataService evalListDataService;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("批量创建评估数据接口成功")
    void shouldOkWhenCreateEvalData() {
        doNothing().when(this.evalDataService).insertAll(anyLong(), anyList());

        EvalDataCreateDto evalDataCreateDto = new EvalDataCreateDto();
        evalDataCreateDto.setDatasetId(1L);
        evalDataCreateDto.setContents(Collections.singletonList("{}"));

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/data").jsonEntity(evalDataCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("分页查询评估数据接口成功")
    void shouldOkWhenQueryEvalData() {
        EvalDataEntity entity = new EvalDataEntity();
        entity.setId(1L);
        entity.setContent("abcd");
        List<EvalDataEntity> entities = Collections.singletonList(entity);
        Mockito.when(this.evalListDataService.listEvalData(any())).thenReturn(PageVo.of(1, entities));

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/data")
                .param("datasetId", "1")
                .param("version", "1")
                .param("pageIndex", "1")
                .param("pageSize", "10")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {EvalDataEntity.class}));

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.response.objectEntity()).isPresent();
        PageVo<EvalDataEntity> target = ObjectUtils.cast(this.response.objectEntity().get().object());
        assertThat(target.getTotal()).isEqualTo(1);
        assertThat(target.getItems()).isNotEmpty().extracting(EvalDataEntity::getContent).contains("abcd");
    }

    @Test
    @DisplayName("修改评估数据接口成功")
    public void shouldOkWhenUpdateEvalData() {
        doNothing().when(this.evalDataService).update(anyLong(), anyLong(), anyString());

        EvalDataUpdateDto evalDataUpdateDto = new EvalDataUpdateDto();
        evalDataUpdateDto.setDatasetId(1L);
        evalDataUpdateDto.setDataId(1L);
        evalDataUpdateDto.setContent("{}");

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("/eval/data").jsonEntity(evalDataUpdateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("不合格数据创建评估数据接口失败")
    void shouldFailWhenCreateEvalDataWithInvalidDataId() {
        doNothing().when(this.evalDataService).insertAll(anyLong(), anyList());

        EvalDataCreateDto evalDataCreateDto = new EvalDataCreateDto();
        evalDataCreateDto.setDatasetId(0L);
        evalDataCreateDto.setContents(Collections.singletonList("{}"));

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/data").jsonEntity(evalDataCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("不合格数据软删除评估数据接口失败")
    void shouldFailWhenDeleteEvalDataWithInvalidDataId() {
        doNothing().when(this.evalDataService).delete(anyList());

        EvalDataDeleteParam evalDataDeleteParam = new EvalDataDeleteParam();
        evalDataDeleteParam.setDataIds(Collections.singletonList(-1L));

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete("/eval/data").jsonEntity(evalDataDeleteParam).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("批量软删除评估数据接口成功")
    void shouldOkWhenDeleteEvalData() {
        doNothing().when(this.evalDataService).delete(anyList());

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete("/eval/data").param("dataIds", "1").responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }
}