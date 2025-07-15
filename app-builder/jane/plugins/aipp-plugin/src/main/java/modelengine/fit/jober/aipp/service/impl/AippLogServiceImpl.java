/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.log.repository.AippLogRepository;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import modelengine.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import modelengine.fit.jober.aipp.dto.aipplog.AippLogQueryCondition;
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.AippTypeEnum;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.mapper.AippChatMapper;
import modelengine.fit.jober.aipp.mapper.AippLogMapper;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AopAippLogService;
import modelengine.fit.jober.aipp.service.UploadedFileManageService;
import modelengine.fit.jober.aipp.util.AippLogUtils;
import modelengine.fit.jober.aipp.util.DataUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.common.RangedResultSet;

import lombok.AllArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * aipp实例历史记录服务接口实现
 *
 * @author 刘信宏
 * @since 2024-01-08
 */
@Component
@AllArgsConstructor
public class AippLogServiceImpl implements AippLogService {
    private static final Logger log = Logger.get(AippLogServiceImpl.class);

    private final AippLogMapper aippLogMapper;
    private final AippChatMapper aippChatMapper;
    private final UploadedFileManageService uploadedFileManageService;
    private final AopAippLogService aopAippLogService;
    private final AppTaskInstanceService appTaskInstanceService;
    private final AppTaskService appTaskService;
    private final AippLogRepository aippLogRepository;

    private AippInstLog completeFormDataJson(AippInstLog instanceLog, OperationContext context) {
        if (AippInstLogType.FORM.name().equals(instanceLog.getLogType())) {
            AippLogData form = JsonUtils.parseObject(instanceLog.getLogData(), AippLogData.class);
            if (form == null) {
                return instanceLog;
            }
            Map<String, String> newLogData = MapBuilder.<String, String>get()
                    .put("formData", form.getFormData())
                    .put("formAppearance", form.getFormAppearance())
                    .build();
            instanceLog.setLogData(JsonUtils.toJsonString(newLogData));
        }
        return instanceLog;
    }

    /**
     * 查询指定aipp最近5个的历史记录
     *
     * @param appId 指定aipp的id
     * @param type 指定aipp的类型
     * @param context 登录信息
     * @return log数据
     */
    @Override
    public List<AippInstLogDataDto> queryAippRecentInstLog(String appId, String type, OperationContext context) {
        String aippType = AippTypeEnum.getType(type).type();
        List<String> metaIds = getMetaIds(appId, context, aippType);
        if (metaIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<AippInstLogDataDto> recentLogData = this.queryAippRecentInstLog(metaIds, aippType, 5, context);
        // 只对最后一个记录查询状态
        if (!recentLogData.isEmpty()) {
            AippInstLogDataDto lastLogData = recentLogData.get(recentLogData.size() - 1);
            String appSuiteId = lastLogData.getAippId();
            String version = lastLogData.getVersion();
            Optional<AppTask> taskOp = this.appTaskService.getLatest(appSuiteId, version, context);
            if (taskOp.isEmpty()) {
                return Collections.emptyList();
            }
            String versionId = taskOp.get().getEntity().getTaskId();
            Optional<AppTaskInstance> instanceOp = this.appTaskInstanceService.getInstance(versionId,
                    lastLogData.getInstanceId(), context);
            if (instanceOp.isEmpty()) {
                return Collections.emptyList();
            }
            lastLogData.setStatus(instanceOp.get().getEntity().getStatus().orElse(MetaInstStatusEnum.RUNNING.name()));
        }
        return recentLogData;
    }

    private List<String> getMetaIds(String appId, OperationContext context, String aippType) {
        return this.appTaskService.getTasksByAppId(appId, aippType, context)
                .stream()
                .filter(Objects::nonNull)
                .map(t -> t.getEntity().getAppSuiteId())
                .toList();
    }

    private List<AippInstLogDataDto> queryAippRecentInstLog(List<String> aippIds, String aippType, Integer count,
            OperationContext context) {
        List<String> instanceIds =
                this.aippLogMapper.selectRecentInstanceIdByAippIds(aippIds, aippType, count, context.getAccount());
        return this.queryAndSortLogs(instanceIds, context);
    }

    @Override
    public List<AippInstLogDataDto> queryAppRecentChatLog(String appId, String aippType, OperationContext context) {
        List<String> chatIds = aippChatMapper.selectChatByAppId(appId, aippType, 1);
        if (chatIds.isEmpty()) {
            return new ArrayList<>();
        }
        return this.queryChatRecentChatLog(chatIds.get(0), appId, context);
    }

    private List<AippInstLogDataDto> getAippLogWithAppInfo(List<AippInstLogDataDto> logData, String appId,
            OperationContext context) {
        // 获取被@应用的头像、名称
        List<String> originAippId = this.appTaskService.getTasksByAppId(appId, context)
                .stream()
                .filter(Objects::nonNull)
                .map(t -> t.getEntity().getAppSuiteId())
                .distinct()
                .toList();
        List<String> atAippIds = logData.stream()
                .map(AippInstLogDataDto::getAippId)
                .filter(aippId -> !originAippId.contains(aippId))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(atAippIds)) {
            return logData;
        }
        RangedResultSet<AppTask> resultSet = this.appTaskService.getTasks(
                AppTask.asQueryEntity(0, atAippIds.size()).latest().addAppSuiteIds(atAippIds).build(), context);
        if (resultSet.isEmpty()) {
            return logData;
        }
        Map<String, AppTask> taskMap = resultSet.getResults()
                .stream()
                .collect(Collectors.toMap(t -> t.getEntity().getAppSuiteId(), Function.identity()));
        return logData.stream().peek(l -> {
            if (!taskMap.containsKey(l.getAippId())) {
                return;
            }
            AppTask task = taskMap.get(l.getAippId());
            l.setAppName(task.getEntity().getName());
            l.setAppIcon(task.getEntity().getIcon());
        }).collect(Collectors.toList());
    }

