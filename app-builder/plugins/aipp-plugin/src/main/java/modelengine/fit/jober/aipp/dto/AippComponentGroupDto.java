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
 * 组件分组信息
 *
 * @author 夏斐
 * @since 2023/12/11
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippComponentGroupDto {
    @Property(description = "分组类别", name = "type")
    private String type;

    @Property(description = "分组名称", name = "name")
    private String name;
}
