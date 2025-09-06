/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.service;

import modelengine.jade.app.engine.metrics.po.TimeType;
import modelengine.jade.app.engine.metrics.vo.MetricsAnalysisVo;

/**
 * MetricService类消息处理策略
 *
 * @author 陈霄宇
 * @since 2024/05/21
 */
public interface MetricsAnalysisService {
    /**
     * collect data hourly
     */
    void collectAccessData();

    /**
     * 收集feedback面板数据
     *
     * @param appId 应用id
     * @param timeType 根据时间类型计算起始时间
     * @return feedback面板数据
     */
    MetricsAnalysisVo findMetricsData(String appId, TimeType timeType);
}
