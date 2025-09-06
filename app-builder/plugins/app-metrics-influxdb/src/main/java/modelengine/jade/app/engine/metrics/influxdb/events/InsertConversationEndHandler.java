/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.events;

import modelengine.fit.jober.aipp.events.InsertConversationEnd;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.event.EventHandler;
import modelengine.jade.app.engine.metrics.influxdb.service.MetricsRecordService;

/**
 * 插入历史对话结束事件 handler。
 *
 * @author 高嘉乐
 * @since 2025-01-07
 */
@Component
public class InsertConversationEndHandler implements EventHandler<InsertConversationEnd> {
    private final MetricsRecordService metricsRecordService;

    public InsertConversationEndHandler(MetricsRecordService metricsRecordService) {
        this.metricsRecordService = metricsRecordService;
    }

    @Override
    public void handleEvent(InsertConversationEnd event) {
        this.metricsRecordService.recordMetrics(event.getMetrics());
    }
}