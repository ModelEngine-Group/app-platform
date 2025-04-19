/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.mapper;

import modelengine.fit.jober.aipp.dto.chat.ChatDto;
import modelengine.fit.jober.aipp.dto.chat.QueryChatInfoRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRsp;
import modelengine.fit.jober.aipp.entity.ChatAndInstanceMap;
import modelengine.fit.jober.aipp.entity.ChatInfo;
import modelengine.fit.jober.aipp.po.MsgInfoPO;

import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 会话消息Mapper
 *
 * @author 翟卉馨
 * @since 2024-05-29
 */
public interface AippChatMapper {
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

    /**
     * 查询会话记录
     *
     * @param request 表示请求体的 {@link QueryChatRequest}。
     * @param chatId 表示会话 ID 的 {@link String}。
     * @param user 表示创建该会话的用户的 {@link String}。
     * @return List<QueryChatRsp>
     */
    List<QueryChatRsp> selectChatList(@Param("requestParam") QueryChatRequest request, @Param("chatId") String chatId,
            @Param("createBy") String user);

    /**
     * 查询会话记录数目
     *
     * @param request 请求体
     * @param chatId 会话ID
     * @return 会话记录数目
     */
    long getChatListCount(@Param("requestParam") QueryChatRequest request, @Param("chatId") String chatId, @Param("createBy") String createBy);

    /**
     * 查询会话
     *
     * @param chatId 会话ID
     * @param offset 起始查询
     * @param limit 查询条数
     * @return List<ChatDto>
     */
    List<ChatDto> selectChat(@Param("chatId") String chatId, @Param("offset") Integer offset,
            @Param("limit") Integer limit);

    /**
     * 删除会话
     *
     * @param chatId 会话ID
     * @return Integer
     */
    Integer deleteChat(@Param("chatId") String chatId);

    /**
     * 删除APP
     *
     * @param appId appId
     * @return Integer
     */
    Integer deleteApp(@Param("appId") String appId);

    /**
     * 删除APP
     *
     * @param aippId aippId
     * @return Integer
     */
    Integer deleteAppByAippId(@Param("aippId") String aippId);

    /**
     * 查询实例记录
     *
     * @param chatId 会话ID
     * @param limit 查询条数
     * @return List<String>
     */
    List<String> selectInstanceByChat(String chatId, Integer limit);

    /**
     * 查询历史实例记录
     *
     * @param chatId 会话ID
     * @param limit 查询条数
     * @return List<String> 实例id列表
     */
    List<String> selectFormerInstanceByChat(String chatId, Integer limit);

    /**
     * 根据instance id列表批量查询对话消息
     * @param instanceIds instance id列表
     * @return 对应会话信息
     */
    List<MsgInfoPO> selectMsgByInstanceIds(List<String> instanceIds);

    /**
     * countChat
     *
     * @param chatId chatId
     * @return Integer
     */
    Integer countChat(String chatId);

    /**
     * 根据指定应用ID, 查询最近count个会话ID
     *
     * @param appId 应用Id
     * @param appType 应用是否发布
     * @param count count
     * @return List<String> 会话ID列表
     */
    List<String> selectChatByAppId(String appId, String appType, int count);

    /**
     * 根据指定实例 ID 查询会话 ID。
     *
     * @param instanceId 指定实例 ID。
     * @return 表示会话 ID 的 {@link String}。
     */
    List<String> selectChatIdByInstanceId(String instanceId);

    /**
     * 根据指定实例 ID 删除关系宽表。
     *
     * @param instanceId 指定实例 ID。
     */
    void deleteWideRelationshipByInstanceId(String instanceId);

    /**
     * 根据chatId列表 查询会话
     *
     * @param chatIds 会话ID列表
     * @return List<QueryChatRsp> 会话记录列表
     */
    List<QueryChatRsp> selectChatListByChatIds(@Param("chatIds") List<String> chatIds);

    /**
     * 根据会话实例Id 查询会话
     *
     * @param instId 会话实例id
     * @return List<QueryChatRsp> 会话记录列表
     */
    List<QueryChatRsp> selectChatListByInstId(String instId);

    /**
     * 根据所给条件查询会话
     *
     * @param condition 所给条件
     * @param queryChatInfoRequest 请求体
     * @return List<QueryChatRsp> 会话记录列表
     */
    List<QueryChatRsp> selectChatByCondition(@Param("condition") Map<String, String> condition,
            @Param("requestParam") QueryChatInfoRequest queryChatInfoRequest);

    /**
     * 获取超期的对话唯一标识。
     *
     * @param expiredDays 表示超期时长的 {@link LocalDateTime}。
     * @param limit 表示查询数量的 {@code int}。
     * @return 表示对话唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> getExpiredChatIds(LocalDateTime expiredDays, int limit);

    /**
     * 根据对话标识列表强制删除对话。
     *
     * @param chatIds 表示对话唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    void forceDeleteChat(List<String> chatIds);

    /**
     * 根据对话标识列表强制删除对话和任务实例关系。
     *
     * @param chatIds 表示对话唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    void deleteWideRelationshipByChatIds(List<String> chatIds);

    /**
     * 根据对话唯一标识列表批量查询会话记录实体。
     *
     * @param chatIds 表示对话唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示会话记录实体列表的 {@link List}{@code <}{@link ChatInfo}{@code >}。
     */
    List<ChatInfo> selectByChatIds(@Param("chatIds") List<String> chatIds);

    /**
     * 根据对话唯一标识列表批量查询会话记录和任务实例的关系。
     *
     * @param chatIds 表示对话唯一标识列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示会话记录和任务实例的关系的 {@link List}{@code <}{@link ChatAndInstanceMap}{@code >}。
     */
    List<ChatAndInstanceMap> selectTaskInstanceRelationsByChatIds(@Param("chatIds") List<String> chatIds);
}