/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.dynamicform.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * 表单信息
 *
 * @author 熊以可
 * @since 2023-12-13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicFormEntity {
    @Property(description = "表单ID")
    private String id;

    @Property(description = "表单版本")
    private String version;

    @Property(description = "用户ID")
    private String tenantId;

    @Property(description = "表单名称")
    private String formName;

    @Property(description = "表单创建时间")
    private String createTime;

    @Property(description = "表单创建用户")
    private String createUser;

    @Property(description = "表单更新时间")
    private String updateTime;

    @Property(description = "表单更新用户")
    private String updateUser;
}
