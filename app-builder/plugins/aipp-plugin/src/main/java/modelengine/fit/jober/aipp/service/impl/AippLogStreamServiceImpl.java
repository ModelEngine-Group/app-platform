/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.dto.chat.AppChatRsp;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.StreamMsgType;
import modelengine.fit.jober.aipp.service.AippLogStreamService;
import modelengine.fit.jober.aipp.service.AppChatSseService;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.aipp.util.SensitiveFilterTools;
import modelengine.fit.jober.aipp.vo.AippLogVO;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.JobberException;

import modelengine.fit.waterflow.domain.enums.FlowTraceStatus;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * log流式服务实现，单进程实现方案.
 *
 * @author 张越
 * @since 2024-05-23
 */
@Component
public class AippLogStreamServiceImpl implements AippLogStreamService {
    private static final List<String> OUTPUT_WITH_MSG_WHITE_LIST = Arrays.asList(AippInstLogType.MSG.name(),
            AippInstLogType.ERROR.name(),
            AippInstLogType.META_MSG.name(),
            StreamMsgType.KNOWLEDGE.value(),
            AippInstLogType.HIDDEN_MSG.name());

    private final AppChatSseService appChatSseService;
    private final SensitiveFilterTools sensitiveFilterTools;
    private final AppTaskInstanceService appTaskInstanceService;

    public AippLogStreamServiceImpl(AppChatSseService appChatSseService,
            SensitiveFilterTools sensitiveFilterTools, AppTaskInstanceService appTaskInstanceService) {
        this.appChatSseService = appChatSseService;
        this.sensitiveFilterTools = sensitiveFilterTools;
        this.appTaskInstanceService = appTaskInstanceService;
    }

    @Override
    public void send(AippLogVO log) {
        if (!log.displayable()) {
            return;
        }
        AppChatRsp appChatRsp = this.buildData(log);
        if (!appChatRsp.getStatus().equalsIgnoreCase(FlowTraceStatus.RUNNING.name()) && !appChatRsp.getStatus()
                .equalsIgnoreCase(FlowTraceStatus.READY.name())) {
            this.appChatSseService.sendLastData(log.getInstanceId(), appChatRsp);
        } else {
            this.appChatSseService.send(log.getInstanceId(), appChatRsp);
        }
    }

    private AppChatRsp buildData(AippLogVO log) {
        String instanceId = log.getInstanceId();
        AppTaskInstance instance = this.appTaskInstanceService.getInstanceById(instanceId, null)
                .orElseThrow(() -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                        StringUtils.format("App task instance[{0}] not found.", instanceId)));

        // 在当前某些情况下，会出现插入log日志，但是不修改instance状态的情况.
        // 参考modelengine.fit.jober.aipp.fitable.agent.AippFlowAgent.fetchAgentErrorMsgToMain
        String status = log.getLogType().equals(AippInstLogType.ERROR.name())
                ? FlowTraceStatus.ERROR.name()
                : instance.getEntity().getStatus().orElse(null);

        AppChatRsp.Answer answer = this.buildAnswer(log);
        Map<String, Object> extensionMap = new HashMap<>();
        extensionMap.put("isEnableLog", log.isEnableLog());
        return AppChatRsp.builder()
                .chatId(log.getChatId())
                .atChatId(log.getAtChatId())
                .status(status)
                .instanceId(instanceId)
                .answer(Collections.singletonList(answer))
                .logId(log.getLogId())
                .extension(extensionMap)
                .build();
    }

    private AppChatRsp.Answer buildAnswer(AippLogVO log) {
        AppChatRsp.Answer.AnswerBuilder builder =
                AppChatRsp.Answer.builder().type(log.getLogType()).msgId(log.getMsgId());
        if (OUTPUT_WITH_MSG_WHITE_LIST.contains(StringUtils.toUpperCase(log.getLogType()))) {
            Object msg = JsonUtils.parseObject(log.getLogData()).get("msg");
            if (msg instanceof String) {
                msg = this.sensitiveFilterTools.filterString(ObjectUtils.cast(msg));
            }
            builder.content(msg);
        } else if (JsonUtils.isValidJson(log.getLogData())) {
            builder.content(JsonUtils.parseObject(log.getLogData()));
        } else {
            builder.content(log.getLogData());
        }
        return builder.build();
    }
}
