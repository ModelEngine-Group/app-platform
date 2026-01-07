/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jade.aipp.domain.division.aop;

import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.jade.aipp.domain.division.UserGroup;
import modelengine.fit.jade.aipp.domain.division.entity.UserInfo;
import modelengine.fit.jade.aipp.domain.division.entity.UserInfoHolder;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Scope;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.aop.JoinPoint;
import modelengine.fitframework.aop.annotation.After;
import modelengine.fitframework.aop.annotation.AfterThrowing;
import modelengine.fitframework.aop.annotation.Aspect;
import modelengine.fitframework.aop.annotation.Before;

import java.util.List;

/**
 * 表示获取资源的切面
 *
 * @author 邬涨财
 * @since 2025-08-12
 */
@Aspect(scope = Scope.GLOBAL)
@Component
public class GetSourceAspect extends SourceAspect {
    private final List<String> allGroupUsers;
    private final boolean isEnableDomainDivision;

    public GetSourceAspect(@Value("${domain-division.all-group.users}") final List<String> allGroupUsers,
            HttpClassicClientFactory httpClientFactory,
            @Value("${domain-division.host}") final String allGroupHost,
            @Value("${domain-division.path}") final String allGroupPath,
            @Value("${domain-division.isEnable}") boolean isEnableDomainDivision) {
        super(httpClientFactory, allGroupHost + allGroupPath);
        this.allGroupUsers = allGroupUsers;
        this.isEnableDomainDivision = isEnableDomainDivision;
    }

    private UserInfo buildUserInfo(String username, String userGroupId) {
        return UserInfo.builder().username(username).userGroupId(userGroupId).build();
    }

    /**
     * 获取资源前的切面处理。
     *
     * @param joinPoint 表示连接点的 {@link JoinPoint}。
     */
    @Before("@annotation(modelengine.fit.jade.aipp.domain.division.annotation.GetSource)")
    public void beforeGet(JoinPoint joinPoint) {
        if (!this.isEnableDomainDivision) {
            return;
        }
        String username = this.getUserName();
        if (this.allGroupUsers != null && this.allGroupUsers.contains(username)) {
            UserInfo userInfo = this.buildUserInfo(username, "*allGroupUser*");
            UserInfoHolder.set(userInfo);
            return;
        }
        UserGroup userGroup = this.getUserGroup(username);
        UserInfo userInfo = this.buildUserInfo(username, userGroup.getId());
        UserInfoHolder.set(userInfo);
    }

    /**
     * 获取资源后的切面处理。
     *
     * @param joinPoint 表示连接点的 {@link JoinPoint}。
     */
    @After("@annotation(modelengine.fit.jade.aipp.domain.division.annotation.GetSource)")
    public void afterGet(JoinPoint joinPoint) {
        this.clear();
    }

    /**
     * 获取资源异常后的切面处理。
     *
     * @param joinPoint 表示连接点的 {@link JoinPoint}。
     */
    @AfterThrowing("@annotation(modelengine.fit.jade.aipp.domain.division.annotation.GetSource)")
    public void afterGetThrowing(JoinPoint joinPoint) {
        this.clear();
    }
}
