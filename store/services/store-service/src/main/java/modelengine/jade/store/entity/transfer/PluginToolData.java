/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.entity.transfer;

/**
 * 表示插件的数据内容。
 *
 * @author 鲁为
 * @since 2024-06-15
 */
public class PluginToolData extends StoreToolData {
    /**
     * 表示插件工具点赞数量。
     */
    private Integer likeCount;

    /**
     * 表示插件工具下载数量。
     */
    private Integer downloadCount;

    /**
     * 表示插件的唯一标识。
     */
    private String pluginId;

    /**
     * 表示用户组的唯一标识。
     */
    private String userGroupId;

    /**
     * 获取插件工具点赞数量。
     *
     * @return 表示插件工具点赞数量的 {@link String}。
     */
    public Integer getLikeCount() {
        return this.likeCount;
    }

    /**
     * 设置插件工具点赞数量。
     *
     * @param likeCount 表示插件工具点赞数量的 {@link Integer}。
     */
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    /**
     * 获取插件工具下载数量。
     *
     * @return 表示插件工具下载数量的 {@link Integer}。
     */
    public Integer getDownloadCount() {
        return this.downloadCount;
    }

    /**
     * 设置插件工具下载数量。
     *
     * @param downloadCount 表示插件工具下载数量的 {@link Integer}。
     */
    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    /**
     * 获取插件的唯一标识。
     *
     * @return 表示插件的唯一标识的 {@link String}。
     */
    public String getPluginId() {
        return this.pluginId;
    }

    /**
     * 设置插件的唯一标识。
     *
     * @param pluginId 表示插件的唯一标识的 {@link String}。
     */
    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    /**
     * 获取用户组的唯一标识。
     *
     * @return 表示用户组的唯一标识的 {@link String}。
     */
    public String getUserGroupId() {
        return this.userGroupId;
    }

    /**
     * 设置用户组的唯一标识。
     *
     * @return 表示用户组的唯一标识的 {@link String}。
     */
    public void setUserGroupId(String userGroupId) {
        this.userGroupId = userGroupId;
    }
}
