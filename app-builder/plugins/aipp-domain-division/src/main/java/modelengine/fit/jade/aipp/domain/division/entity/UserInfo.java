/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jade.aipp.domain.division.entity;

import lombok.Builder;
import lombok.Data;

/**
 * 用户信息
 *
 * @author 邬涨财
 * @since 2025-08-12
 */
@Data
@Builder
public class UserInfo {
    private String username;
    private String userGroupId;
}
