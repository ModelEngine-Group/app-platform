/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.chat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link ChatQueryParams} 的单元测试。
 *
 * @author 曹嘉美
 * @since 2024-12-31
 */
@DisplayName("测试 ChatQueryParams")
class ChatQueryParamsTest {
    @Test
    @DisplayName("用构建器构建会话应用查询信息类时，返回成功")
    void constructChatQueryParams() {
        ChatQueryParams request = new ChatQueryParams();
        request.setAippId("12345");
        request.setAippVersion("1.0");
        request.setOffset(0);
        request.setLimit(10);
        request.setAppId("app123");
        request.setAppVersion("1.2.3");
        request.setAppState("active");

        assertThat(request).isNotNull();
        assertThat(request.getAippId()).isEqualTo("12345");
        assertThat(request.getAippVersion()).isEqualTo("1.0");
        assertThat(request.getOffset()).isEqualTo(0);
        assertThat(request.getLimit()).isEqualTo(10);
        assertThat(request.getAppId()).isEqualTo("app123");
        assertThat(request.getAppVersion()).isEqualTo("1.2.3");
        assertThat(request.getAppState()).isEqualTo("active");
    }
}
