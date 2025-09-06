/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.influxdb.events;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import modelengine.fit.jober.aipp.entity.AippFlowData;
import modelengine.fit.jober.aipp.events.InsertConversationEnd;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.jade.app.engine.metrics.influxdb.service.MetricsRecordService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link InsertConversationEndHandler} 的测试。
 *
 * @author 高嘉乐
 * @since 2025-01-07
 */
@FitTestWithJunit(includeClasses = InsertConversationEndHandler.class)
@DisplayName("测试插入对话结束事件 handler")
class InsertConversationEndHandlerTest {
    @Fit
    private InsertConversationEndHandler insertConversationEndHandler;

    @Fit
    private Plugin plugin;

    @Mock
    private MetricsRecordService metricsRecordService;

    @Test
    @DisplayName("当事件发布时，应调用一次 service 方法")
    void shouldInvokeOneWhenPublishEvent() {
        this.plugin.publisherOfEvents().publishEvent(new InsertConversationEnd(plugin, any(AippFlowData.class)));
        verify(this.metricsRecordService, times(1)).recordMetrics(any());
    }
}