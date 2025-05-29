/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AppBuilder组件结构体
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderComponentPo {
    private String id;
    private String name;
    private String type;
    private String description;
    private String formId;
    private String serviceId;
    private String tenantId;
    private String createBy;
    private String updateBy;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
