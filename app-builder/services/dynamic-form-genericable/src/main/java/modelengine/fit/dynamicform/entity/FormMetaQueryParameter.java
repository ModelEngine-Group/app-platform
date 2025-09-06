/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.dynamicform.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表单元数据查询
 *
 * @author 夏斐
 * @since 2023/12/13
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FormMetaQueryParameter {
    /**
     * 表单id
     * 必填
     */
    private String formId;

    /**
     * 表单版本
     * 必填
     */
    private String version;
}
