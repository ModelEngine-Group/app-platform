/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.service;

import modelengine.jade.app.engine.metrics.po.ConversationRecordPo;

/**
 * ConversationRecordService类消息处理策略
 *
 * @author 陈霄宇
 * @since 2024/05/28
 */
public interface ConversationRecordService {
    /**
     * insert into conversation_record
     *
     * @param conversationRecordPo conversation_record字段
     */
    void insertConversationRecord(ConversationRecordPo conversationRecordPo);
}
