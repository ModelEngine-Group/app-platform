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

import java.util.Map;

/**
 * Aipp详情
 *
 * @author 刘信宏
 * @since 2023-12-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AippDetailDto extends AippOverviewDto {
    @Property(description = "流程视图定义", name = "flow_view_data")
    private Map<String, Object> flowViewData;

    @Property(description = "aipp 描述")
    private String description;

    @Property(description = "aipp 头像")
    private String icon;
}
