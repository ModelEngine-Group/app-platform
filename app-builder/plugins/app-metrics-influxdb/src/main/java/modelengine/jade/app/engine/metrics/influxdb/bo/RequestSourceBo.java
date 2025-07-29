/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.bo;

import lombok.Data;

import org.influxdb.annotation.Column;

/**
 * 请求来源业务对象。
 *
 * @author 高嘉乐
 * @since 2024-12-18
 */
@Data
public class RequestSourceBo {
    @Column(name = "dep_name")
    private String departmentName;

    @Column(name = "count")
    private long count;
}