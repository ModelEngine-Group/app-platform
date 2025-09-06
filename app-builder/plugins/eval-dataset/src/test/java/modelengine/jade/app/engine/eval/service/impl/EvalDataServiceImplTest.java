/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.service.impl;

import static modelengine.jade.app.engine.eval.code.AppEvalDatasetRetCode.DATA_INVALID_ERROR;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.eval.manager.EvalDataValidator;
import modelengine.jade.app.engine.eval.manager.EvalDatasetVersionManager;
import modelengine.jade.app.engine.eval.mapper.EvalDataMapper;
import modelengine.jade.app.engine.eval.service.EvalDataService;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;
import modelengine.jade.common.exception.ModelEngineException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalDataServiceImpl} 的测试用例。
 *
 * @author 易文渊
 * @since 2024-07-22
 */
@FitTestWithJunit(includeClasses = EvalDataServiceImpl.class)
public class EvalDataServiceImplTest {
    private static final List<String> TEST_CONTENTS = Arrays.asList("test1", "test2");

    private final UserContext userContext = new UserContext("agent", "", "");

    @Fit
    private EvalDataService evalDataService;

    @Mock
    private EvalDataMapper evalDataMapper;

    @Mock
    private EvalDataValidator evalDataValidator;

    @Mock
    private EvalDatasetVersionManager evalDatasetVersionManager;

    @BeforeEach
    void setUp() {
        when(this.evalDatasetVersionManager.applyVersion()).thenReturn(1L);
        doNothing().when(this.evalDataMapper).insertAll(anyList());
        when(this.evalDataMapper.updateExpiredVersion(anyList(), anyLong(), any(), any())).thenReturn(1);
    }

    @AfterEach
    void teardown() {
        clearInvocations(this.evalDataMapper, this.evalDataValidator, this.evalDatasetVersionManager);
    }

    @Test
    @DisplayName("批量插入评估数据成功")
    void shouldOkWhenInsertAll() {
        doNothing().when(this.evalDataValidator).verify(anyLong(), anyList());
        this.evalDataService.insertAll(1L, TEST_CONTENTS);
        verify(this.evalDataMapper, times(1)).insertAll(anyList());
    }

    @Test
    @DisplayName("批量软删除评估数据成功")
    void shouldOkWhenDelete() {
        UserContextHolder.apply(userContext, () -> this.evalDataService.delete(Collections.singletonList(1L)));
        verify(this.evalDataMapper, times(1)).updateExpiredVersion(anyList(), anyLong(), any(), any());
    }

    @Test
    @DisplayName("更新评估数据成功")
    void shouldOkWhenUpdate() {
        UserContextHolder.apply(userContext, () -> this.evalDataService.update(1L, 1L, "test1"));
        verify(this.evalDataMapper, times(1)).updateExpiredVersion(anyList(), anyLong(), any(), any());
        verify(this.evalDataMapper, times(1)).insertAll(anyList());
    }

    @Test
    @DisplayName("更新评估数据失败")
    void shouldFailWhenUpdate() {
        when(this.evalDataMapper.updateExpiredVersion(anyList(), anyLong(), any(), any())).thenReturn(0);
        assertThatThrownBy(() -> this.evalDataService.update(1L, 2L, "test")).isInstanceOf(ModelEngineException.class);
    }

    @Test
    @DisplayName("批量插入评估数据，校验 schema 失败")
    void shouldFailWhenVerifyError() {
        doThrow(new ModelEngineException(DATA_INVALID_ERROR, "a", "b", "c")).when(this.evalDataValidator)
                .verify(anyLong(), anyList());
        assertThatThrownBy(() -> this.evalDataService.insertAll(1L, TEST_CONTENTS)).isInstanceOf(
                ModelEngineException.class);
    }

    @Test
    @DisplayName("硬删除评估数据成功")
    void shouldOkWhenHardDelete() {
        this.evalDataService.hardDelete(Collections.singletonList(1L));
        verify(this.evalDataMapper, times(1)).deleteAll(anyList());
    }
}