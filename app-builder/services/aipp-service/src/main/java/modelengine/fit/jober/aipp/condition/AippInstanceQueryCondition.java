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
 * Aipp实例排序条件
 *
 * @author 刘信宏
 * @since 2023-12-08
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AippInstanceQueryCondition {
    @RequestQuery(name = "creator", required = false)
    private String creator;

    @RequestQuery(name = "aipp_instance_name", required = false)
    private String aippInstanceName;

    @Property(description = "排序条件,支持字段:start_time/end_time", example = "start_time")
    @RequestQuery(name = "sort", required = false, defaultValue = "start_time")
    private String sort;

    @Property(description = "排序方向,descend表示降序，ascend表示升序", example = "descend")
    @RequestQuery(name = "order", required = false, defaultValue = "descend")
    private String order;
}
