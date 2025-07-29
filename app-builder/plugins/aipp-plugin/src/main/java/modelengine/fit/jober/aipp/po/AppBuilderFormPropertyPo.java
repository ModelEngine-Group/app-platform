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

/**
 * AppBuilder表单属性结构体
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppBuilderFormPropertyPo {
    private String id;
    private String formId;
    private String name;
    private String dataType;
    private String appId;
    @LocaleField
    private String defaultValue;
    private String from;
    private String group;
    @LocaleField
    private String description;
    private int index;
}
