/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fit.jober.aipp.util.LangChain4jMcpClient;

import java.util.function.Function;

/**
 * MCP 客户端工厂接口，用于创建 {@link LangChain4jMcpClient} 实例。
 *
 * @author songyongtan
 * @since 2026-03-02
 */
public interface McpClientFactory extends Function<String, LangChain4jMcpClient> {
}
