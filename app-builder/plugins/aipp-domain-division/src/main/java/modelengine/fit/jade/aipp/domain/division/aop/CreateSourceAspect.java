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
import modelengine.fitframework.util.StringUtils;

/**
 * 表示创建资源的切面
 *
 * @author 邬涨财
 * @since 2025-08-12
 */
@Aspect(scope = Scope.GLOBAL)
@Component
public class CreateSourceAspect extends SourceAspect {
    private final String allGroupId;
    private final String allGroupAliasId;
    private final boolean isEnableDomainDivision;

    public CreateSourceAspect(@Value("${domain-division.all-group.id}") final String allGroupId,
            @Value("${domain-division.all-group.alias.id}") final String allGroupAliasId,
            HttpClassicClientFactory httpClientFactory,
            @Value("${domain-division.host}") final String allGroupHost,
            @Value("${domain-division.path}") final String allGroupPath,
            @Value("${domain-division.isEnable}") boolean isEnableDomainDivision) {
        super(httpClientFactory, allGroupHost + allGroupPath);
        this.allGroupId = allGroupId;
        this.allGroupAliasId = allGroupAliasId;
        this.isEnableDomainDivision = isEnableDomainDivision;
    }

    private UserInfo buildUserInfo(String username, String userGroupId) {
        return UserInfo.builder().username(username).userGroupId(userGroupId).build();
    }

    /**
     * 资源创建前的切面处理。
     *
     * @param joinPoint 表示连接点的 {@link JoinPoint}。
     */
    @Before("@annotation(modelengine.fit.jade.aipp.domain.division.annotation.CreateSource)")
    public void beforeCreate(JoinPoint joinPoint) {
        if (!this.isEnableDomainDivision) {
            return;
        }
        String username = this.getUserName();
        UserGroup userGroup = this.getUserGroup(username);
        String id = userGroup.getId();
        if (StringUtils.equals(this.allGroupAliasId, id)) {
            id = this.allGroupId;
        }
        UserInfo userInfo = this.buildUserInfo(username, id);
        UserInfoHolder.set(userInfo);
    }

    /**
     * 资源创建后的切面处理。
     *
     * @param joinPoint 表示连接点的 {@link JoinPoint}。
     */
    @After("@annotation(modelengine.fit.jade.aipp.domain.division.annotation.CreateSource)")
    public void afterCreate(JoinPoint joinPoint) {
        this.clear();
    }

    /**
     * 资源创建出现异常后的切面处理。
     *
     * @param joinPoint 表示连接点的 {@link JoinPoint}。
     */
    @AfterThrowing("@annotation(modelengine.fit.jade.aipp.domain.division.annotation.CreateSource)")
    public void afterCreateThrowing(JoinPoint joinPoint) {
        this.clear();
    }
}
