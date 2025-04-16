/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.tool;

import modelengine.fitframework.annotation.Genericable;

import java.util.Map;

/**
 * 同步工具调用接口
 *
 * @author 夏斐
 * @since 2025/3/12
 */
public interface SyncToolCall {
    /**
     * 支持携带工具上下文的调用。
     *
     * @param uniqueName 表示工具唯一标识的 {@link String}。
     * @param toolArgs 表示工具调用参数的 {@link String}。
     * @param toolContext 表示工具调用上下文的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示工具执行结果的 {@link String}。
     */
    @Genericable("modelengine.jober.aipp.tool.sync.call")
    String call(String uniqueName, String toolArgs, Map<String, Object> toolContext);
}
