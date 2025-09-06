/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippTaskNotFoundException;
import modelengine.fit.jober.aipp.dto.chat.ChatCreateEntity;
import modelengine.fit.jober.aipp.dto.chat.ChatInfoRspDto;
import modelengine.fit.jober.aipp.dto.chat.CreateChatRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatInfoRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRsp;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRspDto;
import modelengine.fit.jober.common.RangedResultSet;

import java.util.List;

/**
 * 历史会话服务接口.
 *
 * @author 翟卉馨
 * @since 2024-05-29
 */
public interface AippChatService {
    /**
     * 缓存运行时数据.
     *
     * @param body 请求体
     * @param context 上下文
     * @return 表示会话相应体的 {@link QueryChatRsp}。
     */
    QueryChatRsp createChat(CreateChatRequest body, OperationContext context);

    /**
     * 缓存运行时数据.
     *
     * @param body 请求体
     * @param chatId 会话ID
     * @param context 上下文
     * @return 表示会话相应体的 {@link QueryChatRsp}。
     * @throws AippTaskNotFoundException 任务不存在异常
     */
    QueryChatRsp queryChat(QueryChatRequest body, String chatId, OperationContext context)
            throws AippTaskNotFoundException;

    /**
     * 缓存运行时数据.
     *
     * @param body 请求体
     * @param context 上下文
     * @return 表示会话相应体列表 {@link QueryChatRsp}。List<QueryChatRsp>
     */
    RangedResultSet<QueryChatRspDto> queryChatList(QueryChatRequest body, OperationContext context);

    /**
     * 缓存运行时数据.
     *
     * @param chatIds 会话ID, 当传入多个id时，以“,”进行分割
     * @param appId 应用ID
     * @param context 上下文
     * @return Void
     */
    Void deleteChat(String chatIds, String appId, OperationContext context);

    /**
     * 缓存运行时数据.
     *
     * @param originChatId 主应用会话ID
     * @param body 请求体
     * @param context 上下文
     * @return 表示会话相应体的 {@link QueryChatRsp}。
     * @throws AippTaskNotFoundException 任务不存在异常
     */
    QueryChatRsp updateChat(String originChatId, CreateChatRequest body, OperationContext context)
            throws AippTaskNotFoundException;

    /**
     * 查询对话列表集合
     *
     * @param queryChatInfoRequest 请求体
     * @param context 上下文
     * @return 表示会话相应体列表 {@link ChatInfoRspDto}。List<ChatInfoRspDto>
     */
    List<ChatInfoRspDto> queryChatInfo(QueryChatInfoRequest queryChatInfoRequest, OperationContext context);

    /**
     * 保存会话数据.
     *
     * @param chatCreateEntity 待保存数据.
     * @param context 操作人上下文信息.
     */
    void saveChatInfo(ChatCreateEntity chatCreateEntity, OperationContext context);
}
