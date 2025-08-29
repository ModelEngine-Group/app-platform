/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */
package modelengine.fit.jade.aipp.domain.division.impl;

import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.jade.aipp.domain.division.UserGroup;
import modelengine.fit.jade.aipp.domain.division.aop.SourceAspect;
import modelengine.fit.jade.aipp.domain.division.entity.UserInfoHolder;
import modelengine.fit.jade.aipp.domain.division.service.DomainDivisionService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.util.StringUtils;

import java.util.List;

/**
 * 分域服务的实现类
 *
 * @author 邬涨财
 * @since 2025-08-13
 */
@Component
public class DomainDivisionServiceImpl extends SourceAspect implements DomainDivisionService {
    private final List<String> allGroupUsers;

    public DomainDivisionServiceImpl(@Value("${domain-division.all-group.users}") final List<String> allGroupUsers,
            HttpClassicClientFactory httpClientFactory,
            @Value("${domain-division.host}") final String allGroupHost,
            @Value("${domain-division.path}") final String allGroupPath) {
        super(httpClientFactory, allGroupHost + allGroupPath);
        this.allGroupUsers = allGroupUsers;
    }

    @Override
    @Fitable
    public String getUserGroupId() {
        if (UserInfoHolder.get() == null) {
            return null;
        }
        return UserInfoHolder.get().getUserGroupId();
    }

    @Override
    public boolean validate(List<String> toBeVerifiedIds) {
        String username = this.getUserName();
        if (this.allGroupUsers != null && this.allGroupUsers.contains(username)) {
            return true;
        }
        UserGroup userGroup = this.getUserGroup(username);
        String currUserGroupId = userGroup.getId();
        for(String toBeVerifiedId : toBeVerifiedIds) {
            if (!(StringUtils.equals(toBeVerifiedId, "*") || StringUtils.equals(toBeVerifiedId, currUserGroupId))) {
                return false;
            }
        }
        return true;
    }
}
