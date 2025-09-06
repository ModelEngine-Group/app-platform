/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.genericable.adapter;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.chat.ChatQueryParams;
import modelengine.fit.jober.aipp.dto.chat.ChatRequest;
import modelengine.fitframework.flowable.Choir;

import java.util.Map;

/**
 * 历史会话服务接口。
 *
 * @author 曹嘉美
 * @since 2024-12-19
 */
public interface AppChatServiceAdapter {
    /**
     * 会话。
     *
     * @param appId 表示应用唯一标识符的 {@link String}。
     * @param params 表示会话参数的 {@link ChatQueryParams}。
     * @param operationContext 表示操作上下文的 {@link OperationContext}。
     * @param isDebug 表示是否是调试状态的 {@link Boolean}。
     * @return 表示会话相应体的 {@link Choir}{@code <}{@link Object}{@code >}。
     */
    Choir<Object> chat(String appId, ChatRequest params, OperationContext operationContext, boolean isDebug);

    /**
     * 重新发起会话。
     *
     * @param currentInstanceId 表示当前会话实例唯一标识符的 {@link String}。
     * @param additionalContext 表示附加上下文的 {@link Map}。
     * @param operationContext 表示操作上下文的 {@link OperationContext}。
     * @return 表示会话流的 {@link Choir}{@code <}{@link Object}{@code >}。
     */
    Choir<Object> restartChat(String currentInstanceId, Map<String, Object> additionalContext,
            OperationContext operationContext);
}
