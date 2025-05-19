/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.chat.repository;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.chat.ChatCreateEntity;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRsp;

import java.util.Optional;

/**
 * 会话仓库接口.
 *
 * @author 张越
 * @since 2025-02-08
 */
public interface AppChatRepository {
    /**
     * 保存chat.
     *
     * @param chatCreateEntity 创建数据.
     * @param context 操作人上下文信息.
     */
    void saveChat(ChatCreateEntity chatCreateEntity, OperationContext context);

    /**
     * 通过chatId获取chat数据.
     *
     * @param chatId 会话id.
     * @param user 创建人.
     * @return {@link Optional}{@code <}{@link QueryChatRsp}{@code >} 对象.
     */
    Optional<QueryChatRsp> getChatById(String chatId, String user);
}
