/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.http.websocket.Session;

import java.util.Optional;

/**
 * 流式服务接口.
 *
 * @author 张越
 * @since 2024-05-14
 */
public interface AippStreamService {
    /**
     * 添加session.
     *
     * @param instanceId 实例id.
     * @param session websocket会话对象.
     */
    void addSession(String instanceId, Session session);

    /**
     * 添加session.
     *
     * @param session websocket会话对象.
     */
    void addSession(Session session);

    /**
     * 删除session.
     *
     * @param session {@link Session} 对象.
     */
    void removeSession(Session session);

    /**
     * 将instance绑定为整改后对话
     *
     * @param instId instance的Id
     */
    void addNewChat(String instId);

    /**
     * 判断一个instance是否属于整改后的对话
     *
     * @param instId instance的id
     * @return 该instance是否属于整改后的对话
     */
    Boolean isNewChat(String instId);

    /**
     * 获取session.
     *
     * @param instanceId 实例id.
     * @return {@link Optional}{@code <}{@link Session}{@code >}对象.
     */
    Optional<Session> getSession(String instanceId);

    /**
     * 通过会话id获取session.
     *
     * @param sessionId 会话id.
     * @return {@link Optional}{@code <}{@link Session}{@code >}对象.
     */
    Optional<Session> getSessionById(String sessionId);

    /**
     * 推送数据到前端.
     *
     * @param instanceId 实例id.
     * @param data 数据.
     */
    void send(String instanceId, Object data);

    /**
     * 推送数据到前端祖先的流。
     *
     * @param instanceId 实例id.
     * @param data 数据.
     */
    void sendToAncestor(String instanceId, Object data);
}
