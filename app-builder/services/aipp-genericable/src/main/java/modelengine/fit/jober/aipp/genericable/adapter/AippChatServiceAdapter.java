/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.genericable.adapter;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.chat.ChatInfo;
import modelengine.fit.jober.aipp.dto.chat.ChatQueryParams;
import modelengine.fit.jober.common.RangedResultSet;

/**
 * 历史会话服务接口。
 *
 * @author 曹嘉美
 * @since 2024-12-19
 */
public interface AippChatServiceAdapter {
    /**
     * 查询会话列表。
     *
     * @param params 表示会话查询参数的 {@link ChatQueryParams}。
     * @param context 表示上下文的 {@link OperationContext}。
     * @return 表示会话相应体列表 {@code List<}{@link ChatInfo}{@code >}。
     */
    RangedResultSet<ChatInfo> queryChatList(ChatQueryParams params, OperationContext context);

    /**
     * 删除会话。
     *
     * @param chatIds 表示会话唯一标识符的 {@link String}。
     * @param appId 表示应用唯一标识符的 {@link String}。
     * @param operationContext 表示操作上下文的 {@link OperationContext}。
     */
    void deleteChat(String chatIds, String appId, OperationContext operationContext);
}
