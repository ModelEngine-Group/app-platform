/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import static modelengine.fit.jober.aipp.constants.AippConst.BS_NODE_ID_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.BUSINESS_DATA_INTERNAL_KEY;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jade.waterflow.FlowInstanceService;
import modelengine.fit.jober.FlowSmartFormService;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import modelengine.fit.jober.aipp.dto.chat.AppChatRsp;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.enums.MetaInstStatusEnum;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AopAippLogService;
import modelengine.fit.jober.aipp.service.AppBuilderFormService;
import modelengine.fit.jober.aipp.service.AppChatSseService;
import modelengine.fit.jober.aipp.service.RuntimeInfoService;
import modelengine.fit.jober.aipp.util.ConvertUtils;
import modelengine.fit.jober.aipp.util.DataUtils;
import modelengine.fit.jober.aipp.util.FormUtils;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.JobberException;

import lombok.AllArgsConstructor;
import modelengine.fit.waterflow.domain.enums.FlowTraceStatus;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * aipp智能表单实现实现
 *
 * @author 刘信宏
 * @since 2023-12-25
 */
@Component
@AllArgsConstructor
public class AippFlowSmartFormHandle implements FlowSmartFormService {
    private static final Logger log = Logger.get(AippFlowSmartFormHandle.class);
    private static final String DEFAULT_CURR_FORM_VERSION = "1.0.0";

    private final AppBuilderFormService formService;
    private final AppChatSseService appChatSseService;
    private final AippLogService aippLogService;
    private final AppTaskService appTaskService;
    private final AppTaskInstanceService appTaskInstanceService;
    private final AppVersionService appVersionService;
    private final FlowInstanceService flowInstanceService;
    private final AopAippLogService aopAippLogService;
    private final RuntimeInfoService runtimeInfoService;

    /**
     * 智能表单处理
     *
     * @param contexts 流程上下文信息
     * @param sheetId 表单Id
     */
    @Override
    @Fitable("qz90ufu144m607hfud1ecbk0dnq3xavd")
    public void handleSmartForm(List<Map<String, Object>> contexts, String sheetId) {
        long startTime = ConvertUtils.toLong(LocalDateTime.now());
        String nodeId = ObjectUtils.cast(contexts.get(0).get(BS_NODE_ID_KEY));
        Map<String, Object> businessData = DataUtils.getBusiness(contexts);
        log.debug("handleSmartForm nodeId {} businessData {}", nodeId, businessData);
        businessData.put(BS_NODE_ID_KEY, nodeId);

        this.updateInstance(sheetId, nodeId, businessData);

        AppBuilderForm appBuilderForm = this.formService.selectWithId(sheetId);
        if (appBuilderForm == null) {
            log.warn("Failed to handle smart form: form is not exist. {}", sheetId);
            this.exceptionHandler(businessData);
            return;
        }
        RunContext runContext = new RunContext(businessData, new OperationContext());
        String parentInstanceId = runContext.getParentInstanceId();
        String instanceId = runContext.getTaskInstanceId();
        String chatId = runContext.getOriginChatId();
        String atChatId = runContext.getAtChatId();
        String appId = runContext.getAppId();
        AppVersion appVersion = this.appVersionService.retrieval(appId);
        Map<String, Object> formDataMap = FormUtils.buildFormData(businessData, appBuilderForm, parentInstanceId);
        formDataMap.put(AippConst.NODE_START_TIME_KEY, startTime);
        if (businessData.containsKey(BUSINESS_DATA_INTERNAL_KEY)) {
            formDataMap.put(BUSINESS_DATA_INTERNAL_KEY, businessData.get(BUSINESS_DATA_INTERNAL_KEY));
        }
        formDataMap.put(BS_NODE_ID_KEY, nodeId);
        String logId = this.insertFormLog(appVersion.getFormProperties(), sheetId, businessData, formDataMap);
        AppChatRsp appChatRsp = AppChatRsp.builder()
                .chatId(chatId)
                .atChatId(atChatId)
                .status(FlowTraceStatus.RUNNING.name())
                .answer(Collections.singletonList(AppChatRsp.Answer.builder()
                        .content(formDataMap)
                        .type(AippInstLogType.FORM.name())
                        .build()))
                .instanceId(instanceId)
                .logId(logId)
                .build();
        this.appChatSseService.sendToAncestorLastData(instanceId, appChatRsp);
    }

