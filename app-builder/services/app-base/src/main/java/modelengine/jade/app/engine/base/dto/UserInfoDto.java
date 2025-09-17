/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.dto;

import modelengine.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息传输类。
 *
 * @author 陈潇文
 * @since 2024-05-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    @Property(description = "用户信息 id")
    private Long id;

    @Property(description = "用户名")
    private String userName;

    @Property(description = "默认应用")
    private String defaultApp;
}
