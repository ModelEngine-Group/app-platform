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

import java.util.List;

/**
 * Aipp的流程组件信息
 *
 * @author 夏斐
 * @since 2023/12/11
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippFlowComponentDto {
    @Property(description = "分组列表", name = "groups")
    private List<AippComponentGroupDto> groups;

    @Property(description = "组件列表", name = "items")
    private List<AippComponentFlowItemDto> items;
}
