/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fitframework.flowable.Choir;

/**
 * 大模型会话流式接口命令服务。
 *
 * @author 曹嘉美
 * @since 2025-01-14
 */
public interface AppWsCommand<T> {
    /**
     * 获取命令名称。
     *
     * @return 表示命令名称的 {@link String}。
     */
    String method();

    /**
     * 获取命令入参类型。
     *
     * @return 表示命令入参类型的 {@link Class}。
     */
    Class<T> paramClass();

    /**
     * 调用命令。
     *
     * @param context 表示会话上下文信息的 {@link OperationContext}。
     * @param params 表示命令参数的 {@link T}.
     * @return 表示命令执行结果的 {@link Choir}{@code <}{@link Object}{@code >}。
     */
    Choir<Object> execute(OperationContext context, T params);
}
