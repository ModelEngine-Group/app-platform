/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.entity;

import static modelengine.jade.carver.util.SerializeUtils.json2obj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.jade.carver.entity.CommonDo;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.service.support.DeployStatus;

import java.util.List;

/**
 * 存入插件数据库的实体类。
 *
 * @author 鲁为
 * @since 2024-07-25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PluginDo extends CommonDo {
    /**
     * 表示插件的唯一标识。
     */
    private String pluginId;

    /**
     * 表示插件的名字。
     */
    private String pluginName;

    /**
     * 表示插件的扩展。
     */
    private String extension;

    /**
     * 表示插件的部署状态。
     */
    private DeployStatus deployStatus;

    /**
     * 表示插件是否内置。
     */
    private boolean isBuiltin;

    /**
     * 表示插件的来源。
     */
    private String source;

    /**
     * 表示插件的图标。
     */
    private String icon;

    /**
     * 表示用户组 id。
     */
    private String userGroupId;

    /**
     * 用传输层的插件数据 {@link PluginData} 构造 {@link PluginDo}。
     *
     * @param pluginData 表示传输层的插件数据的 {@link PluginData}。
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     * @return 表示数据库层的插件数据的 {@link PluginDo}。
     */
    public static PluginDo fromPluginData(PluginData pluginData, ObjectSerializer serializer) {
        PluginDo pluginDo = new PluginDo();
        if (pluginData == null) {
            return pluginDo;
        }
        pluginDo.setCreator(pluginData.getCreator());
        pluginDo.setModifier(pluginData.getModifier());
        pluginDo.setPluginId(pluginData.getPluginId());
        pluginDo.setPluginName(pluginData.getPluginName());
        pluginDo.setExtension(serializer.serialize(pluginData.getExtension()));
        pluginDo.setDeployStatus(DeployStatus.from(pluginData.getDeployStatus()));
        pluginDo.setSource(pluginData.getSource());
        pluginDo.setUserGroupId(pluginData.getUserGroupId());
        if (pluginData.getIcon() != null) {
            pluginDo.setIcon(pluginData.getIcon());
        }
        return pluginDo;
    }

    /**
     * 用数据库持久层 {@link PluginDo} 构造传输层的插件数据 {@link PluginData}。
     *
     * @param pluginDo 表示持久层的插件数据的 {@link PluginDo}。
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     * @param pluginToolDataList 表示插件工具数据列表的 {@link List<PluginToolData>}。
     * @return 表示传输层的插件数据的 {@link PluginData}。
     */
    public static PluginData convertToPluginData(PluginDo pluginDo, ObjectSerializer serializer,
            List<PluginToolData> pluginToolDataList) {
        PluginData pluginData = new PluginData();
        if (pluginDo == null) {
            return pluginData;
        }
        pluginData.setCreator(pluginDo.getCreator());
        pluginData.setModifier(pluginDo.getModifier());
        pluginData.setPluginId(pluginDo.getPluginId());
        pluginData.setPluginName(pluginDo.getPluginName());
        pluginData.setExtension(json2obj(pluginDo.getExtension(), serializer));
        if (pluginDo.getDeployStatus() != null) {
            pluginData.setDeployStatus(pluginDo.getDeployStatus().toString());
        }
        pluginData.setBuiltin(pluginDo.isBuiltin());
        pluginData.setSource(pluginDo.getSource());
        if (pluginDo.getIcon() != null) {
            pluginData.setIcon(pluginDo.getIcon());
        }
        pluginData.setPluginToolDataList(pluginToolDataList);
        pluginData.setUserGroupId(pluginDo.getUserGroupId());
        pluginData.setModifiable(true);
        return pluginData;
    }
}
