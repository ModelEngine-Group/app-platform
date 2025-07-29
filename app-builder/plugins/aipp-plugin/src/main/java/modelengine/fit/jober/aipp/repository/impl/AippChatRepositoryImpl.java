/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository.impl;

import modelengine.fit.jober.aipp.entity.ChatAndInstanceMap;
import modelengine.fit.jober.aipp.entity.ChatInfo;
import modelengine.fit.jober.aipp.mapper.AippChatMapper;
import modelengine.fit.jober.aipp.repository.AippChatRepository;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link AippChatRepository} 对应实现类。
 *
 * @author 杨祥宇
 * @since 2025-04-09
 */
@Component
public class AippChatRepositoryImpl implements AippChatRepository {
    private final AippChatMapper aippChatMapper;

    /**
     * 表示用对话持久层构造 {@link AippChatRepositoryImpl} 的实例。
     *
     * @param aippChatMapper 表示对话持久层实例的 {@link AippChatMapper}。
     */
    public AippChatRepositoryImpl(AippChatMapper aippChatMapper) {this.aippChatMapper = aippChatMapper;}

    @Override
    public List<String> getExpiredChatIds(int expiredDays, int limit) {
        return this.aippChatMapper.getExpiredChatIds(expiredDays, limit);
    }

    @Override
    @Transactional
    public void forceDeleteChat(List<String> chatIds) {
        if (CollectionUtils.isEmpty(chatIds)) {
            return;
        }
        this.aippChatMapper.forceDeleteChat(chatIds);
        this.aippChatMapper.deleteWideRelationshipByChatIds(chatIds);
    }

    @Override
    public List<ChatInfo> selectByChatIds(List<String> chatIds) {
        if (CollectionUtils.isEmpty(chatIds)) {
            return new ArrayList<>();
        }
        return this.aippChatMapper.selectByChatIds(chatIds);
    }

    @Override
    public List<ChatAndInstanceMap> selectTaskInstanceRelationsByChatIds(List<String> chatIds) {
        if (CollectionUtils.isEmpty(chatIds)) {
            return new ArrayList<>();
        }
        return this.aippChatMapper.selectTaskInstanceRelationsByChatIds(chatIds);
    }
}
