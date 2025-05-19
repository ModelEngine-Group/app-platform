/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.StatisticsDTO;
import modelengine.fit.jober.aipp.service.impl.StatisticsServiceImpl;
import modelengine.jade.store.service.PluginService;
import modelengine.jade.store.service.support.DeployStatus;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * {@link StatisticsService} 测试类
 *
 * @author 陈潇文
 * @since 2024-12-30
 */
@ExtendWith(MockitoExtension.class)
public class StatisticsServiceImplTest {
    private StatisticsService statisticsService;

    @Mock
    private AppBuilderAppService appBuilderAppService;

    @Mock
    private AppBuilderFormService appBuilderFormService;

    @Mock
    private PluginService pluginService;

    @BeforeEach
    void before() {
        this.statisticsService =
                new StatisticsServiceImpl(this.pluginService, this.appBuilderFormService, this.appBuilderAppService);
    }

    @Test
    @DisplayName("测试查询统计数据成功")
    void testGetStatisticSuccess() {
        when(this.appBuilderAppService.getAppCount(any(), any())).thenReturn(10L);
        when(this.appBuilderFormService.countByType(any(), any())).thenReturn(20L);
        when(this.pluginService.getPluginsCount(any(DeployStatus.class))).thenReturn(5);
        StatisticsDTO result = this.statisticsService.getStatistics(new OperationContext());
        Assertions.assertEquals(10L, result.getPublishedAppNum());
        Assertions.assertEquals(0, result.getUnpublishedAppNum());
        Assertions.assertEquals(20L, result.getFormNum());
        Assertions.assertEquals(20, result.getPluginNum());
    }
}
