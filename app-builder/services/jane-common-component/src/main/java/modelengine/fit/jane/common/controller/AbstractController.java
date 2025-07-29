/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.common.controller;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jane.task.gateway.User;
import modelengine.jade.authentication.context.HttpRequestUtils;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;

import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.authentication.context.HttpRequestUtils;
import modelengine.jade.authentication.context.UserContext;
import modelengine.jade.authentication.context.UserContextHolder;

/**
 * 为 Controller 提供工具方法。
 *
 * @author 梁济时
 * @since 2023-08-08
 */
public abstract class AbstractController {
    private final Authenticator authenticator;

    public AbstractController(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    private static String getSourcePlatform(HttpClassicServerRequest request) {
        return request.headers().first("SourcePlatform").orElse(StringUtils.EMPTY);
    }

    /**
     * 获取操作上下文。
     *
     * @param request 表示 HTTP 请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @return 表示操作上下文的 {@link OperationContext}。
     */
    public OperationContext contextOf(HttpClassicServerRequest request, String tenantId) {
        UserContext userContext = UserContextHolder.get();
        String ip = (userContext == null) ? HttpRequestUtils.getUserIp(request) : userContext.getIp();
        String language =
                (userContext == null) ? HttpRequestUtils.getAcceptLanguages(request) : userContext.getLanguage();
        User user = this.authenticator.authenticate(request);
        if (user == null) {
            return new OperationContext(tenantId, null, null, null, null, null, ip, getSourcePlatform(request),
                    language);
        }
        return new OperationContext(tenantId, user.fqn(), null, user.account(), null, user.name(), ip,
                getSourcePlatform(request), language);
    }
}
