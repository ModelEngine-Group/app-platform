/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.repository.support;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.MapBuilder;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.repository.pgsql.entity.PluginDo;
import modelengine.jade.store.repository.pgsql.mapper.PluginMapper;
import modelengine.jade.store.service.support.DeployStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link DefaultPluginRepository} 的单元测试。
 *
 * @author 鲁为
 * @since 2024-08-09
 */
@DisplayName("测试 DefaultPluginRepository")
public class DefaultPluginRepositoryTest {
    private DefaultPluginRepository pluginRepository;
    private PluginMapper pluginMapper;

    private final ObjectSerializer serializer =
            new JacksonObjectSerializer(null, null, null, true);

    @BeforeEach
    void setUp() {
        this.pluginMapper = mock(PluginMapper.class);
        this.pluginRepository = new DefaultPluginRepository(this.pluginMapper, this.serializer);
    }

    @Test
    @DisplayName("添加插件时，返回正确结果")
    void shouldSuccessWhenAddPlugin() {
        PluginData pluginData = new PluginData();
        pluginData.setDeployStatus(DeployStatus.DEPLOYED.toString());
        pluginData.setExtension(MapBuilder.<String, Object>get()
                .put("type", "java")
                .put("artifactId", "jade-demo-parent")
                .put("groupId", "store-demo-plugin")
                .put("version", "1.0.0")
                .build());
        pluginData.setBuiltin(true);
        String res = this.pluginRepository.addPlugin(pluginData);
        assertThat(res).isEqualTo(null);
    }

    @Test
    @DisplayName("删除插件时，返回正确结果")
    void shouldSuccessWhenDeletePlugin() {
        String pluginId = "testPluginId";
        this.pluginRepository.deletePlugin(pluginId);
        verify(this.pluginMapper).deletePlugin(pluginId);
    }

    @Test
    @DisplayName("查询插件时，返回正确结果")
    void shouldSuccessWhenGetPluginByPluginId() {
        String pluginId = "testPluginId";
        PluginDo pluginDo = new PluginDo();
        pluginDo.setPluginId(pluginId);
        Mockito.when(this.pluginMapper.getPluginByPluginId(pluginId)).thenReturn(pluginDo);
        assertThat(this.pluginRepository.getPluginByPluginId(pluginId).getPluginId()).isEqualTo(pluginId);
    }

    @Test
    @DisplayName("根据部署状态查询插件时，返回正确结果")
    void shouldSuccessWhenGetPluginByDeployStatus() {
        DeployStatus deployStatus = DeployStatus.DEPLOYED;
        String pluginId = "testPluginId";
        PluginDo pluginDo = new PluginDo();
        pluginDo.setPluginId(pluginId);
        List<PluginDo> pluginDos = new ArrayList<>();
        pluginDos.add(pluginDo);
        Mockito.when(this.pluginMapper.getPluginsByDeployStatus(deployStatus)).thenReturn(pluginDos);
        assertThat(this.pluginRepository.getPlugins(deployStatus).get(0).getPluginId()).isEqualTo(pluginId);
    }

    @Test
    @DisplayName("根据部署状态查询插件数量时，返回正确结果")
    void shouldSuccessWhenGetPluginCountByDeployStatus() {
        DeployStatus deployStatus = DeployStatus.DEPLOYED;
        Mockito.when(this.pluginMapper.getPluginsCountByDeployStatus(deployStatus)).thenReturn(0);
        assertThat(this.pluginRepository.getPluginsCount(deployStatus)).isEqualTo(0);
    }

    @Test
    @DisplayName("更新插件列表部署状态时，返回正确结果")
    void shouldSuccessWhenUpdateDeployStatus() {
        DeployStatus deployStatus = DeployStatus.DEPLOYED;
        List<String> pluginIdList = new ArrayList<>();
        Map<String, Object> mp = MapBuilder.<String, Object>get()
                .put("pluginIdList", pluginIdList)
                .put("deployStatus", deployStatus)
                .build();
        this.pluginRepository.updateDeployStatus(pluginIdList, deployStatus);
        verify(this.pluginMapper).updateDeployStatus(pluginIdList, deployStatus);
    }
}
