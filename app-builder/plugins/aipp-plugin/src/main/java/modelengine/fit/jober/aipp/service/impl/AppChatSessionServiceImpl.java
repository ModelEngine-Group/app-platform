/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.entity.ChatSession;
import modelengine.fit.jober.aipp.mapper.AppChatNumMapper;
import modelengine.fit.jober.aipp.service.AppChatSessionService;

import lombok.RequiredArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.schedule.annotation.Scheduled;
import modelengine.fitframework.transaction.DataAccessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * {@link AppChatSessionService} 的默认实现
 *
 * @author 陈潇文
 * @since 2024-10-14
 */
@Component
@RequiredArgsConstructor
public class AppChatSessionServiceImpl implements AppChatSessionService {
    private static final Logger log = Logger.get(AppChatSessionServiceImpl.class);

    private final Map<String, ChatSession<Object>> emitterMap = new ConcurrentHashMap<>();
    private final AppChatNumMapper appChatNumMapper;

    @Override
    public void addSession(String instanceId, ChatSession<Object> chatSession) {
        this.emitterMap.put(instanceId, chatSession);
        try {
            this.appChatNumMapper.insertOrAddOne(Entities.generateId(),
                    chatSession.getAppId(),
                    String.valueOf(chatSession.isDebug()));
            chatSession.setOccupied(true);
        } catch (DataAccessException e) {
            log.warn("chat queue too long");
            throw new AippException(AippErrCode.CHAT_QUEUE_TOO_LONG);
        }
    }

    @Override
    public void removeSession(String instanceId) {
        ChatSession<Object> removedSession = this.emitterMap.remove(instanceId);
        Optional.ofNullable(removedSession)
                .filter(ChatSession::isOccupied)
                .ifPresent(session -> this.appChatNumMapper.minusOne(session.getAppId(),
                        String.valueOf(session.isDebug())));
    }

    @Override
    public Optional<ChatSession<Object>> getSession(String instanceId) {
        return Optional.ofNullable(this.emitterMap.get(instanceId));
    }

    /**
     * 定时清理过期的 ChatSession。
     */
    @Scheduled(strategy = Scheduled.Strategy.FIXED_RATE, value = "600000")
    public void sweepOutExpiredSession() {
        log.info("sweeping out expired sessions");
        List<String> expiredSessionId = this.emitterMap.entrySet()
                .stream()
                .filter(e -> e.getValue().getExpireTime().isBefore(LocalDateTime.now()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        expiredSessionId.forEach(this::removeSession);
    }
}
