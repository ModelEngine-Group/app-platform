/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.task.entity.EvalCaseEntity;
import modelengine.jade.app.engine.task.entity.EvalRecordEntity;
import modelengine.jade.app.engine.task.service.EvalCaseService;
import modelengine.jade.app.engine.task.vo.EvalCaseVo;
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

/**
 * 表示 {@link EvalCaseController} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-09-23
 */
@MvcTest(classes = {EvalCaseController.class})
@DisplayName("测试 EvalCaseControllerTest")
public class EvalCaseControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Mock
    private EvalCaseService evalCaseService;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("分页查询评估用例成功")
    void shouldOkWhenListEvalCase() {
        EvalCaseEntity caseEntity = new EvalCaseEntity();
        caseEntity.setPass(true);
        caseEntity.setInstanceId(1L);
        caseEntity.setId(1L);

        EvalRecordEntity recordEntity = new EvalRecordEntity();
        recordEntity.setId(1L);
        recordEntity.setInput("{\"input\":\"1+1\", \"output\":\"2\", \"expected\":\"2\"}");
        recordEntity.setNodeName("'node1'");
        recordEntity.setScore(100.0);

        EvalCaseVo evalCaseVo = new EvalCaseVo();
        evalCaseVo.setEvalCaseEntity(caseEntity);
        evalCaseVo.setEvalRecordEntities(Collections.singletonList(recordEntity));

        when(this.evalCaseService.listEvalCase(any())).thenReturn(PageVo.of(1, Collections.singletonList(evalCaseVo)));
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/task/case")
                .param("instanceId", "1")
                .param("pageIndex", "1")
                .param("pageSize", "10")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {EvalCaseVo.class}));

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.response.objectEntity()).isPresent();

        PageVo<EvalCaseVo> caseVos = ObjectUtils.cast(this.response.objectEntity().get().object());
        assertThat(caseVos.getTotal()).isEqualTo(1);

        EvalCaseVo caseVo = caseVos.getItems().get(0);
        assertThat(caseVo.getEvalCaseEntity()).extracting(EvalCaseEntity::getId,
                        EvalCaseEntity::getPass)
                .containsExactly(1L, true);

        EvalRecordEntity resultEntity = caseVo.getEvalRecordEntities().get(0);
        assertThat(resultEntity).extracting(EvalRecordEntity::getId,
                        EvalRecordEntity::getInput,
                        EvalRecordEntity::getNodeName,
                        EvalRecordEntity::getScore)
                .containsExactly(1L, "{\"input\":\"1+1\", \"output\":\"2\", \"expected\":\"2\"}", "'node1'", 100.0);
    }

    @Test
    @DisplayName("分页查询评估用例失败")
    void shouldFailWhenListEvalCase() {
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/task/case")
                .param("instanceId", "1")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {EvalCaseVo.class}));

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }
}