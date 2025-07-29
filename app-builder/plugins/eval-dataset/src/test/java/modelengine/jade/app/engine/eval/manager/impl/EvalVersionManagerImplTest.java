/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.eval.manager.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.eval.entity.EvalVersionEntity;
import modelengine.jade.app.engine.eval.manager.EvalDatasetVersionManager;
import modelengine.jade.app.engine.eval.mapper.EvalDataMapper;
import modelengine.jade.app.engine.uid.UidGenerator;

import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

/**
 * 表示 {@link EvalVersionManagerImpl} 的测试用例。
 *
 * @author 何嘉斌
 * @since 2024-07-24
 */
@FitTestWithJunit(includeClasses = {EvalVersionManagerImpl.class})
@DisplayName("测试 EvalVersionManagerImpl")
public class EvalVersionManagerImplTest {
    @Mock
    EvalDataMapper evalDataMapper;

    @Mock
    private UidGenerator generator;

    @Test
    @DisplayName("获取新版本成功")
    void shouldOkWhenApplyVersion() {
        when(this.generator.getUid()).thenReturn(0L, 1L, 2L);
        for (int i = 0; i < 3; i++) {
            EvalDatasetVersionManager versionManager = new EvalVersionManagerImpl(evalDataMapper, generator);
            assertThat(versionManager.applyVersion()).isEqualTo(i);
        }
    }

    @Test
    @DisplayName("查询数据集版本成功")
    void shouldOkWhenListEvalData() {
        EvalDatasetVersionManager versionManager = new EvalVersionManagerImpl(evalDataMapper, generator);
        LocalDateTime currentTime = LocalDateTime.now();
        EvalVersionEntity entity = new EvalVersionEntity();
        entity.setVersion(1L);
        entity.setCreatedTime(currentTime);

        when(this.evalDataMapper.getLatestVersion(any())).thenReturn(entity);

        EvalVersionEntity firstEntity = versionManager.getLatestVersion(1L);
        assertThat(firstEntity).extracting(EvalVersionEntity::getVersion, EvalVersionEntity::getCreatedTime)
                .containsExactly(1L, currentTime);
    }
}