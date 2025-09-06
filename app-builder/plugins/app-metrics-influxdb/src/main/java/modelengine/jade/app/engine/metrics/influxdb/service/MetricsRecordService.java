/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.service;

import modelengine.fit.jober.aipp.entity.AippFlowData;

import java.util.Map;

/**
 * 指标数据上报服务。
 *
 * @author 高嘉乐
 * @since 2024-12-28
 */
public interface MetricsRecordService {
    /**
     * 应用上报数据。
     *
     * @param aippFlowData 表示模型应用数据的 {@link AippFlowData}。
     */
    void recordMetrics(AippFlowData aippFlowData);

    /**
     * 应用上报数据，上传自定义标签。
     *
     * @param aippFlowData 表示模型应用数据的 {@link AippFlowData}。
     * @param userTags 表示用户自定义标签的 {@link Map}{@code <}{@link String}{@code ,}{@link String}{@code >}。
     */
    void recordMetrics(AippFlowData aippFlowData, Map<String, String> userTags);
}