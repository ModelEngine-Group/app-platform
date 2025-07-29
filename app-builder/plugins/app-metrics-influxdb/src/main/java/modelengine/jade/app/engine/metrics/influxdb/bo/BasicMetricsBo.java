/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.bo;

import lombok.Data;

import org.influxdb.annotation.Column;

/**
 * 基本数据业务对象。
 *
 * @author 高嘉乐
 * @since 2024-12-18
 */
@Data
public class BasicMetricsBo {
    @Column(name = "sum")
    private long sum = 0L;

    @Column(name = "count")
    private long count = 0L;

    @Column(name = "bucket0")
    private long bucket0 = 0L;

    @Column(name = "bucket1")
    private long bucket1 = 0L;

    @Column(name = "bucket2")
    private long bucket2 = 0L;

    @Column(name = "bucket3")
    private long bucket3 = 0L;
}