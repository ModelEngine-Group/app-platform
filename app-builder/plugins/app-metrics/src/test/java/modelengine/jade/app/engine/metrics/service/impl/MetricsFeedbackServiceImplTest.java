/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.meta.multiversion.MetaService;
import modelengine.fit.jane.meta.multiversion.definition.Meta;
import modelengine.fit.jober.common.RangeResult;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.jade.app.engine.metrics.dto.MetricsFeedbackDto;
import modelengine.jade.app.engine.metrics.mapper.ConversationRecordMapper;
import modelengine.jade.app.engine.metrics.vo.MetricsFeedbackVo;
import modelengine.jade.app.engine.metrics.vo.Page;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link MetricsFeedbackServiceImpl} 的测试。
 *
 * @author 刘信宏
 * @since 2024-08-21
 */
@FitTestWithJunit(includeClasses = MetricsFeedbackServiceImpl.class)
class MetricsFeedbackServiceImplTest {
    @Mock
    private ConversationRecordMapper conversationRecordMapper;

    @Mock
    private MetaService metaService;

    @Fit
    private MetricsFeedbackServiceImpl metricsFeedbackServiceImpl;

    @Test
    void test_getMetricsFeedback_should_ok_when_test_data_combination() {
        // setup
        when(conversationRecordMapper.getCountByCondition(any(MetricsFeedbackDto.class), anyList())).thenReturn(5L);
        List<MetricsFeedbackVo> conversations = new ArrayList<>();
        MetricsFeedbackVo metricsFeedbackVo = new MetricsFeedbackVo();
        conversations.add(metricsFeedbackVo);
        when(conversationRecordMapper.getByCondition(any(MetricsFeedbackDto.class),
                anyList())).thenReturn(conversations);

        MetricsFeedbackDto metricsFeedbackDTO = new MetricsFeedbackDto();
        metricsFeedbackDTO.setPageIndex(0);
        metricsFeedbackDTO.setPageSize(10);

        Page<MetricsFeedbackVo> result = metricsFeedbackServiceImpl.getMetricsFeedback(metricsFeedbackDTO);

        assertEquals(5, result.getTotal());
        assertEquals(1, result.getData().size());
    }

    @Test
    void test_export_should_return_not_null_when_test_data_combination() throws IOException {
        // setup
        when(conversationRecordMapper.getCountByCondition(any(MetricsFeedbackDto.class), anyList())).thenReturn(5L);
        MetricsFeedbackVo metricsFeedbackVo = new MetricsFeedbackVo();
        metricsFeedbackVo.setResponseTime(10L);
        metricsFeedbackVo.setCreateTime(LocalDateTime.now());
        List<MetricsFeedbackVo> conversations = Collections.singletonList(metricsFeedbackVo);
        when(conversationRecordMapper.getByCondition(any(MetricsFeedbackDto.class), anyList()))
                .thenReturn(conversations);
        Meta meta = this.buildMeta();
        RangeResult rangeResult = new RangeResult(0, 0, 1);
        RangedResultSet set = new RangedResultSet(Collections.singletonList(meta), rangeResult);
        when(metaService.list(any(), anyBoolean(), anyLong(), anyInt(), any())).thenReturn(set);

        MetricsFeedbackDto metricsFeedbackDTO = new MetricsFeedbackDto();
        metricsFeedbackDTO.setPageIndex(0);
        metricsFeedbackDTO.setPageSize(10);

        // run the test
        ByteArrayInputStream result = metricsFeedbackServiceImpl.export(metricsFeedbackDTO);

        // verify the results
        assertNotNull(result);
    }

    Meta buildMeta() {
        Meta meta = new Meta();
        meta.setId("appId1");
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("meta_status", "active");
        attributes.put("app_id", "appId1");
        meta.setAttributes(attributes);
        return meta;
    }
}