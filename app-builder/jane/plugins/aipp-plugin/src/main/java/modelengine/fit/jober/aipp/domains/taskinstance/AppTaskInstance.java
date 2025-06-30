/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.taskinstance;

import static modelengine.fit.jober.aipp.enums.AippTypeEnum.NORMAL;
import static modelengine.fit.jober.aipp.util.UsefulUtils.lazyGet;

import lombok.Getter;
import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.AppTaskRunnable;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.domains.business.MemoryConfig;
import modelengine.fit.jober.aipp.domains.business.MemoryGetter;
import modelengine.fit.jober.aipp.domains.business.MemoryTypeEnum;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.log.AppLog;
import modelengine.fit.jober.aipp.domains.log.repository.AippLogRepository;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.dto.MemoryConfigDto;
import modelengine.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRsp;
import modelengine.fit.jober.aipp.entity.ChatSession;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.mapper.AippChatMapper;
import modelengine.fit.jober.aipp.service.AppChatSseService;
import modelengine.fit.jober.aipp.util.AippLogUtils;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fit.jober.entity.FlowInstanceResult;
import modelengine.fit.waterflow.entity.FlowStartInfo;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 应用任务实例领域对象.
 *
 * @author 张越
 * @since 2024-12-31
 */
public class AppTaskInstance implements AppTaskRunnable {
    /**
     * 用户自定义日志接口id.
     */
    public static final String GENERICABLE_ID = "68dc66a6185cf64c801e55c97fc500e4";
    private static final Logger log = Logger.get(AppTaskInstance.class);
    private final TaskInstanceEntity<?> entity;

    @Getter
    private String id;

    @Getter
    private String taskId;

    private String parentInstanceId;
    private List<QueryChatRsp> chatRspList;
    private List<AppLog> logs;
    private List<AppLog> allLogs;
    private AppChatSseService appChatSSEService;
    private AppTaskInstanceService appTaskInstanceService;
    private FlowInstanceService flowInstanceService;
    private BrokerClient client;
    private AippChatMapper aippChatMapper;
    private AippLogRepository aippLogRepository;

    AppTaskInstance(AppTaskInstanceService appTaskInstanceService,
            FlowInstanceService flowInstanceService, BrokerClient client,
            AppChatSseService appChatSSEService, AippChatMapper aippChatMapper, AippLogRepository aippLogRepository) {
        this.entity = new TaskInstanceDomainEntity();
        this.appTaskInstanceService = appTaskInstanceService;
        this.flowInstanceService = flowInstanceService;
        this.client = client;
        this.appChatSSEService = appChatSSEService;
        this.aippChatMapper = aippChatMapper;
        this.aippLogRepository = aippLogRepository;
    }

    AppTaskInstance(TaskInstanceEntity<?> entity) {
        this.entity = entity;
    }

    /**
     * 作为数据对象.
     *
     * @return {@link TaskInstanceEntity} 对象.
     */
    public static TaskInstanceDomainEntity asEntity() {
        return new TaskInstanceDomainEntity();
    }

    /**
     * 作为创建时的数据对象.
     *
     * @param taskId 任务id.
     * @param creator 创建者.
     * @param name 实例名称.
     * @return {@link TaskInstanceCreateEntity} 对象.
     */
    public static TaskInstanceCreateEntity asCreate(String taskId, String creator, String name) {
        return new TaskInstanceCreateEntity(taskId, creator, name);
    }

    /**
     * 作为修改时的数据对象.
     *
     * @param taskId 任务id.
     * @param instanceId 实例id.
     * @return {@link TaskInstanceUpdateEntity} 对象.
     */
    public static TaskInstanceUpdateEntity asUpdate(String taskId, String instanceId) {
        return new TaskInstanceUpdateEntity(taskId, instanceId);
    }

    /**
     * 作为查询时的数据对象.
     *
     * @param order 排序参数.
     * @param sort 排序顺序.
     * @return {@link TaskInstanceQueryEntity} 对象.
     */
    public static TaskInstanceQueryEntity asQuery(String order, String sort) {
        return new TaskInstanceQueryEntity().setOrder(order).setSort(sort);
    }

    /**
     * 将一个 {@link AppTaskInstance} 转换为一个 {@link AippInstLogDataDto}.
     *
     * @param instance 实例对象.
     * @return {@link AippInstLogDataDto} 的 {@link Optional} 对象.
     */
    public static Optional<AippInstLogDataDto> toLogDataDto(AppTaskInstance instance) {
        List<AppLog> logs = instance.getAllLogs()
                .stream()
                .filter(l -> !l.is(AippInstLogType.HIDDEN_MSG, AippInstLogType.HIDDEN_FORM))
                .toList();
        if (CollectionUtils.isEmpty(logs)) {
            return Optional.empty();
        }
        return Optional.of(new AippInstLogDataDto(instance, logs));
    }

    /**
     * 将entity转换为特定的Entity类型对象.
     *
     * @param <T> 代表Entity的类型.
     * @return 特定的 {@link TaskInstanceEntity} 类型.
     */
    public <T extends TaskInstanceEntity<T>> T getEntity() {
        return ObjectUtils.cast(this.entity);
    }

