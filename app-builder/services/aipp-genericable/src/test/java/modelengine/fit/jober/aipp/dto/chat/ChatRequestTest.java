/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.chat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link ChatRequest} 的单元测试。
 *
 * @author 曹嘉美
 * @since 2024-12-31
 */
@DisplayName("测试 ChatRequest")
class ChatRequestTest {
    @Test
    @DisplayName("用构建器构建会话创建类时，返回成功")
    void constructChatRequest() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");

        ChatRequest.Context context = ChatRequest.Context.builder()
                .useMemory(true)
                .userContext(map)
                .atAppId("app1")
                .atChatId("chat1")
                .dimension("dimension1")
                .dimensionId("dimId1")
                .build();
        ChatRequest request =
                ChatRequest.builder().chatId("chat123").question("What is the weather?").context(context).build();
        assertThat(request).isNotNull();
        assertThat(request.getChatId()).isEqualTo("chat123");
        assertThat(request.getQuestion()).isEqualTo("What is the weather?");
        assertThat(request.getContext()).isNotNull();
        assertThat(request.getContext().getUseMemory()).isTrue();
        assertThat(request.getContext().getAtAppId()).isEqualTo("app1");
        assertThat(request.getContext().getAtChatId()).isEqualTo("chat1");
        assertThat(request.getContext().getDimension()).isEqualTo("dimension1");
        assertThat(request.getContext().getDimensionId()).isEqualTo("dimId1");
    }
}
