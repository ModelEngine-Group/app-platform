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
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.dto.chat.AppChatRsp;
import modelengine.fit.jober.aipp.entity.AippFlowData;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.events.InsertConversationEnd;
import modelengine.fit.jober.aipp.genericable.AppFlowFinishObserver;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AppBuilderFormService;
import modelengine.fit.jober.aipp.service.AppChatSseService;
import modelengine.fit.jober.aipp.util.DataUtils;
import modelengine.fit.jober.aipp.util.FormUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fit.waterflow.domain.enums.FlowTraceStatus;
import modelengine.fit.waterflow.spi.FlowCallbackService;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public AippFlowEndCallback(@Fit AippLogService aippLogService, @Fit BrokerClient brokerClient,
            @Fit BeanContainer beanContainer, @Fit ConversationRecordService conversationRecordService,
            @Fit AppBuilderFormService formService, @Fit AppChatSseService appChatSseService,
            @Fit OutputFormatterChain formatterChain, @Fit AppTaskInstanceService appTaskInstanceService,
            @Fit AppTaskService appTaskService, @Fit AppVersionService appVersionService, FitRuntime fitRuntime) {
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
        this.saveInstance(businessData, versionId, aippInstId, context, appTask);
        String parentInstanceId = ObjectUtils.cast(businessData.get(AippConst.PARENT_INSTANCE_ID));
        String appId = ObjectUtils.cast(appTask.getEntity().getAppId());
        businessData.put(AippConst.ATTR_APP_ID_KEY, appId);
        //  表明流程结果是否需要再经过模型加工，当前场景全为false。
        //  正常情况下应该是在结束节点配上该key并放入businessData中，此处模拟该过程。
        //  如果子流程结束后需要再经过模型加工，子流程结束节点不打印日志；否则子流程结束节点需要打印日志。
        //  如果前一个节点是人工检查节点，并在结束节点reference到了表单，那么这里一定会打印消息。
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
                    .status(FlowTraceStatus.ARCHIVED.name())
                    .answer(Collections.singletonList(AppChatRsp.Answer.builder()
                            .content(formDataMap).type(AippInstLogType.FORM.name()).build()))
                    .instanceId(aippInstId).logId(returnedLogId)
                    .build();
            this.appChatSseService.sendLastData(aippInstId, appChatRsp);
            this.insertConversation(businessData, aippInstId, ObjectUtils.cast(businessData.get("chartsData")));
        } else {
            this.logFinalOutput(businessData, aippInstId);
        }

        // 子流程 callback 主流程
        String parentCallbackId = ObjectUtils.cast(businessData.get(AippConst.PARENT_CALLBACK_ID));
        if (StringUtils.isNotEmpty(parentCallbackId)) {
            this.brokerClient.getRouter(FlowCallbackService.class, "w8onlgq9xsw13jce4wvbcz3kbmjv3tuw")
                    .route(new FitableIdFilter(parentCallbackId))
                    .format(SerializationFormat.CBOR)
                    .invoke(contexts);
        }
    }

    private void saveInstance(Map<String, Object> businessData, String versionId, String aippInstId,
            OperationContext context, AppTask appTask) {
        this.appTaskInstanceService.update(AppTaskInstance.asUpdate(versionId, aippInstId)
                .setFinishTime(LocalDateTime.now())
                .setStatus(MetaInstStatusEnum.ARCHIVED.name())
                .fetch(businessData, appTask.getEntity().getProperties())
                .build(), context);
    }

    private String saveFormToLog(String appId, Map<String, Object> businessData, String endFormId,
            String endFormVersion, Map<String, Object> formDataMap) {
        AppVersion appVersion = this.appVersionService.retrieval(appId);
        AippLogData logData = FormUtils.buildLogDataWithFormData(appVersion.getFormProperties(), endFormId,
                endFormVersion, businessData);
        logData.setFormAppearance(JsonUtils.toJsonString(formDataMap.get(AippConst.FORM_APPEARANCE_KEY)));
        logData.setFormData(JsonUtils.toJsonString(formDataMap.get(AippConst.FORM_DATA_KEY)));
        // 子应用/工作流的结束节点表单不需要在历史记录展示
        return this.aippLogService.insertLogWithInterception((this.isExistParent(businessData)
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
        this.aippLogService.insertLogWithInterception(logType.name(), AippLogData.builder().msg(logMsg).build(), businessData);
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
