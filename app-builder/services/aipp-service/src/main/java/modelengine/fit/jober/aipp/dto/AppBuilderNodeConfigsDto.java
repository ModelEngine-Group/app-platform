/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 节点配置 dto
 *
 * @author 邬涨财
 * @since 2025-10-20
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderNodeConfigsDto {
    private String appSuiteId;
    private Map<String, Object> nodeConfigs;
}
