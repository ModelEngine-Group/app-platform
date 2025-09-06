/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.po;

import modelengine.fit.jober.aipp.aop.LocaleField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 应用的业务分类类型.
 *
 * @author songyongtan
 * @since 2025-01-04
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppBuilderAppTypePo {
    private String id;
    @LocaleField
    private String name;
    private String tenantId;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
