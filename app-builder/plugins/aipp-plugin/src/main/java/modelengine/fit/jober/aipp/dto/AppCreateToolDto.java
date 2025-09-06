/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * 创建应用工具DTO
 *
 * @author 邬涨财
 * @since 2024-05-21
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppCreateToolDto {
    private String name;
    private String description;
    private String icon;
    private String greeting;

    @Property(name = "app_type")
    private String appType;
    private String type;
    private String systemPrompt;
}
