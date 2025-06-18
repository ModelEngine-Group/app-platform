/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.entity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.MapBuilder;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.service.support.DeployStatus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * 表示 {@link PluginDo} 的单元测试。
 *
 * @author 鲁为
 * @since 2024-08-09
 */
@DisplayName("测试 PluginDo")
public class PluginDoTest {
    private final ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null, true);

    @Test
    @DisplayName("从传输层实体类转换数据库类，返回正确结果")
    void shouldSuccessWhenFromPluginData() {
        PluginData pluginData = new PluginData();
        pluginData.setCreator("user");
        pluginData.setModifier("user");
        pluginData.setPluginId("testPluginId");
        pluginData.setPluginName("testPlugin");
        pluginData.setExtension(MapBuilder.<String, Object>get().put("type", "java").build());
        pluginData.setDeployStatus("deployed");
        pluginData.setBuiltin(true);
        PluginDo pluginDo = PluginDo.fromPluginData(pluginData, this.serializer);
        assertThat(pluginDo.getPluginId()).isEqualTo("testPluginId");
        assertThat(pluginDo.getPluginName()).isEqualTo("testPlugin");
        assertThat(pluginDo.getExtension()).isEqualTo("{\"type\":\"java\"}");
        assertThat(pluginDo.getDeployStatus().toString()).isEqualTo("DEPLOYED");
    }

    @Test
    @DisplayName("从数据库类转换传输层实体类，返回正确结果")
    void shouldSuccessWhenToPluginData() {
        PluginDo pluginDo = new PluginDo();
        pluginDo.setPluginId("testPluginId");
        pluginDo.setPluginName("testPlugin");
        pluginDo.setExtension("{\"type\":\"java\"}");
        pluginDo.setDeployStatus(DeployStatus.DEPLOYED);
        pluginDo.setBuiltin(true);
        PluginData pluginData = PluginDo.convertToPluginData(pluginDo, this.serializer, Collections.EMPTY_LIST);
        assertThat(pluginData.getPluginId()).isEqualTo("testPluginId");
        assertThat(pluginData.getPluginName()).isEqualTo("testPlugin");
        assertThat(pluginData.getExtension()).isEqualTo(MapBuilder.<String, Object>get().put("type", "java").build());
        assertThat(pluginData.getDeployStatus()).isEqualTo("DEPLOYED");
        assertThat(pluginData.getBuiltin()).isEqualTo(true);
    }
}
