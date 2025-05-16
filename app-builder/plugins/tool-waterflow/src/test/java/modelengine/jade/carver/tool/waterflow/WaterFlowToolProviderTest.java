/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.waterflow;

import modelengine.fel.core.chat.support.FlatChatMessage;
import modelengine.fel.core.tool.ToolCall;
import modelengine.fel.core.tool.ToolInfo;
import modelengine.fel.tool.Tool;
import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fitframework.util.MapBuilder;
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
import java.util.List;
import java.util.Map;

/**
 * {@link WaterFlowToolProvider} 的测试
 *
 * @author songyongtan
 * @since 2024/12/17
 */
@ExtendWith(MockitoExtension.class)
class WaterFlowToolProviderTest {
    @Mock
    private ToolService toolService;

    @Mock
    private ToolInvoker toolInvoker;

    private WaterFlowToolProvider waterFlowToolProvider;

    @BeforeEach
    void setUp() {
        this.waterFlowToolProvider =
                new WaterFlowToolProvider(this.toolService, Collections.singletonList(this.toolInvoker));
    }

    @Test
    void shouldReturnCorrectToolWhenGetToolGivenNormalTool() {
        final String toolUniqueName = "toolUniqueName";
        final Map<String, Object> schema =
                MapBuilder.<String, Object>get().put("name", toolUniqueName).put("k1", "v1").put("k2", "v2").build();
        final Map<String, Object> runnables = MapBuilder.<String, Object>get().put("r1", "v1").put("r2", "v2").build();
        final String desc = "desc";
        final ToolData toolData = ToolData.from(Tool.Info.custom()
                .name("toolName")
                .uniqueName(toolUniqueName)
                .schema(schema)
                .runnables(runnables)
                .description(desc)
                .build());
        Mockito.when(this.toolService.getTool(toolUniqueName)).thenReturn(toolData);
        Mockito.when(this.toolInvoker.match(Mockito.same(toolData))).thenReturn(true);
        Mockito.when(this.toolInvoker.getToolInfo(Mockito.same(toolData)))
                .thenReturn(ToolInfo.custom()
                        .name(toolData.getUniqueName())
                        .description(toolData.getDescription())
                        .parameters(schema)
                        .extensions(new HashMap<>())
                        .build());

        final List<ToolInfo> toolList = waterFlowToolProvider.getTool(Collections.singletonList(toolUniqueName));

        Assertions.assertEquals(1, toolList.size());
        final ToolInfo toolInfo = toolList.get(0);
        Assertions.assertEquals(toolUniqueName, toolInfo.name());
        Assertions.assertEquals(desc, toolInfo.description());
        Assertions.assertEquals(schema, toolInfo.parameters());
        Assertions.assertEquals(new HashMap(), toolInfo.extensions());
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
        Mockito.when(this.toolInvoker.match(Mockito.same(toolData))).thenReturn(true);
        String result = "chat";
        Mockito.when(this.toolInvoker.invoke(Mockito.same(toolCall), Mockito.same(toolContext))).thenReturn(result);

        FlatChatMessage chatMessage = this.waterFlowToolProvider.call(toolCall, toolContext);

        Assertions.assertEquals(toolCall.id(), chatMessage.getId());
        Assertions.assertEquals(result, chatMessage.getText());
    }
}