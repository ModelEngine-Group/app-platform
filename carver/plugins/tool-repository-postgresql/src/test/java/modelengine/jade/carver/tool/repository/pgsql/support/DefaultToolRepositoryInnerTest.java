/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.support;

import static org.mockito.Mockito.verify;

import modelengine.fel.tool.Tool;
import modelengine.jade.carver.tool.repository.pgsql.repository.support.DefaultToolRepositoryInner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 表示 {@link DefaultToolRepositoryInner} 的单元测试。
 *
 * @author 鲁为
 * @since 2024-08-08
 */
@DisplayName("测试 DefaultToolRepository")
public class DefaultToolRepositoryInnerTest {
    @Mock
    private DefaultToolRepositoryInner toolRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("验证插入工具列表成功")
    void shouldSuccessWhenAddTool() {
        Tool.Info info = Tool.Info.custom()
                .namespace("namespace")
                .name("name")
                .description("description")
                .parameters(new HashMap<>())
                .uniqueName("testUniqueName")
                .extensions(new HashMap<>())
                .build();
        this.toolRepository.addTool(info);
        verify(toolRepository).addTool(info);
    }

    @Test
    @DisplayName("验证删除工具列表成功")
    void shouldSuccessWhenDeleteTools() {
        List<String> uniqueNames = new ArrayList<>();
        this.toolRepository.deleteTools(uniqueNames);
        verify(toolRepository).deleteTools(uniqueNames);
    }
}
