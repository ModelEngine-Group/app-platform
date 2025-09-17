/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.filter.support;

import modelengine.fit.http.protocol.HttpRequestMethod;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fit.http.server.HttpServerFilter;
import modelengine.fit.http.server.HttpServerFilterChain;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.jade.authentication.AuthenticationService;
import modelengine.jade.authentication.context.HttpRequestUtils;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表示获取用户登录信息 http 请求过滤器。
 *
 * @author 陈潇文
 * @since 2024-07-30
 */
@Component
public class LoginFilter implements HttpServerFilter {
    private final AuthenticationService authenticationService;
    private static final Logger log = Logger.get(LoginFilter.class);

    /**
     * 用用户认证服务 {@link AuthenticationService} 构造 {@link LoginFilter}。
     *
     * @param authenticationService 表示用户认证服务的 {@link AuthenticationService}。
     */
    public LoginFilter(AuthenticationService authenticationService) {
        this.authenticationService = Validation.notNull(authenticationService, "The auth service cannot be null.");
    }

    @Override
    public String name() {
        return "LoginFilter";
    }

    @Override
    public int priority() {
        return Order.HIGHEST;
    }

    @Override
    public List<String> matchPatterns() {
        return Collections.singletonList("/**");
    }

    @Override
    public List<String> mismatchPatterns() {
        return Arrays.asList("/api/app/v1/**", "/v1/api/guest/**");
    }

    @Override
    public void doFilter(HttpClassicServerRequest request, HttpClassicServerResponse response,
            HttpServerFilterChain chain) {
        if (isExcludeUrl(request)) {
            chain.doFilter(request, response);
        }
        UserContext operationContext = new UserContext(this.authenticationService.getUserName(request),
                HttpRequestUtils.getUserIp(request),
                HttpRequestUtils.getAcceptLanguages(request));
        UserContextHolder.apply(operationContext, () -> chain.doFilter(request, response));
    }

    @Override
    public Scope scope() {
        return Scope.GLOBAL;
    }

    /**
     * 仅仅打开文件下载接口的认证
     * @param request 用户请求
     * @return 是否例外
     */
    private boolean isExcludeUrl(HttpClassicServerRequest request) {
        final String downloadUrl = "/v1/api/31f20efc7e0848deab6a6bc10fc3021e/file?";
        HttpRequestMethod method = request.method();
        String uri = request.requestUri();
        log.info("uri : {}", uri);
        return uri.contains(downloadUrl) && method.name().equals(HttpRequestMethod.GET.name());
    }
}
