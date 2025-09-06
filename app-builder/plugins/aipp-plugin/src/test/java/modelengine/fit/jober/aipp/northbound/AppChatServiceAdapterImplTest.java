/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jober.aipp.northbound;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.chat.ChatRequest;
import modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import modelengine.fit.jober.aipp.service.AppChatService;
import modelengine.fitframework.flowable.Choir;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link AppChatServiceAdapterImpl} 的单元测试。
 *
 * @author 陈潇文
 * @since 2025-07-15
 */
public class AppChatServiceAdapterImplTest {
    private final AppChatService appChatService = mock(AppChatService.class);
    private final AppChatServiceAdapterImpl appChatServiceAdapter =
            new AppChatServiceAdapterImpl(appChatService);

    @Test
    @DisplayName("测试对话")
    void shouldOkWhenTestChat() {
        String appId = "appId";
        ChatRequest params = ChatRequest.builder().chatId("chatId").question("q").build();
        Map<String, Object> userContext = new HashMap<>();
        userContext.put("a", "aaa");
        ChatRequest.Context context = ChatRequest.Context.builder()
                .atChatId("atChatId")
                .atAppId("atAppId")
                .useMemory(true)
                .dimension("dimension")
                .dimensionId("dimensionId")
                .userContext(userContext)
                .build();
        params.setContext(context);
        OperationContext operationContext = new OperationContext();
        boolean isDebug = true;
        when(this.appChatService.chat(any(CreateAppChatRequest.class),
                any(OperationContext.class),
                anyBoolean())).thenReturn(mock(Choir.class));
        // when
        Choir<Object> objectChoir = Assertions.assertDoesNotThrow(() -> this.appChatServiceAdapter.chat(appId,
                params,
                operationContext,
                isDebug));
        // then
        Assertions.assertNotNull(objectChoir);
    }
}
