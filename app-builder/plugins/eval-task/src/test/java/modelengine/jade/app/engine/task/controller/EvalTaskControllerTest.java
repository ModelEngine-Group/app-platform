/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.controller;

import static modelengine.jade.app.engine.task.entity.EvalInstanceStatusEnum.SUCCESS;
import static modelengine.jade.app.engine.task.entity.EvalTaskStatusEnum.PUBLISHED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.task.dto.EvalTaskCreateDto;
import modelengine.jade.app.engine.task.entity.EvalInstanceStatusEnum;
import modelengine.jade.app.engine.task.entity.EvalTaskStatusEnum;
import modelengine.jade.app.engine.task.service.EvalTaskService;
import modelengine.jade.app.engine.task.vo.EvalTaskVo;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalTaskController} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
@MvcTest(classes = {EvalTaskController.class})
@DisplayName("测试 EvalTaskController")
public class EvalTaskControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Mock
    private EvalTaskService evalTaskService;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("创建评估任务接口成功")
    void shouldOkWhenCreateEvalTask() {
        doNothing().when(this.evalTaskService).createEvalTask(any());

        EvalTaskCreateDto evalTaskCreateDto = new EvalTaskCreateDto();
        evalTaskCreateDto.setName("task1");
        evalTaskCreateDto.setDescription("eval task");
        evalTaskCreateDto.setStatus(PUBLISHED);
        evalTaskCreateDto.setAppId("123456");
        evalTaskCreateDto.setWorkflowId("flow1");

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/task").jsonEntity(evalTaskCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("创建评估任务接口失败")
    void shouldFailWhenCreateEvalTask() {
        doNothing().when(this.evalTaskService).createEvalTask(any());

        EvalTaskCreateDto evalTaskCreateDto = new EvalTaskCreateDto();

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/task").jsonEntity(evalTaskCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("批量软删除评估任务接口成功")
    void shouldOkWhenDeleteEvalData() {
        doNothing().when(this.evalTaskService).deleteEvalTask(anyList());
        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete("/eval/task").param("taskIds", "1").responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("批量软删除评估任务接口失败")
    void shouldNotOkWhenDeleteEvalData() {
        doNothing().when(this.evalTaskService).deleteEvalTask(anyList());
        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete("/eval/task").responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("分页查询评估任务接口成功")
    void shouldOkWhenQueryEvalTask() {
        Long taskId = 1L;
        String name = "task1";
        String description = "eval task";
        EvalTaskStatusEnum status = PUBLISHED;
        String appId = "123456";
        String workflowId = "flow1";
        Double passRate = 95.0;
        EvalInstanceStatusEnum instanceStatus = SUCCESS;
        int passCount = 10;
        LocalDateTime instanceFinishTime = LocalDateTime.of(2024, 1, 1, 0, 0);

        EvalTaskVo vo = new EvalTaskVo();
        vo.setId(taskId);
        vo.setName(name);
        vo.setDescription(description);
        vo.setStatus(status);
        vo.setAppId(appId);
        vo.setWorkflowId(workflowId);
        vo.setPassRate(passRate);
        vo.setPassCount(passCount);
        vo.setInstanceStatus(instanceStatus);
        vo.setInstanceFinishedAt(instanceFinishTime);
        List<EvalTaskVo> entities = Collections.singletonList(vo);
        when(this.evalTaskService.listEvalTask(any())).thenReturn(PageVo.of(1, entities));

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/task")
                .param("appId", "123456")
                .param("pageIndex", "1")
                .param("pageSize", "10")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {EvalTaskVo.class}));

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.response.objectEntity()).isPresent();

        PageVo<EvalTaskVo> target = ObjectUtils.cast(this.response.objectEntity().get().object());
        assertThat(target.getTotal()).isEqualTo(1);
        assertThat(target.getItems()).isNotEmpty();
        assertThat(target.getItems().get(0)).extracting(EvalTaskVo::getId,
                EvalTaskVo::getName,
                EvalTaskVo::getDescription,
                EvalTaskVo::getStatus,
                EvalTaskVo::getAppId,
                EvalTaskVo::getWorkflowId).containsExactly(taskId, name, description, status, appId, workflowId);
        assertThat(target.getItems().get(0)).extracting(EvalTaskVo::getPassRate,
                        EvalTaskVo::getPassCount,
                        EvalTaskVo::getInstanceStatus,
                        EvalTaskVo::getInstanceFinishedAt)
                .containsExactly(passRate, passCount, instanceStatus, instanceFinishTime);
    }
}