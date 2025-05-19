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

/**
 * 存放节点信息类。
 *
 * @author 邬涨财
 * @since 2024-05-13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreNodeInfoDto {
    private String name;
    private String type;
    private String uniqueName;
}
