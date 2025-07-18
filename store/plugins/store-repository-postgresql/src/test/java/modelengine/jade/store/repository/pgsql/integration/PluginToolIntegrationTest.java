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

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.IntegrationTest;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.store.entity.query.PluginToolQuery;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.service.DefinitionGroupService;
import modelengine.jade.store.service.PluginToolService;
import modelengine.jade.store.service.ToolGroupService;
import modelengine.jade.store.service.ToolService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 表示 插件工具 的集成测试用例集。
 *
 * @author 李金绪
 * @since 2024-09-19
 */
@IntegrationTest(scanPackages = "modelengine.jade.store")
@Sql(before = {"sql/create/pluginTool.sql", "sql/create/tag.sql", "sql/create/plugin.sql"})
@DisplayName("PluginTool 集成测试")
public class PluginToolIntegrationTest {
    @Fit
    private PluginToolService pluginToolService;

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
    @DisplayName("测试插入插件工具")
    void shouldOkWhenAdd() {
        when(this.toolService.getTool(any())).thenReturn(this.mockPluginToolData());
        PluginToolQuery pluginToolQuery = new PluginToolQuery();
        assertThat(this.pluginToolService.getPluginTools(pluginToolQuery).getCount()).isEqualTo(3);

        PluginToolData mockPluginToolData = this.mockPluginToolData();
        when(this.toolService.addTool(any())).thenReturn(null);

        this.pluginToolService.addPluginTool(mockPluginToolData);
        assertThat(this.pluginToolService.getPluginTools(pluginToolQuery).getCount()).isEqualTo(4);
    }

    @Test
    @Sql(before = {
            "sql/create/pluginTool.sql", "sql/create/tag.sql", "sql/create/plugin.sql", "sql/insert/pluginTool.sql",
            "sql/insert/tag.sql", "sql/insert/plugin.sql"
    })
    @DisplayName("测试插入插件工具集合")
    void shouldOkWhenAdds() {
        when(this.toolService.getTool(any())).thenReturn(this.mockPluginToolData());
        PluginToolQuery pluginToolQuery = new PluginToolQuery();
        assertThat(this.pluginToolService.getPluginTools(pluginToolQuery).getCount()).isEqualTo(3);

        PluginToolData mockPluginToolData1 = this.mockPluginToolData();
        mockPluginToolData1.setUniqueName("uname1");
        PluginToolData mockPluginToolData2 = this.mockPluginToolData();
        mockPluginToolData2.setUniqueName("uname2");
        doNothing().when(this.toolService).addTools(any());
        List<String> unames =
                this.pluginToolService.addPluginTools(Arrays.asList(mockPluginToolData1, mockPluginToolData2));
        assertThat(this.pluginToolService.getPluginTools(pluginToolQuery).getCount()).isEqualTo(5);
    }

    @Test
    @Sql(before = {
            "sql/create/pluginTool.sql", "sql/create/tag.sql", "sql/create/plugin.sql", "sql/insert/pluginTool.sql",
            "sql/insert/tag.sql", "sql/insert/plugin.sql"
    })
    @DisplayName("测试获取插件工具集合通过查询")
    void shouldOkWhenGetByQuery() {
        when(this.toolService.getTool(any())).thenReturn(this.mockPluginToolData());

        PluginToolQuery pluginToolQuery = new PluginToolQuery();
        assertThat(this.pluginToolService.getPluginTools(pluginToolQuery).getCount()).isEqualTo(3);
    }

    @Test
    @Sql(before = {
            "sql/create/pluginTool.sql", "sql/create/tag.sql", "sql/create/plugin.sql", "sql/insert/pluginTool.sql",
            "sql/insert/tag.sql", "sql/insert/plugin.sql"
    })
    @DisplayName("测试插入插件工具结合通过插件唯一标识")
    void shouldOkWhenGetByPluginId() {
        when(this.toolService.getTool(any())).thenReturn(this.mockPluginToolData());

        List<PluginToolData> pluginToolDataList = this.pluginToolService.getPluginTools("pid1");
        assertThat(pluginToolDataList.get(0).getTags()).isEqualTo(new HashSet<>(Arrays.asList("HUGGINGFACE", "FIT")));
        assertThat(pluginToolDataList.get(0).getPluginId()).isEqualTo("pid1");
        assertThat(pluginToolDataList.size()).isEqualTo(2);
    }

    @Test
    @Sql(before = {
            "sql/create/pluginTool.sql", "sql/create/tag.sql", "sql/create/plugin.sql", "sql/insert/pluginTool.sql",
            "sql/insert/tag.sql", "sql/insert/plugin.sql"
    })
    @DisplayName("测试插入插件工具结合通过工具唯一标识")
    void shouldOkWhenGetByUniqueName() {
        PluginToolData mockPluginToolData = this.mockPluginToolData();
        mockPluginToolData.setUniqueName("uniqueName1");
        when(this.toolService.getTool("uniqueName1")).thenReturn(mockPluginToolData);

        PluginToolData pluginToolData = this.pluginToolService.getPluginTool("uniqueName1");
        assertThat(pluginToolData.getTags()).isEqualTo(new HashSet<>(Arrays.asList("HUGGINGFACE", "FIT")));
        assertThat(pluginToolData.getName()).isEqualTo("name1");
    }

    @Test
    @Sql(before = {
            "sql/create/pluginTool.sql", "sql/create/tag.sql", "sql/create/plugin.sql", "sql/insert/pluginTool.sql",
            "sql/insert/tag.sql", "sql/insert/plugin.sql"
    })
    @DisplayName("测试删除插件工具")
    void shouldOkWhenDelete() {
        PluginToolQuery pluginToolQuery = new PluginToolQuery();
        when(this.toolService.getTool(any())).thenReturn(this.mockPluginToolData());
        assertThat(this.pluginToolService.getPluginTools(pluginToolQuery).getCount()).isEqualTo(3);

        when(this.toolService.deleteTool(any())).thenReturn(null);
        when(this.toolGroupService.get(any())).thenReturn(Collections.emptyList());
        when(this.deGroupService.delete(anyString())).thenReturn(StringUtils.EMPTY);
        String deleteUniqueName = this.pluginToolService.deletePluginTool("uniqueName1");
        assertThat(this.pluginToolService.getPluginTools(pluginToolQuery).getCount()).isEqualTo(2);
        assertThat(deleteUniqueName).isEqualTo("uniqueName1");
    }

    private PluginToolData mockPluginToolData() {
        PluginToolData pluginToolData = new PluginToolData();
        pluginToolData.setModifier("mockModifier");
        pluginToolData.setCreator("mockCreator");
        pluginToolData.setSource("mockSource");
        pluginToolData.setIcon("mockIcon");
        pluginToolData.setDownloadCount(100);
        pluginToolData.setLikeCount(200);
        pluginToolData.setTags(new HashSet<>(Arrays.asList("mockTag")));
        pluginToolData.setPluginId("mockPluginId");

        pluginToolData.setName("mockName");
        pluginToolData.setUniqueName("mockUniqueName");
        pluginToolData.setDescription("mockDescription");
        Map<String, Object> schema = new HashMap<>();
        schema.put("name", "name");
        schema.put("description", "description");
        pluginToolData.setSchema(schema);
        pluginToolData.setRunnables(new HashMap<>());
        pluginToolData.setExtensions(new HashMap<>());
        pluginToolData.setVersion("1.0.0");
        pluginToolData.setLatest(true);
        return pluginToolData;
    }
}
