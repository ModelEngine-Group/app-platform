/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.uid;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.when;

import modelengine.jade.app.engine.uid.mapper.IdGeneratorMapper;

import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link SimpleUidGenerator} 的测试用例。
 *
 * @author 何嘉斌
 * @since 2024-07-29
 */
@FitTestWithJunit(includeClasses = SimpleUidGenerator.class)
public class SimpleUidGeneratorTest {
    @Mock
    private IdGeneratorMapper idGeneratorMapper;

    @BeforeEach
    void setUp() {
        clearInvocations(this.idGeneratorMapper);
    }

    @Test
    @DisplayName("获取UID时，获取成功")
    void shouldOkWhenGetUid() {
        when(this.idGeneratorMapper.getNextId()).thenReturn(1L);
        SimpleUidGenerator uidGenerator = new SimpleUidGenerator(this.idGeneratorMapper);
        assertThat(uidGenerator.getUid()).isEqualTo(1L);
    }
}
