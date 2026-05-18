/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

/**
 * MCP 客户端工厂接口，用于创建 {@link LangChain4jMcpClient} 实例。
 *
 * @author songyongtan
 * @since 2026-03-02
 */
public interface McpClientFactory {
    /**
     * 创建 MCP 客户端实例。
     *
     * @param url 表示 MCP 服务器的 URL。
     * @return 返回创建的 {@link LangChain4jMcpClient} 实例。
     */
    LangChain4jMcpClient create(String url);
}
