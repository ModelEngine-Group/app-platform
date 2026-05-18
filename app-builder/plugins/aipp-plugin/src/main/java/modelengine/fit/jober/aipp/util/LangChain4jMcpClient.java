/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/


package modelengine.fit.jober.aipp.util;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;

import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fitframework.util.StringUtils;

import java.util.List;

/**
 * LangChain4jMcpClient is a client for calling ModelEngine's MCP server.
 * 
 * @author songyongtan
 * @since 2026-03-01
 */
public class LangChain4jMcpClient implements AutoCloseable {
    private final McpClient mcpClient;
    private final String url;

    /**
     * 构造函数，用于初始化LangChain4jMcpClient对象。
     *
     * @param url MCP服务器的URL，格式为http://host:port
     */
    public LangChain4jMcpClient(String url) {
        this.url = url;
        
        HttpMcpTransport transport = new HttpMcpTransport.Builder()
                .sseUrl(url)
                .build();
        
        this.mcpClient = new DefaultMcpClient.Builder()
                .transport(transport)
                .build();
    }

    /**
     * 获取MCP服务器上注册的所有工具。
     *
     * @return 包含所有工具规范的列表
     */
    public List<ToolSpecification> getTools() {
        try {
            return this.mcpClient.listTools();
        } catch (Exception e) {
            throw new AippException(AippErrCode.CALL_MCP_SERVER_FAILED, 
                    StringUtils.format("Failed to get tools from MCP server. [url={0}]", this.url), e);
        }
    }

    /**
     * 调用MCP服务器上的指定工具。
     *
     * @param toolName 要调用的工具名称
     * @param arguments 工具调用的参数，格式为JSON字符串
     * @return 工具调用的结果，格式为JSON字符串
     */
    public String callTool(String toolName, String arguments) {
        try {
            ToolExecutionRequest request = ToolExecutionRequest.builder()
                    .name(toolName)
                    .arguments(arguments)
                    .build();
            return mcpClient.executeTool(request).resultText();
        } catch (Exception e) {
            throw new AippException(AippErrCode.CALL_MCP_SERVER_FAILED, 
                    StringUtils.format("Failed to call tool. [toolName={0}, url={1}]", toolName, this.url), e);
        }
    }

    @Override
    public void close() {
        try {
            this.mcpClient.close();
        } catch (Exception e) {
            throw new AippException(AippErrCode.CALL_MCP_SERVER_FAILED, 
                    StringUtils.format("Failed to close MCP client. [url={0}]", this.url), e);
        }
    }
}
