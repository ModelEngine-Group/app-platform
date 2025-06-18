/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.jade.store.entity.query.PluginQuery;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.repository.pgsql.entity.PluginDo;
import modelengine.jade.store.repository.pgsql.repository.PluginRepository;
import modelengine.jade.store.service.DefinitionGroupService;
import modelengine.jade.store.service.PluginToolService;
import modelengine.jade.store.service.ToolGroupService;
import modelengine.jade.store.service.support.DeployStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * 表示 {@link DefaultPluginService} 的单元测试。
 *
 * @author 鲁为
 * @since 2024-08-09
 */
@DisplayName("测试 DefaultPluginService")
public class DefaultPluginServiceTest {
    private final ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null, true);

    private DefaultPluginService pluginService;
    private PluginRepository pluginRepository;
    private PluginToolService pluginToolService;
    private ToolGroupService toolGroupService;
    private DefinitionGroupService defGroupService;

    @BeforeEach
    void setUp() {
        this.pluginRepository = mock(PluginRepository.class);
        this.pluginToolService = mock(PluginToolService.class);
        this.toolGroupService = mock(ToolGroupService.class);
        this.defGroupService = mock(DefinitionGroupService.class);
        this.pluginService = new DefaultPluginService(this.pluginRepository,
                this.pluginToolService,
                this.defGroupService,
                this.toolGroupService,
                this.serializer);
    }

    @Test
    @DisplayName("添加插件时，返回成功")
    void shouldSuccessWhenAddPlugin() {
        String pluginId = "testPluginId";
        String uniqueName = "testUniqueName";
        PluginData pluginData = new PluginData();
        List<PluginToolData> pluginToolDataList = new ArrayList<>();
        PluginToolData pluginToolData = new PluginToolData();
        pluginToolData.setUniqueName(uniqueName);
        pluginToolDataList.add(pluginToolData);
        pluginData.setPluginToolDataList(pluginToolDataList);
        pluginData.setPluginId(pluginId);
        Mockito.when(this.pluginRepository.addPlugin(pluginData)).thenReturn(pluginId);
        Mockito.when(this.pluginToolService.addPluginTools(pluginData.getPluginToolDataList()))
                .thenReturn(new ArrayList<>());
        assertThat(this.pluginService.addPlugin(pluginData)).isEqualTo(pluginId);
    }

    @Test
    @DisplayName("查询插件列表时，返回成功")
    void shouldSuccessWhenGetPlugins() {
        PluginQuery pluginQuery = new PluginQuery();
        pluginQuery.setOffset(1);
        pluginQuery.setLimit(10);
        pluginQuery.setIncludeTags(new HashSet<>(Arrays.asList("APP")));
        pluginQuery.setExcludeTags(new HashSet<>());
        List<PluginDo> pluginDos = new ArrayList<>();
        PluginDo pluginDo = new PluginDo();
        pluginDo.setPluginId("testPluginId");
        pluginDo.setDeployStatus(DeployStatus.DEPLOYED);
        pluginDo.setCreator("system");
        pluginDos.add(pluginDo);

        Mockito.when(this.pluginRepository.getPlugins(pluginQuery)).thenReturn(pluginDos);
        Mockito.when(this.pluginRepository.getPluginsCount(pluginQuery)).thenReturn(1);
        assertThat(this.pluginService.getPlugins(pluginQuery)
                .getData()
                .get(0)
                .getPluginId())
                .isEqualTo("testPluginId");
    }

    @Test
    @DisplayName("根据部署状态查询插件列表时，返回成功")
    void shouldSuccessWhenGetPluginsByDeployStatus() {
        DeployStatus deployStatus = DeployStatus.DEPLOYED;
        List<PluginDo> pluginDos = new ArrayList<>();
        PluginDo pluginDo = new PluginDo();
        pluginDo.setPluginId("testPluginId");
        pluginDo.setDeployStatus(DeployStatus.DEPLOYED);
        pluginDo.setCreator("system");
        pluginDos.add(pluginDo);

        Mockito.when(this.pluginRepository.getPlugins(deployStatus)).thenReturn(pluginDos);
        assertThat(this.pluginService.getPlugins(deployStatus).get(0).getPluginId()).isEqualTo("testPluginId");
    }

    @Test
    @DisplayName("根据部署状态查询插件数量时，返回成功")
    void shouldSuccessWhenGetPluginsCountByDeployStatus() {
        DeployStatus deployStatus = DeployStatus.DEPLOYED;
        Mockito.when(this.pluginRepository.getPluginsCount(deployStatus)).thenReturn(0);
        assertThat(this.pluginService.getPluginsCount(deployStatus)).isEqualTo(0);
    }

    @Test
    @DisplayName("更新插件列表部署状态时，返回成功")
    void shouldSuccessWhenUpdateDeployStatus() {
        DeployStatus deployStatus = DeployStatus.DEPLOYED;
        List<String> pluginIdList = new ArrayList<>();
        this.pluginRepository.updateDeployStatus(pluginIdList, deployStatus);
        verify(this.pluginRepository).updateDeployStatus(pluginIdList, deployStatus);
    }

    @Test
    @DisplayName("查询插件时，返回成功")
    void shouldSuccessWhenGetPlugin() {
        String pluginId = "testPluginId";
        PluginDo pluginDo = new PluginDo();
        pluginDo.setPluginId(pluginId);
        pluginDo.setDeployStatus(DeployStatus.DEPLOYED);
        pluginDo.setCreator("system");
        List<PluginToolData> pluginToolDataList = new ArrayList<>();
        Mockito.when(this.pluginRepository.getPluginByPluginId(pluginId)).thenReturn(pluginDo);
        Mockito.when(this.pluginToolService.getPluginTools(pluginId)).thenReturn(pluginToolDataList);
        assertThat(this.pluginService.getPlugin(pluginId).getPluginId()).isEqualTo("testPluginId");
    }

    @Test
    @DisplayName("删除插件时，返回成功")
    void shouldSuccessWhenDeletePlugin() {
        String pluginId = "testPluginId";
        PluginDo pluginDo = new PluginDo();
        pluginDo.setPluginId(pluginId);
        List<PluginToolData> pluginToolDataList = new ArrayList<>();
        Mockito.when(this.pluginToolService.getPluginTools(pluginId)).thenReturn(pluginToolDataList);
        assertThat(this.pluginService.deletePlugin(pluginId)).isEqualTo("testPluginId");
    }
}
