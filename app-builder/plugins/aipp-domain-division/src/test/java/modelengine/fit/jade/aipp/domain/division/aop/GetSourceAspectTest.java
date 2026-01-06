/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jade.aipp.domain.division.aop;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.jade.aipp.domain.division.UserGroup;
import modelengine.fit.jade.aipp.domain.division.entity.UserInfo;
import modelengine.fit.jade.aipp.domain.division.entity.UserInfoHolder;
import modelengine.fitframework.aop.JoinPoint;
import modelengine.fitframework.test.adapter.north.junit5.FitExtension;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;
import modelengine.jade.common.exception.ModelEngineException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.Collections;

/**
 * 获取资源的切面单测类
 *
 * @author 邬涨财
 * @since 2025-09-01
 */
@ExtendWith(FitExtension.class)
public class GetSourceAspectTest {
    @Mock
    private HttpClassicClientFactory httpClientFactory;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private GetSourceAspect getSourceAspect;

    @Mock
    private HttpClassicClient client;

    @BeforeEach
    void setUp() {
        when(this.httpClientFactory.create(any())).thenReturn(this.client);
        HttpClassicClientRequest request = mock(HttpClassicClientRequest.class);
        when(this.client.createRequest(any(), eq("http://localhost//user3"))).thenReturn(request);
        when(this.client.createRequest(any(), eq("http://localhost//admin1"))).thenReturn(request);
        when(this.client.createRequest(any(), eq("http://localhost//admin2"))).thenReturn(request);
        UserGroup userGroup = UserGroup.builder().id("g_user3").build();
        Object response = Collections.singletonList(userGroup);
        when(this.client.exchangeForEntity(eq(request), any())).thenReturn(response);
        this.getSourceAspect = new GetSourceAspect(Arrays.asList("admin1", "admin2"),
                this.httpClientFactory,
                "http:////localhost////{0}",
                "",
                true);
    }

    @Test
    void testBeforeGetUserInGroupUsers() {
        UserContext context = new UserContext("user3", "localhost", "en");
        UserContextHolder.apply(context, () -> {
            this.getSourceAspect.beforeGet(this.joinPoint);
            UserInfo userInfoHolder = UserInfoHolder.get();
            assertEquals("user3", userInfoHolder.getUsername());
            this.getSourceAspect.afterGet(this.joinPoint);
            assertNull(UserInfoHolder.get());
        });
    }

    @Test
    void testBeforeGetUserNotInGroupUsers() {
        UserContext context = new UserContext("user2", "localhost", "en");
        UserContextHolder.apply(context, () -> {
            assertThatThrownBy(() -> this.getSourceAspect.beforeGet(this.joinPoint)).isInstanceOf(ModelEngineException.class);
            assertNull(UserInfoHolder.get());
        });
    }

    @Test
    void testBeforeGetUserInAllGroupUsers() {
        UserContext context = new UserContext("admin1", "localhost", "en");
        UserContextHolder.apply(context, () -> {
            this.getSourceAspect.beforeGet(this.joinPoint);
            assertNotNull(UserInfoHolder.get());
            assertEquals("*allGroupUser*", UserInfoHolder.get().getUserGroupId());
            this.getSourceAspect.afterGet(this.joinPoint);
            assertNull(UserInfoHolder.get());
        });
    }

    @Test
    void testAfterThrowing() {
        this.getSourceAspect.afterGetThrowing(this.joinPoint);
        assertNull(UserInfoHolder.get());
    }
}
