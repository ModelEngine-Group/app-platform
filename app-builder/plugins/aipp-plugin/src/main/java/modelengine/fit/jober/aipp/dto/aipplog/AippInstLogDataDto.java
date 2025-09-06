/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.aipplog;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.domains.log.AppLog;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.JobberException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * aipp实例历史记录数据
 *
 * @author 刘信宏
 * @since 2024-01-08
 */
@AllArgsConstructor
@Data
public class AippInstLogDataDto {
    private static final Logger log = Logger.get(AippInstLogDataDto.class);
    private static final int LOG_COUNT_AFTER_TZ_TOOL = 3;
    private static final HashSet<String> QUESTION_TYPE = new HashSet<>(Arrays.asList(AippInstLogType.QUESTION.name(),
            AippInstLogType.HIDDEN_QUESTION.name(),
            AippInstLogType.QUESTION_WITH_FILE.name()));
    private static final List<String> MEMORY_MSG_TYPE_WHITE_LIST = Arrays.asList(AippInstLogType.MSG.name(),
            AippInstLogType.FORM.name(),
            AippInstLogType.META_MSG.name());

    private String aippId;
    private String version;
    private String instanceId;
    private String status;
    private String appName;
    private String appIcon;
    private LocalDateTime createAt;
    private AippInstanceLogBody question;
    private List<AippInstanceLogBody> instanceLogBodies;

    public AippInstLogDataDto(AppTaskInstance instance, List<AppLog> logs) {
        if (CollectionUtils.isNotEmpty(logs)) {
            this.aippId = logs.get(0).getLogData().getAippId();
            this.version = logs.get(0).getLogData().getVersion();
            this.instanceId = logs.get(0).getLogData().getInstanceId();
            this.status = instance.getEntity().getStatus().orElse(MetaInstStatusEnum.ARCHIVED.name());
            this.appName = null;
            this.appIcon = null;
            this.createAt = logs.get(0).getLogData().getCreateAt();
            this.question = logs.stream().filter(AppLog::isQuestionType).findFirst().map(AppLog::toBody).orElse(null);
            this.instanceLogBodies = logs.stream()
                    .filter(l -> !l.isQuestionType())
                    .map(AppLog::toBody)
                    .sorted(Comparator.comparing(AippInstanceLogBody::getLogId))
                    .collect(Collectors.toList());
        }
    }