    /**
     * 1. 关闭表单流程（terminate）  2、error 信息写入日志，并推送给前端
     *
     * @param businessData 表示业务数据
     */
    private void exceptionHandler(Map<String, Object> businessData) {
        OperationContext context =
                JsonUtils.parseObject(ObjectUtils.cast(businessData.get(AippConst.BS_HTTP_CONTEXT_KEY)),
                        OperationContext.class);
        String instanceId = ObjectUtils.cast(businessData.get(AippConst.CONTEXT_INSTANCE_ID));
        String taskId = this.appTaskInstanceService.getTaskId(instanceId);
        AppTask task = this.appTaskService.retrieveById(taskId, context);
        AppTaskInstance instance = this.appTaskInstanceService.getInstanceById(instanceId, context)
                .orElseThrow(() -> new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                        StringUtils.format("App task instance[{0}] not found.", instanceId)));

        // 终止所有流程.
        String flowTraceId = instance.getEntity().getFlowTranceId();
        this.flowInstanceService.terminateFlows(null, flowTraceId, Collections.emptyMap(), context);

        // 修改实例.
        AppTaskInstance updateEntity = AppTaskInstance.asUpdate(taskId, instanceId)
                .setFinishTime(LocalDateTime.now())
                .setStatus(MetaInstStatusEnum.TERMINATED.name())
                .build();
        this.appTaskInstanceService.update(updateEntity,
                JsonUtils.parseObject(ObjectUtils.cast(businessData.get(AippConst.BS_HTTP_CONTEXT_KEY)),
                        OperationContext.class));

        // 插入日志.
        String message = AippErrCode.FORM_RUNNING_FAILED_CAUSE_NOT_EXISTED.getMessage();
        this.aopAippLogService.insertLog(AippLogCreateDto.builder()
                .aippId(task.getEntity().getAppSuiteId())
                .version(task.getEntity().getVersion())
                .aippType(task.getEntity().getAippType())
                .instanceId(instanceId)
                .logType(AippInstLogType.ERROR.name())
                .logData(JsonUtils.toJsonString(AippLogData.builder().msg(message).build()))
                .createUserAccount(context.getAccount())
                .path(this.aippLogService.buildPath(instanceId, null))
                .build());
        this.runtimeInfoService.insertRuntimeInfo(instanceId, businessData, MetaInstStatusEnum.ERROR, message, context);
    }

    private String insertFormLog(List<AppBuilderFormProperty> formProperties, String sheetId,
            Map<String, Object> businessData, Map<String, Object> formDataMap) {
        AippLogData logData =
                FormUtils.buildLogDataWithFormData(formProperties, sheetId, DEFAULT_CURR_FORM_VERSION, businessData);
        Object appearance = formDataMap.get(AippConst.FORM_APPEARANCE_KEY);
        logData.setFormAppearance(ObjectUtils.cast(JsonUtils.toJsonString(appearance)));
        logData.setFormData(ObjectUtils.cast(JsonUtils.toJsonString(formDataMap.get(AippConst.FORM_DATA_KEY))));
        return this.aippLogService.insertLogWithInterception(AippInstLogType.FORM.name(), logData, businessData);
    }

    private void updateInstance(String sheetId, String nodeId, Map<String, Object> businessData) {
        String taskId = ObjectUtils.cast(businessData.get(AippConst.BS_META_VERSION_ID_KEY));
        String taskInstanceId = ObjectUtils.cast(businessData.get(AippConst.BS_AIPP_INST_ID_KEY));
        AppTaskInstance updateEntity = AppTaskInstance.asUpdate(taskId, taskInstanceId)
                .setFormId(sheetId)
                .setFormVersion(DEFAULT_CURR_FORM_VERSION)
                .setCurrentNodeId(nodeId)
                .setSmartFormTime(LocalDateTime.now())
                .build();
        this.appTaskInstanceService.update(updateEntity,
                JsonUtils.parseObject(ObjectUtils.cast(businessData.get(AippConst.BS_HTTP_CONTEXT_KEY)),
                        OperationContext.class));
    }
}
