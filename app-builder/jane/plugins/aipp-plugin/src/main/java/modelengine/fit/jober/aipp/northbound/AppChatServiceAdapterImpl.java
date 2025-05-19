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
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.serialization.ObjectSerializer;

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
    private final ObjectSerializer serializer;

    public AppChatServiceAdapterImpl(AppChatService appChatService, @Fit(alias = "json") ObjectSerializer serializer) {
        this.appChatService = notNull(appChatService, "The app chat service must not be null.");
        this.serializer = notNull(serializer, "The serializer must not be null.");
    }

    @Override
    public Choir<Object> chat(String appId, ChatRequest params, OperationContext operationContext, boolean isDebug) {
        CreateAppChatRequest createAppChatRequest =
                this.serializer.deserialize(this.serializer.serialize(params), CreateAppChatRequest.class);
        createAppChatRequest.setAppId(appId);
        return this.appChatService.chat(createAppChatRequest, operationContext, isDebug);
    }

    @Override
    public Choir<Object> restartChat(String currentInstanceId, Map<String, Object> additionalContext,
            OperationContext operationContext) {
        return this.appChatService.restartChat(currentInstanceId, additionalContext, operationContext);
    }
}
