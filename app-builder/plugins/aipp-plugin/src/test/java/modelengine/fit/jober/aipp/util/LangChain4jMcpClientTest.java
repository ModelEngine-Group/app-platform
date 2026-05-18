/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.service.tool.ToolExecutionResult;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class LangChain4jMcpClientTest {
    private static final String TEST_URL = "http://localhost:8080/sse";
    private static final String TOOL_NAME = "testTool";
    private static final String TOOL_ARGUMENTS = "{\"key\":\"value\"}";
    private static final String TOOL_RESULT = "{\"result\":\"success\"}";

    private MockedConstruction<dev.langchain4j.mcp.client.DefaultMcpClient> mcpClientMockedConstruction;
    private MockedConstruction<HttpMcpTransport> transportMockedConstruction;
    private LangChain4jMcpClient client;

    @BeforeEach
    void setUp() {
        transportMockedConstruction = mockConstruction(HttpMcpTransport.class);
        mcpClientMockedConstruction = mockConstruction(dev.langchain4j.mcp.client.DefaultMcpClient.class,
                (mock, context) -> {
                    when(mock.listTools()).thenReturn(Arrays.asList(
                            ToolSpecification.builder()
                                    .name(TOOL_NAME)
                                    .description("Test tool")
                                    .build()
                    ));
                    
                    ToolExecutionResult mockResult = mock(ToolExecutionResult.class);
                    lenient().when(mockResult.resultText()).thenReturn(TOOL_RESULT);
                    lenient().when(mock.executeTool(any(ToolExecutionRequest.class))).thenReturn(mockResult);
                });
        client = new LangChain4jMcpClient(TEST_URL);
    }

    @AfterEach
    void tearDown() {
        if (client != null) {
            client.close();
        }
        if (mcpClientMockedConstruction != null) {
            mcpClientMockedConstruction.close();
        }
        if (transportMockedConstruction != null) {
            transportMockedConstruction.close();
        }
    }

    @Test
    @DisplayName("测试获取工具列表成功")
    void shouldGetToolsWhenListTools() {
        List<ToolSpecification> tools = Assertions.assertDoesNotThrow(() -> client.getTools());

        Assertions.assertNotNull(tools);
        Assertions.assertEquals(1, tools.size());
        Assertions.assertEquals(TOOL_NAME, tools.get(0).name());
    }

    @Test
    @DisplayName("测试调用工具成功")
    void shouldCallToolWhenExecuteTool() {
        String result = Assertions.assertDoesNotThrow(() -> client.callTool(TOOL_NAME, TOOL_ARGUMENTS));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(TOOL_RESULT, result);
    }

    @Test
    @DisplayName("测试关闭客户端成功")
    void shouldCloseClientWhenClose() {
        Assertions.assertDoesNotThrow(() -> client.close());
    }

    @Test
    @DisplayName("测试使用try-with-resources自动关闭客户端")
    void shouldAutoCloseWhenUsingTryWithResources() {
        Assertions.assertDoesNotThrow(() -> {
            try (LangChain4jMcpClient autoCloseClient = new LangChain4jMcpClient(TEST_URL)) {
                List<ToolSpecification> tools = autoCloseClient.getTools();
                Assertions.assertNotNull(tools);
            }
        });
    }
}
