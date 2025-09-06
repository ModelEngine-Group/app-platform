/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.chat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link ChatInfo} 的单元测试。
 *
 * @author 曹嘉美
 * @since 2024-12-31
 */
@DisplayName("测试 ChatInfo")
public class ChatInfoTest {
    @Test
    @DisplayName("用构建器构建会话响应信息类时，返回成功")
    public void constructChatInfo() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("key1", "value1");
        attributes.put("key2", "value2");

        ChatMessage message = new ChatMessage("msg1",
                "user",
                "2024-12-28T10:00:00",
                1,
                Arrays.asList("Hello", "World"),
                "parent1",
                "child1",
                "MyApp",
                "app_icon.png");

        ChatInfo queryChatRspDto = new ChatInfo("app123",
                "1.0",
                "chat123",
                "Test Chat",
                "originChat123",
                attributes,
                Collections.singletonList(message),
                "instance123",
                "2024-12-28T10:00:00",
                "Some recent info",
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                100);

        assertThat(queryChatRspDto).isNotNull();
        assertThat(queryChatRspDto.getAppId()).isEqualTo("app123");
        assertThat(queryChatRspDto.getVersion()).isEqualTo("1.0");
        assertThat(queryChatRspDto.getChatId()).isEqualTo("chat123");
        assertThat(queryChatRspDto.getChatName()).isEqualTo("Test Chat");
        assertThat(queryChatRspDto.getMessageList()).hasSize(1);
    }
}
