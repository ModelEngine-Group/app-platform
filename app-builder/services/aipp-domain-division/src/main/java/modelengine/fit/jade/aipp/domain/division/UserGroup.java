/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jade.aipp.domain.division;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户信息
 *
 * @author 邬涨财
 * @since 2025-08-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGroup {
    private String id;
    private String parentId;
    private String name;
    private String description;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
