/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.task;

import static modelengine.fit.jober.aipp.constants.AippConst.BUSINESS_INPUT_KEY;
import static modelengine.fit.jober.aipp.enums.AippInstLogType.HIDDEN_QUESTION;
import static modelengine.fit.jober.aipp.enums.AippInstLogType.QUESTION;
import static modelengine.fit.jober.aipp.enums.AippInstLogType.QUESTION_WITH_FILE;
import static modelengine.fit.jober.aipp.enums.AippTypeEnum.NORMAL;
import static modelengine.fit.jober.aipp.util.UsefulUtils.doIfNotBlank;

import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;
import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.task.domain.type.DateTimeConverter;
import modelengine.fit.jober.aipp.common.AppTaskRunnable;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.convertor.FormMetaConvertor;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.log.repository.AippLogRepository;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.TaskInstanceDecorator;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.dto.AppInputParam;
import modelengine.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.entity.ChatSession;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.AippMetaStatusEnum;
import modelengine.fit.jober.aipp.enums.AippTypeEnum;
import modelengine.fit.jober.aipp.enums.JaneCategory;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.service.AopAippLogService;
import modelengine.fit.jober.aipp.service.AppChatSessionService;
import modelengine.fit.jober.aipp.service.AppChatSseService;
import modelengine.fit.jober.aipp.util.AippLogUtils;
import modelengine.fit.jober.aipp.util.DataUtils;
import modelengine.fit.jober.aipp.util.FormUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.aipp.util.UsefulUtils;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 应用任务对象.
 *
 * @author 张越
 * @since 2025-01-03
 */
public class AppTask implements AppTaskRunnable {
    private static final Logger log = Logger.get(AppTask.class);

    private final TaskEntity<?> entity;

    private AppTaskInstanceService appTaskInstanceService;
    private AippLogRepository aippLogRepository;
    private FlowsService flowsService;
    private AppChatSessionService appChatSessionService;
    private FlowInstanceService flowInstanceService;
    private AppTaskService appTaskService;
    private AppBuilderFormPropertyRepository formPropertyRepository;
    private AopAippLogService aopAippLogService;
    private AppChatSseService appChatSseService;

    @Getter
    @Setter
    private AppVersion appVersion;

    private List<AppTaskInstance> instances;
    private List<AppBuilderFormProperty> formProperties;

    AppTask(TaskEntity<?> entity) {
        this.entity = entity;
    }

    AppTask(AippLogRepository aippLogRepository, AppTaskInstanceService appTaskInstanceService,
            FlowsService flowsService, AppChatSessionService appChatSessionService,
            FlowInstanceService flowInstanceService, AppTaskService appTaskService,
            AppBuilderFormPropertyRepository formPropertyRepository, AopAippLogService aopAippLogService,
            AppChatSseService appChatSseService) {
        this.entity = new TaskDomainEntity();
        this.aippLogRepository = aippLogRepository;
        this.appTaskInstanceService = appTaskInstanceService;
        this.flowsService = flowsService;
        this.appChatSessionService = appChatSessionService;
        this.flowInstanceService = flowInstanceService;
        this.appTaskService = appTaskService;
        this.formPropertyRepository = formPropertyRepository;
        this.aopAippLogService = aopAippLogService;
        this.appChatSseService = appChatSseService;
    }

    /**
     * 作为实体.
     *
     * @return {@link TaskDomainEntity} 对象.
     */
    public static TaskDomainEntity asEntity() {
        return new TaskDomainEntity();
    }

    /**
     * 作为创建参数.
     *
     * @return {@link TaskDomainEntity} 对象
     */
    public static TaskDomainEntity asCreateEntity() {
        TaskDomainEntity entity = new TaskDomainEntity();
        entity.setCategory(JaneCategory.AIPP.name());
        entity.setStatus(AippMetaStatusEnum.INACTIVE.getCode());
        entity.setProperties(AippConst.STATIC_META_ITEMS.stream()
                .map(FormMetaConvertor.INSTANCE::toTaskProperty)
                .collect(Collectors.toList()));
        return entity;
    }

