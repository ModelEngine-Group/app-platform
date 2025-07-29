/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.bo;

import lombok.Data;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.TimeColumn;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
 * 用户访问趋势业务对象。
 *
 * @author 高嘉乐
 * @since 2024-12-18
 */
@Data
public class UserAccessTrendBo {
    @TimeColumn(timeUnit = TimeUnit.MINUTES)
    @Column(name = "time")
    private Instant time;

    @Column(name = "count")
    private long count;
}