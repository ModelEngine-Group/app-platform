/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.flow.graph.repo;

import static modelengine.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.flow.graph.entity.FlowGraphDefinition;
import modelengine.fit.jane.flow.graph.entity.FlowGraphQueryParam;
import modelengine.fit.jane.flow.graph.entity.FlowSaveEntity;
import modelengine.fit.jober.common.exceptions.JobberParamException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

/**
 * DbFlowsGraphRepoTest
 *
 * @author 孙怡菲
 * @since 2023/10/30
 */
class DbFlowsGraphRepoTest {
    private DbFlowsGraphRepo dbFlowsGraphRepo;

    private FlowGraphRepo flowGraphRepo;

    @BeforeEach
    void setUp() {
        flowGraphRepo = Mockito.mock(FlowGraphRepo.class);
        dbFlowsGraphRepo = new DbFlowsGraphRepo(flowGraphRepo);
    }

    @Test
    @DisplayName("测试保存流程成功")
    void saveFlowSuccessfully() {
        int res = dbFlowsGraphRepo.saveFlow(getMockEntity(), getMockContext());
        verify(flowGraphRepo, times(1)).save(any());
        Assertions.assertEquals(0, res);
    }

    @Test
    @DisplayName("测试保存流程失败")
    void saveFlowFailed() {
        int res = dbFlowsGraphRepo.saveFlow(new FlowSaveEntity(), getMockContext());
        Assertions.assertEquals(-1, res);
    }

    @Test
    @DisplayName("测试查询单个流程成功")
    void getFlowSuccessfully() {
        FlowGraphDefinition flowGraphDefinition = new FlowGraphDefinition();
        flowGraphDefinition.setGraphData("graphData");
        when(flowGraphRepo.findFlowByFlowIdAndVersion(any(), any())).thenReturn(flowGraphDefinition);
        String graphData = dbFlowsGraphRepo.getFlow(getMockEntity(), getMockContext());
        Assertions.assertEquals("graphData", graphData);
    }

    @Test
    @DisplayName("测试查询单个流程失败")
    void getFlowFailed() {
        when(flowGraphRepo.findFlowByFlowIdAndVersion(any(), any())).thenReturn(null);
        assertThrows(JobberParamException.class, () -> dbFlowsGraphRepo.getFlow(getMockEntity(), getMockContext()));
    }

    @Test
    @DisplayName("测试删除流程成功")
    void deleteFlowSuccessfully() {
        int res = dbFlowsGraphRepo.deleteFlow(getMockEntity(), getMockContext());
        verify(flowGraphRepo, times(1)).delete(any(), any());
        Assertions.assertEquals(0, res);
    }

    @Test
    @DisplayName("测试删除流程失败")
    void deleteFlowFailed() {
        when(flowGraphRepo.delete(any(), any())).thenThrow(new JobberParamException(INPUT_PARAM_IS_EMPTY));
        int res = dbFlowsGraphRepo.deleteFlow(getMockEntity(), getMockContext());
        Assertions.assertEquals(-1, res);
    }

    @Test
    void getFlowList() {
        FlowGraphQueryParam queryParam = new FlowGraphQueryParam();
        List<String> flowIds = Collections.singletonList("123");
        queryParam.setFlowIds(flowIds);
        queryParam.setCreatUser(null);
        queryParam.setOffset(0);
        queryParam.setLimit(10);
        dbFlowsGraphRepo.getFlowList(queryParam, new OperationContext());
        verify(flowGraphRepo, times(1)).getFlowList(flowIds, null, 0, 10);
    }

    private FlowSaveEntity getMockEntity() {
        FlowSaveEntity flowSaveEntity = new FlowSaveEntity();
        flowSaveEntity.setGraphData("{\"id\":\"b4d0e186a6974f68aaccce0982af412a\",\"title\":\"test graph\"}");
        flowSaveEntity.setId("123");
        flowSaveEntity.setVersion("1.0.0");
        return flowSaveEntity;
    }

    private OperationContext getMockContext() {
        OperationContext context = new OperationContext();
        context.setTenantId("tenantId");
        context.setName("testUser");
        return context;
    }
}