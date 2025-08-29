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
import modelengine.jade.store.entity.transfer.AppPublishData;

import java.util.Set;

/**
 * 存入数据库的应用的实体类。
 *
 * @author 鲁为
 * @since 2024-06-15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppDo extends CommonDo {
    /**
     * 表示应用的点赞数量。
     */
    private Integer likeCount;

    /**
     * 表示应用的下载量。
     */
    private Integer downloadCount;

    /**
     * 表示应用的来源。
     */
    private String source;

    /**
     * 表示应用的图标。
     */
    private String icon;

    /**
     * 表示应用的名字。
     */
    private String toolName;

    /**
     * 表示应用的唯一标识。
     */
    private String toolUniqueName;

    /**
     * 表示应用种类。
     */
    private String appCategory;

    /**
     * 用户组唯一标识。
     */
    private String userGroupId;

    /**
     * 表示用 {@link AppPublishData} 构造 {@link AppDo}。
     *
     * @param appData 表示传输层应用的数据的 {@link AppPublishData}。
     * @return 表示应用的数据库层数据的 {@link AppDo}。
     */
    public static AppDo from(AppPublishData appData) {
        AppDo appDo = new AppDo();
        appDo.setLikeCount(appData.getLikeCount());
        appDo.setDownloadCount(appData.getDownloadCount());
        appDo.setToolName(appData.getName());
        appDo.setToolUniqueName(appData.getUniqueName());
        appDo.setIcon(appData.getIcon());
        appDo.setSource(appData.getSource());
        appDo.setCreator(appData.getCreator());
        appDo.setModifier(appData.getModifier());
        appDo.setAppCategory(appData.getAppCategory());
        appDo.setUserGroupId(appData.getUserGroupId());
        return appDo;
    }

    /**
     * 将 {@link ToolData} 和 {@link AppDo} 转换为 {@link AppPublishData}。
     *
     * @param toolData 表示工具基本信息的 {@link ToolData}。
     * @param appDo 表示应用的数据库层数据的 {@link AppDo}
     * @param tags 表示应用的标签的集合。
     * @return 表示应用的传输层数据的 {@link AppPublishData}。
     */
    public static AppPublishData convertToAppPublishData(ToolData toolData, AppDo appDo, Set<String> tags) {
        AppPublishData appData = new AppPublishData();
        appData.setName(toolData.getName());
        appData.setUniqueName(toolData.getUniqueName());
        appData.setDescription(toolData.getDescription());
        appData.setSchema(toolData.getSchema());
        appData.setRunnables(toolData.getRunnables());
        appData.setVersion(toolData.getVersion());
        appData.setTags(tags);
        appData.setSource(appDo.getSource());
        appData.setIcon(appDo.getIcon());
        appData.setLikeCount(appDo.getLikeCount());
        appData.setDownloadCount(appDo.getDownloadCount());
        appData.setAppCategory(appDo.getAppCategory());
        appData.setCreator(appDo.getCreator());
        appData.setModifier(appDo.getModifier());
        appData.setDefGroupName(toolData.getDefGroupName());
        appData.setGroupName(toolData.getGroupName());
        appData.setDefName(toolData.getDefName());
        return appData;
    }
}
