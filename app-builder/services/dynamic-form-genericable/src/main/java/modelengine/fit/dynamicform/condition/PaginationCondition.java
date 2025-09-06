/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.dynamicform.condition;

import lombok.Data;
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fitframework.annotation.Property;

/**
 * 分页条件
 *
 * @author 熊以可
 * @since 2023/12/13
 */
@Data
public class PaginationCondition {
    @Property(description = "页码(1开始)", example = "1")
    @RequestQuery(name = "pageNum", required = false)
    private Integer pageNum = 1;

    @Property(description = "每页大小", example = "10")
    @RequestQuery(name = "pageSize", required = false)
    private Integer pageSize = 10;
}
