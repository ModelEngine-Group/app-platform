/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.po;

import modelengine.fit.jober.aipp.aop.LocaleField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AppBuilder的App结构体
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderAppPo {
    private String id;
    @LocaleField
    private String name;
    private String tenantId;
    private String configId;
    private String flowGraphId;
    private String type;
    private String createBy;
    private String updateBy;
    private String version;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    @LocaleField
    private String attributes;
    private String path;
    private String state;
    private String appBuiltType;
    private String appCategory;
    private String appType;

    // 新增字段.
    private String appId;
    private String appSuiteId;
    private Boolean isActive;
    private String status;
    private String uniqueName;
    private LocalDateTime publishAt;
}
