/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.waterflow;

import modelengine.fel.core.tool.ToolCall;
import modelengine.fel.tool.Tool;
import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.jade.carver.tool.waterflow.invoker.ToolInvoker;
import modelengine.jade.store.service.ToolService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;

/**
 * {@link WaterFlowToolCallAdapter} 的测试。
 *
 * @author 宋永坦
 * @since 2024-12-17
 */
@ExtendWith(MockitoExtension.class)
class WaterFlowToolCallAdapterTest {
    @Mock
    private ToolService toolService;

    @Mock
    private ToolInvoker toolInvoker;

    private WaterFlowToolCallAdapter waterFlowToolProvider;

    @BeforeEach
    void setUp() {
        this.waterFlowToolProvider =
                new WaterFlowToolCallAdapter(this.toolService, Collections.singletonList(this.toolInvoker));
    }

    @Test
    void shouldReturnResultOfInvokerWhenCallGivenInvoker() {
        String toolUniqueName = "toolUniqueName";
        ToolData toolData =
                ToolData.from(Tool.Info.custom().name(toolUniqueName).uniqueName(toolUniqueName).build());
        ToolCall toolCall =
                ToolCall.custom().id("toolCallId1").name(toolUniqueName).arguments("{\"inputParams\":{}").build();
        HashMap<String, Object> toolContext = new HashMap<>();
        Mockito.when(this.toolService.getTool(toolUniqueName)).thenReturn(toolData);
        Mockito.when(this.toolInvoker.match(Mockito.any())).thenReturn(true);
        String result = "chat";
        Mockito.when(this.toolInvoker.invoke(Mockito.any(), Mockito.any())).thenReturn(result);

        String toolResult = this.waterFlowToolProvider.call(toolCall.name(), toolCall.arguments(), toolContext);

        Assertions.assertEquals(result, toolResult);
    }
}