/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.filter.support;

import static modelengine.jade.authentication.context.HttpRequestUtils.AUTH_TOKEN_KEY;
import static modelengine.jade.authentication.context.HttpRequestUtils.CSRF_TOKEN_KEY;
import static modelengine.jade.authentication.context.HttpRequestUtils.REAL_IP_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.http.Cookie;
import modelengine.fit.http.protocol.support.DefaultMessageHeaders;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.HttpServerFilterChain;
import modelengine.fit.http.support.DefaultCookieCollection;
import modelengine.jade.authentication.AuthenticationService;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link LoginFilter} 请求上下文的测试集。
 *
 * @author 陈镕希
 * @since 2026-08-25
 */
class LoginFilterContextTest {
    @Test
    @DisplayName("登录过滤器保存认证 Cookie 和真实 IP 并在请求后清理上下文")
    void shouldSetAndClearAuthenticationContext() {
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        HttpClassicServerRequest request = mock(HttpClassicServerRequest.class);
        HttpClassicServerResponse response = mock(HttpClassicServerResponse.class);
        HttpServerFilterChain chain = mock(HttpServerFilterChain.class);
        DefaultMessageHeaders headers = new DefaultMessageHeaders();
        headers.add(REAL_IP_KEY, "203.0.113.10");
        DefaultCookieCollection cookies = new DefaultCookieCollection();
        cookies.add(Cookie.builder().name(AUTH_TOKEN_KEY).value("auth-token").build());
        cookies.add(Cookie.builder().name(CSRF_TOKEN_KEY).value("csrf-token").build());
        when(request.headers()).thenReturn(headers);
        when(request.cookies()).thenReturn(cookies);
        when(request.requestUri()).thenReturn("/knowledge-manager/list/repos");
        when(authenticationService.getUserName(request)).thenReturn("user");
        doAnswer(invocation -> {
            UserContext context = UserContextHolder.get();
            assertThat(context).isNotNull();
            assertThat(context.getName()).isEqualTo("user");
            assertThat(context.getIp()).isEqualTo("203.0.113.10");
            assertThat(context.getAuthToken()).isEqualTo("auth-token");
            assertThat(context.getCsrfToken()).isEqualTo("csrf-token");
            return null;
        }).when(chain).doFilter(request, response);

        new LoginFilter(authenticationService).doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(UserContextHolder.get()).isNull();
    }
}
