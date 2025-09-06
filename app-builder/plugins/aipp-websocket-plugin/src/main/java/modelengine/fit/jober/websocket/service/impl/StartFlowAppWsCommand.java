/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.service.impl;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.service.AippRunTimeService;
import modelengine.fit.jober.websocket.dto.StartFlowChatParams;
import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.flowable.Choir;

import java.util.Map;

/**
 * 用户选择历史后启动流程。
 *
 * @author 曹嘉美
 * @since 2025-01-14
 */
@Component
public class StartFlowAppWsCommand extends AbstractAppWsCommand<StartFlowChatParams> {
    private final AippRunTimeService aippRunTimeService;

    /**
     * 构造启动流程的命令对象。
     *
     * @param aippRunTimeService 表示启动流程的运行时对象的 {@link AippRunTimeService}。
     */
    public StartFlowAppWsCommand(AippRunTimeService aippRunTimeService) {
        this.aippRunTimeService = aippRunTimeService;
    }

    @Override
    public String method() {
        return "startChatWithUserSelectMemory";
    }

    @Override
    public Class<StartFlowChatParams> paramClass() {
        return StartFlowChatParams.class;
    }

    @Override
    @CarverSpan("operation.websocket.startFlow")
    public Choir<Object> execute(@SpanAttr("operation_context") OperationContext context,
            @SpanAttr("params") StartFlowChatParams params) {
        String metaInstId = params.getInstanceId();
        Map<String, Object> initContext = params.getInitContext();
        this.setUserInOperationContext(context, params.getName(), params.getAccount());
        return this.aippRunTimeService.startFlowWithUserSelectMemory(metaInstId, initContext, context, false);
    }
}