    /**
     * 作为修改参数.
     *
     * @param taskId 任务唯一标识.
     * @return {@link TaskDomainEntity} 对象.
     */
    public static TaskDomainEntity asUpdateEntity(String taskId) {
        TaskDomainEntity entity = new TaskDomainEntity();
        entity.setTaskId(taskId);
        return entity;
    }

    /**
     * 作为查询参数.
     *
     * @param offset 偏移量.
     * @param limit 限制.
     * @return {@link TaskQueryEntity} 对象.
     */
    public static TaskQueryEntity asQueryEntity(long offset, int limit) {
        return new TaskQueryEntity(offset, limit);
    }

    /**
     * 将entity转换为特定的Entity类型对象.
     *
     * @param <T> 代表Entity的类型.
     * @return 特定的 {@link TaskEntity} 类型.
     */
    public <T extends TaskEntity<T>> T getEntity() {
        return ObjectUtils.cast(this.entity);
    }

    @Override
    public void run(RunContext runContext) {
        this.run(runContext, null);
    }

    @Override
    public void run(RunContext ctx, ChatSession<Object> chatSession) {
        OperationContext context = ctx.getOperationContext();

        // 创建实例
        String taskId = this.entity.getTaskId();
        String name = ctx.getInstanceName();
        AppTaskInstance taskInstance = this.appTaskInstanceService.createInstance(
                AppTaskInstance.asCreate(taskId, context.getOperator(), name)
                        .setStatus(MetaInstStatusEnum.RUNNING.name())
                        .build(), context);

        // 设置上下文属性.
        ctx.setAppSuiteId(this.entity.getAppSuiteId());
        ctx.setAppVersion(this.entity.getVersion());
        ctx.setTaskId(this.entity.getTaskId());
        ctx.setAippType(Optional.ofNullable(this.entity.getAippType()).orElse(NORMAL.name()));
        ctx.setTaskInstanceId(taskInstance.getId());
        ctx.setHttpContext(JsonUtils.toJsonString(context));

        log.info("[perf] [{}] startChat persistAippLog start, metaInstId={}", System.currentTimeMillis(),
                taskInstance.getId());
        this.persistAippLog(ctx, taskInstance);

        log.info("[perf] [{}] startChat persistAippLog end, metaInstId={}", System.currentTimeMillis(),
                taskInstance.getId());
        // 持久化aipp实例表单记录
        this.persistAippFormLog(ctx, taskInstance);
        log.info("[perf] [{}] startChat persistAippFormLog end, metaInstId={}", System.currentTimeMillis(),
                taskInstance.getId());

        // 记录上下文
        this.recordContext(ctx, taskInstance);
        log.info("[perf] [{}] startChat recordContext end, metaInstId={}", System.currentTimeMillis(),
                taskInstance.getId());

        // 启动实例.
        ctx.setMemoryConfig(this.getMemoryConfigs(this.entity.getFlowDefinitionId(), context));
        ctx.setAppTask(this);
        TaskInstanceDecorator.create(taskInstance)
                .chat(this.appChatSessionService, this.appChatSseService)
                .run(ctx, chatSession);

        ctx.setFlowTraceId(taskInstance.getEntity().getFlowTranceId());
    }

    private void persistAippLog(RunContext ctx, AppTaskInstance instance) {
        String question = ctx.getQuestion();
        List<Map<String, String>> fileDescList = ctx.getFileDescriptions();

        // 持久化日志
        if (CollectionUtils.isEmpty(fileDescList)) {
            if (ctx.isIncrementMode()) {
                // 如果是处于增长式的重新对话中，插入 hidden_question
                this.insertLog(HIDDEN_QUESTION.name(), AippLogData.builder().msg(question).build(), ctx, instance);
            } else {
                // 插入question日志
                Map<String, Object> infos = this.buildLogInfos(ctx);
                this.insertLog(QUESTION.name(), AippLogData.builder().msg(question).infos(infos).build(), ctx,
                        instance);
            }
        } else {
            JSONObject msgJsonObj = new JSONObject();
            msgJsonObj.put("question", question);
            msgJsonObj.put("files", fileDescList);
            this.insertLog(QUESTION_WITH_FILE.name(), AippLogData.builder().msg(msgJsonObj.toJSONString()).build(), ctx,
                    instance);
        }
    }

