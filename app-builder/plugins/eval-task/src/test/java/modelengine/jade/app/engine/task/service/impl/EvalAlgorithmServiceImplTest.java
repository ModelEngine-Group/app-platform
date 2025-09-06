/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.task.entity.EvalAlgorithmEntity;
import modelengine.jade.app.engine.task.mapper.EvalAlgorithmMapper;
import modelengine.jade.app.engine.task.service.EvalAlgorithmService;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * 表示 {@link EvalAlgorithmServiceImpl} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-09-19
 */
@FitTestWithJunit(includeClasses = EvalAlgorithmServiceImpl.class)
public class EvalAlgorithmServiceImplTest {
    @Fit
    private EvalAlgorithmService service;

    @Mock
    private EvalAlgorithmMapper mapper;

    @AfterEach
    void tearDown() {
        clearInvocations(this.mapper);
    }

    @Test
    @DisplayName("插入评估算法成功")
    void shouldOkWhenInsertEvalAlgorithm() {
        doNothing().when(this.mapper).insert(anyList());
        EvalAlgorithmEntity result = new EvalAlgorithmEntity();

        this.service.insert(Collections.singletonList(result));
        verify(this.mapper, times(1)).insert((any()));
    }

    @Test
    @DisplayName("检测算法节点成功")
    void shouldOkWhenExistEvalAlgorithm() {
        when(this.mapper.countByNodeId(anyString())).thenReturn(1);
        assertThat(this.service.exist("node")).isEqualTo(true);
    }

    @Test
    @DisplayName("检测算法节点失败")
    void shouldFailWhenExistEvalAlgorithm() {
        when(this.mapper.countByNodeId(anyString())).thenReturn(0);
        assertThat(this.service.exist("node")).isEqualTo(false);
    }
}