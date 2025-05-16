/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.service;

import static modelengine.jade.carver.tool.repository.pgsql.ToolDataBuilder.mockDefinitionGroupData;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

import modelengine.fel.tool.model.transfer.DefinitionGroupData;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.carver.tool.repository.pgsql.repository.DefinitionGroupRepository;
import modelengine.jade.store.service.DefinitionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * 表示 {@link DefaultToolGroupService} 的测试。
 *
 * @author 李金绪
 * @since 2024-12-10
 */
@DisplayName("测试 DefaultToolGroupService")
public class DefaultDefinitionGroupServiceTest {
    @InjectMocks
    private DefaultDefinitionGroupService defGroupService;

    @Mock
    private DefinitionGroupRepository defGroupRepository;

    @Mock
    private DefinitionService defService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("当插入定义组数据时，成功")
    void shouldOkWhenAddDefGroupData() {
        DefinitionGroupData mockDefGroupData = mockDefinitionGroupData();
        this.defGroupService.add(mockDefGroupData);
        verify(this.defService).add(anyList());
        verify(this.defGroupRepository).add(any());
    }

    @Test
    @DisplayName("当删除定义组数据时，成功")
    void shouldOkWhenDeleteDefGroupData() {
        this.defGroupService.delete(StringUtils.EMPTY);
        verify(this.defService).delete(any());
        verify(this.defGroupRepository).delete(any());
    }

    @Test
    @DisplayName("当查询定义组数据时，成功")
    void shouldOkWhenGetDefGroupData() {
        this.defGroupService.get(StringUtils.EMPTY);
        verify(this.defGroupRepository).get(any());
    }
}
