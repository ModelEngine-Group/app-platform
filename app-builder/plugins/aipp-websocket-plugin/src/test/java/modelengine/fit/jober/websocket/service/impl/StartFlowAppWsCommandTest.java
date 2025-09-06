/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.service.AippRunTimeService;
import modelengine.fit.jober.websocket.dto.StartFlowChatParams;

import modelengine.fitframework.flowable.Choir;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

/**
 * {@link StartFlowAppWsCommand} 的测试类。
 *
 * @author 曹嘉美
 * @since 2025-01-15
 */
public class StartFlowAppWsCommandTest {
    private final AippRunTimeService aippRunTimeService = mock(AippRunTimeService.class);
    private final OperationContext context = mock(OperationContext.class);
    private StartFlowAppWsCommand command;

    @BeforeEach
    public void setUp() {
        this.command = new StartFlowAppWsCommand(this.aippRunTimeService);
    }

    @Test
    @DisplayName("测试 startChatWithUserSelectMemory 接口正常运行")
    void testSuccessRunningStartChatWithUserSelectMemory() {
        assertThat(this.command.method()).isEqualTo("startChatWithUserSelectMemory");
        assertThat(this.command.paramClass()).isEqualTo(StartFlowChatParams.class);
        when(aippRunTimeService.startFlowWithUserSelectMemory(any(), any(), any(), anyBoolean())).thenReturn(Choir.just(
                "111"));
        StartFlowChatParams params = StartFlowChatParams.builder()
                .tenantId("123")
                .instanceId("456")
                .initContext(new HashMap<>())
                .name("aa")
                .account("bb")
                .build();
        Choir<Object> result = this.command.execute(context, params);
        assertThat(result.blockAll()).hasSize(1).contains("111");
    }
}