    @Override
    public void run(RunContext ctx) {
        this.run(ctx, null);
    }

    @Override
    public void run(RunContext ctx, ChatSession<Object> chatSession) {
        AppTask task = ctx.getAppTask();
        if (task == null) {
            log.error("The task is not found. [taskId={}]", ctx.getTaskId());
            throw new AippException(AippErrCode.TASK_NOT_FOUND);
        }
        String flowDefinitionId = task.getEntity().getFlowDefinitionId();
        if (ctx.shouldUseMemory()) {
            if (ctx.isUserCustomMemory()) {
                Optional.ofNullable(chatSession).ifPresent(c -> {
                    MemoryConfigDto dto = MemoryConfigDto.builder()
                            .initContext(ctx.getBusinessData())
                            .instanceId(this.getId())
                            .memory(MemoryTypeEnum.USER_SELECT.type())
                            .build();
                    this.appChatSSEService.sendToAncestorLastData(this.getId(), dto);
                });
            } else {
                ctx.setMemories(this.getMemories(ctx, task));
                this.start(ctx, flowDefinitionId);
            }
        } else {
            if (!ctx.isUserCustomMemory()) {
                ctx.clearMemories();
            }
            this.start(ctx, flowDefinitionId);
        }
    }

    private void start(RunContext ctx, String flowDefinitionId) {
        FlowInstanceResult flowInstance = this.startFlow(ctx, flowDefinitionId);

        // 记录流程实例id到meta实例
        // 修改taskInstance.
        this.appTaskInstanceService.update(
                AppTaskInstance.asUpdate(this.taskId, this.id).setFlowTraceId(flowInstance.getId()).build(),
                ctx.getOperationContext());
    }

    private FlowInstanceResult startFlow(RunContext ctx, String flowDefinitionId) {
        try {
            return this.flowInstanceService.startFlow(flowDefinitionId,
                    new FlowStartInfo(ctx.getOperationContext().getOperator(), null, ctx.getBusinessData()),
                    ctx.getOperationContext());
        } catch (JobberException e) {
            log.error("start flow failed, flowDefinitionId: {}", flowDefinitionId);
            throw new AippException(ctx.getOperationContext(), AippErrCode.APP_CHAT_WAIT_RESPONSE_ERROR);
        }
    }

    private List<Map<String, Object>> getMemories(RunContext ctx, AppTask task) {
        MemoryConfig memoryConfig = ctx.getMemoryConfig();
        MemoryGetter memoryGetter = new MemoryGetter(memoryConfig);
        memoryGetter.register(MemoryTypeEnum.BY_CONVERSATION_TURN, (v) -> this.getConversationTurns(v, ctx));
        memoryGetter.register(MemoryTypeEnum.NOT_USE_MEMORY, (v) -> this.getNotUserMemory(ctx));
        memoryGetter.register(MemoryTypeEnum.CUSTOMIZING, (v) -> this.getCustomizedLogs(v, task, ctx));
        return memoryGetter.get();
    }

    private List<Map<String, Object>> getCustomizedLogs(Object value, AppTask task, RunContext ctx) {
        // 如何定义这个genericable接口，入参为一个map?
        String fitableId = ObjectUtils.cast(value);

        // 目前flow graph中并没有params的配置，暂时用一个空map
        Map<String, Object> params = new HashMap<>();
        String appSuiteId = task.getEntity().getAppSuiteId();
        String aippType = Optional.ofNullable(task.getEntity().getAippType()).orElse(NORMAL.name());
        if (fitableId == null) {
            log.warn("no fitable id in customized log selection.");
            return Collections.emptyList();
        }
        try {
            return this.client.getRouter(GENERICABLE_ID)
                    .route(new FitableIdFilter(fitableId))
                    .invoke(params, appSuiteId, aippType, ctx.getOperationContext());
        } catch (FitException t) {
            log.error("Error occurred when get history logs, error: {}", t.getMessage());
            throw new AippException(AippErrCode.GET_HISTORY_LOG_FAILED);
        }
    }

    private List<Map<String, Object>> getNotUserMemory(RunContext ctx) {
        ctx.setUseMemory(false);
        return new ArrayList<>();
    }

    private List<Map<String, Object>> getConversationTurns(Object value, RunContext ctx) {
        Integer count = Integer.parseInt(ObjectUtils.cast(value));
        String memoryChatId = ctx.getOriginChatId();
        return Optional.ofNullable(memoryChatId).map(cid -> {
            OperationContext operationContext = ctx.getOperationContext();
            List<String> instanceIds = this.aippChatMapper.selectInstanceByChat(cid, count);
            List<AppTaskInstance> instances = instanceIds.stream()
                    .map(id -> this.appTaskInstanceService.getInstance(this.taskId, id, operationContext))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            return instances.stream()
                    .map(AppTaskInstance::toLogDataDto)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .sorted(Comparator.comparing(AippInstLogDataDto::getCreateAt))
                    .map(AippInstLogDataDto::toMemory)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        }).orElseGet(ArrayList::new);
    }

