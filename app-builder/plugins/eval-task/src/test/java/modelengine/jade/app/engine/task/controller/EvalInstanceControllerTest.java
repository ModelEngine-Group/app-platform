/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.controller;

import static modelengine.jade.app.engine.task.entity.EvalInstanceStatusEnum.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import modelengine.fit.jober.aipp.genericable.AippRunTimeService;
import modelengine.jade.app.engine.task.dto.EvalInstanceCreateDto;
import modelengine.jade.app.engine.task.dto.EvalInstanceUpdateDto;
import modelengine.jade.app.engine.task.entity.EvalInstanceEntity;
import modelengine.jade.app.engine.task.service.EvalInstanceService;
import modelengine.jade.authentication.AuthenticationService;
import modelengine.jade.common.filter.support.LoginFilter;
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
import modelengine.jade.app.engine.task.dto.EvalInstanceCreateDto;
import modelengine.jade.app.engine.task.dto.EvalInstanceUpdateDto;
import modelengine.jade.app.engine.task.entity.EvalInstanceEntity;
import modelengine.jade.app.engine.task.service.EvalInstanceService;
import modelengine.jade.authentication.AuthenticationService;
import modelengine.jade.common.filter.support.LoginFilter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link EvalInstanceController} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-12
 */
@MvcTest(classes = {EvalInstanceController.class, LoginFilter.class})
@DisplayName("测试 EvalTaskInstanceController")
public class EvalInstanceControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Mock
    private EvalInstanceService evalInstanceService;

    @Mock
    private AuthenticationService authenticationServiceMock;

    @Mock
    private AippRunTimeService aippRunTimeService;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("创建评估任务实例接口成功")
    void shouldOkWhenCreateEvalInstance() {
        when(this.authenticationServiceMock.getUserName(any())).thenReturn("jane");
        String appId = "appId";
        Map<String, Object> initContext = new HashMap<>();
        initContext.put("total", 1);
        doNothing().when(this.evalInstanceService).createEvalInstance(anyLong(), anyString());
        when(this.aippRunTimeService.createLatestAippInstanceByAppId(anyString(),
                anyBoolean(),
                anyMap(),
                any())).thenReturn("traceId");

        EvalInstanceCreateDto createDto = new EvalInstanceCreateDto();
        createDto.setTaskId(1L);
        createDto.setInitContext(initContext);
        createDto.setTenantId("tenantId");
        createDto.setIsDebug(true);
        createDto.setAppId(appId);

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/task/instance").jsonEntity(createDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("创建评估任务实例接口失败")
    void shouldFailWhenCreateEvalInstance() {
        doNothing().when(this.evalInstanceService).createEvalInstance(anyLong(), anyString());

        EvalInstanceCreateDto createDto = new EvalInstanceCreateDto();
        createDto.setTaskId(0L);

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/task/instance").jsonEntity(createDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("分页查询评估任务实例接口成功")
    void shouldSuccessWhenQueryEvalInstance() {
        EvalInstanceEntity entity = new EvalInstanceEntity();
        entity.setId(1L);
        entity.setPassRate(76.0);
        entity.setPassCount(15);
        entity.setCreatedBy("Sky");
        entity.setStatus(RUNNING);
        entity.setTaskId(1L);
        List<EvalInstanceEntity> entities = Collections.singletonList(entity);
        Mockito.when(this.evalInstanceService.listEvalInstance(any())).thenReturn(PageVo.of(1, entities));

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/task/instance")
                .param("taskId", "1")
                .param("pageSize", "1")
                .param("pageIndex", "1")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {EvalInstanceEntity.class}));

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.response.objectEntity()).isPresent();
        PageVo<EvalInstanceEntity> target = ObjectUtils.cast(this.response.objectEntity().get().object());
        assertThat(target.getTotal()).isEqualTo(1);
        assertThat(target.getItems().get(0)).extracting(EvalInstanceEntity::getPassRate,
                EvalInstanceEntity::getPassCount,
                EvalInstanceEntity::getCreatedBy,
                EvalInstanceEntity::getStatus,
                EvalInstanceEntity::getTaskId).containsExactly(76.0, 15, "Sky", RUNNING, 1L);
    }

    @Test
    @DisplayName("分页查询评估任务实例接口失败")
    void shouldFailWhenQueryEvalInstance() {
        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/task/instance")
                .param("taskId", "0")
                .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {EvalInstanceEntity.class}));

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @DisplayName("更新评估任务实例接口成功")
    void shouldOkWhenUpdateEvalInstance() {
        doNothing().when(this.evalInstanceService).updateEvalInstance(any());

        EvalInstanceUpdateDto updateDto = new EvalInstanceUpdateDto();
        updateDto.setId(1L);
        updateDto.setFinishAt(LocalDateTime.now());
        updateDto.setPassRate(100.0);
        updateDto.setPassCount(100);

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("/eval/task/instance").jsonEntity(updateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("更新评估任务实例接口失败")
    void shouldFailWhenUpdateEvalInstance() {
        doNothing().when(this.evalInstanceService).updateEvalInstance(any());

        EvalInstanceUpdateDto updateDto = new EvalInstanceUpdateDto();

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("/eval/task/instance").jsonEntity(updateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }
}