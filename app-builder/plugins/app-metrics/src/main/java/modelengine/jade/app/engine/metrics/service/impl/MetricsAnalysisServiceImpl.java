/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.service.impl;

import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.schedule.annotation.Scheduled;
import modelengine.jade.app.engine.metrics.mapper.ConversationRecordMapper;
import modelengine.jade.app.engine.metrics.mapper.MetricsAccessMapper;
import modelengine.jade.app.engine.metrics.po.ConversationRecordPo;
import modelengine.jade.app.engine.metrics.po.MetricsAccessPo;
import modelengine.jade.app.engine.metrics.po.TimeType;
import modelengine.jade.app.engine.metrics.service.MetricsAnalysisService;
import modelengine.jade.app.engine.metrics.utils.MetaUtils;
import modelengine.jade.app.engine.metrics.vo.MetricsAnalysisVo;
import modelengine.jade.app.engine.metrics.vo.UserAccessVo;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MetricServiceImpl类消息处理策略
 *
 * @author 陈霄宇
 * @since 2024-05-21
 */
@Component
public class MetricsAnalysisServiceImpl implements MetricsAnalysisService {
    @Fit
    private ConversationRecordMapper conversationRecordMapper;

    @Fit
    private MetricsAccessMapper metricsAccessMapper;

    @Fit
    private MetaService metaService;

    /**
     * collect data hourly
     */
    @Scheduled(strategy = Scheduled.Strategy.CRON, value = "0 0 0/1 * * ?")
    @Override
    public void collectAccessData() {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(1);
        List<ConversationRecordPo> metricMessages = conversationRecordMapper.getRecordByTime(startTime, endTime);
        Map<String, Long> totalAccessByApp = metricMessages.stream()
                .collect(Collectors.groupingBy(ConversationRecordPo::getAppId, Collectors.counting()));

        List<MetricsAccessPo> metricsAccessList = totalAccessByApp.entrySet().stream().map(entry -> {
            MetricsAccessPo metricAccess = new MetricsAccessPo();
            metricAccess.setAppId(entry.getKey());
            metricAccess.setTotalAccess(entry.getValue().intValue());
            metricAccess.setCreateTime(endTime);
            return metricAccess;
        }).collect(Collectors.toList());

        metricsAccessMapper.insertMetricAccessBatch(metricsAccessList);
    }

    /**
     * 收集feedback面板数据
     *
     * @param appId 应用id
     * @param timeType 根据时间类型计算起始时间
     * @return feedback面板数据
     */
    @Override
    public MetricsAnalysisVo findMetricsData(String appId, TimeType timeType) {
        List<String> appIds = MetaUtils.getAllPublishedAppId(this.metaService, appId, null);
        return this.getMetricsAnalysisVo(appIds, timeType);
    }

    // 临时先通过迭代方式处理。待后续appId整改后，再做处理
    private MetricsAnalysisVo getMetricsAnalysisVo(List<String> appIds, TimeType timeType) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = calculateStartTime(timeType, now);
        LocalDateTime endTime = calculateEndTime(timeType, now);
        Map<String, Map<String, Object>> basicMetrics =
                conversationRecordMapper.getBasicMetrics(appIds, startTime, endTime);
        if (basicMetrics.containsKey("average_response_time")) {
            Object averageResponseTime = basicMetrics.get("average_response_time").get("value");
            if (averageResponseTime instanceof BigDecimal) {
                basicMetrics.get("average_response_time").put("value", ((BigDecimal) averageResponseTime).longValue());
            }
        }
        Map<String, Object> avgResponseRange = conversationRecordMapper.getAvgResponseRange(appIds, startTime, endTime);
        List<UserAccessVo> topUsers = conversationRecordMapper.getTopUsers(appIds, startTime, endTime);
        List<Map<String, Object>> userAccessData = getUserAccessData(timeType, appIds, startTime, endTime);

        MetricsAnalysisVo metricsAnalysisVO = new MetricsAnalysisVo();
        metricsAnalysisVO.setBasicMetrics(basicMetrics);
        metricsAnalysisVO.setAvgResponseRange(avgResponseRange);
        metricsAnalysisVO.setTopUsers(topUsers);
        metricsAnalysisVO.setUserAccessData(userAccessData);
        return metricsAnalysisVO;
    }

    private List<Map<String, Object>> getUserAccessData(TimeType timeType, List<String> appIds, LocalDateTime startTime,
            LocalDateTime endTime) {
        if (timeType == TimeType.TODAY || timeType == TimeType.YESTERDAY) {
            return metricsAccessMapper.getHourlyAccessData(appIds, startTime, endTime);
        } else {
            return metricsAccessMapper.getDailyAccessData(appIds, startTime, endTime);
        }
    }

    private LocalDateTime calculateStartTime(TimeType timeType, LocalDateTime now) {
        // 根据 TimeType 计算开始时间
        switch (timeType) {
            case TODAY:
                return now.withHour(0).withMinute(0).withSecond(0).withNano(0);
            case YESTERDAY:
                return now.minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case LAST_7_DAYS:
                return now.minusDays(7);
            case LAST_30_DAYS:
                return now.minusDays(30);
            case THIS_WEEK:
                return now.with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case LAST_WEEK:
                return now.minusWeeks(1).with(DayOfWeek.MONDAY).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case THIS_MONTH:
                return now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case LAST_MONTH:
                return now.minusMonths(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            default:
                return now;
        }
    }

    private LocalDateTime calculateEndTime(TimeType timeType, LocalDateTime now) {
        switch (timeType) {
            case YESTERDAY:
                return now.minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            case LAST_WEEK:
                return now.minusWeeks(1)
                        .with(DayOfWeek.SUNDAY)
                        .withHour(23)
                        .withMinute(59)
                        .withSecond(59)
                        .withNano(999999999);
            case LAST_MONTH:
                return now.minusMonths(1)
                        .withDayOfMonth(now.minusMonths(1).toLocalDate().lengthOfMonth())
                        .withHour(23)
                        .withMinute(59)
                        .withSecond(59)
                        .withNano(999999999);
            default:
                return now;
        }
    }
}
