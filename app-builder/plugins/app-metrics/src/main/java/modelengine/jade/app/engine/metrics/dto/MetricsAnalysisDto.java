/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.dto;

import modelengine.jade.app.engine.metrics.po.TimeType;
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fitframework.annotation.Property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MetricsAnalyze类消息处理策略
 *
 * @author 陈霄宇
 * @since 2024/05/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricsAnalysisDto {
    @Property(description = "应用id")
    @RequestQuery(name = "appId")
    private String appId;

    @Property(description = "时间")
    @RequestQuery(name = "timeType")
    private TimeType timeType;
}