    /**
     * 实例是否处于运行状态.
     *
     * @return true/false.
     */
    public boolean isRunning() {
        return this.getEntity()
                .getStatus()
                .map(status -> MetaInstStatusEnum.getMetaInstStatus(status) == MetaInstStatusEnum.RUNNING)
                .orElse(false);
    }

    /**
     * 判断实例是否处于传入的多个状态中的其中一个.
     *
     * @param statusEnums 多个状态.
     * @return true/false.
     */
    public boolean is(MetaInstStatusEnum... statusEnums) {
        Optional<String> statusOp = this.entity.getStatus();
        if (statusOp.isEmpty()) {
            return false;
        }
        int value = MetaInstStatusEnum.getMetaInstStatus(statusOp.get()).getValue();
        return Arrays.stream(statusEnums).anyMatch(e -> value == e.getValue());
    }

    /**
     * 设置任务id.
     *
     * @param taskId 任务id.
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
        this.getEntity().setTaskId(taskId);
    }

    /**
     * 设置以为标识.
     *
     * @param id 唯一标识.
     */
    public void setId(String id) {
        this.id = id;
        this.getEntity().setInstanceId(id);
    }

    /**
     * 获取父流程id，若没有父流程，则返回自己.
     *
     * @return {@link String} 父流程id.
     */
    public String getParentInstanceId() {
        return lazyGet(this.parentInstanceId, () -> {
            String path = this.aippLogRepository.getParentPath(this.id);
            return StringUtils.isNotEmpty(path) ? path.split(AippLogUtils.PATH_DELIMITER)[1] : this.id;
        }, (t) -> this.parentInstanceId = t);
    }

    /**
     * 获取全路径.
     *
     * @param context 操作人上下文信息.
     * @return 全路径.
     */
    public String getPath(OperationContext context) {
        String parentId = this.getParentInstanceId();
        if (StringUtils.equals(parentId, this.id)) {
            return AippLogUtils.PATH_DELIMITER + this.getId();
        } else {
            String parentPath = this.getParent(context).map(ti -> ti.getPath(context)).orElse(null);
            return StringUtils.isEmpty(parentPath)
                    ? AippLogUtils.PATH_DELIMITER + this.getId()
                    : String.join(AippLogUtils.PATH_DELIMITER, parentPath, this.id);
        }
    }

    /**
     * 获取父实例.
     *
     * @param context 操作人上下文信息.
     * @return 父实例的 {@link Optional} 对象.
     */
    public Optional<AppTaskInstance> getParent(OperationContext context) {
        String parentId = this.getParentInstanceId();
        if (StringUtils.equals(parentId, this.id)) {
            return Optional.empty();
        }
        return this.appTaskInstanceService.getInstance(this.taskId, parentId, context);
    }

    /**
     * 获取会话列表.
     *
     * @return {@link List}{@code <}{@link QueryChatRsp}{@code >} 会话列表.
     */
    public List<QueryChatRsp> getChats() {
        return lazyGet(this.chatRspList, this::loadChats, (chats) -> this.chatRspList = chats);
    }

    private List<QueryChatRsp> loadChats() {
        List<String> chatIds = this.aippChatMapper.selectChatIdByInstanceId(this.getParentInstanceId());
        if (chatIds.isEmpty()) {
            return Collections.emptyList();
        }
        return this.aippChatMapper.selectChatListByChatIds(chatIds);
    }

    /**
     * 获取实例日志列表.
     *
     * @return {@link List}{@code <}{@link AppLog}{@code >} 会话列表.
     */
    public List<AppLog> getLogs() {
        return lazyGet(this.logs, () -> this.aippLogRepository.selectByInstanceIdAndLogTypes(this.getParentInstanceId(),
                        Arrays.asList(AippInstLogType.QUESTION.name(), AippInstLogType.HIDDEN_QUESTION.name())),
                (logs) -> this.logs = logs);
    }

    /**
     * 获取所有日志，包含当前实例的子实例的日志.
     *
     * @return 日志列表.
     */
    public List<AppLog> getAllLogs() {
        return lazyGet(this.allLogs, () -> this.aippLogRepository.selectAllLogsByInstanceId(this.getParentInstanceId()),
                (logs) -> this.allLogs = logs);
    }

    /**
     * 覆盖写会话或日志.
     */
    public void overWrite() {
        String parentId = this.getParentInstanceId();
        this.aippChatMapper.deleteWideRelationshipByInstanceId(parentId);
        this.aippLogRepository.deleteByInstanceId(parentId);
    }

    /**
     * 恢复执行.
     *
     * @param flowDefinitionId 流程定义id.
     * @param formArgs 表单参数.
     * @param context 操作人上下文信息.
     */
    public void resume(String flowDefinitionId, Map<String, Object> formArgs, OperationContext context) {
        String flowTraceId = this.getEntity().getFlowTranceId();
        Validation.notNull(flowTraceId, "flowTraceId can not be null");
        this.flowInstanceService.resumeFlow(flowDefinitionId, flowTraceId, formArgs, context);
    }
}
