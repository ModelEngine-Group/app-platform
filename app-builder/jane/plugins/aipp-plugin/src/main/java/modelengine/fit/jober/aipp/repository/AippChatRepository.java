/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository;

import modelengine.fit.jober.aipp.entity.ChatAndInstanceMap;
import modelengine.fit.jober.aipp.entity.ChatInfo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 应用对话repo层。
 *
 * @author 杨祥宇
 * @since 2025-04-09
 */
public interface AippChatRepository {
    /**
     * 获取超期的对话唯一标识列表。
     *
     * @param expiredDays 表示超期时长的 {@code int}。
     * @param limit 表示查询数量的 {@code int}。
     * @return 表示对话唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> getExpiredChatIds(int expiredDays, int limit);

    /**
     * 根据对话标识列表强制删除对话。
     *
     * @param chatIds 表示对话唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    void forceDeleteChat(List<String> chatIds);

    /**
     * 根据对话唯一标识列表批量查询会话记录实体。
     *
     * @param chatIds 表示对话唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示会话记录实体列表的 {@link List}{@code <}{@link ChatInfo}{@code >}。
     */
    List<ChatInfo> selectByChatIds(List<String> chatIds);

    /**
     * 根据对话唯一标识列表批量查询会话记录和任务实例的关系。
     *
     * @param chatIds 表示对话唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示会话记录和任务实例的关系的 {@link List}{@code <}{@link ChatAndInstanceMap}{@code >}。
     */
    List<ChatAndInstanceMap> selectTaskInstanceRelationsByChatIds(List<String> chatIds);
}
