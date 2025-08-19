/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.jade.carver.entity.CommonDo;
import modelengine.jade.store.entity.transfer.PluginToolData;

import java.util.Set;

/**
 * 存入数据库的模型的实体类。
 *
 * @author 鲁为
 * @since 2024-06-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PluginToolDo extends CommonDo {
    /**
     * 表示插件工具的点赞数量。
     */
    private Integer likeCount;

    /**
     * 表示插件工具的下载量。
     */
    private Integer downloadCount;

    /**
     * 表示插件工具的名字。
     */
    private String name;

    /**
     * 表示插件的唯一标识。
     */
    private String pluginId;

    /**
     * 表示工具的唯一标识。
     */
    private String toolUniqueName;

    /**
     * 表示插件工具的来源。
     */
    private String source;

    /**
     * 表示插件工具的图标。
     */
    private String icon;

    /**
     * 表示用户组的唯一标识。
     */
    private String userGroupId;

    /**
     * 用传输层的插件数据 {@link PluginToolData} 构造 {@link PluginToolDo}。
     *
     * @param pluginToolData 表示传输层的插件数据的 {@link PluginToolData}。
     * @return 表示数据库层的插件数据的 {@link PluginToolDo}。
     */
    public static PluginToolDo fromPluginToolData(PluginToolData pluginToolData) {
        PluginToolDo pluginToolDo = new PluginToolDo();
        pluginToolDo.setCreator(pluginToolData.getCreator());
        pluginToolDo.setModifier(pluginToolData.getModifier());
        pluginToolDo.setLikeCount(pluginToolData.getLikeCount());
        pluginToolDo.setDownloadCount(pluginToolData.getDownloadCount());
        pluginToolDo.setName(pluginToolData.getName());
        pluginToolDo.setToolUniqueName(pluginToolData.getUniqueName());
        pluginToolDo.setSource(pluginToolData.getSource());
        pluginToolDo.setIcon(pluginToolData.getIcon());
        if (pluginToolData.getPluginId() != null) {
            pluginToolDo.setPluginId(pluginToolData.getPluginId());
        }
        pluginToolDo.setUserGroupId(pluginToolData.getUserGroupId());
        return pluginToolDo;
    }

    /**
     * 用工具数据 {@link ToolData} 和插件工具持久层 {@link PluginToolDo} 构造 {@link PluginToolData}。
     *
     * @param toolData 表示工具数据的 {@link ToolData}。
     * @param pluginToolDo 表示插件工具持久层的 {@link PluginToolDo}。
     * @param tags 表示插件工具的标签集合的 {@link Set}{@code <}{@link String}{@code >}。
     * @return 表示插件工具数据信息的 {@link PluginToolData}。
     */
    public static PluginToolData convertToPluginToolData(ToolData toolData, PluginToolDo pluginToolDo,
            Set<String> tags) {
        PluginToolData pluginToolData = new PluginToolData();
        pluginToolData.setCreator(pluginToolDo.getCreator());
        pluginToolData.setModifier(pluginToolDo.getModifier());
        pluginToolData.setLikeCount(pluginToolDo.getLikeCount());
        pluginToolData.setDownloadCount(pluginToolDo.getDownloadCount());
        pluginToolData.setName(pluginToolDo.getName());
        pluginToolData.setSource(pluginToolDo.getSource());
        pluginToolData.setIcon(pluginToolDo.getIcon());
        pluginToolData.setUniqueName(toolData.getUniqueName());
        pluginToolData.setDescription(toolData.getDescription());
        pluginToolData.setSchema(toolData.getSchema());
        pluginToolData.setRunnables(toolData.getRunnables());
        pluginToolData.setExtensions(toolData.getExtensions());
        pluginToolData.setVersion(toolData.getVersion());
        pluginToolData.setLatest(toolData.getLatest());
        pluginToolData.setTags(tags);
        if (pluginToolDo.getPluginId() != null) {
            pluginToolData.setPluginId(pluginToolDo.getPluginId());
        }
        pluginToolData.setDefName(toolData.getDefName());
        pluginToolData.setDefGroupName(toolData.getDefGroupName());
        pluginToolData.setGroupName(toolData.getGroupName());
        pluginToolData.setUserGroupId(pluginToolDo.getUserGroupId());
        return pluginToolData;
    }
}