    private List<AippInstLogDataDto> queryAndSortLogs(List<String> instanceIds, OperationContext context) {
        return instanceIds.stream()
                .map(id -> {
                    String metaVersionId = appTaskInstanceService.getTaskId(id);
                    return this.appTaskInstanceService.getInstance(metaVersionId, id, context);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(AppTaskInstance::toLogDataDto)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparing(AippInstLogDataDto::getCreateAt))
                .toList();
    }

    @Override
    public List<AippInstLogDataDto> queryChatRecentChatLog(String chatId, String appId,
            OperationContext context) {
        List<String> instanceIds = aippChatMapper.selectInstanceByChat(chatId, 10);
        List<AippInstLogDataDto> logData = queryAndSortLogs(instanceIds, context);
        return this.getAippLogWithAppInfo(logData, appId, context);
    }

    @Override
    public List<AippInstLogDataDto> queryRecentLogsSinceResume(String aippId, String aippType,
            OperationContext context) {
        List<String> instanceIds = aippLogMapper.selectRecentAfterResume(aippId, aippType, context.getAccount());
        // 该功能未上线，待测试
        return this.queryAndSortLogs(instanceIds, context);
    }

    private Map<String, List<AippInstLog>> queryRecentLogByInstanceIds(List<String> instanceIds,
            OperationContext context) {
        if (instanceIds == null || instanceIds.isEmpty()) {
            return new HashMap<>();
        }
        List<String> filterLogTypes =
                new ArrayList<>(Arrays.asList(AippInstLogType.HIDDEN_MSG.name(), AippInstLogType.HIDDEN_FORM.name()));
        List<AippInstLog> aippInstLogs = this.queryBatchAndFilterFullLogsByLogType(instanceIds, filterLogTypes);
        Map<String, List<AippInstLog>> result =
                instanceIds.stream().collect(Collectors.toMap(key -> key, key -> new ArrayList<>()));
        for (AippInstLog instLog : aippInstLogs) {
            AippInstLog newInstLog = completeFormDataJson(instLog, context);
            for (String key : instanceIds) {
                if (instLog.getPath().contains(key)) {
                    result.get(key).add(newInstLog);
                }
            }
        }
        return result;
    }

    private LocalDateTime getLocalDateTime(String timeString) {
        LocalDateTime sinceTime = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
            sinceTime = LocalDateTime.parse(timeString, formatter);
        } catch (DateTimeParseException e) {
            log.error("queryInstanceSince invalid after_at timestamp: {}, error: {}", timeString, e.getMessage());
        }
        return sinceTime;
    }

    /**
     * 查询指定aipp instance的历史记录, 可选开始时间
     *
     * @param instanceId 指定aipp instance的id
     * @param timeString 开始的时间范围, 可能为空
     * @return log数据
     */
    @Override
    public List<AippInstLog> queryInstanceLogSince(String instanceId, String timeString) {
        LocalDateTime sinceTime = Optional.ofNullable(timeString).map(this::getLocalDateTime).orElse(null);
        AippLogQueryCondition sqlCondition = AippLogQueryCondition.builder()
                .instanceId(instanceId)
                .afterAt(sinceTime)
                .build();
        return aippLogMapper.selectWithCondition(sqlCondition)
                .stream()
                .filter(AippLogServiceImpl::isNeededLog)
                .collect(Collectors.toList());
    }

