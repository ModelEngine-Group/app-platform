/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.service.impl;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.service.AppChatService;
import modelengine.fit.jober.websocket.dto.RestartChatParams;
import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.flowable.Choir;

import java.util.Map;

/**
 * 重新启动应用对话。
 *
 * @author 曹嘉美
 * @since 2025-01-14
 */
@Component
public class RestartChatAppWsCommand extends AbstractAppWsCommand<RestartChatParams> {
    private final AppChatService appChatService;

    /**
     * 构造应用重开对话命令对象。
     *
     * @param appChatService 表示应用对话服务的 {@link AppChatService}。
     */
    public RestartChatAppWsCommand(AppChatService appChatService) {
        this.appChatService = appChatService;
    }

    @Override
    public String method() {
        return "restartChat";
    }

    @Override
    public Class<RestartChatParams> paramClass() {
        return RestartChatParams.class;
    }

    @Override
    @CarverSpan("operation.websocket.restartChat")
    public Choir<Object> execute(@SpanAttr("operation_context") OperationContext context,
            @SpanAttr("params") RestartChatParams params) {
        String currentInstanceId = params.getCurrentInstanceId();
        Map<String, Object> additionalContext = params.getAdditionalContext();
        this.setUserInOperationContext(context, params.getName(), params.getAccount());
        return this.appChatService.restartChat(currentInstanceId, additionalContext, context);
    }
}
