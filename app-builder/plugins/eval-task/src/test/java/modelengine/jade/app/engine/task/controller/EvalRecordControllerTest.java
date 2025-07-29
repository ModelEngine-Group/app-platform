/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.task.entity.EvalRecordEntity;
import modelengine.jade.app.engine.task.service.EvalRecordService;
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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalRecordController} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-31
 */
@MvcTest(classes = {EvalRecordController.class})
@DisplayName("测试 EvalTaskRecordController")
public class EvalRecordControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Mock
    private EvalRecordService evalRecordService;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("分页查询评估任务用例结果接口成功")
    void shouldSuccessWhenQueryEvalRecord() {
        EvalRecordEntity entity = new EvalRecordEntity();
        entity.setId(1L);
        entity.setInput("{\"input\":\"1+1\", \"output\":\"2\", \"expected\":\"2\"}");
        entity.setNodeName("'node1'");
        entity.setScore(100.0);
        List<EvalRecordEntity> entities = Collections.singletonList(entity);
        when(this.evalRecordService.listEvalRecord(any())).thenReturn(PageVo.of(1, entities));

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/task/record")
                .param("nodeIds", "1")
                .param("pageIndex", "1")
                .param("pageSize", "10")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {EvalRecordEntity.class}));

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.response.objectEntity()).isPresent();
        PageVo<EvalRecordEntity> target = ObjectUtils.cast(this.response.objectEntity().get().object());
        assertThat(target.getTotal()).isEqualTo(1);
        assertThat(target.getItems().get(0)).extracting(EvalRecordEntity::getId,
                        EvalRecordEntity::getInput,
                        EvalRecordEntity::getNodeName,
                        EvalRecordEntity::getScore)
                .containsExactly(1L, "{\"input\":\"1+1\", \"output\":\"2\", \"expected\":\"2\"}", "'node1'", 100.0);
    }
}