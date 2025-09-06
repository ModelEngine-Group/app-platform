/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.chat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * {@link ChatMessage} 的单元测试类。
 *
 * @author 曹嘉美
 * @since 2024-12-31
 */
@DisplayName("测试 ChatMessage")
class ChatMessageTest {
    private ChatMessage chatMessage;

    @BeforeEach
    void setUp() {
        // 创建 ChatMessage 实例
        chatMessage = new ChatMessage("msg123",
                "admin",
                "2024-12-30T12:00:00",
                1,
                Arrays.asList("Content 1", "Content 2"),
                "parent123",
                "children123",
                "MyApp",
                "app_icon.png");
    }

    @Test
    @DisplayName("用构建器构建消息信息类时，返回成功")
    void constructChatMessage() {
        assertThat(chatMessage).isNotNull();
        assertThat(chatMessage.getMsgId()).isEqualTo("msg123");
        assertThat(chatMessage.getRole()).isEqualTo("admin");
        assertThat(chatMessage.getCreateTime()).isEqualTo("2024-12-30T12:00:00");
        assertThat(chatMessage.getContentType()).isEqualTo(1);
        assertThat(chatMessage.getContent()).containsExactly("Content 1", "Content 2");
        assertThat(chatMessage.getParentId()).isEqualTo("parent123");
        assertThat(chatMessage.getChildrenId()).isEqualTo("children123");
        assertThat(chatMessage.getAppName()).isEqualTo("MyApp");
        assertThat(chatMessage.getAppIcon()).isEqualTo("app_icon.png");

        chatMessage.setMsgId("msg456");
        chatMessage.setRole("user");
        chatMessage.setCreateTime("2024-12-31T12:00:00");
        chatMessage.setContentType(2);
        chatMessage.setContent(Collections.singletonList("New Content"));
        chatMessage.setParentId("parent456");
        chatMessage.setChildrenId("children456");
        chatMessage.setAppName("NewApp");
        chatMessage.setAppIcon("new_app_icon.png");

        assertThat(chatMessage.getMsgId()).isEqualTo("msg456");
        assertThat(chatMessage.getRole()).isEqualTo("user");
        assertThat(chatMessage.getCreateTime()).isEqualTo("2024-12-31T12:00:00");
        assertThat(chatMessage.getContentType()).isEqualTo(2);
        assertThat(chatMessage.getContent()).containsExactly("New Content");
        assertThat(chatMessage.getParentId()).isEqualTo("parent456");
        assertThat(chatMessage.getChildrenId()).isEqualTo("children456");
        assertThat(chatMessage.getAppName()).isEqualTo("NewApp");
        assertThat(chatMessage.getAppIcon()).isEqualTo("new_app_icon.png");
    }

    @Test
    @DisplayName("构建空内容的消息信息类时，返回成功")
    void constructEmptyContent() {
        chatMessage.setContent(Collections.emptyList());
        assertThat(chatMessage.getContent()).isEmpty();
    }
}