    private Map<String, Object> buildLogInfos(RunContext runContext) {
        FlowInfo flowInfo = this.flowsService.getFlows(this.entity.getFlowDefinitionId(),
                runContext.getOperationContext());
        List<String> names = flowInfo.getInputParamsByName("input")
                .stream()
                .map(AppInputParam::from)
                .map(AppInputParam::getName)
                .toList();
        if (CollectionUtils.isEmpty(names)) {
            return new HashMap<>();
        }
        Map<String, Object> inputParams = new HashMap<>();
        runContext.getBusinessData()
                .entrySet()
                .stream()
                .filter(data -> names.contains(data.getKey()))
                .forEach(data -> inputParams.put(data.getKey(), data.getValue()));
        Map<String, Object> infos = new HashMap<>();
        infos.put(BUSINESS_INPUT_KEY, inputParams);
        return infos;
    }

    private void persistAippFormLog(RunContext context, AppTaskInstance instance) {
        String formId = this.entity.getStartFormId();
        String formVersion = this.entity.getStartFormVersion();
        if (StringUtils.isNotEmpty(formId) && StringUtils.isNotEmpty(formVersion)) {
            AippLogData logData = FormUtils.buildLogDataWithFormData(this.getFormProperties(), formId, formVersion,
                    context.getBusinessData());
            this.insertLog(AippInstLogType.FORM.name(), logData, context, instance);
        }
    }

    private void insertLog(String logType, AippLogData logData, RunContext runContext, AppTaskInstance instance) {
        String aippId = runContext.getAppSuiteId();
        String instId = runContext.getTaskInstanceId();
        String version = runContext.getAppVersion();
        String aippType = runContext.getAippType();

        String account = DataUtils.getOpContext(runContext.getBusinessData()).getAccount();
        if (!AippLogUtils.validFormMsg(logData, logType)) {
            return;
        }
        String path = instance.getPath(runContext.getOperationContext());
        String chatId = runContext.getOriginChatId();
        String atChatId = runContext.getAtChatId();
        AippLogCreateDto createDto = AippLogCreateDto.builder()
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
                .build();
        this.aopAippLogService.insertLog(createDto);
    }

    /**
     * 获取表单配置项集合.
     *
     * @return {@link List}{@code <}{@link AppBuilderFormProperty}{@code >} 集合.
     */
    public List<AppBuilderFormProperty> getFormProperties() {
        return UsefulUtils.lazyGet(this.formProperties,
                () -> this.formPropertyRepository.selectWithAppId(this.getEntity().getAppId()),
                ps -> this.formProperties = ps);
    }

    private void recordContext(RunContext context, AppTaskInstance taskInstance) {
        context.setAppId(this.entity.getAppId());
        context.setUserId(context.getOperationContext().getOperator());
        context.setContextTaskInstanceId(taskInstance.getId());
        List<String> fileUrls = context.getFileDescriptions()
                .stream()
                .map(fileDesc -> fileDesc.get("file_url"))
                .toList();
        if (CollectionUtils.isNotEmpty(fileUrls)) {
            context.setFileUrls(fileUrls);
        }
    }

    private List<Map<String, Object>> getMemoryConfigs(String flowDefinitionId, OperationContext context) {
        try {
            FlowInfo flowInfo = this.flowsService.getFlows(flowDefinitionId, context);
            return flowInfo.getInputParamsByName(AippConst.MEMORY_CONFIG_KEY);
        } catch (JobberException e) {
            log.error("get flow failed, flowDefinitionId {}", flowDefinitionId);
            throw new AippException(context, AippErrCode.OBTAIN_APP_ORCHESTRATION_INFO_FAILED);
        }
    }

    /**
     * 是否处于草稿态.
     *
     * @return true/false.
     */
    public boolean isDraft() {
        String baseLineVersion = this.entity.getBaseLineVersion();
        String status = this.entity.getStatus();
        return baseLineVersion != null && AippMetaStatusEnum.getAippMetaStatus(status) != AippMetaStatusEnum.ACTIVE;
    }

    /**
     * 是否是正常类型任务.
     *
     * @return true/false.
     */
    public boolean isNormal() {
        return StringUtils.equalsIgnoreCase(this.entity.getAippType(), NORMAL.name());
    }

