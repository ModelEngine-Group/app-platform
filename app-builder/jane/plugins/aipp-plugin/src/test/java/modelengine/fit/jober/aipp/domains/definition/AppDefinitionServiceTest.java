/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.definition;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import modelengine.fit.jade.waterflow.AippFlowDefinitionService;
import modelengine.fit.jade.waterflow.entity.FlowDefinitionResult;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.definition.service.AppDefinitionService;
import modelengine.fit.jober.aipp.domains.definition.service.impl.AppDefinitionServiceImpl;
import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fitframework.annotation.Fit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link AppDefinitionService} 的测试类。
 *
 * @author 孙怡菲
 * @since 2025-02-20
 */
@ExtendWith(MockitoExtension.class)
class AppDefinitionServiceTest {
    @Mock
    private AippFlowDefinitionService flowDefinitionService;

    @Fit
    private AppDefinitionService appDefinitionService;

    @BeforeEach
    void setUp() {
        this.appDefinitionService = new AppDefinitionServiceImpl(flowDefinitionService);
    }

    @Test
    @DisplayName("测试成功获取相同流程定义")
    public void TestGetSameDef() {
        String metaId = "id1";
        AippDto mockDto = mockAippDto();
        String mockData = "{\"metaId\":\"id1\"}";
        FlowDefinitionResult mockDef = new FlowDefinitionResult();
        mockDef.setGraph(mockData);
        mockDef.setMetaId(metaId);
        when(flowDefinitionService.getFlowDefinitionByMetaIdAndPartVersion(any(), any(), any())).thenReturn(
            Collections.singletonList(mockDef));
        when(flowDefinitionService.getParsedGraphData(any(), any())).thenReturn(mockData);

        FlowDefinitionResult result = this.appDefinitionService.getSameFlowDefinition(mockDto);

        Assertions.assertEquals(metaId, result.getMetaId());
    }

    @Test
    @DisplayName("测试没有获取到相同流程定义")
    public void TestNotGetSameDef() {
        AippDto mockDto = mockAippDto();
        String mockData = "{\"metaId\":\"id1\"}";
        String mockData1 = "{\"metaId\":\"id2\"}";
        FlowDefinitionResult mockDef = new FlowDefinitionResult();
        mockDef.setGraph(mockData1);
        mockDef.setMetaId("id1");
        when(flowDefinitionService.getFlowDefinitionByMetaIdAndPartVersion(any(), any(), any())).thenReturn(
            Collections.singletonList(mockDef));
        when(flowDefinitionService.getParsedGraphData(any(), any())).thenReturn(mockData);

        FlowDefinitionResult result = this.appDefinitionService.getSameFlowDefinition(mockDto);

        Assertions.assertNull(result);
    }

    private AippDto mockAippDto() {
        Map<String, Object> mockViewData = new HashMap<>();
        mockViewData.put(AippConst.FLOW_CONFIG_ID_KEY, "id");
        mockViewData.put(AippConst.FLOW_CONFIG_VERSION_KEY, "1.0.0");
        return AippDto.builder()
            .flowViewData(mockViewData)
            .build();
    }
}