/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.service;

import static modelengine.jade.carver.tool.repository.pgsql.ToolDataBuilder.mockToolGroupData;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

import modelengine.fel.tool.model.transfer.ToolGroupData;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.carver.tool.repository.pgsql.repository.ToolGroupRepository;
import modelengine.jade.store.service.ToolService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

/**
 * 表示 {@link DefaultToolGroupService} 的测试。
 *
 * @author 李金绪
 * @since 2024-12-10
 */
@DisplayName("测试 DefaultToolGroupService")
public class DefaultToolGroupServiceTest {
    @InjectMocks
    private DefaultToolGroupService toolGroupService;

    @Mock
    private ToolGroupRepository toolGroupRepository;

    @Mock
    private ToolService toolService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("当插入工具组数据时，成功")
    void shouldOkWhenAddToolGroupData() {
        ToolGroupData mockToolGroupData = mockToolGroupData();
        this.toolGroupService.add(mockToolGroupData);
        verify(this.toolService).addTools(any(), any(), anyList());
        verify(this.toolGroupRepository).add(any());
    }

    @Test
    @DisplayName("当删除工具组数据时，成功")
    void shouldOkWhenDeleteToolGroupData() {
        this.toolGroupService.delete(StringUtils.EMPTY, StringUtils.EMPTY);
        verify(this.toolService).deleteTools(any(), any());
        verify(this.toolGroupRepository).delete(any(), any());
    }

    @Test
    @DisplayName("当查询工具组数据时，成功")
    void shouldOkWhenGetToolGroupData() {
        this.toolGroupService.get(StringUtils.EMPTY, Collections.singletonList(StringUtils.EMPTY));
        verify(this.toolService).getTools(any(), any());
        verify(this.toolGroupRepository).get(any(), any());
    }
}