    /**
     * 从原始日志列表中获取实例历史记录
     *
     * @param rawLogs 表示日志列表的 {@link List}{@code <}{@link AippInstLog}{@code >}
     * @param context 表示操作上下文的 {@link OperationContext}
     * @param appTaskInstanceService 表示app任务实例服务的 {@link AppTaskInstanceService}
     * @return 实例历史记录
     */
    public static AippInstLogDataDto fromAippInstLogList(List<AippInstLog> rawLogs, OperationContext context,
            AppTaskInstanceService appTaskInstanceService) {
        List<AippInstLog> instanceLogs = rawLogs.stream()
                .sorted((d1, d2) -> Math.toIntExact(d1.getLogId() - d2.getLogId()))
                .toList();

        final String inAippId = instanceLogs.get(0).getAippId();
        final String inAippVersion = instanceLogs.get(0).getVersion();
        final String inInstanceId = instanceLogs.get(0).getInstanceId();
        final LocalDateTime inCreateAt = instanceLogs.get(0).getCreateAt();

        List<AippInstLogDataDto.AippInstanceLogBody> logBodies = instanceLogs.stream()
                .filter(log -> !(QUESTION_TYPE.contains(log.getLogType())))
                .map(AippInstLogDataDto::convert)
                .collect(Collectors.toList());
        AippInstLog questionInfo = instanceLogs.stream()
                .filter(log -> QUESTION_TYPE.contains(log.getLogType()))
                .findFirst()
                .orElse(null);
        String metaVersionId = appTaskInstanceService.getTaskId(inInstanceId);
        AppTaskInstance instance = appTaskInstanceService.getInstance(metaVersionId, inInstanceId, context)
                .orElseThrow(() -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                        StringUtils.format("App task instance[{0}] not found.", inInstanceId)));
        String status = instance.getEntity().getStatus().orElse(MetaInstStatusEnum.ARCHIVED.name());
        return new AippInstLogDataDto(inAippId, inAippVersion, inInstanceId, status, null, null, inCreateAt,
                convert(questionInfo), logBodies);
    }

    /**
     * 获取经过AI提示词拼接工具处理后的历史记录
     *
     * @param rawLogs 表示日志列表的 {@link List}{@code <}{@link AippInstLog}{@code >}
     * @param appTaskInstanceService 表示app任务实例服务的 {@link AppTaskInstanceService}
     * @param context 表示操作上下文的 {@link OperationContext}
     * @return 实例历史记录
     */
    public static AippInstLogDataDto fromAippInstLogListAfterSplice(List<AippInstLog> rawLogs,
            AppTaskInstanceService appTaskInstanceService, OperationContext context) {
        List<AippInstLog> updatedLogs = rawLogs.stream()
            .filter(log -> !isUserQuestionLogBeforeTzTool(rawLogs, log))
            .collect(Collectors.toList());

        return fromAippInstLogList(updatedLogs, context, appTaskInstanceService);
    }

    private static boolean isUserQuestionLogBeforeTzTool(List<AippInstLog> rawLogs, AippInstLog log) {
        return rawLogs.size() == LOG_COUNT_AFTER_TZ_TOOL && AippInstLogType.QUESTION.name().equals(log.getLogType());
    }

    /**
     * 将日志转换为memory.
     *
     * @return {@link Optional}{@code <}{@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >} 对象.
     */
    public Optional<Map<String, Object>> toMemory() {
        Map<String, Object> logMap = new HashMap<>();
        AippInstLogDataDto.AippInstanceLogBody questionBody = this.getQuestion();
        if (questionBody == null) {
            return Optional.empty();
        }
        logMap.put("question", getLogData(questionBody.getLogData(), questionBody.getLogType()));
        List<AippInstLogDataDto.AippInstanceLogBody> answers = this.getInstanceLogBodies()
                .stream()
                .filter(item -> MEMORY_MSG_TYPE_WHITE_LIST.contains(StringUtils.toUpperCase(item.getLogType())))
                .toList();
        List<AippInstLogDataDto.AippInstanceLogBody> files = this.getInstanceLogBodies()
                .stream()
                .filter(l -> StringUtils.equals(l.getLogType(), AippInstLogType.FILE.name()))
                .toList();
        if (!answers.isEmpty()) {
            AippInstLogDataDto.AippInstanceLogBody logBody = answers.get(answers.size() - 1);
            logMap.put("answer", getLogData(logBody.getLogData(), logBody.getLogType()));
        }
        if (!files.isEmpty()) {
            AippInstLogDataDto.AippInstanceLogBody fileBody = files.get(0);
            logMap.put("fileDescription", getLogData(fileBody.getLogData(), fileBody.getLogType()));
        }
        return Optional.of(logMap);
    }

    private static String getLogData(String logData, String logType) {
        Map<String, String> logInfo = ObjectUtils.cast(JSON.parse(logData));
        if (!StringUtils.isEmpty(logInfo.get("form_args"))) {
            return logInfo.get("form_args");
        }
        String msg = logInfo.get("msg");
        if (Objects.equals(logType, AippInstLogType.META_MSG.name())) {
            Map<String, Object> referenceMsg = ObjectUtils.cast(JSON.parse(msg));
            return ObjectUtils.cast(referenceMsg.get("data"));
        }
        if (Objects.equals(logType, AippInstLogType.QUESTION_WITH_FILE.name())) {
            return JSONObject.parseObject(msg).getString("question");
        }
        return msg;
    }

    /**
     * 转换实例日志为实例日志体
     */
    @AllArgsConstructor
    @Getter
    public static class AippInstanceLogBody {
        private long logId;
        private String logData;
        private String logType;
        private LocalDateTime createAt;
        private String createUserAccount;
    }

    static AippInstLogDataDto.AippInstanceLogBody convert(AippInstLog instanceLog) {
        if (instanceLog == null) {
            return null;
        }
        return new AippInstanceLogBody(instanceLog.getLogId(),
            instanceLog.getLogData(),
            instanceLog.getLogType(),
            instanceLog.getCreateAt(),
            instanceLog.getCreateUserAccount());
    }
}
