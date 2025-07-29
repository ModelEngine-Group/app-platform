/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.service.impl;

import static modelengine.fitframework.inspection.Validation.notBlank;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import modelengine.fit.jober.aipp.service.AppChatService;
import modelengine.fit.jober.websocket.dto.ChatParams;
import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.flowable.Choir;

/**
 * 应用对话。
 *
 * @author 曹嘉美
 * @since 2025-01-14
 */
@Component
public class ChatAppWsCommand extends AbstractAppWsCommand<ChatParams> {
    private final AppChatService appChatService;

    /**
     * 构造应用对话命令对象。
     *
     * @param appChatService 表示应用对话服务的 {@link AppChatService}。
     */
    public ChatAppWsCommand(AppChatService appChatService) {
        this.appChatService = appChatService;
    }

    @Override
    public String method() {
        return "appChat";
    }

    @Override
    public Class<ChatParams> paramClass() {
        return ChatParams.class;
    }

    @Override
    @CarverSpan("operation.websocket.chat")
    public Choir<Object> execute(@SpanAttr("operation_context") OperationContext context,
            @SpanAttr("params") ChatParams params) {
        boolean isDebug = params.getIsDebug();
        CreateAppChatRequest request = params.getData();
        this.validateChatRequest(request);
        this.setUserInOperationContext(context, params.getName(), params.getAccount());
        return this.appChatService.chat(request, context, isDebug);
    }

    private void validateChatRequest(CreateAppChatRequest request) {
        notNull(request, () -> new AippParamException(AippErrCode.APP_CHAT_REQUEST_IS_NULL));
        notNull(request.getContext(), () -> new AippParamException(AippErrCode.APP_CHAT_REQUEST_IS_NULL));
        notBlank(request.getAppId(), () -> new AippParamException(AippErrCode.APP_CHAT_REQUEST_IS_NULL));
    }
}
