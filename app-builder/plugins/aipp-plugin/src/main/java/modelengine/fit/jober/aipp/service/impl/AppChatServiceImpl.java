/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import lombok.RequiredArgsConstructor;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import modelengine.fit.jober.aipp.service.AppChatService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.log.Logger;

import java.util.Map;

/**
 * 历史会话服务实现类
 *
 * @author 姚江
 * @since 2024-07-23
 */
@Component
@RequiredArgsConstructor
public class AppChatServiceImpl implements AppChatService {
    private static final Logger LOGGER = Logger.get(AppChatServiceImpl.class);
    private static final int FROM_OTHER_CHAT = 2;

    private final AppVersionService appVersionService;

    @Override
    public Choir<Object> chat(CreateAppChatRequest body, OperationContext context, boolean isDebug) {
        LOGGER.info("[perf] [{}] chat start, appId={}, isDebug={}", System.currentTimeMillis(), body.getAppId(),
                isDebug);

        Choir<Object> choir = isDebug
                ? this.appVersionService.debug(body, context)
                : this.appVersionService.run(body, context);

        LOGGER.info("[perf] [{}] chat saveChatInfos end, appId={}", System.currentTimeMillis(), body.getAppId());
        LOGGER.info("[perf] [{}] chat end, appId={}, isDebug={}", System.currentTimeMillis(), body.getAppId(), isDebug);
        return choir;
    }

    @Override
    public Choir<Object> restartChat(String instanceId, Map<String, Object> additionalContext,
            OperationContext operationContext) {
        return this.appVersionService.restart(instanceId, additionalContext, operationContext);
    }
}