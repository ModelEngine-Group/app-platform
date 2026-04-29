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
import modelengine.fitframework.log.Logger;
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
    private static final Logger log = Logger.get(AippLogStreamServiceImpl.class);

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
    public void send(AippLogVO aippLog) {
        if (!aippLog.displayable()) {
            return;
        }
        AppChatRsp appChatRsp = this.buildData(aippLog);

        if (!appChatRsp.getStatus().equalsIgnoreCase(FlowTraceStatus.RUNNING.name()) && !appChatRsp.getStatus()
                .equalsIgnoreCase(FlowTraceStatus.READY.name())) {
            this.appChatSseService.sendLastData(aippLog.getInstanceId(), appChatRsp);
        } else {
            this.appChatSseService.send(aippLog.getInstanceId(), appChatRsp);
        }
    }

    private AppChatRsp buildData(AippLogVO aippLog) {
        String instanceId = aippLog.getInstanceId();
        AppTaskInstance instance = this.appTaskInstanceService.getInstanceById(instanceId, null)
                .orElseThrow(() -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                        StringUtils.format("App task instance[{0}] not found.", instanceId)));

        // 日志流阶段统一透出 RUNNING，避免中间态误触发前端结束动画。
        String status = aippLog.getLogType().equals(AippInstLogType.ERROR.name())
            ? FlowTraceStatus.ERROR.name()
                : instance.getEntity().getStatus().orElse(null);

        AppChatRsp.Answer answer = this.buildAnswer(aippLog);
        Map<String, Object> extensionMap = new HashMap<>();
        extensionMap.put("isEnableLog", aippLog.isEnableLog());
        return AppChatRsp.builder()
                .chatId(aippLog.getChatId())
                .atChatId(aippLog.getAtChatId())
                .status(status)
                .instanceId(instanceId)
                .answer(Collections.singletonList(answer))
                .logId(aippLog.getLogId())
                .extension(extensionMap)
                .build();
    }

    private AppChatRsp.Answer buildAnswer(AippLogVO aippLog) {
        AppChatRsp.Answer.AnswerBuilder builder =
                AppChatRsp.Answer.builder().type(aippLog.getLogType()).msgId(aippLog.getMsgId());
        if (OUTPUT_WITH_MSG_WHITE_LIST.contains(StringUtils.toUpperCase(aippLog.getLogType()))) {
            Object msg = JsonUtils.parseObject(aippLog.getLogData()).get("msg");
            if (msg instanceof String) {
                msg = this.sensitiveFilterTools.filterString(ObjectUtils.cast(msg));
            }
            builder.content(msg);
        } else if (JsonUtils.isValidJson(aippLog.getLogData())) {
            builder.content(JsonUtils.parseObject(aippLog.getLogData()));
        } else {
            builder.content(aippLog.getLogData());
        }
        return builder.build();
    }
}
