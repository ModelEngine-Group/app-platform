/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.northbound;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.chat.ChatRequest;
import modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import modelengine.fit.jober.aipp.genericable.adapter.AppChatServiceAdapter;
import modelengine.fit.jober.aipp.service.AppChatService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.flowable.Choir;

import java.util.Map;

/**
 * {@link AppChatService} 的适配器类的实现类。
 *
 * @author 曹嘉美
 * @since 2024-12-19
 */
@Component
public class AppChatServiceAdapterImpl implements AppChatServiceAdapter {
    private final AppChatService appChatService;

    /**
     * 用历史会话服务接口 {@link AppChatService} 构造 {@link AppChatServiceAdapterImpl}。
     *
     * @param appChatService 表示历史会话服务接口的 {@link AppChatService}。
     */
    public AppChatServiceAdapterImpl(AppChatService appChatService) {
        this.appChatService = notNull(appChatService, "The app chat service must not be null.");
    }

    @Override
    public Choir<Object> chat(String appId, ChatRequest params, OperationContext operationContext, boolean isDebug) {
        CreateAppChatRequest createAppChatRequest = this.convertToCreateAppChatRequest(params);
        createAppChatRequest.setAppId(appId);
        return this.appChatService.chat(createAppChatRequest, operationContext, isDebug);
    }

    @Override
    public Choir<Object> restartChat(String currentInstanceId, Map<String, Object> additionalContext,
            OperationContext operationContext) {
        return this.appChatService.restartChat(currentInstanceId, additionalContext, operationContext);
    }

    private CreateAppChatRequest convertToCreateAppChatRequest(ChatRequest params) {
        if (params == null) {
            return null;
        }
        ChatRequest.Context ctx = params.getContext();
        CreateAppChatRequest.Context newCtx = null;
        if (ctx != null) {
            newCtx = CreateAppChatRequest.Context.builder()
                    .useMemory(ctx.getUseMemory())
                    .userContext(ctx.getUserContext())
                    .atAppId(ctx.getAtAppId())
                    .atChatId(ctx.getAtChatId())
                    .dimension(ctx.getDimension())
                    .dimensionId(ctx.getDimensionId())
                    .build();
        }
        return CreateAppChatRequest.builder()
                .chatId(params.getChatId())
                .question(params.getQuestion())
                .context(newCtx)
                .build();
    }
}
