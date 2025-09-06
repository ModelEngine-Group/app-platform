/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.service.AppChatService;
import modelengine.fit.jober.websocket.dto.RestartChatParams;

import modelengine.fitframework.flowable.Choir;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

/**
 * {@link RestartChatAppWsCommand} 的测试类。
 *
 * @author 曹嘉美
 * @since 2025-01-15
 */
public class RestartChatAppWsCommandTest {
    private final AppChatService appChatService = mock(AppChatService.class);

    private final OperationContext context = mock(OperationContext.class);

    private RestartChatAppWsCommand command;

    @BeforeEach
    public void setUp() {
        this.command = new RestartChatAppWsCommand(this.appChatService);
    }

    @Test
    @DisplayName("测试 restartChat 接口正常运行")
    void testSuccessRunningRestartChat() {
        assertThat(this.command.method()).isEqualTo("restartChat");
        assertThat(this.command.paramClass()).isEqualTo(RestartChatParams.class);
        when(appChatService.restartChat(any(), any(), any())).thenReturn(Choir.just("111"));
        RestartChatParams params = RestartChatParams.builder()
                .tenantId("123")
                .currentInstanceId("456")
                .additionalContext(new HashMap<>())
                .name("aa")
                .account("bb")
                .build();
        Choir<Object> result = this.command.execute(context, params);
        assertThat(result.blockAll()).hasSize(1).contains("111");
    }
}
