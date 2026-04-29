/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import static modelengine.fit.jober.aipp.fitable.LlmComponent.checkEnableLog;

import modelengine.fit.jade.aipp.formatter.OutputFormatterChain;
import modelengine.fit.jade.aipp.formatter.constant.Constant;
import modelengine.fit.jade.aipp.formatter.support.ResponsibilityResult;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.domain.AppBuilderFlowGraph;
import modelengine.fit.jober.aipp.domain.EndNodeStatus;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.TaskInstanceUpdateEntity;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.dto.chat.AppChatRsp;
import modelengine.fit.jober.aipp.enums.NodeType;
import modelengine.fit.jober.aipp.entity.AippFlowData;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.events.InsertConversationEnd;
import modelengine.fit.jober.aipp.genericable.AppFlowFinishObserver;
import modelengine.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import modelengine.fit.jober.aipp.repository.EndNodeStatusRepository;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AppBuilderFormService;
import modelengine.fit.jober.aipp.service.AppChatSseService;
import modelengine.fit.jober.aipp.util.ConvertUtils;
import modelengine.fit.jober.aipp.util.DataUtils;
import modelengine.fit.jober.aipp.util.FormUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fit.waterflow.domain.enums.FlowTraceStatus;
import modelengine.fit.waterflow.spi.FlowCallbackService;
import modelengine.fit.waterflow.spi.lock.DistributedLockProvider;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.filter.route.FitableIdFilter;
import modelengine.fitframework.conf.runtime.SerializationFormat;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.ioc.BeanContainer;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.app.engine.metrics.po.ConversationRecordPo;
import modelengine.jade.app.engine.metrics.service.ConversationRecordService;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * 流程结束回调节点
 *
 * @author 邬涨财
 * @since 2024-05-24
 */
@Component
public class AippFlowEndCallback implements FlowCallbackService {
    private static final Logger log = Logger.get(AippFlowEndCallback.class);
    private static final String DEFAULT_END_FORM_VERSION = "1.0.0";
    private static final String CHECK_TIP = "获取到的结果为 null，请检查配置。";
    private static final Map<String, AippInstLogType> LOG_STRATEGY = MapBuilder.<String, AippInstLogType>get()
            .put(Constant.DEFAULT, AippInstLogType.MSG)
            .put(Constant.LLM_OUTPUT, AippInstLogType.META_MSG)
            .build();
    private static final String TERMINATE_LOCK_PREFIX = "aipp-terminate-lock:";

    private final AippLogService aippLogService;
    private final BrokerClient brokerClient;
    private final BeanContainer beanContainer;
    private final ConversationRecordService conversationRecordService;
    private final AppBuilderFormService formService;
    private final AppChatSseService appChatSseService;
    private final OutputFormatterChain formatterChain;
    private final FitRuntime fitRuntime;
    private final AppTaskInstanceService appTaskInstanceService;
    private final AppTaskService appTaskService;
    private final AppVersionService appVersionService;
    private final EndNodeStatusRepository endNodeStatusRepository;
    private final AppBuilderFlowGraphRepository flowGraphRepository;
    private final DistributedLockProvider distributedLockProvider;

    public AippFlowEndCallback(@Fit AippLogService aippLogService, @Fit BrokerClient brokerClient,
            @Fit BeanContainer beanContainer, @Fit ConversationRecordService conversationRecordService,
            @Fit AppBuilderFormService formService, @Fit AppChatSseService appChatSseService,
            @Fit OutputFormatterChain formatterChain, @Fit AppTaskInstanceService appTaskInstanceService,
            @Fit AppTaskService appTaskService, @Fit AppVersionService appVersionService,
            @Fit EndNodeStatusRepository endNodeStatusRepository,
            @Fit AppBuilderFlowGraphRepository flowGraphRepository,
            @Fit DistributedLockProvider distributedLockProvider, FitRuntime fitRuntime) {
        this.formService = formService;
        this.aippLogService = aippLogService;
        this.brokerClient = brokerClient;
        this.beanContainer = beanContainer;
        this.conversationRecordService = conversationRecordService;
        this.appChatSseService = appChatSseService;
        this.formatterChain = formatterChain;
        this.appTaskInstanceService = appTaskInstanceService;
        this.appTaskService = appTaskService;
        this.appVersionService = appVersionService;
        this.endNodeStatusRepository = endNodeStatusRepository;
        this.flowGraphRepository = flowGraphRepository;
        this.distributedLockProvider = distributedLockProvider;
        this.fitRuntime = fitRuntime;
    }

