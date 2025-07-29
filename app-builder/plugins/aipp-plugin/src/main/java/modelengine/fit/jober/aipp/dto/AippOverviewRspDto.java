/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * Aipp列表一览返回结构
 *
 * @author 熊以可
 * @since 2024-03-01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AippOverviewRspDto extends AippOverviewDto {
    @Property(description = "aipp 草稿版本", example = "1.0.1", name = "draft_version")
    private String draftVersion;
}
