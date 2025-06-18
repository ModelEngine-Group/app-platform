/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.execution.support;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fel.tool.Tool;
import modelengine.fel.tool.ToolFactory;
import modelengine.fel.tool.ToolFactoryRepository;
import modelengine.fel.tool.model.transfer.DefinitionData;
import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.util.MapBuilder;
import modelengine.jade.store.service.DefinitionService;
import modelengine.jade.store.service.ToolService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link DefaultToolExecuteService} 的单元测试。
 *
 * @author 王攀博
 * @since 2024-04-27
 */
@DisplayName("测试 DefaultToolExecuteService")
public class DefaultToolExecuteServiceTest {
    private DefaultToolExecuteService service;
    private JacksonObjectSerializer serializer;

    @BeforeEach
    void setup() {
        ToolService toolService = mock(ToolService.class);
        DefinitionService definitionService = mock(DefinitionService.class);
        ToolFactoryRepository toolFactoryRepository = mock(ToolFactoryRepository.class);
        this.serializer = new JacksonObjectSerializer(null, null, null, true);
        this.service =
                new DefaultToolExecuteService(definitionService, toolService, toolFactoryRepository, this.serializer);
        ToolFactory toolFactory = mock(ToolFactory.class);
        Tool tool = mock(Tool.class);
        when(toolService.getTool(any())).thenReturn(this.buildToolData());
        when(definitionService.get(any(), any())).thenReturn(this.buildDefinitionData());
        when(toolFactory.create(any(), any())).thenReturn(tool);
        when(toolFactoryRepository.match(any())).thenReturn(Optional.of(toolFactory));
        when(tool.executeWithJson(any())).thenReturn("OK");
        when(tool.executeWithJsonObject(any())).thenReturn("OK");
        Tool.Metadata metadata = mock(Tool.Metadata.class);
        when(tool.metadata()).thenReturn(metadata);
        Tool.Info toolInfo = mock(Tool.Info.class);
        when(tool.info()).thenReturn(toolInfo);
        when(tool.info().returnConverter()).thenReturn("");
    }

    @Test
    @DisplayName("当使用 Json 参数调用执行器时，返回正确结果")
    void shouldReturnOKWhenExecuteWithJson() {
        String uniqueName = "testUniqueName";
        String executeResult = this.service.execute(uniqueName, this.buildJsonArgs());
        assertThat(executeResult).isEqualTo("\"OK\"");
    }

    @Test
    @DisplayName("当使用 Json 对象参数调用执行器时，返回正确结果")
    void shouldReturnOKWhenExecuteWithJsonObject() {
        String uniqueName = "testUniqueName";
        String executeResult = this.service.execute(uniqueName, this.buildJsonObjectArgs());
        assertThat(executeResult).isEqualTo("\"OK\"");
    }

    private Map<String, Object> buildSchema() {
        return MapBuilder.<String, Object>get()
                .put("namespace", "test_namespace")
                .put("name", "test_schema_default_implementation_name")
                .put("index", "test_schema_index")
                .put("description", "This is a demo FIT function.")
                .put("parameters",
                        MapBuilder.<String, Object>get()
                                .put("type", "object")
                                .put("properties",
                                        MapBuilder.<String, Object>get()
                                                .put("p1",
                                                        MapBuilder.<String, Object>get()
                                                                .put("type", "string")
                                                                .put("description", "This is the first parameter.")
                                                                .build())
                                                .build())
                                .put("required", Collections.singletonList("p1"))
                                .build())
                .put("extensions", MapBuilder.get().build())
                .put("order", Collections.singletonList("p1"))
                .put("return", MapBuilder.<String, Object>get().put("type", "string").build())
                .build();
    }

    private Map<String, Object> buildRunnables() {
        return MapBuilder.<String, Object>get()
                .put("FIT", new HashMap<String, Object>())
                .put("WATERFLOW", new HashMap<String, Object>())
                .build();
    }

    private ToolData buildToolData() {
        ToolData toolData = new ToolData();
        toolData.setName("test_schema_default_implementation_name");
        toolData.setDescription("This is a demo FIT function.");
        toolData.setSchema(this.buildSchema());
        toolData.setExtensions(new HashMap<>());
        toolData.setRunnables(this.buildRunnables());
        toolData.setLatest(true);
        return toolData;
    }

    private DefinitionData buildDefinitionData() {
        DefinitionData definitionData = new DefinitionData();
        definitionData.setGroupName("test_definition_group_name");
        definitionData.setName("test_definition_name");
        definitionData.setSchema(this.buildSchema());
        return definitionData;
    }

    private Map<String, Object> buildJsonObjectArgs() {
        return MapBuilder.<String, Object>get().put("p1", "1").build();
    }

    private String buildJsonArgs() {
        Map<String, Object> in = this.buildJsonObjectArgs();
        return new String(this.serializer.serialize(in, UTF_8));
    }
}