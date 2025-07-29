/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.dynamicform.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * PageResponse 分页返回
 *
 * @author 熊以可
 * @since 2023/10/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private Long total;

    private List<T> items;
}
