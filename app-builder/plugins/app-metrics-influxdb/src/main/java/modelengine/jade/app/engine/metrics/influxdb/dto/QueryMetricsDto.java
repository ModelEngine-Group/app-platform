/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.dto;

import lombok.Data;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.NotBlank;

/**
 * 表示查询指标数据时的查询参数。
 *
 * @author 高嘉乐
 * @since 2025-01-03
 */
@Data
public class QueryMetricsDto {
    @Property(description = "应用名", required = true)
    @NotBlank(message = "App id cannot be empty.")
    private String appId;

    @Property(description = "开始时间戳", required = true)
    private Long startTimestamp;

    @Property(description = "结束时间戳", required = true)
    private Long endTimestamp;

    @Property(description = "部门级别")
    private String departmentLevelName;
}