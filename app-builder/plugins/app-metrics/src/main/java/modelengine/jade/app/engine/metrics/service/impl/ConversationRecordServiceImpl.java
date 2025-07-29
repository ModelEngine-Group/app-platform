/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.service.impl;

import modelengine.jade.app.engine.metrics.mapper.ConversationRecordMapper;
import modelengine.jade.app.engine.metrics.po.ConversationRecordPo;
import modelengine.jade.app.engine.metrics.service.ConversationRecordService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;

/**
 * 历史对话服务的实现类。
 *
 * @author 董春寅
 * @since 2024-05-29
 */
@Component
public class ConversationRecordServiceImpl implements ConversationRecordService {
    private final ConversationRecordMapper conversationRecordMapper;

    public ConversationRecordServiceImpl(@Fit ConversationRecordMapper conversationRecordMapper) {
        this.conversationRecordMapper = conversationRecordMapper;
    }

    /**
     * 插入一条对话记录。
     *
     * @param conversationRecordPo 表示历史对话实体类的 {@link ConversationRecordPo}
     */
    @Override
    public void insertConversationRecord(ConversationRecordPo conversationRecordPo) {
        conversationRecordMapper.insertConversationRecord(conversationRecordPo);
    }
}
