/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.service.impl;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.service.AippRunTimeService;
import modelengine.fit.jober.websocket.dto.UpdateChatParams;
import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.flowable.Choir;

import java.util.Map;

/**
 * 更新表单数据，并恢复实例任务执行。
 *
 * @author 曹嘉美
 * @since 2025-01-14
 */
@Component
public class ResumeAndUpdateAppWsCommand extends AbstractAppWsCommand<UpdateChatParams> {
    private final AippRunTimeService aippRunTimeService;

    /**
     * 构造更新表单并恢复任务执行的命令对象。
     *
     * @param aippRunTimeService 表示应用运行时服务对象的 {@link AippRunTimeService}。
     */
    public ResumeAndUpdateAppWsCommand(AippRunTimeService aippRunTimeService) {
        this.aippRunTimeService = aippRunTimeService;
    }

    @Override
    public String method() {
        return "resumeAndUpdateAppInstance";
    }

    @Override
    public Class<UpdateChatParams> paramClass() {
        return UpdateChatParams.class;
    }

    @Override
    @CarverSpan("operation.websocket.resumeUpdate")
    public Choir<Object> execute(@SpanAttr("operation_context") OperationContext context,
            @SpanAttr("params") UpdateChatParams params) {
        String metaInstId = params.getInstanceId();
        Long logId = params.getLogId();
        Map<String, Object> formArgs = params.getFormArgs();
        this.setUserInOperationContext(context, params.getName(), params.getAccount());
        return this.aippRunTimeService.resumeAndUpdateAippInstance(metaInstId, formArgs, logId, context, false);
    }
}