    @Fitable("modelengine.fit.jober.aipp.fitable.AippFlowEndCallback")
    @Override
    public void callback(List<Map<String, Object>> contexts) {
        Map<String, Object> businessData = DataUtils.getBusiness(contexts);
        log.debug("AippFlowEndCallback businessData {}", businessData);

        String versionId = ObjectUtils.cast(businessData.get(AippConst.BS_META_VERSION_ID_KEY));
        OperationContext context =
                JsonUtils.parseObject(
                        ObjectUtils.cast(businessData.get(AippConst.BS_HTTP_CONTEXT_KEY)), OperationContext.class);

        AppTask appTask = this.appTaskService.getTaskById(versionId, context)
                .orElseThrow(() -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                        StringUtils.format("App task[{0}] not found.", versionId)));
        String aippInstId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        String parentCallbackId = ObjectUtils.cast(businessData.get(AippConst.PARENT_CALLBACK_ID));
        boolean allowTerminalSignal = StringUtils.isEmpty(parentCallbackId);
        String lockKey = TERMINATE_LOCK_PREFIX + aippInstId;
        Lock lock = this.distributedLockProvider.get(lockKey);
        lock.lock();
        try {
            this.insertEndNodeStatus(contexts, aippInstId, appTask.getEntity().getFlowConfigId());
            boolean readyToTerminate = this.shouldSendLastData(contexts, appTask);
            this.executeAfterReadyToTerminateCheck(contexts, businessData, versionId, aippInstId, context, appTask,
                    readyToTerminate, allowTerminalSignal);
        } finally {
            lock.unlock();
        }

