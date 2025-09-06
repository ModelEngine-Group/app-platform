/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.controller;

import static modelengine.fitframework.inspection.Validation.lessThan;

import modelengine.jade.app.engine.metrics.influxdb.dto.QueryMetricsDto;
import modelengine.jade.app.engine.metrics.influxdb.service.MetricsAnalysisService;
import modelengine.jade.app.engine.metrics.influxdb.utils.DepartmentLevel;
import modelengine.jade.app.engine.metrics.influxdb.vo.MetricsVo;
import modelengine.jade.app.engine.metrics.influxdb.vo.RequestSourceVo;
import modelengine.jade.app.engine.metrics.influxdb.vo.UserSourceVo;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.validation.Validated;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 指标监控查询接口。
 *
 * @author 高嘉乐
 * @since 2024-12-31
 */
@Component
@RequestMapping(path = "/metrics/v2", group = "指标数据监控相关接口")
public class MetricsController {
    private static final Pattern HEX_STRING_PATTERN = Pattern.compile("^[0-9a-fA-F]+$");

    private final MetricsAnalysisService metricsAnalysisService;

    public MetricsController(MetricsAnalysisService metricsAnalysisService) {
        this.metricsAnalysisService = metricsAnalysisService;
    }

    /**
     * 获取指标数据接口。
     *
     * @param queryMetricsDto 表示查询数据传输对象的 {@link QueryMetricsDto}。
     * @return 表示指标数据视图的 {@link MetricsVo}。
     */
    @GetMapping(path = "/analysis", description = "获取指标数据接口")
    public MetricsVo getMetric(@RequestBean @Validated QueryMetricsDto queryMetricsDto) {
        verifyArgument(queryMetricsDto, false);
        return this.metricsAnalysisService.getMetrics(queryMetricsDto.getAppId(),
                queryMetricsDto.getStartTimestamp(),
                queryMetricsDto.getEndTimestamp());
    }

    /**
     * 获取用户来源接口。
     *
     * @param queryMetricsDto 表示查询数据传输对象的 {@link QueryMetricsDto}。
     * @return 表示用户来源数据视图的 {@link List}{@code <}{@link UserSourceVo}{@code >}。
     */
    @GetMapping(path = "/analysis_user", description = "获取用户来源数据接口")
    public List<UserSourceVo> getUserSource(@RequestBean @Validated QueryMetricsDto queryMetricsDto) {
        verifyArgument(queryMetricsDto, true);
        return this.metricsAnalysisService.getUserSource(queryMetricsDto.getAppId(),
                queryMetricsDto.getStartTimestamp(),
                queryMetricsDto.getEndTimestamp(),
                DepartmentLevel.getLevel(queryMetricsDto.getDepartmentLevelName()));
    }

    /**
     * 获取请求来源接口。
     *
     * @param queryMetricsDto 表示查询数据传输对象的 {@link QueryMetricsDto}。
     * @return 表示请求来源数据视图的 {@link List}{@code <}{@link RequestSourceVo}{@code >}。
     */
    @GetMapping(path = "/analysis_request", description = "获取请求来源数据接口")
    public List<RequestSourceVo> getRequestSource(@RequestBean @Validated QueryMetricsDto queryMetricsDto) {
        verifyArgument(queryMetricsDto, true);
        return this.metricsAnalysisService.getRequestSource(queryMetricsDto.getAppId(),
                queryMetricsDto.getStartTimestamp(),
                queryMetricsDto.getEndTimestamp(),
                DepartmentLevel.getLevel(queryMetricsDto.getDepartmentLevelName()));
    }

    private static void verifyArgument(QueryMetricsDto queryMetricsDto, boolean requireDepartmentLevelName) {
        verifyAppId(queryMetricsDto.getAppId());
        verifyTimestamp(queryMetricsDto.getStartTimestamp(), queryMetricsDto.getEndTimestamp());
        if (requireDepartmentLevelName) {
            verifyDepartmentLevelName(queryMetricsDto.getDepartmentLevelName());
        }
    }

    private static void verifyDepartmentLevelName(String depLevelName) {
        if (DepartmentLevel.getLevel(depLevelName).equals(DepartmentLevel.DEP_INVALID)) {
            throw new IllegalArgumentException("DepartmentLevelName is invalid");
        }
    }

    private static void verifyTimestamp(long startTimestamp, long endTimestamp) {
        lessThan(startTimestamp,
                endTimestamp,
                "Start timestamp must greater than end timestamp. [startTimestamp={0}, endTimestamp={1}].]",
                startTimestamp,
                endTimestamp);
    }

    private static void verifyAppId(String appId) {
        if (!HEX_STRING_PATTERN.matcher(appId).matches()) {
            throw new IllegalArgumentException(StringUtils.format("App id is invalid. [appId={0}]", appId));
        }
    }
}