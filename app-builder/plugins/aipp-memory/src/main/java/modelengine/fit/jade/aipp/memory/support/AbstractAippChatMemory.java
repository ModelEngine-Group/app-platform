/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.memory.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.support.AiMessage;
import modelengine.fel.core.chat.support.HumanMessage;
import modelengine.fel.core.memory.Memory;
import modelengine.fel.core.template.BulkStringTemplate;
import modelengine.fit.jade.aipp.memory.AippChatRound;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 表示聊天历史记录。
 *
 * @author 易文渊
 * @since 2024-09-19
 */
public abstract class AbstractAippChatMemory implements Memory {
    private final BulkStringTemplate template;

    /**
     * 构造聊天历史记录。
     *
     * @param template 表示模板的 {@link BulkStringTemplate}。
     */
    protected AbstractAippChatMemory(BulkStringTemplate template) {
        this.template = notNull(template, "Template cannot be null.");
    }

    @Override
    public final void add(ChatMessage question) {}


    @Override
    public void set(List<ChatMessage> messages) {}

    @Override
    public void clear() {}

    @Override
    public List<ChatMessage> messages() {
        return this.getAvailableHistories()
                .stream()
                .filter(chatRound -> chatRound != null && chatRound.getQuestion() != null
                        && chatRound.getAnswer() != null)
                .flatMap(chatRound -> Stream.of(new HumanMessage(chatRound.getQuestion()),
                        new AiMessage(chatRound.getAnswer())))
                .collect(Collectors.toList());
    }

    @Override
    public String text() {
        return this.template.render(this.getAvailableHistories()
                .stream()
                .map(AippChatRound::toMap)
                .collect(Collectors.toList()));
    }

    /**
     * 获取有效的历史记录。
     *
     * @return 表示有效的历史记录列表的 {@link List}{@code <}{@link AippChatRound}{@code >}。
     */
    protected abstract List<AippChatRound> getAvailableHistories();
}