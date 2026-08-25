/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.filter.support;

import static modelengine.jade.authentication.context.HttpRequestUtils.REAL_IP_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.protocol.Address;
import modelengine.fit.http.protocol.support.DefaultMessageHeaders;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.jade.authentication.context.HttpRequestUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link HttpRequestUtils} 的测试集。
 *
 * @author 陈镕希
 * @since 2026-08-25
 */
class HttpRequestUtilsTest {
    @Test
    @DisplayName("X-Real-IP 优先于其他 IP 请求头")
    void shouldPreferRealIp() {
        DefaultMessageHeaders headers = new DefaultMessageHeaders();
        headers.add("X-Forwarded-For", "198.51.100.1");
        headers.add(REAL_IP_KEY, "203.0.113.1");

        assertThat(HttpRequestUtils.getUserIp(this.mockRequest(headers, "127.0.0.1")))
                .isEqualTo("203.0.113.1");
    }

    @Test
    @DisplayName("X-Forwarded-For 返回第一个有效 IP")
    void shouldUseFirstKnownForwardedIp() {
        DefaultMessageHeaders headers = new DefaultMessageHeaders();
        headers.add(REAL_IP_KEY, "unknown");
        headers.add("X-Forwarded-For", "unknown, , 198.51.100.2, 198.51.100.3");

        assertThat(HttpRequestUtils.getUserIp(this.mockRequest(headers, "127.0.0.1")))
                .isEqualTo("198.51.100.2");
    }

    @Test
    @DisplayName("请求头为空或 unknown 时使用远端 IP")
    void shouldFallbackToRemoteIp() {
        DefaultMessageHeaders headers = new DefaultMessageHeaders();
        headers.add(REAL_IP_KEY, " ");
        headers.add("X-Forwarded-For", "unknown");
        headers.add("Proxy-Client-IP", "");
        headers.add("WL-Proxy-Client-IP", "unknown");

        assertThat(HttpRequestUtils.getUserIp(this.mockRequest(headers, "127.0.0.1")))
                .isEqualTo("127.0.0.1");
    }

    private HttpClassicServerRequest mockRequest(DefaultMessageHeaders headers, String remoteIp) {
        HttpClassicServerRequest request = mock(HttpClassicServerRequest.class);
        when(request.headers()).thenReturn(headers);
        when(request.remoteAddress()).thenReturn(Address.builder().hostAddress(remoteIp).port(8080).build());
        return request;
    }
}
