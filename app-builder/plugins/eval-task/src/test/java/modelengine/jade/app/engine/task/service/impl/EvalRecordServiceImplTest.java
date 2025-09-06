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

import modelengine.jade.app.engine.task.dto.EvalRecordQueryParam;
import modelengine.jade.app.engine.task.entity.EvalRecordEntity;
import modelengine.jade.app.engine.task.mapper.EvalRecordMapper;
import modelengine.jade.common.vo.PageVo;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalRecordServiceImpl} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@FitTestWithJunit(includeClasses = EvalRecordServiceImpl.class)
public class EvalRecordServiceImplTest {
    @Fit
    private EvalRecordServiceImpl caseResultService;

    @Mock
    private EvalRecordMapper mapper;

    @AfterEach
    void tearDown() {
        clearInvocations(this.mapper);
    }

    @Test
    @DisplayName("插入评估用任务例结果成功")
    void shouldOkWhenCreateEvalRecord() {
        doNothing().when(this.mapper).create(anyList());
        EvalRecordEntity result = new EvalRecordEntity();

        this.caseResultService.createEvalRecord(Collections.singletonList(result));
        verify(this.mapper, times(1)).create((any()));
    }

    @Test
    @DisplayName("分页查询评估任务用例结果成功")
    void shouldOkWhenListEvalInstance() {
        EvalRecordEntity entity = new EvalRecordEntity();
        entity.setId(1L);
        entity.setInput("{\"input\":\"1+1\", \"output\":\"2\", \"expected\":\"2\"}");
        entity.setNodeName("'node1'");
        entity.setScore(100.0);
        List<EvalRecordEntity> entities = Collections.singletonList(entity);
        when(this.mapper.listEvalRecord(any())).thenReturn(entities);

        EvalRecordQueryParam queryParam = new EvalRecordQueryParam();
        queryParam.setNodeIds(Collections.singletonList("node1"));
        queryParam.setPageSize(5);
        queryParam.setPageIndex(1);

        PageVo<EvalRecordEntity> response = this.caseResultService.listEvalRecord(queryParam);
        EvalRecordEntity firstItem = response.getItems().get(0);
        assertThat(firstItem).extracting(EvalRecordEntity::getId,
                        EvalRecordEntity::getInput,
                        EvalRecordEntity::getNodeName,
                        EvalRecordEntity::getScore)
                .containsExactly(1L, "{\"input\":\"1+1\", \"output\":\"2\", \"expected\":\"2\"}", "'node1'", 100.0);
    }

    @Test
    @DisplayName("查询评估任务用例结果成功")
    void shouldOkWhenGetEntityByCaseIds() {
        EvalRecordEntity entity = new EvalRecordEntity();
        entity.setId(1L);
        entity.setInput("{\"input\":\"1+1\", \"output\":\"2\", \"expected\":\"2\"}");
        entity.setNodeName("'node1'");
        entity.setScore(100.0);
        when(this.mapper.getEntityByCaseIds(any())).thenReturn(Collections.singletonList(entity));

        List<EvalRecordEntity> entities = this.caseResultService.getEntityByCaseIds(Collections.singletonList(1L));
        assertThat(entities.get(0)).extracting(EvalRecordEntity::getId,
                        EvalRecordEntity::getInput,
                        EvalRecordEntity::getNodeName,
                        EvalRecordEntity::getScore)
                .containsExactly(1L, "{\"input\":\"1+1\", \"output\":\"2\", \"expected\":\"2\"}", "'node1'", 100.0);
    }
}