        // 子流程 callback 主流程
        if (StringUtils.isNotEmpty(parentCallbackId)) {
            this.brokerClient.getRouter(FlowCallbackService.class, "w8onlgq9xsw13jce4wvbcz3kbmjv3tuw")
                    .route(new FitableIdFilter(parentCallbackId))
                    .format(SerializationFormat.CBOR)
                    .invoke(contexts);
        }
    }

    private void executeAfterReadyToTerminateCheck(List<Map<String, Object>> contexts,
            Map<String, Object> businessData, String versionId, String aippInstId,
            OperationContext context, AppTask appTask, boolean readyToTerminate, boolean allowTerminalSignal) {
        this.saveInstance(businessData, versionId, aippInstId, context, appTask, readyToTerminate);
        String parentInstanceId = ObjectUtils.cast(businessData.get(AippConst.PARENT_INSTANCE_ID));
        String appId = ObjectUtils.cast(appTask.getEntity().getAppId());
        businessData.put(AippConst.ATTR_APP_ID_KEY, appId);
        businessData.put(AippConst.BS_AIPP_OUTPUT_IS_NEEDED_LLM, false);
        if (businessData.containsKey(AippConst.BS_END_FORM_ID_KEY)) {
            String endFormId = ObjectUtils.cast(businessData.get(AippConst.BS_END_FORM_ID_KEY));
            String endFormVersion = DEFAULT_END_FORM_VERSION;
            AppBuilderForm appBuilderForm = this.formService.selectWithId(endFormId);
            Map<String, Object> formDataMap = FormUtils.buildFormData(businessData, appBuilderForm, parentInstanceId);

            RunContext runContext = new RunContext(businessData, context);
            String chatId = runContext.getOriginChatId();
            String atChatId = runContext.getAtChatId();
            String returnedLogId = null;
            if (StringUtils.isNotEmpty(endFormId) && StringUtils.isNotEmpty(endFormVersion)) {
                returnedLogId = this.saveFormToLog(appId, businessData, endFormId, endFormVersion, formDataMap);
            }
            AppChatRsp appChatRsp = AppChatRsp.builder().chatId(chatId).atChatId(atChatId)
                                        .status(readyToTerminate ? FlowTraceStatus.ARCHIVED.name() : FlowTraceStatus.RUNNING.name())
                    .answer(Collections.singletonList(AppChatRsp.Answer.builder()
                            .content(formDataMap).type(AippInstLogType.FORM.name()).build()))
                                        .extension(this.buildEndNodeSummary(contexts, appTask))
                    .instanceId(aippInstId).logId(returnedLogId)
                    .build();
                        if (readyToTerminate && allowTerminalSignal) {
                            this.doSendTerminalSignal(aippInstId, appChatRsp);
                        } else {
                                this.appChatSseService.send(aippInstId, appChatRsp);
                        }
            this.insertConversation(businessData, aippInstId, ObjectUtils.cast(businessData.get("chartsData")));
        } else {
            this.logFinalOutput(businessData, aippInstId);
            this.sendTerminalEventIfReady(readyToTerminate && allowTerminalSignal, contexts, appTask,
                                        businessData, context, aippInstId);
        }
    }

    private void sendTerminalEventIfReady(boolean readyToTerminate, List<Map<String, Object>> contexts, AppTask appTask,
            Map<String, Object> businessData, OperationContext operationContext, String aippInstId) {
        if (!readyToTerminate) {
            return;
        }
        RunContext runContext = new RunContext(businessData, operationContext);
        AppChatRsp terminalRsp = AppChatRsp.builder()
                .chatId(runContext.getOriginChatId())
                .atChatId(runContext.getAtChatId())
                .status(FlowTraceStatus.ARCHIVED.name())
                .instanceId(aippInstId)
                .answer(Collections.emptyList())
                .extension(this.buildEndNodeSummary(contexts, appTask))
                .build();
        this.doSendTerminalSignal(aippInstId, terminalRsp);
    }

    private void sendTerminalSignalWithLock(String aippInstId, AppChatRsp appChatRsp) {
        String lockKey = TERMINATE_LOCK_PREFIX + aippInstId;
        Lock lock = this.distributedLockProvider.get(lockKey);
        if (lock.tryLock()) {
            try {
                this.appChatSseService.sendLastData(aippInstId, appChatRsp);
            } finally {
                lock.unlock();
            }
        }
        // If lock acquisition fails, another thread has already sent the terminal signal, skip.
    }

    private void doSendTerminalSignal(String aippInstId, AppChatRsp appChatRsp) {
        this.appChatSseService.sendLastData(aippInstId, appChatRsp);
    }

    private void insertEndNodeStatus(List<Map<String, Object>> contexts, String aippInstId, String flowConfigId) {
        String flowTraceId = DataUtils.getFlowTraceId(contexts);
        String currentNodeId = ObjectUtils.cast(contexts.get(0).get(AippConst.BS_NODE_ID_KEY));
        String status = ObjectUtils.cast(contexts.get(0).get("status"));
        long currentTime = ConvertUtils.toLong(LocalDateTime.now());
        EndNodeStatus endNodeStatus = EndNodeStatus.builder()
                .traceId(flowTraceId)
                .endNodeId(currentNodeId)
                .status(status)
                .startTime(currentTime)
                .endTime(currentTime)
                .flowDefinitionId(flowConfigId)
                .instanceId(aippInstId)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();
        this.endNodeStatusRepository.insertOne(endNodeStatus);
    }

    private boolean shouldSendLastData(List<Map<String, Object>> contexts, AppTask appTask) {
        Set<String> configuredEndNodeIds = this.getConfiguredEndNodeIds(appTask);
        if (configuredEndNodeIds.isEmpty()) {
            log.warn("Cannot resolve configured end nodes. Skip terminal event this round. taskId={0}, flowConfigId={1}",
                    appTask.getEntity().getTaskId(), appTask.getEntity().getFlowConfigId());
            return false;
        }
        String flowTraceId = DataUtils.getFlowTraceId(contexts);
        Set<String> arrivedEndNodeIds = Optional.ofNullable(this.endNodeStatusRepository.selectByTraceId(flowTraceId))
                .orElse(Collections.emptyList())
                .stream()
                .filter(endNodeStatus -> this.isArrivedStatus(endNodeStatus.getStatus()))
                .map(EndNodeStatus::getEndNodeId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        String currentNodeId = ObjectUtils.cast(contexts.get(0).get(AippConst.BS_NODE_ID_KEY));
        if (StringUtils.isNotBlank(currentNodeId)) {
            arrivedEndNodeIds.add(currentNodeId);
        }
        return !arrivedEndNodeIds.isEmpty() && arrivedEndNodeIds.containsAll(configuredEndNodeIds);
    }
    

        private boolean isArrivedStatus(String status) {
                return StringUtils.equalsIgnoreCase(status, "ARCHIVED") || StringUtils.equalsIgnoreCase(status, "SKIPPED");
        }

        private Set<String> getConfiguredEndNodeIds(AppTask appTask) {
                String flowConfigId = appTask.getEntity().getFlowConfigId();
                if (StringUtils.isBlank(flowConfigId)) {
                        return Collections.emptySet();
                }
                AppBuilderFlowGraph flowGraph = this.flowGraphRepository.selectWithId(flowConfigId);
                if (flowGraph == null || StringUtils.isBlank(flowGraph.getAppearance())) {
                        return Collections.emptySet();
                }
                JSONObject appearance = JSONObject.parseObject(flowGraph.getAppearance());
                JSONArray pages = appearance.getJSONArray("pages");
                if (pages == null) {
                        return Collections.emptySet();
                }
                return pages.stream()
                                .filter(JSONObject.class::isInstance)
                                .map(JSONObject.class::cast)
                                .map(page -> page.getJSONArray("shapes"))
                                .filter(shapes -> shapes != null)
                                .flatMap(List::stream)
                                .filter(JSONObject.class::isInstance)
                                .map(JSONObject.class::cast)
                                .filter(shape -> StringUtils.equalsIgnoreCase(shape.getString("type"), NodeType.END_NODE.type()))
                                .map(shape -> shape.getString("id"))
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.toSet());
        }

    private Map<String, Object> buildEndNodeSummary(List<Map<String, Object>> contexts, AppTask appTask) {
        String flowTraceId = DataUtils.getFlowTraceId(contexts);
        List<Map<String, Object>> endNodes = Optional.ofNullable(this.endNodeStatusRepository.selectByTraceId(flowTraceId))
                .orElse(Collections.emptyList())
                .stream()
                .map(endNodeStatus -> {
                    Map<String, Object> node = new HashMap<>();
                    node.put("nodeId", endNodeStatus.getEndNodeId());
                    node.put("status", endNodeStatus.getStatus());
                    node.put("startTime", endNodeStatus.getStartTime());
                    node.put("endTime", endNodeStatus.getEndTime());
                    return node;
                })
                .collect(Collectors.toList());
        Map<String, Object> summary = new HashMap<>();
        summary.put("flowTraceId", flowTraceId);
        summary.put("configuredEndNodeCount", this.getConfiguredEndNodeIds(appTask).size());
        summary.put("endNodeContexts", endNodes);
        return summary;
    }

    private void saveInstance(Map<String, Object> businessData, String versionId, String aippInstId,
            OperationContext context, AppTask appTask, boolean readyToTerminate) {
                TaskInstanceUpdateEntity updateEntity = AppTaskInstance.asUpdate(versionId, aippInstId)
                                .fetch(businessData, appTask.getEntity().getProperties())
                                .setStatus(readyToTerminate ? MetaInstStatusEnum.ARCHIVED.name() : MetaInstStatusEnum.RUNNING.name());
        if (readyToTerminate) {
                        updateEntity.setFinishTime(LocalDateTime.now());
        }
                this.appTaskInstanceService.update(updateEntity.build(), context);
    }

    private String saveFormToLog(String appId, Map<String, Object> businessData, String endFormId,
            String endFormVersion, Map<String, Object> formDataMap) {
        AppVersion appVersion = this.appVersionService.retrieval(appId);
        AippLogData logData = FormUtils.buildLogDataWithFormData(appVersion.getFormProperties(), endFormId,
                endFormVersion, businessData);
        logData.setFormAppearance(JsonUtils.toJsonString(formDataMap.get(AippConst.FORM_APPEARANCE_KEY)));
        logData.setFormData(JsonUtils.toJsonString(formDataMap.get(AippConst.FORM_DATA_KEY)));
        // 子应用/工作流的结束节点表单不需要在历史记录展示
        return this.aippLogService.insertLog((this.isExistParent(businessData)
                ? AippInstLogType.HIDDEN_FORM
                : AippInstLogType.FORM).name(), logData, businessData);
    }

    private boolean isExistParent(Map<String, Object> businessData) {
        return businessData.containsKey(AippConst.PARENT_INSTANCE_ID) && StringUtils.isNotBlank(ObjectUtils.cast(
                businessData.get(AippConst.PARENT_INSTANCE_ID)));
    }

    private void logFinalOutput(Map<String, Object> businessData, String aippInstId) {
        if (ObjectUtils.<Boolean>cast(businessData.get(AippConst.BS_AIPP_OUTPUT_IS_NEEDED_LLM))) {
            return;
        }
        if (!businessData.containsKey(AippConst.BS_AIPP_FINAL_OUTPUT)) {
            return;
        }
        Object finalOutput = businessData.get(AippConst.BS_AIPP_FINAL_OUTPUT);
        Optional<ResponsibilityResult> formatOutput = this.formatterChain.handle(finalOutput);
        String logMsg = formatOutput.map(ResponsibilityResult::text).orElse(CHECK_TIP);
        AippInstLogType logType = formatOutput.flatMap(result -> Optional.ofNullable(LOG_STRATEGY.get(result.owner())))
                .orElse(AippInstLogType.MSG);
        if (!checkEnableLog(businessData)) {
            logType = AippInstLogType.HIDDEN_MSG;
        }
        this.aippLogService.insertLog(logType.name(), AippLogData.builder().msg(logMsg).build(), businessData);
        this.beanContainer.all(AppFlowFinishObserver.class)
                .stream()
                .<AppFlowFinishObserver>map(BeanFactory::get)
                .forEach(finishObserver -> finishObserver.onFinished(logMsg, this.buildAttributes(aippInstId)));
        this.insertConversation(businessData, aippInstId, logMsg);
    }

    private void insertConversation(Map<String, Object> businessData, String aippInstId, String logMsg) {
        // 评估调用接口时不记录历史会话
        Object isEval = businessData.get(AippConst.IS_EVAL_INVOCATION);
        if (isEval == null || !ObjectUtils.<Boolean>cast(isEval)) {
            OperationContext context =
                    JsonUtils.parseObject(ObjectUtils.cast(businessData.get(AippConst.BS_HTTP_CONTEXT_KEY)),
                            OperationContext.class);

            // 构造用户历史对话记录并插表
            String resumeDuration =
                    ObjectUtils.cast(businessData.getOrDefault(AippConst.INST_RESUME_DURATION_KEY, "0"));
            Object createTimeObj = Validation.notNull(businessData.get(AippConst.INSTANCE_START_TIME),
                    "The create time cannot be null.");
            LocalDateTime createTime = LocalDateTime.parse(createTimeObj.toString());
            LocalDateTime finishTime = LocalDateTime.now();
            long realCost = Duration.between(createTime, finishTime).toMillis() - Long.parseLong(resumeDuration);
            LocalDateTime realFinishTime = (realCost > 0) ? createTime.plus(realCost, ChronoUnit.MILLIS) : finishTime;
            ConversationRecordPo conversationRecordPo = ConversationRecordPo.builder()
                    .appId(DataUtils.getAppId(businessData))
                    .question(ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_QUESTION_KEY)))
                    .answer(StringUtils.blankIf(logMsg, StringUtils.EMPTY))
                    .createUser(context.getName())
                    .createTime(createTime)
                    .finishTime(realFinishTime)
                    .instanceId(aippInstId)
                    .build();
            conversationRecordService.insertConversationRecord(conversationRecordPo);

            AippFlowData aippFlowData = AippFlowData.builder()
                    .appId(DataUtils.getAppId(businessData))
                    .username(context.getOperator())
                    .createTime(createTime)
                    .finishTime(realFinishTime)
                    .build();
            this.fitRuntime.publisherOfEvents().publishEvent(new InsertConversationEnd(this.fitRuntime, aippFlowData));
        }
    }

    private Map<String, Object> buildAttributes(String aippInstId) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(AippConst.BS_AIPP_INST_ID_KEY, aippInstId);
        return attributes;
    }
}
