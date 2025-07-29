/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * 展示应用模板的详细或简略信息的类。
 *
 * @author 方誉州
 * @since 2024-12-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateInfoDto {
    @Property(description = "应用模板的唯一 id")
    private String id;

    @Property(description = "应用模板的名字")
    private String name;

    @Property(description = "应用模板的标签")
    private String appType;

    @Property(description = "应用模板的分类")
    private String category;

    @Property(description = "应用模板的头像的链接")
    private String icon;

    @Property(description = "应用模板的简介信息")
    private String description;

    @Property(description = "应用模板的创建者")
    private String creator;

    @Property(description = "应用模板的创建者")
    private String appBuiltType;
}
