/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.controller;

import modelengine.jade.app.engine.metrics.dto.MetricsFeedbackDto;
import modelengine.jade.app.engine.metrics.po.TimeType;
import modelengine.jade.app.engine.metrics.service.MetricsAnalysisService;
import modelengine.jade.app.engine.metrics.service.MetricsFeedbackService;
import modelengine.jade.app.engine.metrics.vo.MetricsAnalysisVo;
import modelengine.jade.app.engine.metrics.vo.MetricsFeedbackVo;
import modelengine.jade.app.engine.metrics.vo.Page;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.http.server.HttpClassicServerResponse;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * MetricsController类消息处理策略
 *
 * @author 陈霄宇
 * @since 2024/05/21
 */
@Component
@RequestMapping(path = "/metrics", group = "分析与反馈看板相关接口")
public class MetricsController {
    @Fit
    private MetricsAnalysisService metricsAnalysisService;

    @Fit
    private MetricsFeedbackService metricsFeedbackService;

    /**
     * 获取分析看板数据
     *
     * @param appId 应用id
     * @param timeType 时间枚举类
     * @return 分析看板数据
     */
    @GetMapping(path = "/analysis", description = "获取分析数据接口")
    public MetricsAnalysisVo getAnalysis(
            @RequestParam("appId") String appId, @RequestParam("timeType") TimeType timeType) {
        return metricsAnalysisService.findMetricsData(appId, timeType);
    }

    /**
     * 获取反馈看板数据
     *
     * @param metricsFeedbackDTO 用户输入
     * @return 反馈看板数据
     */
    @PostMapping(path = "/feedback", description = "获取反馈数据接口")
    public Page<MetricsFeedbackVo> getFeedback(@RequestBody MetricsFeedbackDto metricsFeedbackDTO) {
        return metricsFeedbackService.getMetricsFeedback(metricsFeedbackDTO);
    }

    /**
     * 下载反馈看板数据
     *
     * @param response 前端
     * @param metricsFeedbackDTO 用户输入
     * @return 三个推荐问题列表
     * @throws IOException 异常捕获
     */
    @PostMapping(path = "/export", description = "下载反馈数据接口")
    public FileEntity export(HttpClassicServerResponse response, @RequestBody MetricsFeedbackDto metricsFeedbackDTO)
            throws IOException {
        try (ByteArrayInputStream inputStream = metricsFeedbackService.export(metricsFeedbackDTO)) {
            return FileEntity.createAttachment(response, "问答记录.xls", inputStream, inputStream.available());
        }
    }
}
