/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.service;

import modelengine.jade.app.engine.metrics.dto.MetricsFeedbackDto;
import modelengine.jade.app.engine.metrics.vo.MetricsFeedbackVo;
import modelengine.jade.app.engine.metrics.vo.Page;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * MetricsFeedbackService类消息处理策略
 *
 * @author 陈霄宇
 * @since 2024/05/21
 */
public interface MetricsFeedbackService {
    /**
     * 获取以页为单位的feedback数据
     *
     * @param metricsFeedbackDTO 筛选条件
     * @return 以页为单位的feedback数据
     */
    Page<MetricsFeedbackVo> getMetricsFeedback(MetricsFeedbackDto metricsFeedbackDTO);

    /**
     * 导出所有符合条件的数据
     *
     * @param metricsFeedbackDTO 筛选条件
     * @return ByteArrayInputStream
     * @throws IOException 异常捕获
     */
    ByteArrayInputStream export(MetricsFeedbackDto metricsFeedbackDTO) throws IOException;
}
