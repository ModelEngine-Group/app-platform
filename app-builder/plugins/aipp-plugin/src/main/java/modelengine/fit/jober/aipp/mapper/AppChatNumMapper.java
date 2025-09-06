/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.mapper;

/**
 * 记录应用的对话数量的数据库操作
 *
 * @author 姚江
 * @since 2024-10-17
 */
public interface AppChatNumMapper {
    /**
     * 插入或者更新app的对话数量 +1
     *
     * @param id 表的id
     * @param appId 应用id
     * @param chatMode 应用对话方式（active、inactive）
     */
    void insertOrAddOne(String id, String appId, String chatMode);

    /**
     * 更新app的对话数量 -1
     *
     * @param appId 应用id
     * @param chatMode 应用对话方式
     */
    void minusOne(String appId, String chatMode);

    /**
     * 清空所有 app 计数
     */
    void clearNum();
}
