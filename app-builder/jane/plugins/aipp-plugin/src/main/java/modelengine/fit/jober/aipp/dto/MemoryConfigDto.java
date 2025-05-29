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

import java.util.Map;

/**
 * 历史记录配置相关的 dto 对象
 *
 * @author 邬涨财
 * @since 2024-05-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryConfigDto {
    private Map<String, Object> initContext;
    private String instanceId;
    private String memory;
}
