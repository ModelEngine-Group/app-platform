/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.task.dto.EvalCaseQueryParam;
import modelengine.jade.app.engine.task.entity.EvalCaseEntity;
import modelengine.jade.app.engine.task.entity.EvalRecordEntity;
import modelengine.jade.app.engine.task.mapper.EvalCaseMapper;
import modelengine.jade.app.engine.task.service.EvalCaseService;
import modelengine.jade.app.engine.task.service.EvalRecordService;
import modelengine.jade.app.engine.task.vo.EvalCaseVo;
import modelengine.jade.common.vo.PageVo;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * 表示 {@link EvalCaseServiceImpl} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@FitTestWithJunit(includeClasses = EvalCaseServiceImpl.class)
public class EvalCaseServiceImplTest {
    @Fit
    private EvalCaseService evalCaseService;

    @Mock
    private EvalRecordService evalRecordService;

    @Mock
    private EvalCaseMapper evalCaseMapper;

    @AfterEach
    void tearDown() {
        clearInvocations(this.evalRecordService, this.evalCaseMapper);
    }

    @Test
    @DisplayName("插入评估用例成功")
    void shouldOkWhenCreateEvalTask() {
        doNothing().when(this.evalCaseMapper).create(any());
        doNothing().when(this.evalRecordService).createEvalRecord(any());

        EvalCaseEntity entity = new EvalCaseEntity();
        entity.setPass(true);
        entity.setInstanceId(1L);

        EvalRecordEntity result1 = new EvalRecordEntity();
        EvalRecordEntity result2 = new EvalRecordEntity();

        this.evalCaseService.createEvalCase(entity, Arrays.asList(result1, result2));
        verify(this.evalCaseMapper, times(1)).create((any()));
        verify(this.evalRecordService, times(1)).createEvalRecord((any()));
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
        recordEntity.setTaskCaseId(1L);

        when(this.evalRecordService.getEntityByCaseIds(anyList())).thenReturn(Collections.singletonList(recordEntity));
        when(this.evalCaseMapper.listEvalCase(any())).thenReturn(Collections.singletonList(caseEntity));
        when(this.evalCaseMapper.countEvalCase(any())).thenReturn(1);

        EvalCaseQueryParam queryParam = new EvalCaseQueryParam();
        queryParam.setInstanceId(1L);
        queryParam.setPageIndex(1);
        queryParam.setPageSize(10);

        PageVo<EvalCaseVo> caseVos = this.evalCaseService.listEvalCase(queryParam);
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
}