/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.dynamicform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * 表单请求信息
 *
 * @author 熊以可
 * @since 2023/12/13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicFormDto {
    @Property(description = "表单名称")
    private String name;

    @Property(description = "表单Id")
    private String id;

    @Property(description = "表单版本")
    private String version;
}
