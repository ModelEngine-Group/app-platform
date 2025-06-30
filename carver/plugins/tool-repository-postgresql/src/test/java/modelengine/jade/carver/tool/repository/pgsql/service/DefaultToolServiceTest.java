/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.jade.carver.tool.repository.pgsql.model.entity.ToolDo;
import modelengine.jade.carver.tool.repository.pgsql.repository.ToolRepositoryInner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link DefaultToolService} 的单元测试。
 *
 * @author 鲁为
 * @since 2024-08-07
 */
@DisplayName("测试 DefaultToolService")
public class DefaultToolServiceTest {
    private final ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null, true);

    @InjectMocks
    private DefaultToolService toolService;

    @Mock
    private ToolRepositoryInner toolRepositoryInner;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private ToolData mockToolData() {
        ToolData toolData = new ToolData();
        Map<String, Object> schema = new HashMap<>();
        schema.put("namespace", "namespace");
        schema.put("name", "name");
        schema.put("description", "description");
        schema.put("parameters", new HashMap<>());
        toolData.setSchema(schema);
        toolData.setLatest(true);
        toolData.setRunnables(new HashMap<>());
        toolData.setExtensions(new HashMap<>());
        toolData.setUniqueName("uniqueName");
        toolData.setVersion("1.0.0");
        return toolData;
    }

    private ToolDo mockToolDo() {
        ToolDo toolDo = new ToolDo();
        Map<String, Object> schema = new HashMap<>();
        schema.put("name", "name");
        schema.put("description", "description");
        schema.put("parameters", new HashMap<>());
        toolDo.setSchema(this.serializer.serialize(schema));
        toolDo.setExtensions("{}");
        toolDo.setUniqueName("testUniqueName");
        toolDo.setVersion("version");
        toolDo.setName("name");
        toolDo.setIsLatest(true);
        return toolDo;
    }

    @Test
    @DisplayName("验证插入工具成功")
    void shouldSuccessWhenAddTool() {
        ToolData toolData = mockToolData();
        this.toolService.addTools(Collections.singletonList(toolData));
        verify(toolRepositoryInner).addTools(Mockito.anyList());
    }

    @Test
    @DisplayName("验证删除工具列表成功")
    void shouldSuccessWhenDeleteTool() {
        List<String> uniqueNames = new ArrayList<>();
        this.toolService.deleteTools(uniqueNames);
        verify(toolRepositoryInner).deleteTools(uniqueNames);
    }

    @Test
    @DisplayName("验证获取工具成功")
    void shouldSuccessWhenGetTool() {
        String uniqueName = "testUniqueName";
        ToolDo toolDo = mockToolDo();
        Mockito.when(this.toolRepositoryInner.getTool(uniqueName))
                .thenReturn(Optional.of(ToolDo.do2Info(toolDo, this.serializer)));
        ToolData toolData = this.toolService.getTool(uniqueName);
        assertThat(toolData.getUniqueName()).isEqualTo(uniqueName);
    }

    @Test
    @DisplayName("验证获取某一版本工具成功")
    void shouldSuccessWhenGetToolByVersion() {
        String uniqueName = "testUniqueName";
        String version = "version";
        ToolDo toolDo = mockToolDo();
        Mockito.when(this.toolRepositoryInner.getToolByVersion(uniqueName, version))
                .thenReturn(Optional.of(ToolDo.do2Info(toolDo, this.serializer)));
        assertThat(this.toolService.getToolByVersion(uniqueName, version).getVersion()).isEqualTo(version);
    }

    @Test
    @DisplayName("验证升级工具成功")
    void shouldSuccessWhenUpgradeTool() {
        String uniqueName = "testUniqueName";
        String version = "version";
        ToolDo toolDo = mockToolDo();
        ToolData toolData = this.mockToolData();
        Mockito.when(this.toolRepositoryInner.getTool(toolData.getUniqueName()))
                .thenReturn(Optional.of(ToolDo.do2Info(toolDo, this.serializer)));
        doNothing().when(this.toolRepositoryInner).setNotLatest(toolData.getUniqueName());
        Mockito.when(this.toolRepositoryInner.getToolByVersion(toolData.getUniqueName(), toolData.getVersion()))
                .thenReturn(Optional.of(ToolDo.do2Info(toolDo, this.serializer)));
        doNothing().when(this.toolRepositoryInner).addTool(any());
        assertThat(this.toolService.upgradeTool(toolData)).isEqualTo(toolData.getUniqueName());
    }
}
