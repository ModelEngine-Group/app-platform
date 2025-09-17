/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.po;

import modelengine.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 应用信息持久化类。
 *
 * @author 陈潇文
 * @since 2025-05-29
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAppInfoAndCollectionPo {
    @Property(description = "collection id")
    private Long id;

    @Property(description = "app id")
    private String appId;

    @Property(description = "user info")
    private String userInfo;

    @Property(description = "name")
    private String name;

    @Property(description = "tenant id")
    private String tenantId;

    @Property(description = "config id")
    private String configId;

    @Property(description = "flow graph id")
    private String flowGraphId;

    @Property(description = "type")
    private String type;

    @Property(description = "create by")
    private String createBy;

    @Property(description = "update by")
    private String updateBy;

    @Property(description = "version")
    private String version;

    @Property(description = "create at")
    private LocalDateTime createAt;

    @Property(description = "update at")
    private LocalDateTime updateAt;

    @Property(description = "attributes")
    private String attributes;

    @Property(description = "state")
    private String state;
}