    /**
     * 是否处于active状态.
     *
     * @return true/false.
     */
    public boolean isActive() {
        return StringUtils.equals(AippMetaStatusEnum.ACTIVE.getCode(), this.entity.getStatus());
    }

    /**
     * 通过最新的版本判断是否是升级.
     *
     * @param newVersion 最新的版本号.
     * @return true/false.
     */
    public boolean isUpgrade(String newVersion) {
        return this.isActive() || !StringUtils.equals(newVersion, this.entity.getVersion());
    }

    /**
     * 判断任务是否属于某个app版本.
     *
     * @param appId 应用版本id.
     * @return true/false.
     */
    public boolean isBelongApp(String appId) {
        return StringUtils.equals(appId, this.entity.getAppId());
    }

    /**
     * 是否已发布.
     *
     * @return true/false.
     */
    public boolean isPublished() {
        if (StringUtils.isBlank(this.entity.getAippType()) || StringUtils.isBlank(this.entity.getStatus())) {
            return false;
        }
        return StringUtils.equals(this.entity.getAippType(), AippTypeEnum.NORMAL.name())
                && StringUtils.equals(this.entity.getStatus(), AippMetaStatusEnum.ACTIVE.getCode());
    }

    /**
     * 停止所有运行中的实例.
     *
     * @param context 操作人上下文信息.
     */
    public void terminateAllInstances(OperationContext context) {
        String taskId = this.entity.getTaskId();
        Stream<AppTaskInstance> instanceStream = this.appTaskInstanceService.getInstanceStreamByTaskId(
                taskId, 15, context);

        // 只停止正在运行的
        instanceStream.filter(AppTaskInstance::isRunning).forEach(instance -> {
            String flowTraceId = instance.getEntity().getFlowTranceId();
            Validation.notNull(flowTraceId, "flowTraceId can not be null");
            this.flowInstanceService.terminateFlows(null, flowTraceId, Collections.emptyMap(), context);

            // 更新实例状态.
            // 修改taskInstance.
            AppTaskInstance updateEntity = AppTaskInstance.asUpdate(taskId, instance.getId())
                    .setFinishTime(LocalDateTime.now())
                    .setStatus(MetaInstStatusEnum.TERMINATED.name())
                    .build();
            this.appTaskInstanceService.update(updateEntity, context);
        });
    }

    /**
     * 清理资源.
     *
     * @param context 操作人上下文信息.
     */
    public void cleanResource(OperationContext context) {
        String previewVersion = this.getEntity().getVersion();
        if (this.isActive()) {
            this.terminateAllInstances(context);
            this.aippLogRepository.deleteAippPreviewLog(this.getEntity().getAppSuiteId(), context);
        }
        String flowId = this.getEntity().getFlowConfigId();
        if (!StringUtils.isBlank(flowId)) {
            try {
                this.flowsService.deleteFlowsWithoutElsa(flowId, previewVersion, context);
            } catch (JobberException e) {
                log.error("delete flow failed, flowId: {} previewVersion: {}", flowId, previewVersion);
                throw new AippException(context, AippErrCode.APP_PUBLISH_FAILED);
            }
        }
        this.appTaskService.deleteTaskById(this.getEntity().getTaskId(), context);
    }

    /**
     * 获取所有实例.
     *
     * @param context 操作人上下文信息.
     * @return {@link AppTaskInstance} 列表.
     */
    public List<AppTaskInstance> getInstances(OperationContext context) {
        return UsefulUtils.lazyGet(this.instances,
                () -> this.appTaskInstanceService.getInstancesByTaskId(this.entity.getTaskId(), 10, context),
                r -> this.instances = r);
    }

    /**
     * 删除task，同时删除相关数据.
     *
     * @param context 操作人上下文信息.
     */
    public void delete(OperationContext context) {
        // 需要先删除instance，再删除task
        this.getInstances(context).forEach(
            instance -> this.appTaskInstanceService.delete(instance.getTaskId(), instance.getId(), context));
        this.appTaskService.deleteTaskById(this.getEntity().getTaskId(), context);
        this.flowsService.deleteFlowsWithoutElsa(this.getEntity().getAppSuiteId(), this.getEntity().getVersion(),
                context);
    }

