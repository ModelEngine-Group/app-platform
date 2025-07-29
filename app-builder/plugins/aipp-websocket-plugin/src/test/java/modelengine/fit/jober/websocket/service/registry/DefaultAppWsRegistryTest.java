/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.service.registry;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.jober.aipp.service.AppChatService;
import modelengine.fit.jober.websocket.dto.ChatParams;
import modelengine.fit.jober.websocket.service.impl.ChatAppWsCommand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * {@link DefaultAppWsRegistry} 的测试类。
 *
 * @author 曹嘉美
 * @since 2025-01-15
 */
class DefaultAppWsRegistryTest {
    private final DefaultAppWsRegistry registry = new DefaultAppWsRegistry();
    @Mock
    private AppChatService appChatService;

    @BeforeEach
    public void setUp() {
        this.registry.register("appChat", new ChatAppWsCommand(appChatService));
    }

    @Test
    @DisplayName("测试 DefaultAppWsRegistry 类正常运行")
    void testSuccessWhenRegisterAndGetParams() {
        assertThat(this.registry.getCommand("appChat")).isInstanceOf(ChatAppWsCommand.class);
        assertThat(this.registry.getParamClass("appChat")).isEqualTo(ChatParams.class);
        this.registry.unregister("appChat");
        assertThat(this.registry.getCommand("appChat")).isNull();
    }
}