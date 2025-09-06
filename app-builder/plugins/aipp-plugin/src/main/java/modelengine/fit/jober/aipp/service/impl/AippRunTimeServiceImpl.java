/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static modelengine.fit.jober.aipp.common.exception.AippExceptionHandler.LOCALES;
import static modelengine.fit.jober.aipp.enums.AippTypeEnum.NORMAL;
import static modelengine.fit.jober.aipp.util.UsefulUtils.doIfNull;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import modelengine.fit.dynamicform.DynamicFormService;
import modelengine.fit.dynamicform.entity.DynamicFormDetailEntity;
import modelengine.fit.dynamicform.entity.FormMetaQueryParameter;
import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.PageResponse;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippForbiddenException;
import modelengine.fit.jober.aipp.condition.AippInstanceQueryCondition;
import modelengine.fit.jober.aipp.condition.PaginationCondition;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.TaskInstanceDecorator;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.dto.AippInstanceDto;
import modelengine.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import modelengine.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.entity.ChatSession;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.enums.RestartModeEnum;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AippRunTimeService;
import modelengine.fit.jober.aipp.service.AopAippLogService;
import modelengine.fit.jober.aipp.service.AppChatSessionService;
import modelengine.fit.jober.aipp.service.AppChatSseService;
import modelengine.fit.jober.aipp.service.RuntimeInfoService;
import modelengine.fit.jober.aipp.util.CacheUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.aipp.vo.MetaVo;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.authentication.context.UserContextHolder;
import modelengine.jade.common.locale.LocaleUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * aipp运行时服务层接口实现
 *
 * @author 刘信宏
 * @since 2023-12-15
 */
