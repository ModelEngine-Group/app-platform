/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fit.jober.aipp.entity.ChatSession;
import modelengine.fit.jober.aipp.mapper.AippLogMapper;
import modelengine.fit.jober.aipp.service.AppChatSessionService;
import modelengine.fit.jober.aipp.service.AppChatSseService;
import modelengine.fit.jober.aipp.util.AippLogUtils;

import lombok.RequiredArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.StringUtils;

import java.util.Optional;

/**
 * {@link AppChatSseService} 的默认实现
 *
 * @author 邬涨财
 * @since 2024-07-28
 */
@Component
@RequiredArgsConstructor
public class AppChatSseServiceImpl implements AppChatSseService {
    private final AippLogMapper aippLogMapper;
    private final AppChatSessionService appChatSessionService;

    @Override
    public Optional<ChatSession<Object>> getEmitter(String instanceId) {
        return this.appChatSessionService.getSession(instanceId);
    }

    @Override
    public void send(String instanceId, Object data) {
        if (data != null) {
            this.getEmitter(instanceId).ifPresent(e -> e.getEmitter().emit(data));
        }
    }

    @Override
    public void sendLastData(String instanceId, Object data) {
        this.send(instanceId, data);
        this.getEmitter(instanceId).ifPresent(e -> e.getEmitter().complete());
        this.appChatSessionService.removeSession(instanceId);
    }

    @Override
    public void sendToAncestor(String instanceId, Object data) {
        String processedInstanceId = this.getProcessedInstanceId(instanceId);
        this.send(processedInstanceId, data);
    }

    @Override
    public void sendToAncestorLastData(String instanceId, Object data) {
        String processedInstanceId = this.getProcessedInstanceId(instanceId);
        this.sendLastData(processedInstanceId, data);
    }

    private String getProcessedInstanceId(String instanceId) {
        String path = this.aippLogMapper.getParentPath(instanceId);
        if (StringUtils.isNotEmpty(path)) {
            return path.split(AippLogUtils.PATH_DELIMITER)[1];
        }
        return instanceId;
    }
}
