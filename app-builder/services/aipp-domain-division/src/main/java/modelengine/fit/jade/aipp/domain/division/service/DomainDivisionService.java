/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */
package modelengine.fit.jade.aipp.domain.division.service;

import modelengine.fitframework.annotation.Genericable;

import java.util.List;

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


    /**
     * 用户组集合校验。
     *
     * @param toBeVerifiedIds 表示需要校验的用户组唯一标识的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示获取用户组校验结果的 {@code String}。
     */
    @Genericable(id = "modelengine.fit.jade.aipp.domain.division.service.validate")
    boolean validate(List<String> toBeVerifiedIds);
}
