/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.IntegrationTest;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.store.entity.query.PluginQuery;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.service.DefinitionGroupService;
import modelengine.jade.store.service.PluginService;
import modelengine.jade.store.service.ToolGroupService;
import modelengine.jade.store.service.ToolService;
import modelengine.jade.store.service.support.DeployStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 表示 插件 的集成测试用例集。
 *
 * @author 李金绪
 * @since 2024-09-19
 */
@IntegrationTest(scanPackages = "modelengine.jade.store")
@Sql(before = {"sql/create/pluginTool.sql", "sql/create/tag.sql", "sql/create/plugin.sql"})
@DisplayName("Plugin 集成测试")
public class PluginIntegrationTest {
    @Fit
    private PluginService pluginService;

    @Mock
    private ToolService toolService;
    @Mock
    private DefinitionGroupService deGroupService;
    @Mock
    private ToolGroupService toolGroupService;

    @Test
    @Sql(before = {
            "sql/create/pluginTool.sql", "sql/create/tag.sql", "sql/create/plugin.sql", "sql/insert/pluginTool.sql",
            "sql/insert/tag.sql", "sql/insert/plugin.sql"
    })
    @DisplayName("测试插入插件")
    void shouldOkWhenAdd() {
        PluginQuery pluginQuery = new PluginQuery();
        PluginData pluginData = this.mockPluginData();
        pluginData.setPluginToolDataList(Arrays.asList(this.mockToolData()));

        doNothing().when(this.toolService).addTools(any());
        when(this.toolService.getTool(any())).thenReturn(this.mockToolData());

        assertThat(this.pluginService.getPlugins(pluginQuery).getCount()).isEqualTo(3);
        this.pluginService.addPlugin(pluginData);
        assertThat(this.pluginService.getPlugins(pluginQuery).getCount()).isEqualTo(4);
    }

    @Test
    @Sql(before = {
            "sql/create/pluginTool.sql", "sql/create/tag.sql", "sql/create/plugin.sql", "sql/insert/pluginTool.sql",
            "sql/insert/tag.sql", "sql/insert/plugin.sql"
    })
    @DisplayName("测试获取单个插件")
    void shouldOkWhenGet() {
        ToolData mockToolData1 = this.mockToolData();
        mockToolData1.setUniqueName("uniqueName1");
        ToolData mockToolData2 = this.mockToolData();
        mockToolData2.setUniqueName("uniqueName2");
        when(this.toolService.getTool("uniqueName1")).thenReturn(mockToolData1);
        when(this.toolService.getTool("uniqueName2")).thenReturn(mockToolData2);

        PluginData pluginData = this.pluginService.getPlugin("pid1");
        assertThat(pluginData.getPluginToolDataList().size()).isEqualTo(2);
        assertThat(pluginData.getPluginId()).isEqualTo("pid1");
    }

    @Test
    @Sql(before = {
            "sql/create/pluginTool.sql", "sql/create/tag.sql", "sql/create/plugin.sql", "sql/insert/pluginTool.sql",
            "sql/insert/tag.sql", "sql/insert/plugin.sql"
    })
    @DisplayName("测试获取插件通过查询")
    void shouldOkWhenGetByQuery() {
        PluginQuery pluginQuery = new PluginQuery();
        when(this.toolService.getTool(any())).thenReturn(this.mockToolData());

        assertThat(this.pluginService.getPlugins(pluginQuery).getCount()).isEqualTo(3);
    }

    @Test
    @Sql(before = {
            "sql/create/pluginTool.sql", "sql/create/tag.sql", "sql/create/plugin.sql", "sql/insert/pluginTool.sql",
            "sql/insert/tag.sql", "sql/insert/plugin.sql"
    })
    @DisplayName("测试获取插件通过状态")
    void shouldOkWhenGetByStatus() {
        when(this.toolService.getTool(any())).thenReturn(this.mockToolData());

        assertThat(this.pluginService.getPlugins(DeployStatus.UNDEPLOYED).size()).isEqualTo(2);
    }

