/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.Range;

/**
 * 分页条件
 *
 * @author 刘信宏
 * @since 2023-12-08
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationCondition {
    @Property(description = "页码(1开始)", example = "1")
    @RequestQuery(name = "pageNum", required = false, defaultValue = "1")
    @Range(min = 1, max = Integer.MAX_VALUE, message = "页码从1开始")
    private int pageNum;

    @Property(description = "每页大小", example = "10")
    @RequestQuery(name = "pageSize", required = false, defaultValue = "10")
    @Range(min = 1, max = 300, message = "每页尺寸范围[1, 300]")
    private int pageSize;

    /**
     * 获取偏移量
     *
     * @return 偏移量
     */
    public long getOffset() {
        return ((pageNum - 1) * (long) pageSize);
    }
}
