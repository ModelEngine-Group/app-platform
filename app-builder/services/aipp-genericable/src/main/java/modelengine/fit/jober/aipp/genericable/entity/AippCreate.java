/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.genericable.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * 创建Aipp响应体实体类对象
 *
 * @author 邬涨财
 * @since 2024-05-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AippCreate {
    @Property(description = "aipp id")
    private String aippId;

    @Property(description = "aipp version")
    private String version;

    @Property(description = "tool unique name")
    private String toolUniqueName;

    @Property(description = "app id")
    private String appId;
}
