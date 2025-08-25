/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */
package modelengine.fit.jade.aipp.domain.division.service;

import modelengine.fitframework.annotation.Genericable;

/**
 * 分域服务
 *
 * @author 邬涨财
 * @since 2025-08-13
 */
public interface DomainDivisionService {
    /**
     * 获取用户组 id。
     *
     * @return 表示获取用户组 id 的 {@link String}。
     */
    @Genericable(id = "modelengine.fit.jade.aipp.domain.division.service.get.user.resource.id")
    String getUserGroupId();
}
