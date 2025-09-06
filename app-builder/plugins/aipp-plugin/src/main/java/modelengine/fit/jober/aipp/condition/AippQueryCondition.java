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

/**
 * Aipp查询条件集
 *
 * @author 刘信宏
 * @since 2023-12-08
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippQueryCondition {
    @RequestQuery(name = "name", required = false)
    private String name;
    @RequestQuery(name = "status", required = false)
    private String status;
    @RequestQuery(name = "version", required = false)
    private String version;
    @RequestQuery(name = "creator", required = false)
    private String creator;
    @Property(description = "排序条件,支持字段:create_at/update_at",
            example = "create_at")
    @RequestQuery(name = "sort", required = false, defaultValue = "update_at")
    private String sort;
    @Property(description = "排序方向,descend表示降序，ascend表示升序",
            example = "descend")
    @RequestQuery(name = "order", required = false, defaultValue = "descend")
    private String order;
}
