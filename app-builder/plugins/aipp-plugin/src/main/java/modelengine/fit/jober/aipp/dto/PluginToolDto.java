/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import modelengine.jade.store.entity.transfer.PluginToolData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 表示ToolData列表和总数的Dto
 *
 * @author 姚江
 * @since 2024-06-28
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PluginToolDto {
    private List<PluginToolData> pluginToolData;
    private int total;
}
