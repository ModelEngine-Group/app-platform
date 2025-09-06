/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.template;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * 根据模板创建应用，以及将应用导出为模板的数据类。
 *
 * @author 方誉州
 * @since 2024-12-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateAppCreateDto {
    @Property(description = "创建的应用或者模板的唯一的 id")
    private String id;

    @Property(description = "应用或模板的头像链接")
    private String icon;

    @Property(description = "创建的应用或者模板的名称")
    private String name;

    @Property(description = "创建的应用或者模板的分类", name = "app_type")
    private String appType;

    @Property(description = "创建的应用或者模板的简介信息")
    private String description;

    @Property(description = "创建的应用或者模板的创建分类", name = "app_built_type")
    @JsonProperty("app_built_type")
    private String appBuiltType;

    @Property(description = "创建的应用或者模板的创建类别", name = "app_category")
    @JsonProperty("app_category")
    private String appCategory;
}
