/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;

import org.junit.jupiter.api.Test;

/**
 * {@link CacheUtils} 测试类
 *
 * @author lixin
 * @since 2025-01-08
 */
public class CacheUtilsTest {
    @Test
    void testCachePutAndGet() {
        String mockKey = "appId";
        AppBuilderAppPo appBuilderAppPo = new AppBuilderAppPo();
        String id = "id1";
        appBuilderAppPo.setId(id);
        CacheUtils.APP_CACHE.put(mockKey, appBuilderAppPo);
        AppBuilderAppPo cachedApp = CacheUtils.APP_CACHE.getIfPresent(mockKey);

        assertNotNull(cachedApp);
        assertEquals(id, cachedApp.getId());
    }

    @Test
    void testCacheHit() {
        FlowsService flowsService = mock(FlowsService.class);
        String flowDefinitionId = "flow123";
        OperationContext context = mock(OperationContext.class);
        FlowInfo flowInfo = new FlowInfo();
        CacheUtils.FLOW_CACHE.put(flowDefinitionId, flowInfo);
        FlowInfo result = CacheUtils.getPublishedFlowWithCache(flowsService, flowDefinitionId, context);

        assertNotNull(result);
        assertEquals(flowInfo, result);
        verify(flowsService, never()).getFlows(anyString(), any());
    }

    @Test
    void testCacheMissAndPopulateCache() {
        FlowsService flowsService = mock(FlowsService.class);
        String flowDefinitionId = "flow123";
        OperationContext context = mock(OperationContext.class);
        FlowInfo flowInfo = new FlowInfo();
        when(flowsService.getFlows(flowDefinitionId, context)).thenReturn(flowInfo);
        FlowInfo result = CacheUtils.getPublishedFlowWithCache(flowsService, flowDefinitionId, context);

        assertNotNull(result);
        assertEquals(flowInfo, result);
        verify(flowsService, times(1)).getFlows(flowDefinitionId, context);
    }
}
