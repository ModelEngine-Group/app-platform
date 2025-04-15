/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository;

import modelengine.fit.jober.aipp.entity.ChatAndInstanceMap;
import modelengine.fit.jober.aipp.entity.ChatInfo;

import java.util.List;

/**
 * 应用对话repo层。
 *
 * @author 杨祥宇
 * @since 2025-04-09
 */
public interface AippChatRepository {
    /**
     * 插入会话信息.
     *
     * @param info 会话信息
     */
    void insertChat(ChatInfo info);

    /**
     * 插入关系宽表
     *
     * @param info 会话信息
     */
    void insertWideRelationship(ChatAndInstanceMap info);

    List<String> getExpiredChatIds(int expiredDays, int limit);

    void forceDeleteChat(List<String> chatIds);

    List<ChatInfo> selectByChatIds(List<String> chatIds);

    List<ChatAndInstanceMap> selectTaskInstanceRelationsByChatIds(List<String> chatIds);
}
