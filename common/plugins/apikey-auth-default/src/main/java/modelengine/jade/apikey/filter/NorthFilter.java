/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.jade.apikey.filter;

import modelengine.fit.http.protocol.HttpResponseStatus;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.HttpServerFilter;
import modelengine.fit.http.server.HttpServerFilterChain;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.jade.apikey.ApikeyAuthService;
import modelengine.jade.authentication.context.HttpRequestUtils;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;

import java.util.Collections;
import java.util.List;

/**
 * 用于北向接口的过滤器类。
 *
 * @author 陈潇文
 * @since 2025-07-07
 */
@Component
public class NorthFilter implements HttpServerFilter {
    private static final Logger log = Logger.get(NorthFilter.class);

    private final ApikeyAuthService apikeyAuthService;
    private final String apikey;
    private final String userName;

    /**
     * 用 apikey 鉴权服务 {@link ApikeyAuthService} 构造 {@link NorthFilter}。
     *
     * @param apikeyAuthService 表示 apikey 鉴权服务的 {@link ApikeyAuthService}。
     * @param apikey 表示默认 apikey 的 {@link String}。
     * @param userName 表示默认用户名的 {@link String}。
     */
    public NorthFilter(ApikeyAuthService apikeyAuthService, @Value("${apikey}") String apikey,
            @Value("${userName}") String userName) {
        this.apikeyAuthService = Validation.notNull(apikeyAuthService, "The auth service cannot be null.");
        this.apikey = apikey;
        this.userName = userName;
    }

    @Override
    public String name() {
        return "NorthFilter";
    }

    @Override
    public int priority() {
        return Order.HIGHEST;
    }

    @Override
    public List<String> matchPatterns() {
        return Collections.singletonList("/api/app/v1/**");
    }

    @Override
    public List<String> mismatchPatterns() {
        return Collections.emptyList();
    }

    @Override
    public void doFilter(HttpClassicServerRequest request, HttpClassicServerResponse response,
            HttpServerFilterChain chain) {

        if (!this.apikeyAuthService.authApikeyInfo(this.apikey)) {
            // 认证失败，返回 401 错误
            response.statusCode(HttpResponseStatus.UNAUTHORIZED.statusCode());
            log.error("Authentication failed: Token is null or invalid.");
            response.send();
            return;
        }

        UserContext operationContext = new UserContext(this.userName,
                HttpRequestUtils.getUserIp(request),
                HttpRequestUtils.getAcceptLanguages(request));
        UserContextHolder.apply(operationContext, () -> chain.doFilter(request, response));
    }

    @Override
    public Scope scope() {
        return Scope.GLOBAL;
    }
}
