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

import java.util.List;

/**
 * 保存应用配置的传输类
 *
 * @author 鲁为
 * @since 2024-10-28
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderSaveConfigDto {
    private List<AppBuilderConfigFormPropertyDto> input;
    private String graph;
}
