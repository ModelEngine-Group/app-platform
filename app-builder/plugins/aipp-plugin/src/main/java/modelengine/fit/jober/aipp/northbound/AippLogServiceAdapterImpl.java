/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jober.aipp.northbound;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.aipplog.AippInstLogData;
import modelengine.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import modelengine.fit.jober.aipp.genericable.adapter.AippLogServiceAdapter;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fitframework.annotation.Component;

import java.util.List;

/**
 * {@link AippLogServiceAdapter} 的适配器类的实现类。
 *
 * @author 陈潇文
 * @since 2025-07-08
 */
@Component
public class AippLogServiceAdapterImpl implements AippLogServiceAdapter {
    private final AippLogService aippLogService;

    /**
     * 用 aipp 实例历史记录服务接口的 {@link AippLogService} 构造 {@link AippLogServiceAdapterImpl}。
     *
     * @param aippLogService 表示 aipp 实例历史记录服务接口的 {@link AippLogService}。
     */
    public AippLogServiceAdapterImpl(AippLogService aippLogService) {
        this.aippLogService = aippLogService;
    }

    @Override
    public List<AippInstLogData> queryChatRecentChatLog(String chatId, String appId, OperationContext context) {
        List<AippInstLogDataDto> logDataDtoList = this.aippLogService.queryChatRecentChatLog(chatId, appId, context);
        return logDataDtoList.stream()
                .map(dto -> AippInstLogData.builder()
                        .aippId(dto.getAippId())
                        .version(dto.getVersion())
                        .instanceId(dto.getInstanceId())
                        .status(dto.getStatus())
                        .appName(dto.getAppName())
                        .appIcon(dto.getAppIcon())
                        .createAt(dto.getCreateAt())
                        .question(this.convertQuestion(dto.getQuestion()))
                        .instanceLogBodies(this.convertLogBodies(dto.getInstanceLogBodies()))
                        .build())
                .toList();
    }

    private AippInstLogData.AippInstLogBody convertBody(AippInstLogDataDto.AippInstanceLogBody dtoBody) {
        if (dtoBody == null) {
            return null;
        }
        return AippInstLogData.AippInstLogBody.builder()
                .logId(dtoBody.getLogId())
                .logData(dtoBody.getLogData())
                .logType(dtoBody.getLogType())
                .createAt(dtoBody.getCreateAt())
                .createUserAccount(dtoBody.getCreateUserAccount())
                .build();
    }

    private AippInstLogData.AippInstLogBody convertQuestion(AippInstLogDataDto.AippInstanceLogBody question) {
        if (question == null) {
            return null;
        }
        return this.convertBody(question);
    }

    private List<AippInstLogData.AippInstLogBody> convertLogBodies(
            List<AippInstLogDataDto.AippInstanceLogBody> logBodies) {
        if (logBodies == null) {
            return null;
        }
        return logBodies.stream().map(this::convertBody).toList();
    }
}