    @Test
    @Sql(before = {
            "sql/create/pluginTool.sql", "sql/create/tag.sql", "sql/create/plugin.sql", "sql/insert/pluginTool.sql",
            "sql/insert/tag.sql", "sql/insert/plugin.sql"
    })
    @DisplayName("测试获取插件数量")
    void shouldOkWhenGetCount() {
        when(this.toolService.getTool(any())).thenReturn(this.mockToolData());

        assertThat(this.pluginService.getPluginsCount(DeployStatus.UNDEPLOYED)).isEqualTo(2);
    }

    @Test
    @Sql(before = {
            "sql/create/pluginTool.sql", "sql/create/tag.sql", "sql/create/plugin.sql", "sql/insert/pluginTool.sql",
            "sql/insert/tag.sql", "sql/insert/plugin.sql"
    })
    @DisplayName("测试更新状态")
    void shouldOkWhenUpdate() {
        ToolData mockToolData3 = this.mockToolData();
        mockToolData3.setUniqueName("uniqueName3");
        when(this.toolService.getTool("uniqueName2")).thenReturn(mockToolData3);

        assertThat(this.pluginService.getPlugin("pid3").getDeployStatus()).isEqualTo(DeployStatus.DEPLOYED.toString());
        this.pluginService.updateDeployStatus(Arrays.asList("pid3"), DeployStatus.UNDEPLOYED);
        assertThat(this.pluginService.getPlugin("pid3")
                .getDeployStatus()).isEqualTo(DeployStatus.UNDEPLOYED.toString());
    }

    @Test
    @Sql(before = {
            "sql/create/pluginTool.sql", "sql/create/tag.sql", "sql/create/plugin.sql", "sql/insert/pluginTool.sql",
            "sql/insert/tag.sql", "sql/insert/plugin.sql"
    })
    @DisplayName("测试删除插件")
    void shouldOkWhenDelete() {
        PluginQuery pluginQuery = new PluginQuery();
        when(this.toolService.getTool(any())).thenReturn(this.mockToolData());
        assertThat(this.pluginService.getPlugins(pluginQuery).getCount()).isEqualTo(3);

        ToolData mockToolData1 = this.mockToolData();
        mockToolData1.setUniqueName("uniqueName1");
        ToolData mockToolData2 = this.mockToolData();
        mockToolData2.setUniqueName("uniqueName2");
        when(this.toolService.getTool("uniqueName1")).thenReturn(mockToolData1);
        when(this.toolService.getTool("uniqueName2")).thenReturn(mockToolData2);
        when(this.toolGroupService.get(any())).thenReturn(Collections.emptyList());
        when(this.deGroupService.delete(anyString())).thenReturn(StringUtils.EMPTY);
        this.pluginService.deletePlugin("pid1");
        assertThat(this.pluginService.getPlugins(pluginQuery).getCount()).isEqualTo(2);
    }

    private PluginData mockPluginData() {
        PluginData pluginData = new PluginData();
        pluginData.setCreator("mockCreator");
        pluginData.setModifier("mockModifier");
        pluginData.setPluginId("mockPluginId");
        pluginData.setPluginName("mockPluginName");
        pluginData.setExtension(new HashMap<>());
        pluginData.setDeployStatus("mockDeployStatus");
        pluginData.setBuiltin(false);
        pluginData.setSource("mockSource");
        pluginData.setIcon("mockIcon");
        pluginData.setPluginToolDataList(Collections.EMPTY_LIST);
        return pluginData;
    }

    private PluginToolData mockToolData() {
        PluginToolData toolData = new PluginToolData();
        toolData.setName("mockName");
        toolData.setUniqueName("mockUniqueName");
        toolData.setDescription("mockDescription");
        Map<String, Object> schema = new HashMap<>();
        schema.put("name", "name");
        schema.put("description", "description");
        toolData.setSchema(schema);
        toolData.setRunnables(new HashMap<>());
        toolData.setExtensions(new HashMap<>());
        toolData.setVersion("1.0.0");
        toolData.setLatest(true);
        toolData.setSource("builtin");
        toolData.setIcon("mockIcon");
        toolData.setTags(new HashSet<>(Arrays.asList("FIT")));
        return toolData;
    }
}
