/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jober.aipp.entity.ChatSession;

import java.util.Optional;

/**
 * 管理app chat session的类
 *
 * @author 陈潇文
 * @since 2024-10-14
 */
public interface AppChatSessionService {
    /**
     * 添加应用会话.
     *
     * @param instanceId 实例id.
     * @param chatSession 应用会话.
     */
    void addSession(String instanceId, ChatSession<Object> chatSession);

    /**
     * 删除应用会话.
     *
     * @param instanceId 实例id.
     */
    void removeSession(String instanceId);

    /**
     * 获取应用会话.
     *
     * @param instanceId 实例id.
     * @return {@link Optional}{@code <}{@link Object}{@code >}对象.
     */
    Optional<ChatSession<Object>> getSession(String instanceId);
}
