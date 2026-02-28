/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Max;
/**
 * 表示分页查询参数的实体。
 *
 * @author 易文渊
 * @since 2024-07-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageQueryParam {
    @Property(description = "页码", example = "1", defaultValue = "1")
    @Positive(message = "页码从1开始")
    private Integer pageIndex;

    @Property(description = "页面大小", example = "10", defaultValue = "10")
    @Min(value = 1, message = "每页尺寸范围[1, 100]")
    @Max(value = 100, message = "每页尺寸范围[1, 100]")
    private Integer pageSize;

    /**
     * 获取查询偏移量。
     *
     * @return 表示查询偏移量的 {@code int}。
     */
    public int getOffset() {
        return (this.pageIndex - 1) * this.pageSize;
    }

    /**
     * 获取查询限定数量大小。
     *
     * @return 表示查询限定数量大小的 {@code int}。
     */
    public int getLimit() {
        return this.pageSize;
    }
}