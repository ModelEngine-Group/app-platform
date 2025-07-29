/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import modelengine.fitframework.flowable.Choir;

import java.util.Map;

/**
 * 历史会话服务接口.
 *
 * @author 姚江
 * @since 2024-07-23
 */
public interface AppChatService {
    /**
     * 缓存运行时数据.
     *
     * @param body 请求体
     * @param context 上下文
     * @param isDebug 是否是调试对话
     * @return 表示会话相应体的 {@link Choir}。
     */
    Choir<Object> chat(CreateAppChatRequest body, OperationContext context, boolean isDebug);

    /**
     * 重新发起会话。
     *
     * @param instanceId 需要重新发起会话的实例 ID。
     * @param additionalContext 重新会话需要的信息，如是否使用多轮对话等等。
     * @param operationContext 上下文。
     * @return 表示会话流的 {@link Choir<Object>}。
     */
    Choir<Object> restartChat(String instanceId, Map<String, Object> additionalContext,
            OperationContext operationContext);
}
