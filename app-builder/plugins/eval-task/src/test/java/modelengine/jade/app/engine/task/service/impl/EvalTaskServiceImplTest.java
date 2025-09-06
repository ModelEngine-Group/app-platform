/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.service.impl;

import static modelengine.jade.app.engine.task.entity.EvalInstanceStatusEnum.SUCCESS;
import static modelengine.jade.app.engine.task.entity.EvalTaskStatusEnum.PUBLISHED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.task.dto.EvalTaskQueryParam;
import modelengine.jade.app.engine.task.entity.EvalInstanceEntity;
import modelengine.jade.app.engine.task.entity.EvalTaskEntity;
import modelengine.jade.app.engine.task.entity.EvalTaskStatusEnum;
import modelengine.jade.app.engine.task.mapper.EvalInstanceMapper;
import modelengine.jade.app.engine.task.mapper.EvalTaskMapper;
import modelengine.jade.app.engine.task.service.EvalTaskService;
import modelengine.jade.app.engine.task.vo.EvalTaskVo;
import modelengine.jade.common.vo.PageVo;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalTaskServiceImpl} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
@FitTestWithJunit(includeClasses = EvalTaskServiceImpl.class)
public class EvalTaskServiceImplTest {
    @Fit
    private EvalTaskService evalTaskService;

    @Mock
    private EvalTaskMapper evalTaskMapper;

    @Mock
    private EvalInstanceMapper evalInstanceMapper;

    @AfterEach
    void teardown() {
        clearInvocations(this.evalTaskMapper);
    }

    @Test
    @DisplayName("插入评估任务成功")
    void shouldOkWhenCreateEvalTask() {
        doNothing().when(this.evalTaskMapper).create(any());
        EvalTaskEntity entity = new EvalTaskEntity();
        entity.setName("task1");
        entity.setDescription("eval task");
        entity.setStatus(PUBLISHED);
        entity.setAppId("123456");
        entity.setWorkflowId("flow1");

        this.evalTaskService.createEvalTask(entity);
        verify(this.evalTaskMapper, times(1)).create((any()));
    }

    @Test
    @DisplayName("查询全量评估任务元数据成功")
    void shouldOkWhenListEvalTask() {
        Long taskId = 1L;
        String name = "task1";
        String description = "eval task";
        EvalTaskStatusEnum status = PUBLISHED;
        String appId = "123456";
        String workflowId = "flow1";
        LocalDateTime instanceFinishTime = LocalDateTime.now();

        EvalTaskEntity entity = new EvalTaskEntity();
        entity.setId(taskId);
        entity.setName(name);
        entity.setDescription(description);
        entity.setStatus(status);
        entity.setAppId(appId);
        entity.setWorkflowId(workflowId);
        List<EvalTaskEntity> entities = Collections.singletonList(entity);

        EvalInstanceEntity instanceEntity = new EvalInstanceEntity();
        instanceEntity.setPassRate(95.0);
        instanceEntity.setStatus(SUCCESS);
        instanceEntity.setPassCount(10);
        instanceEntity.setFinishedAt(instanceFinishTime);
        instanceEntity.setTaskId(taskId);

        when(this.evalInstanceMapper.findLatestInstances(anyList()))
                .thenReturn(Collections.singletonList(instanceEntity));
        when(this.evalTaskMapper.listEvalTask(any())).thenReturn(entities);
        when(this.evalTaskMapper.countEvalTask(any())).thenReturn(1);

        EvalTaskQueryParam queryParam = new EvalTaskQueryParam();
        PageVo<EvalTaskVo> response = this.evalTaskService.listEvalTask(queryParam);

        EvalTaskVo firstVo = response.getItems().get(0);
        assertThat(response).extracting(PageVo::getTotal, r -> r.getItems().size()).containsExactly(1, 1);
        assertThat(firstVo).extracting(EvalTaskVo::getId,
                        EvalTaskVo::getName,
                        EvalTaskVo::getDescription,
                        EvalTaskVo::getStatus,
                        EvalTaskVo::getAppId,
                        EvalTaskVo::getWorkflowId,
                        EvalTaskVo::getInstanceStatus,
                        EvalTaskVo::getPassRate,
                        EvalTaskVo::getPassCount,
                        EvalTaskVo::getInstanceFinishedAt)
                .containsExactly(taskId,
                        name,
                        description,
                        status,
                        appId,
                        workflowId,
                        SUCCESS,
                        95.0,
                        10,
                        instanceFinishTime);
    }

    @Test
    @DisplayName("批量软删除评估任务成功")
    void shouldOkWhenDeleteEvalTask() {
        this.evalTaskService.deleteEvalTask(Collections.singletonList(1L));
        verify(this.evalTaskMapper, times(1)).updateDeletedTask(anyList());
    }
}