/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fel;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.support.AiMessage;
import modelengine.fel.core.chat.support.HumanMessage;
import modelengine.fel.core.memory.Memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AippMemory
 *
 * @author 易文渊
 * @since 2024-04-23
 */
public class AippMemory implements Memory {
    private final List<ChatMessage> messages = new ArrayList<>();

    /**
     * 构造函数，用于初始化AippMemory对象。
     *
     * @param data 包含问题和答案的数据列表
     */
    public AippMemory(List<Map<String, String>> data) {
        data.forEach(m -> {
            if (m.containsKey("question") && m.containsKey("answer")) {
                messages.add(new HumanMessage(m.get("question")));
                messages.add(new AiMessage(m.get("answer")));
            }
        });
    }

    @Override
    public void add(ChatMessage message) {}

    @Override
    public void set(List<ChatMessage> messages) {}

    @Override
    public void clear() {}

    @Override
    public List<ChatMessage> messages() {
        return messages;
    }

    @Override
    public String text() {
        return null;
    }
}