@Component
public class AippRunTimeServiceImpl
        implements AippRunTimeService, modelengine.fit.jober.aipp.genericable.AippRunTimeService {
    private static final Logger log = Logger.get(AippRunTimeServiceImpl.class);
    private static final String PARENT_CALLBACK_ID = "modelengine.fit.jober.aipp.fitable.LLMComponentCallback";
    private static final List<String> MEMORY_MSG_TYPE_WHITE_LIST = Arrays.asList(AippInstLogType.MSG.name(),
            AippInstLogType.FORM.name(),
            AippInstLogType.META_MSG.name());
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);

    private final DynamicFormService dynamicFormService;
    private final FlowInstanceService flowInstanceService;
    private final AippLogService aippLogService;
    private final AopAippLogService aopAippLogService;
    private final AppChatSessionService appChatSessionService;
    private final RuntimeInfoService runtimeInfoService;
    private final AppTaskInstanceService appTaskInstanceService;
    private final AppTaskService appTaskService;
    private final AppVersionService appVersionService;
    private final AppChatSseService appChatSseService;

    public AippRunTimeServiceImpl(@Fit DynamicFormService dynamicFormService,
            @Fit FlowInstanceService flowInstanceService, @Fit AippLogService aippLogService,
            @Fit AopAippLogService aopAippLogService, AppChatSessionService appChatSessionService,
            @Fit RuntimeInfoService runtimeInfoService, AppTaskInstanceService appTaskInstanceService,
            AppTaskService appTaskService, AppVersionService appVersionService, AppChatSseService appChatSseService) {
        this.dynamicFormService = dynamicFormService;
        this.flowInstanceService = flowInstanceService;
        this.aippLogService = aippLogService;
        this.aopAippLogService = aopAippLogService;
        this.appChatSessionService = appChatSessionService;
        this.runtimeInfoService = runtimeInfoService;
        this.appTaskInstanceService = appTaskInstanceService;
        this.appTaskService = appTaskService;
        this.appVersionService = appVersionService;
        this.appChatSseService = appChatSseService;
    }

    /**
     * 启动一个Aipp
     *
     * @param aippId aippId
     * @param version aipp 版本
     * @param initContext 表示start表单填充的内容，作为流程初始化的businessData。 例如 图片url, 文本输入, prompt
     * @param context 操作上下文
     * @return 实例id
     */
    @Override
    @Fitable("default")
    public String createAippInstance(String aippId, String version, Map<String, Object> initContext,
            OperationContext context) {
        AppTask task = this.appTaskService.getLatest(aippId, version, context).orElseThrow(() -> {
            log.error("The app task is not found. [appSuiteId={}, version={}]", aippId, version);
            return new AippException(AippErrCode.APP_NOT_FOUND);
        });

        // 启动任务.
        RunContext ctx = new RunContext(ObjectUtils.cast(initContext.get(AippConst.BS_INIT_CONTEXT_KEY)), context);
        ctx.initStartParams();
        doIfNull(ctx.getRestartMode(), () -> ctx.setRestartMode(RestartModeEnum.OVERWRITE.getMode()));
        ctx.setStartTime(LocalDateTime.now());
        task.run(ctx);
        return ctx.getTaskInstanceId();
    }

    /**
     * 根据 App 唯一标识启动一个最新的 Aipp 实例。
     *
     * @param appId App 唯一标识。
     * @param isDebug 是否调试启动。
     * @param initContext 流程初始化的businessData。
     * @param context 操作上下文。
     * @return Aipp 实例唯一标识。
     */
    @Override
    @Fitable("default")
    public String createLatestAippInstanceByAppId(String appId, boolean isDebug, Map<String, Object> initContext,
            OperationContext context) {
        AppTask task = CacheUtils.getAppTaskByAppId(this.appVersionService, appId, isDebug, context);

        // 启动任务.
        RunContext ctx = new RunContext(ObjectUtils.cast(initContext.get(AippConst.BS_INIT_CONTEXT_KEY)), context);
        ctx.initStartParams();
        doIfNull(ctx.getRestartMode(), () -> ctx.setRestartMode(RestartModeEnum.OVERWRITE.getMode()));
        ctx.setStartTime(LocalDateTime.now());
        task.run(ctx);
        return ctx.getFlowTraceId();
    }

    @Override
    public MetaVo queryLatestMetaVoByAppId(String appId, boolean isDebug, OperationContext context) {
        AppTask task = CacheUtils.getAppTaskByAppId(this.appVersionService, appId, isDebug, context);
        return MetaVo.builder().id(task.getEntity().getAppSuiteId()).version(task.getEntity().getVersion()).build();
    }

    @Override
    @Fitable("default")
    public Boolean isInstanceRunning(String instanceId, OperationContext context) {
        Optional<AppTaskInstance> instanceOptional = appTaskInstanceService.getInstanceById(instanceId, context);
        if (instanceOptional.isEmpty()) {
            return false;
        }
        Map<String, Object> instInfo = instanceOptional.get().getEntity().getInfos();
        if (!instInfo.containsKey(AippConst.INST_STATUS_KEY)) {
            return false;
        }
        return MetaInstStatusEnum.getMetaInstStatus(ObjectUtils.cast(instInfo.get(AippConst.INST_STATUS_KEY)))
                == MetaInstStatusEnum.RUNNING;
    }

    @Override
    public Choir<Object> startFlowWithUserSelectMemory(String taskInstanceId, Map<String, Object> initContext,
            OperationContext context, boolean isDebug) {
        Map<String, Object> businessData = ObjectUtils.cast(initContext.get(AippConst.BS_INIT_CONTEXT_KEY));
        String taskId = this.appTaskInstanceService.getTaskId(taskInstanceId);
        AppTaskInstance instance = this.appTaskInstanceService.getInstance(taskId, taskInstanceId, context)
                .orElseThrow(() -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                        StringUtils.format("App task[{0}] instance[{1}] not found.", taskId, taskInstanceId)));

        AppTask task = this.appTaskService.getTaskById(taskId, context).orElseThrow(() -> {
            log.error("The task is not found. [taskId={}]", taskId);
            return new AippException(AippErrCode.TASK_NOT_FOUND);
        });
        Locale locale = this.getLocale();
        return Choir.create(emitter -> {
            ChatSession<Object> chatSession = new ChatSession<>(emitter, task.getEntity().getAppId(), isDebug, locale);
            this.appChatSessionService.addSession(instance.getParentInstanceId(), chatSession);
            RunContext runContext = new RunContext(businessData, context);
            runContext.initStartParams();
            runContext.setAppSuiteId(task.getEntity().getAppSuiteId());
            runContext.setAppVersion(task.getEntity().getVersion());
            runContext.setAippType(Optional.ofNullable(task.getEntity().getAippType()).orElse(NORMAL.name()));
            runContext.setTaskId(task.getEntity().getTaskId());
            runContext.setTaskInstanceId(taskInstanceId);
            runContext.setHttpContext(JSONUtils.toJSONString(context));
            runContext.setParentInstanceId(instance.getParentInstanceId());
            runContext.setCallbackId(PARENT_CALLBACK_ID);
            runContext.setUserId(context.getOperator());
            runContext.setAppTask(task);
            TaskInstanceDecorator.create(instance)
                    .chat(this.appChatSessionService, this.appChatSseService)
                    .exceptionLog(this.appTaskInstanceService, this.aippLogService)
                    .run(runContext, chatSession);
        });
    }

    private Locale getLocale() {
        Locale locale = Locale.getDefault();
        if (UserContextHolder.get() != null && StringUtils.isNotEmpty(UserContextHolder.get().getLanguage())) {
            List<Locale.LanguageRange> list = Locale.LanguageRange.parse(UserContextHolder.get().getLanguage());
            locale = CollectionUtils.isEmpty(list) ? Locale.getDefault() : Locale.lookup(list, LOCALES);
        }
        return locale;
    }

    /**
     * 将日志数据转换为前端可展示的格式
     *
     * @param logs 日志数据列表
     * @return 转换后的前端展示数据列表
     */
    public static List<Map<String, Object>> getLogMaps(List<AippInstLogDataDto> logs) {
        List<Map<String, Object>> memories = new ArrayList<>();
        logs.forEach(log -> {
            Map<String, Object> logMap = new HashMap<>();
            AippInstLogDataDto.AippInstanceLogBody question = log.getQuestion();
            if (question == null) {
                return;
            }
            logMap.put("question", getLogData(question.getLogData(), question.getLogType()));
            List<AippInstLogDataDto.AippInstanceLogBody> answers = log.getInstanceLogBodies()
                    .stream()
                    .filter(item -> MEMORY_MSG_TYPE_WHITE_LIST.contains(StringUtils.toUpperCase(item.getLogType())))
                    .toList();
            List<AippInstLogDataDto.AippInstanceLogBody> files = log.getInstanceLogBodies()
                    .stream()
                    .filter(l -> l.getLogType().equals(AippInstLogType.FILE.name()))
                    .toList();
            if (!answers.isEmpty()) {
                AippInstLogDataDto.AippInstanceLogBody logBody = answers.get(answers.size() - 1);
                logMap.put("answer", getLogData(logBody.getLogData(), logBody.getLogType()));
            }
            if (!files.isEmpty()) {
                AippInstLogDataDto.AippInstanceLogBody fileBody = files.get(0);
                logMap.put("fileDescription", getLogData(fileBody.getLogData(), fileBody.getLogType()));
            }
            memories.add(logMap);
        });
        return memories.stream().filter(Objects::nonNull).collect(Collectors.toList());
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
     * 删除应用实例
     *
     * @param aippId aippId
     * @param version aipp 版本
     * @param instanceId 实例id
     * @param context 操作上下文
     */
    @Override
    public void deleteAippInstance(String aippId, String version, String instanceId, OperationContext context) {
        AppTask task = this.appTaskService.getLatest(aippId, version, context).orElseThrow(() -> {
            log.error("The app task is not found. [appSuiteId={}, version={}]", aippId, version);
            return new AippException(AippErrCode.APP_NOT_FOUND);
        });
        String taskId = task.getEntity().getTaskId();
        AppTaskInstance instance = this.appTaskInstanceService.getInstance(taskId, instanceId, null)
                .orElseThrow(() -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                        StringUtils.format("App task instance[{0}] not found.", instanceId)));
        if (!instance.isRunning()) {
            log.error("aipp {} version {} inst{}, not allow terminate.", aippId, version, instanceId);
            throw new AippForbiddenException(context, AippErrCode.DELETE_INSTANCE_FORBIDDEN);
        }
        this.appTaskInstanceService.delete(taskId, instanceId, context);
    }

    private AippInstanceDto buildAippInstanceDtoFromEntityList(AppTaskInstance instance,
            List<Map<FormMetaQueryParameter, DynamicFormDetailEntity>> entityMaps, OperationContext context) {
        String formId = instance.getEntity().getFormId();
        String version = instance.getEntity().getFormVersion();
        DynamicFormDetailEntity entity = null;
        if (this.checkParameter(formId, version) && !entityMaps.isEmpty()) {
            entity = entityMaps.stream().filter(Objects::nonNull).filter(map -> {
                FormMetaQueryParameter parameterOfMap = map.keySet().iterator().next();
                return Objects.equals(parameterOfMap.getFormId(), formId) && Objects.equals(parameterOfMap.getVersion(),
                        version);
            }).map(Map::values).flatMap(Collection::stream).findFirst().orElse(null);
        }
        return AippInstanceDto.builder()
                .aippInstanceId(instance.getId())
                .tenantId(context.getTenantId())
                .aippInstanceName(instance.getEntity().getName())
                .status(instance.getEntity().getStatus().orElse(null))
                .formMetadata(entity == null ? null : entity.getData())
                .formArgs(instance.getEntity().getStringInfos())
                .startTime(instance.getEntity().getCreateTime())
                .endTime(instance.getEntity().getFinishTime(null))
                .aippInstanceLogs(null)
                .build();
    }

    private boolean checkParameter(String formId, String version) {
        if (StringUtils.isEmpty(formId) || Objects.equals(formId, AippConst.INVALID_FORM_ID)) {
            return false;
        }
        return StringUtils.isNotEmpty(version) && !Objects.equals(version, AippConst.INVALID_FORM_VERSION_ID);
    }

    /**
     * 更新表单数据，并恢复实例任务执行
     *
     * @param instanceId 实例id
     * @param formArgs 用于填充表单的数据
     * @param context 操作上下文
     * @param logId 日志id
     * @param isDebug 是否是调试状态
     * @return 返回一个Choir对象，用于流式处理
     */
    @Override
    @Transactional
    public Choir<Object> resumeAndUpdateAippInstance(String instanceId, Map<String, Object> formArgs, Long logId,
            OperationContext context, boolean isDebug) {
        String taskId = this.appTaskInstanceService.getTaskId(instanceId);
        AppTask appTask = this.appTaskService.getTaskById(taskId, context)
                .orElseThrow(() -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                        StringUtils.format("App task[{0}] not found.", taskId)));

        Locale locale = LocaleUtil.getLocale();
        return Choir.create(emitter -> {
            this.appChatSessionService.addSession(instanceId,
                    new ChatSession<>(emitter, appTask.getEntity().getAppId(), isDebug, locale));
            appTask.resume(instanceId, logId, formArgs, context);
        });
    }

    @Override
    public String terminateInstance(String instanceId, Map<String, Object> msgArgs, Long logId,
            OperationContext context) {
        this.aippLogService.updateLogType(logId, AippInstLogType.HIDDEN_FORM.name());
        String message = this.terminateInstance(instanceId, msgArgs, context);
        this.runtimeInfoService.insertRuntimeInfo(instanceId,
                msgArgs,
                MetaInstStatusEnum.TERMINATED,
                StringUtils.EMPTY,
                context);
        return message;
    }

    /**
     * 终止aipp实例
     *
     * @param context 操作上下文
     * @param instanceId 实例id
     * @param msgArgs 用于终止时返回的信息
     * @return 终止对话后返回的信息
     */
    @Override
    public String terminateInstance(String instanceId, Map<String, Object> msgArgs, OperationContext context) {
        String versionId = this.appTaskInstanceService.getTaskId(instanceId);
        AppTaskInstance instance = this.appTaskInstanceService.getInstance(versionId, instanceId, null)
                .orElseThrow(() -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                        StringUtils.format("App task instance[{0}] not found.", instanceId)));

        AppTask appTask = this.appTaskService.getTaskById(versionId, context)
                .orElseThrow(() -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                        StringUtils.format("App task[{0}] not found.", versionId)));
        String aippId = appTask.getEntity().getAppSuiteId();
        if (instance.is(MetaInstStatusEnum.READY, MetaInstStatusEnum.TERMINATED)) {
            log.error("aipp {} inst{}, not allow terminate.", aippId, instanceId);
            throw new AippException(context, AippErrCode.TERMINATE_INSTANCE_FORBIDDEN);
        }
        if (instance.isRunning()) {
            String flowTraceId = instance.getEntity().getFlowTranceId();
            Validation.notNull(flowTraceId, "flowTraceId can not be null");
            try {
                this.flowInstanceService.terminateFlows(
                        null,
                        flowTraceId,
                        Collections.emptyMap(),
                        context);
            } catch (JobberException e) {
                log.error("terminate flow failed, flowTraceId:{}.", flowTraceId);
                throw new AippException(context, AippErrCode.TERMINATE_INSTANCE_FAILED);
            }

            // 更新实例状态
            AppTaskInstance updateEntity = AppTaskInstance.asUpdate(versionId, instanceId)
                    .setFinishTime(LocalDateTime.now())
                    .setStatus(MetaInstStatusEnum.TERMINATED.name())
                    .build();
            this.appTaskInstanceService.update(updateEntity, context);
        }
        String message = this.getTerminateMessage(msgArgs);
        String version = appTask.getEntity().getVersion();
        this.aopAippLogService.insertLog(AippLogCreateDto.builder()
                .aippId(aippId)
                .version(version)
                .aippType(appTask.getEntity().getAippType())
                .instanceId(instanceId)
                .logType(AippInstLogType.MSG.name())
                .logData(JsonUtils.toJsonString(AippLogData.builder().msg(message).build()))
                .createUserAccount(context.getAccount())
                .path(this.aippLogService.buildPath(instanceId, null)) // 这块在子流程调用时，得考虑下
                .build());
        return message;
    }

    private String getTerminateMessage(Map<String, Object> msgArgs) {
        return msgArgs.get(AippConst.TERMINATE_MESSAGE_KEY) != null ? msgArgs.get(AippConst.TERMINATE_MESSAGE_KEY)
                .toString() : "已终止对话";
    }

    /**
     * 终止aipp全部实例
     *
     * @param aippId aipp Id
     * @param versionId versionId
     * @param isDeleteLog 是否删除aipp log
     * @param context 操作上下文
     */
    @Override
    public void terminateAllPreviewInstances(String aippId, String versionId, boolean isDeleteLog,
            OperationContext context) {
        Stream<AppTaskInstance> instanceStream = this.appTaskInstanceService.getInstanceStreamByTaskId(versionId, 15,
                context);

        // 只停止正在运行的
        instanceStream.filter(AppTaskInstance::isRunning).forEach(instance -> {
            String flowTraceId = instance.getEntity().getFlowTranceId();
            Validation.notNull(flowTraceId, "flowTraceId can not be null");
            flowInstanceService.terminateFlows(null, flowTraceId, Collections.emptyMap(), context);

            // 更新实例状态.
            AppTaskInstance updateEntity = AppTaskInstance.asUpdate(versionId, instance.getId())
                    .setFinishTime(LocalDateTime.now())
                    .setStatus(MetaInstStatusEnum.TERMINATED.name())
                    .build();
            this.appTaskInstanceService.update(updateEntity, context);
        });

        if (isDeleteLog) {
            aippLogService.deleteAippPreviewLog(aippId, context);
        }
    }
}
