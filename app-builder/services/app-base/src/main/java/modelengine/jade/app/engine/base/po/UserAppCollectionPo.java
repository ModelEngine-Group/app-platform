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

/**
 * Aipp用户应用收藏持久化类
 *
 * @since 2024-5-25
 *
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAppCollectionPo {
    @Property(description = "collection id")
    private Long id;

    @Property(description = "app id")
    private String appId;

    @Property(description = "user info")
    private String userInfo;
}
