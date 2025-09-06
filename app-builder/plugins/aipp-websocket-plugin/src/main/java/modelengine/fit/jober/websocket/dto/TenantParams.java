/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import modelengine.fitframework.annotation.Property;

/**
 * 用户基本信息参数。
 *
 * @author 曹嘉美
 * @since 2025-01-20
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TenantParams {
    @Property(description = "租户的唯一标识符", name = "tenant_id")
    private String tenantId;

    @Property(description = "用户姓名", name = "name")
    private String name;

    @Property(description = "用户工号", name = "account")
    private String account;
}
