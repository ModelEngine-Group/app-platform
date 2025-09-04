/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;

import modelengine.fit.http.Cookie;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.support.DefaultCookieCollection;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.jade.authentication.impl.AuthenticationServiceImpl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * {@link AuthenticationServiceImpl} 的测试。
 *
 * @since 2024-08-10
 */
@FitTestWithJunit(includeClasses = {

})
public class AuthenticationServiceImplTest {
    private static final String USERNAME_KEY = "username";

    private static final String DEFAULT_USERNAME = "Jade";

    private static final String MODEL_ENGINE_USERNAME = "admin";

    @Test
    void testGetUserName_CookiesNotContain() {
        HttpClassicServerRequest request = Mockito.mock(HttpClassicServerRequest.class);
        Mockito.when(request.cookies()).thenReturn(new DefaultCookieCollection());

        AuthenticationServiceImpl authenticationService = new AuthenticationServiceImpl();

        String userName = authenticationService.getUserName(request);
        assertEquals(DEFAULT_USERNAME, userName);
    }

    @Test
    void testGetUserName_CookiesContain() {
        HttpClassicServerRequest request = Mockito.mock(HttpClassicServerRequest.class);
        Cookie userNameCookie = Cookie.builder().name(USERNAME_KEY).value(MODEL_ENGINE_USERNAME).build();
        DefaultCookieCollection cookies = new DefaultCookieCollection();
        cookies.add(userNameCookie);
        Mockito.when(request.cookies()).thenReturn(cookies);

        AuthenticationServiceImpl authenticationService = new AuthenticationServiceImpl();

        String userName = authenticationService.getUserName(request);
        assertEquals(MODEL_ENGINE_USERNAME, userName);
    }
}