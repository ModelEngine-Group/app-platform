/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

import java.util.List;

/**
 * The PageResponse
 *
 * @author Varlamova Natalia
 * @since 2023-10-26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    @Property(description = "总量")
    private Long total;

    @Property(description = "标签等拓展含义")
    private String label;

    @Property(description = "列表数据")
    private List<T> items;
}
