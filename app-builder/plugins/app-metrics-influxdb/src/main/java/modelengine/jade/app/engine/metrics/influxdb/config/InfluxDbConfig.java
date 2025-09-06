/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.config;

import lombok.Data;
import modelengine.fitframework.annotation.AcceptConfigValues;
import modelengine.fitframework.annotation.Component;

/**
 * InfluxDb 连接配置类。
 *
 * @author 高嘉乐
 * @since 2024-12-18
 */
@Data
@Component
@AcceptConfigValues("appengine.metrics.influxdb")
public class InfluxDbConfig {
    private String url;
    private String username;
    private String password;
    private String database;
}