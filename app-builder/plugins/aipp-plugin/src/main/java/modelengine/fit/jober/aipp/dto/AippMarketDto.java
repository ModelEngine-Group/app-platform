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
 * Aipp应用市场
 *
 * @author 刘信宏
 * @since 2023-12-22
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AippMarketDto {
    @Property(description = "创建者")
    private String createUser;

    @Property(description = "aipp 名称", example = "aipp")
    private String name;

    @Property(description = "aipp 描述", example = "aipp 编排应用")
    private String description;

    @Property(description = "app 类型")
    private String type;

    @Property(description = "app 发布链接")
    private String site;

    @Property(description = "app id")
    private String appid;

    @Property(description = "app 一级分类")
    private String l1Classification;

    @Property(description = "app 二级分类")
    private String l2Classification;

    @Property(description = "aipp 头像")
    private String icon;
}
