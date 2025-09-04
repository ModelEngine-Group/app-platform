/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jade.aipp.domain.division.aop;

import static modelengine.fit.jade.aipp.domain.division.code.DomainDivisionRetCode.USER_GROUP_EXCHANGE_ERROR;
import static modelengine.fit.jade.aipp.domain.division.code.DomainDivisionRetCode.USER_GROUP_NOT_EXIST;

import modelengine.fit.http.client.HttpClassicClient;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.client.HttpClassicClientRequest;
import modelengine.fit.http.client.HttpClientException;
import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.jade.aipp.domain.division.UserGroup;
import modelengine.fit.jade.aipp.domain.division.entity.UserInfo;
import modelengine.fit.jade.aipp.domain.division.entity.UserInfoHolder;
import modelengine.fitframework.exception.ClientException;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;
import modelengine.jade.common.exception.ModelEngineException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 表示资源的切面
 *
 * @author 邬涨财
 * @since 2025-08-15
 */
public abstract class SourceAspect {
    private final HttpClassicClientFactory httpClientFactory;
    private final LazyLoader<HttpClassicClient> httpClient;
    private final String allGroupUrl;

    public SourceAspect(HttpClassicClientFactory httpClientFactory,
            String allGroupUrl) {
        this.httpClientFactory = httpClientFactory;
        this.httpClient = new LazyLoader<>(this::getHttpClient);
        this.allGroupUrl = allGroupUrl;
    }

    private HttpClassicClient getHttpClient() {
        Map<String, Object> custom = MapBuilder.<String, Object>get()
                .put("client.http.secure.ignore-trust", true)
                .put("client.http.secure.ignore-hostname", true)
                .build();
        return this.httpClientFactory.create(HttpClassicClientFactory.Config.builder().custom(custom).build());
    }

    /**
     * 用户信息清理。
     */
    protected void clear() {
        UserInfo userInfo = UserInfoHolder.get();
        if (userInfo != null) {
            UserInfoHolder.remove();
        }
    }

    /**
     * 获取用户名。
     *
     * @return 表示用户名的 {@link String}。
     */
    public String getUserName() {
        UserContext userContext = UserContextHolder.get();
        if (userContext == null) {
            throw new ModelEngineException(USER_GROUP_NOT_EXIST);
        }
        return userContext.getName();
    }

    /**
     * 获取用户组。
     *
     * @return 表示用户组的 {@link UserGroup}。
     */
    public UserGroup getUserGroup(String username) {
        String fullUrl = StringUtils.format(this.allGroupUrl, username);
        HttpClassicClientRequest request =
                this.httpClient.get().createRequest(HttpRequestMethod.GET, fullUrl);
        try {
            Object response = this.httpClient.get().exchangeForEntity(request, Object.class);
            ParameterizedType parameterizedType = TypeUtils.parameterized(List.class, new Type[] {UserGroup.class});
            List<UserGroup> userGroups = ObjectUtils.toCustomObject(response, parameterizedType);
            if (CollectionUtils.isEmpty(userGroups)) {
                throw new ModelEngineException(USER_GROUP_NOT_EXIST);
            }
            return userGroups.get(0);
        } catch (HttpClientException | ClientException ex) {
            throw new ModelEngineException(USER_GROUP_EXCHANGE_ERROR, ex);
        }
    }
}
