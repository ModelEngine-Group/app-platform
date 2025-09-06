/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.chat.repository.impl;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.chat.repository.AppChatRepository;
import modelengine.fit.jober.aipp.dto.chat.ChatCreateEntity;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRsp;
import modelengine.fit.jober.aipp.entity.ChatAndInstanceMap;
import modelengine.fit.jober.aipp.entity.ChatInfo;
import modelengine.fit.jober.aipp.mapper.AippChatMapper;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.aipp.util.UUIDUtil;

import lombok.AllArgsConstructor;
import modelengine.fitframework.annotation.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 会话仓库实现类
 *
 * @author 张越
 * @since 2025-02-08
 */
@Component
@AllArgsConstructor
public class AppChatRepositoryImpl implements AppChatRepository {
    private static final String DEFAULT_CHAT_NAME_PREFIX = "@appBuilderDebug-";

    private final AippChatMapper aippChatMapper;

    @Override
    public void saveChat(ChatCreateEntity chatCreateEntity, OperationContext context) {
        String cutChatName = this.generateChatName(chatCreateEntity.getChatName());
        LocalDateTime operateTime = LocalDateTime.now();
        ChatInfo chatInfo = ChatInfo.builder()
                .appId(chatCreateEntity.getAppId())
                .version(chatCreateEntity.getAppVersion())
                .attributes(JsonUtils.toJsonString(chatCreateEntity.getAttributes()))
                .chatId(chatCreateEntity.getChatId())
                .chatName(cutChatName)
                .status(AippConst.CHAT_STATUS)
                .updater(context.getOperator())
                .createTime(operateTime)
                .updateTime(operateTime)
                .creator(context.getOperator())
                .build();
        this.aippChatMapper.insertChat(chatInfo);
        ChatAndInstanceMap wideRelationInfo = ChatAndInstanceMap.builder()
                .msgId(UUIDUtil.uuid())
                .instanceId(chatCreateEntity.getTaskInstanceId())
                .chatId(chatCreateEntity.getChatId())
                .createTime(operateTime)
                .updateTime(operateTime)
                .build();
        this.aippChatMapper.insertWideRelationship(wideRelationInfo);
    }

    private String generateChatName(String chatName) {
        if (chatName == null) {
            return DEFAULT_CHAT_NAME_PREFIX + UUIDUtil.uuid().substring(0, 6);
        }
        return chatName.length() > 64 ? chatName.substring(0, 32) : chatName;
    }

    @Override
    public Optional<QueryChatRsp> getChatById(String chatId, String user) {
        List<QueryChatRsp> chats = this.aippChatMapper.selectChatList(null, chatId, user);
        return chats.stream().findFirst();
    }
}