    private static boolean isNeededLog(AippInstLog l) {
        return AippInstLogType.QUESTION.name().equals(l.getLogType()) || AippInstLogType.MSG.name()
                .equals(l.getLogType()) || AippInstLogType.FILE.name().equals(l.getLogType())
                || AippInstLogType.ERROR.name().equals(l.getLogType()) || AippInstLogType.QUESTION_WITH_FILE.name()
                .equals(l.getLogType());
    }

    private boolean checkLogValid(String lastLog, String cacheLog) {
        if (cacheLog == null) {
            return false;
        }
        if (lastLog == null) {
            return true;
        }
        if (lastLog.length() != cacheLog.length()) {
            return true;
        }
        return !lastLog.equals(cacheLog);
    }

    private String getLastLog(List<AippInstLog> aippInstLogs) {
        if (aippInstLogs.isEmpty()) {
            return null;
        }
        AippInstLog endLog = aippInstLogs.get(aippInstLogs.size() - 1);
        if (!AippInstLogType.MSG.name().equals(endLog.getLogType())) {
            return null;
        }
        AippLogData logData = JsonUtils.parseObject(endLog.getLogData(), AippLogData.class);
        return logData.getMsg();
    }

    /**
     * 查询指定aipp instance的form类型的最新一条历史记录
     *
     * @param instanceId 指定aipp instance的id
     * @return log数据
     */
    @Override
    public AippInstLog queryLastInstanceFormLog(String instanceId) {
        if (StringUtils.isBlank(instanceId)) {
            log.error("instanceId is null");
            throw new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID);
        }
        return aippLogMapper.selectLastInstanceFormLog(instanceId);
    }

    /**
     * 删除指定aipp的历史记录
     *
     * @param appId 指定aipp的id
     * @param type 指定aipp的类型
     * @param context 登录信息
     */
    @Override
    public void deleteAippInstLog(String appId, String type, OperationContext context) {
        String aippType = AippTypeEnum.getType(type).type();
        List<String> metaIds = getMetaIds(appId, context, aippType);
        if (metaIds.isEmpty()) {
            return;
        }
        List<String> instanceIdList =
                this.aippLogMapper.selectNormalInstanceIdOrderByTimeDesc(metaIds, aippType, context.getAccount());
        if (!instanceIdList.isEmpty()) {
            // check最后的实例是不是还在运行
            String instanceId = instanceIdList.get(0);
            String taskId = this.appTaskInstanceService.getTaskId(instanceId);
            this.appTaskInstanceService.getInstance(taskId, instanceId, context).ifPresent(appTaskInstance -> {
                String instanceIdExclude = null;
                if (appTaskInstance.isRunning()) {
                    instanceIdExclude = instanceId;
                } else {
                    this.uploadedFileManageService.cleanAippFiles(metaIds);
                }
                this.aippLogMapper.delete(metaIds, aippType, context.getAccount(), instanceIdExclude);
            });
        }
    }

    @Override
    public void deleteAippPreviewLog(String previewAippId, OperationContext context) {
        this.aippLogRepository.deleteAippPreviewLog(previewAippId, context);
    }

    /**
     * 插入aipp的历史记录
     *
     * @param logType 日志类型
     * @param logData 日志数据
     * @param businessData 业务数据
     * @return 日志id
     */
    @Override
    public String insertLogWithInterception(String logType, AippLogData logData, Map<String, Object> businessData) {
        AippLogCreateDto logCreateDto = this.buildAippLogCreateDto(logType, logData, businessData);
        if (logCreateDto == null) {
            return null;
        }
        return this.aopAippLogService.insertLog(logCreateDto);
    }

    private AippLogCreateDto buildAippLogCreateDto(String logType, AippLogData logData,
            Map<String, Object> businessData) {
        RunContext runContext = new RunContext(businessData, new OperationContext());
        String aippId = runContext.getAppSuiteId();
        String instId = runContext.getTaskInstanceId();
        String parentInstId = runContext.getParentInstanceId();
        String version = runContext.getAppVersion();
        String aippType = runContext.getAippType();
        String account = DataUtils.getOpContext(businessData).getAccount();
        if (!AippLogUtils.validFormMsg(logData, logType)) {
            return null;
        }
        String path = this.buildPath(instId, parentInstId);
        String chatId = runContext.getOriginChatId();
        String atChatId = runContext.getAtChatId();
        return AippLogCreateDto.builder()
                .aippId(aippId)
                .version(version)
                .aippType(aippType)
                .instanceId(instId)
                .logType(logType)
                .logData(JsonUtils.toJsonString(logData))
                .createUserAccount(account)
                .path(path)
                .chatId(chatId)
                .atChatId(atChatId)
                .isEnableLog(this.isEnableLog(businessData))
                .build();
    }

    private Boolean isEnableLog(Map<String, Object> businessData) {
        // 兼容老数据，老数据没有这个开关的时候（enableLog为null）默认返回true。
        // 有开关后（enableLog为null），返回enableLog的值
        return Objects.isNull(businessData.get(AippConst.BS_LLM_ENABLE_LOG))
                || ObjectUtils.<Boolean>cast(businessData.get(AippConst.BS_LLM_ENABLE_LOG));
    }

    /**
     * 插入ERROR类型的历史记录
     *
     * @param msg ERROR日志内容
     * @param flowData 流程执行上下文数据。
     */
    @Override
    public void insertErrorLog(String msg, List<Map<String, Object>> flowData) {
        AippLogData logData = AippLogData.builder().msg(msg).build();
        insertLogWithInterception(AippInstLogType.ERROR.name(), logData, DataUtils.getBusiness(flowData));
    }

    /**
     * 更新指定log id的记录
     *
     * @param logId 需要更新的记录的id
     * @param newLogData 新的记录数据
     * @throws IllegalArgumentException 如果logId为空，则抛出此异常
     */
    @Override
    public void updateLog(Long logId, String newLogData) throws IllegalArgumentException {
        if (logId == null) {
            log.error("logId is null");
            throw new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID);
        }
        aippLogMapper.updateOne(logId, newLogData);
    }

    @Override
    public void updateLogType(Long logId, String newLogType) throws IllegalArgumentException {
        if (logId == null) {
            log.error("logId is null");
            throw new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID);
        }
        this.aippLogMapper.updateLogType(logId, newLogType);
    }

    @Override
    public void updateLog(Long logId, String newLogType, String newLogData) throws IllegalArgumentException {
        if (logId == null) {
            log.error("logId is null");
            throw new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID);
        }
        this.aippLogRepository.updateDataAndType(logId, newLogType, newLogData);
    }

    @Override
    public String getParentPath(String parentInstId) {
        if (parentInstId == null) {
            return "";
        }
        return this.aippLogRepository.getParentPath(parentInstId);
    }

    @Override
    public String buildPath(String instId, String parentInstId) {
        String path;
        if (parentInstId == null) {
            path = AippLogUtils.PATH_DELIMITER + instId;
        } else {
            String parentPath = this.getParentPath(parentInstId);
            path = StringUtils.isEmpty(parentPath)
                    ? AippLogUtils.PATH_DELIMITER + instId
                    : String.join(AippLogUtils.PATH_DELIMITER, parentPath, instId);
        }
        return path;
    }

    @Override
    public List<AippInstLogDataDto> queryAippRecentInstLogAfterSplice(String aippId, String aippType, Integer count,
        OperationContext context) {
        List<String> instanceIds =
            aippLogMapper.selectRecentInstanceId(aippId, aippType, count, context.getAccount());
        // 该功能未上线，待测试
        return queryRecentLogByInstanceIds(instanceIds, context).values()
            .stream()
            .filter(CollectionUtils::isNotEmpty)
                .map((List<AippInstLog> rawLogs) -> AippInstLogDataDto.fromAippInstLogListAfterSplice(rawLogs,
                        this.appTaskInstanceService,
                        context))
            .sorted(Comparator.comparing(AippInstLogDataDto::getCreateAt))
            .collect(Collectors.toList());
    }

    @Override
    public List<AippInstLog> queryBatchAndFilterFullLogsByLogType(List<String> instanceIds,
            List<String> filterLogTypes) {
        if (CollectionUtils.isEmpty(instanceIds)) {
            log.error("Instance id list is null or empty.");
            throw new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID);
        }
        return this.aippLogMapper.getFullLogsByInstanceIds(instanceIds)
                .stream()
                .filter(log -> !filterLogTypes.contains(log.getLogType()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteLogs(List<Long> logIds) {
        if (CollectionUtils.isEmpty(logIds)) {
            log.error("logIds is null or empty.");
            return;
        }
        this.aippLogMapper.deleteInstanceLogs(logIds);
    }

    @Override
    public void insertLog(String logType, AippLogData logData, Map<String, Object> businessData) {
        AippLogCreateDto logCreateDto = this.buildAippLogCreateDto(logType, logData, businessData);
        if (logCreateDto == null) {
            return;
        }
        this.aippLogMapper.insertOne(logCreateDto);
    }
}
