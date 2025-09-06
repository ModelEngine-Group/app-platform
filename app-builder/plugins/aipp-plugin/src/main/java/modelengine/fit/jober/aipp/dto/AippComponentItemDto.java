/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import modelengine.fitframework.annotation.Property;

import java.util.List;

/**
 * Aipp的组件信息
 *
 * @author 夏斐
 * @since 2023/12/11
 */
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippComponentItemDto {
    @Property(description = "类型", name = "type")
    private String type;

    @Property(description = "名称", name = "name")
    private String name;

    @Property(description = "图标", name = "icon")
    private String icon;

    @Property(description = "描述", name = "description")
    private String description;

    @Property(description = "分组", name = "group")
    private List<String> group;
}
