/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fitframework.annotation.Component;

/**
 * MCP 客户端工厂的默认实现，使用 {@link LangChain4jMcpClient} 创建客户端实例。
 *
 * @author songyongtan
 * @since 2026-03-02
 */
@Component
public class DefaultMcpClientFactory implements McpClientFactory {
    @Override
    public LangChain4jMcpClient create(String url) {
        return new LangChain4jMcpClient(url);
    }
}