    /**
     * 恢复执行.
     *
     * @param instanceId 实例id.
     * @param logId 日志id.
     * @param formArgs 表单参数.
     * @param context 操作人上下文.
     */
    public void resume(String instanceId, Long logId, Map<String, Object> formArgs, OperationContext context) {
        AppTaskInstance instance = this.appTaskInstanceService.getInstanceById(instanceId, context)
                .orElseThrow(() -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                        StringUtils.format("App task instance[{0}] not found.", instanceId)));

        // 更新表单数据
        RunContext runContext = new RunContext(ObjectUtils.cast(formArgs.get(AippConst.BS_DATA_KEY)), context);
        runContext.setAppSuiteId(this.getEntity().getAppSuiteId());
        runContext.setAppVersion(this.getEntity().getVersion());
        runContext.setTaskId(this.getEntity().getTaskId());
        runContext.setAippType(Optional.ofNullable(this.getEntity().getAippType()).orElse(NORMAL.name()));
        runContext.setTaskInstanceId(instanceId);
        runContext.setHttpContext(JsonUtils.toJsonString(context));

        // 获取人工节点开始时间戳 [记录人工节点时延]
        runContext.setResumeDuration(
                instance.getEntity().getResumeDuration() + instance.getEntity().getDuration().toMillis());
        doIfNotBlank(instance.getEntity().getCreateTime(),
                (ct) -> runContext.setStartTime(DateTimeConverter.INSTANCE.fromExternal(ct)));

        this.updateLog(logId, formArgs, instance, runContext);
        this.updateInstance(instanceId, context, runContext);

        try {
            instance.resume(this.getEntity().getFlowDefinitionId(), formArgs, context);
        } catch (JobberException e) {
            log.error("resume flow failed, flowDefinitionId:{}, flowTraceId:{}, formArgs:{}",
                    this.getEntity().getFlowDefinitionId(), instance.getEntity().getFlowTranceId(), formArgs);
            throw new AippException(context, AippErrCode.RESUME_CHAT_FAILED);
        } catch (AippException e) {
            this.updateInstanceStatusError(this.entity.getTaskId(), instanceId, context);
            this.insertLog(AippInstLogType.ERROR.name(), AippLogData.builder().msg(e.getMessage()).build(), runContext,
                    instance);
        }
    }

    private void updateInstance(String instanceId, OperationContext context, RunContext runContext) {
        // 更新实例并清空当前表单数据
        // 修改taskInstance.
        AppTaskInstance updateEntity = AppTaskInstance.asUpdate(this.getEntity().getTaskId(), instanceId)
                .fetch(runContext.getBusinessData(), this.getEntity().getProperties())
                .setFormId(AippConst.INVALID_FORM_ID)
                .setFormVersion(AippConst.INVALID_FORM_VERSION_ID)
                .build();
        this.appTaskInstanceService.update(updateEntity, context);
    }

    private void updateLog(Long logId, Map<String, Object> formArgs, AppTaskInstance instance, RunContext runContext) {
        // 持久化aipp实例表单记录
        String formId = instance.getEntity().getFormId();
        String formVersion = instance.getEntity().getFormVersion();
        AippLogData logData = FormUtils.buildLogDataWithFormData(this.getFormProperties(), formId, formVersion,
                runContext.getBusinessData());

        // 设置表单的渲染数据和填充数据
        logData.setFormAppearance(ObjectUtils.cast(formArgs.get(AippConst.FORM_APPEARANCE_KEY)));
        logData.setFormData(ObjectUtils.cast(formArgs.get(AippConst.FORM_DATA_KEY)));
        this.aippLogRepository.updateDataAndType(logId, AippInstLogType.HIDDEN_FORM.name(),
                JsonUtils.toJsonString(logData));
    }

    private void updateInstanceStatusError(String versionId, String instanceId, OperationContext context) {
        AppTaskInstance updateEntity = AppTaskInstance.asUpdate(versionId, instanceId)
                .setFinishTime(LocalDateTime.now())
                .setStatus(MetaInstStatusEnum.ERROR.name())
                .build();
        this.appTaskInstanceService.update(updateEntity, context);
    }
}
