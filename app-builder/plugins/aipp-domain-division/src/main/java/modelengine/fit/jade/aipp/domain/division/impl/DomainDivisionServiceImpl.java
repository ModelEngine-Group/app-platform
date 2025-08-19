/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */
package modelengine.fit.jade.aipp.domain.division.impl;

import modelengine.fit.jade.aipp.domain.division.entity.UserInfoHolder;
import modelengine.fit.jade.aipp.domain.division.service.DomainDivisionService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

/**
 * 分域服务的实现类
 *
 * @author 邬涨财
 * @since 2025-08-13
 */
@Component
public class DomainDivisionServiceImpl implements DomainDivisionService {
    @Override
    @Fitable
    public String getUserGroupId() {
        if (UserInfoHolder.get() == null) {
            return null;
        }
        return UserInfoHolder.get().getUserGroupId();
    }
}
