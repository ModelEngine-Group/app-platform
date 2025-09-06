/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.business;

import static modelengine.fit.jober.aipp.constants.AippConst.ATTR_AIPP_TYPE_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.BS_AIPP_FILE_DESC_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.BS_AIPP_ID_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.BS_AIPP_INST_ID_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.BS_AIPP_MEMORIES_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.BS_AIPP_QUESTION_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.BS_AIPP_USE_MEMORY_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.BS_AIPP_VERSION_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.BS_CHAT_ID;
import static modelengine.fit.jober.aipp.constants.AippConst.BS_HTTP_CONTEXT_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.BS_META_VERSION_ID_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.CONTEXT_USER_ID;
import static modelengine.fit.jober.aipp.constants.AippConst.PARENT_CALLBACK_ID;
import static modelengine.fit.jober.aipp.constants.AippConst.PARENT_INSTANCE_ID;
import static modelengine.fit.jober.aipp.constants.AippConst.RESTART_MODE;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import modelengine.fit.jober.aipp.enums.RestartModeEnum;

import com.alibaba.fastjson.JSONObject;

import lombok.Getter;
import lombok.Setter;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 运行时的上下文.
 *
 * @author 张越
 * @since 2025-01-08
 */
public class RunContext {
    private static final String START_NODE_INPUT_PARAMS = "startNodeInputParams";

    @Getter
    private final Map<String, Object> businessData;

    @Getter
    private final OperationContext operationContext;

    @Setter
    @Getter
    private boolean isDebug;

    private MemoryConfig memoryConfig;

    @Setter
    @Getter
    private Map<String, Object> userContext;

    @Setter
    @Getter
    private AppTask appTask;

    public RunContext(Map<String, Object> businessData, OperationContext context) {
        this.businessData = businessData;
        this.operationContext = context;
    }

    /**
     * 通过 {@link CreateAppChatRequest} 和 {@link OperationContext} 创建 {@link RunContext}.
     *
     * @param request 请求参数.
     * @param context 操作人上线文信息.
     * @return {@link RunContext} 对象.
     */
    public static RunContext from(CreateAppChatRequest request, OperationContext context) {
        CreateAppChatRequest.Context requestContext = Optional.ofNullable(request.getContext())
                .orElseGet(() -> CreateAppChatRequest.Context.builder().build());
        RunContext runContext = new RunContext(new HashMap<>(), context);
        runContext.putAllToBusiness(requestContext.getUserContext());
        runContext.setUseMemory(requestContext.getUseMemory());
        runContext.setDimension(requestContext.getDimension());
        runContext.setChatId(request.getChatId());
        runContext.setQuestion(request.getQuestion());
        runContext.setAtChatId(requestContext.getAtChatId());
        runContext.setAtAppId(requestContext.getAtAppId());
        runContext.setUserContext(requestContext.getUserContext());
        runContext.setDimensionId(requestContext.getDimensionId());
        return runContext;
    }

    /**
     * 业务数据深拷贝.
     *
     * @return {@link RunContext} 运行时上下文信息.
     */
    public RunContext businessDeepClone() {
        return new RunContext(
                this.businessData.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                this.getOperationContext());
    }

    /**
     * 将整个keyValue集合放入businessData中.
     *
     * @param keyValues 键值对集合.
     */
    public void putAllToBusiness(Map<String, Object> keyValues) {
        Optional.ofNullable(keyValues).ifPresent(this.businessData::putAll);
    }

    /**
     * 设置memory配置数据.
     *
     * @param memoryConfigs memory配置数据.
     */
    public void setMemoryConfig(List<Map<String, Object>> memoryConfigs) {
        this.memoryConfig = new MemoryConfig(memoryConfigs);
    }

    /**
     * 获取memory配置.
     *
     * @return {@link MemoryConfig} 对象.
     */
    public MemoryConfig getMemoryConfig() {
        return Optional.ofNullable(this.memoryConfig).orElseGet(() -> new MemoryConfig(new ArrayList<>()));
    }

    /**
     * 是否开启memory.
     *
     * @return true/false.
     */
    public boolean shouldUseMemory() {
        Boolean enableMemory = this.memoryConfig.getEnableMemory();
        Boolean shouldUseMemory = Optional.ofNullable(this.isUseMemory()).orElse(enableMemory);
        this.setUseMemory(shouldUseMemory);
        return shouldUseMemory;
    }

    /**
     * 是否是用户自定义历史记录.此时，需要用户在前端手动选择需要的历史记录，然后再启动流程.
     *
     * @return true/false.
     */
    public boolean isUserCustomMemory() {
        return StringUtils.equalsIgnoreCase(MemoryTypeEnum.USER_SELECT.type(), this.memoryConfig.getMemoryType());
    }

    /**
     * 初始化启动参数.
     */
    public void initStartParams() {
        this.businessData.put(START_NODE_INPUT_PARAMS, JSONObject.parse(JSONObject.toJSONString(this.businessData)));
    }

    /**
     * 设置appSuiteId.
     *
     * @param appSuiteId app唯一标识.
     */
    public void setAppSuiteId(String appSuiteId) {
        this.businessData.put(BS_AIPP_ID_KEY, appSuiteId);
    }

    /**
     * 获取应用id.
     *
     * @return 应用id.
     */
    public String getAppSuiteId() {
        return ObjectUtils.cast(this.businessData.get(AippConst.ATTR_AIPP_TYPE_KEY));
    }

    /**
     * 设置appVersion.
     *
     * @param appVersion app版本号.
     */
    public void setAppVersion(String appVersion) {
        this.businessData.put(BS_AIPP_VERSION_KEY, appVersion);
    }

    /**
     * 获取应用版本.
     *
     * @return 应用版本.
     */
    public String getAppVersion() {
        return ObjectUtils.cast(this.businessData.get(BS_AIPP_VERSION_KEY));
    }

    /**
     * 设置任务id.
     *
     * @param taskId 任务id.
     */
    public void setTaskId(String taskId) {
        this.businessData.put(BS_META_VERSION_ID_KEY, taskId);
    }

    /**
     * 获取任务id.
     *
     * @return 任务id.
     */
    public String getTaskId() {
        return ObjectUtils.cast(this.businessData.get(BS_META_VERSION_ID_KEY));
    }

    /**
     * 设置aipp类型.
     *
     * @param aippType aipp类型.
     */
    public void setAippType(String aippType) {
        this.businessData.put(ATTR_AIPP_TYPE_KEY, aippType);
    }

    /**
     * 获取应用aipp类型.
     *
     * @return aipp类型.
     */
    public String getAippType() {
        return ObjectUtils.cast(this.businessData.get(AippConst.ATTR_AIPP_TYPE_KEY));
    }

    /**
     * 设置任务实例id.
     *
     * @param taskInstanceId 任务实例id.
     */
    public void setTaskInstanceId(String taskInstanceId) {
        this.businessData.put(BS_AIPP_INST_ID_KEY, taskInstanceId);
    }

    /**
     * 设置文件urls.
     *
     * @param fileUrls 文件urls.
     */
    public void setFileUrls(List<String> fileUrls) {
        this.businessData.put(AippConst.BS_AIPP_FILES_DOWNLOAD_KEY, fileUrls);
        this.businessData.put(AippConst.BS_AIPP_FILE_DOWNLOAD_KEY, fileUrls.get(0));
    }

    /**
     * 设置开始时间.
     *
     * @param startTime 开始时间.
     */
    public void setStartTime(Object startTime) {
        this.businessData.put(AippConst.INSTANCE_START_TIME, startTime);
    }

    /**
     * 设置restart模式.
     *
     * @param restartMode restart模式.
     */
    public void setRestartMode(String restartMode) {
        this.businessData.put(AippConst.RESTART_MODE, restartMode);
    }

    /**
     * 是否是覆盖写模式.
     *
     * @return true/false.
     */
    public boolean isOverWriteMode() {
        return StringUtils.equals(RestartModeEnum.OVERWRITE.getMode(), this.getRestartMode());
    }

    /**
     * 设置问题.
     *
     * @param question 问题.
     */
    public void setQuestion(String question) {
        this.businessData.put(AippConst.BS_AIPP_QUESTION_KEY, question);
    }

    /**
     * 获取restart模式.
     *
     * @return {@link String} 对象
     */
    public String getRestartMode() {
        return ObjectUtils.cast(this.businessData.get(AippConst.RESTART_MODE));
    }

    /**
     * 获取任务实例id.
     *
     * @return {@link String} 对象
     */
    public String getTaskInstanceId() {
        return ObjectUtils.cast(this.businessData.get(BS_AIPP_INST_ID_KEY));
    }

    /**
     * 设置app版本id.
     *
     * @param appId app版本id.
     */
    public void setAppId(String appId) {
        this.businessData.put(AippConst.CONTEXT_APP_ID, appId);
    }

    /**
     * 获取appId.
     *
     * @return appId.
     */
    public String getAppId() {
        return ObjectUtils.cast(this.businessData.get(AippConst.CONTEXT_APP_ID));
    }

    /**
     * 设置http请求上下文.
     *
     * @param httpContext http请求上下文.
     */
    public void setHttpContext(String httpContext) {
        this.businessData.put(BS_HTTP_CONTEXT_KEY, httpContext);
    }

    /**
     * 设置父流程id.
     *
     * @param parentInstanceId 父流程id.
     */
    public void setParentInstanceId(String parentInstanceId) {
        this.businessData.put(PARENT_INSTANCE_ID, parentInstanceId);
    }

    /**
     * 获取父任务实例id.
     *
     * @return 父任务实例id.
     */
    public String getParentInstanceId() {
        return ObjectUtils.cast(this.businessData.get(PARENT_INSTANCE_ID));
    }

    /**
     * 设置回调id.
     *
     * @param callbackId 回调id.
     */
    public void setCallbackId(String callbackId) {
        this.businessData.put(PARENT_CALLBACK_ID, callbackId);
    }

    /**
     * 设置用户id.
     *
     * @param userId 用户id.
     */
    public void setUserId(String userId) {
        this.businessData.put(CONTEXT_USER_ID, userId);
    }

    /**
     * 清除 memories.
     */
    public void clearMemories() {
        this.setMemories(new ArrayList<>());
    }

    /**
     * 设置memories.
     *
     * @param memories 记忆集.
     */
    public void setMemories(List<Map<String, Object>> memories) {
        this.businessData.put(BS_AIPP_MEMORIES_KEY, memories);
    }

    /**
     * 获取会话的id.
     *
     * @return 会话id.
     */
    public String getChatId() {
        return ObjectUtils.cast(this.businessData.get(BS_CHAT_ID));
    }

    /**
     * 设置会话id.
     *
     * @param chatId 会话id.
     */
    public void setChatId(String chatId) {
        this.businessData.put(BS_CHAT_ID, chatId);
    }

    /**
     * 获取被艾特的会话id.
     *
     * @return {@link String} 对象
     */
    public String getAtChatId() {
        return ObjectUtils.cast(this.businessData.get(AippConst.BS_AT_CHAT_ID));
    }

    /**
     * 设置被艾特的会话id.
     *
     * @param atChatId 被at的会话id
     */
    public void setAtChatId(String atChatId) {
        this.businessData.put(AippConst.BS_AT_CHAT_ID, atChatId);
    }

    /**
     * 设置被艾特的应用版本id.
     *
     * @param atAppId 被at的应用版本id
     */
    public void setAtAppId(String atAppId) {
        this.businessData.put(AippConst.BS_AT_APP_ID, atAppId);
    }

    /**
     * 获取被at的应用版本id.
     *
     * @return 应用版本id.
     */
    public String getAtAppId() {
        return ObjectUtils.cast(this.businessData.get(AippConst.BS_AT_APP_ID));
    }

    /**
     * 是否使用memory.
     *
     * @return {@link Boolean} 对象
     */
    public Boolean isUseMemory() {
        return ObjectUtils.cast(this.businessData.get(BS_AIPP_USE_MEMORY_KEY));
    }

    /**
     * 设置是否使用memory.
     *
     * @param useMemory 是否使用memory.
     */
    public void setUseMemory(Boolean useMemory) {
        this.businessData.put(BS_AIPP_USE_MEMORY_KEY, useMemory);
    }

    /**
     * 设置dimension.
     *
     * @param dimension 维度.
     */
    public void setDimension(String dimension) {
        this.businessData.put("dimension", dimension);
    }

    /**
     * 获取问题.
     *
     * @return {@link String} 对象
     */
    public String getQuestion() {
        return ObjectUtils.cast(this.businessData.get(BS_AIPP_QUESTION_KEY));
    }

    /**
     * 获取实例名称.
     *
     * @return {@link String} 对象
     */
    public String getInstanceName() {
        return ObjectUtils.cast(this.businessData.get(AippConst.INST_NAME_KEY));
    }

    /**
     * 设置原始应用版本id.
     *
     * @param originAppId 原始应用版本id.
     */
    public void setOriginAppId(String originAppId) {
        this.businessData.put(AippConst.BS_ORIGIN_APP_ID, originAppId);
    }

    /**
     * 获取原始的appId.
     *
     * @return 原始appId.
     */
    public String getOriginAppId() {
        return ObjectUtils.cast(this.businessData.get(AippConst.BS_ORIGIN_APP_ID));
    }

    /**
     * 设置原始应用会话id.
     *
     * @param originChatId 原始应用会话id.
     */
    public void setOriginChatId(String originChatId) {
        this.businessData.put(AippConst.BS_ORIGIN_CHAT_ID, originChatId);
    }

    /**
     * 设置流程id.
     *
     * @param flowTraceId 流程id..
     */
    public void setFlowTraceId(String flowTraceId) {
        this.businessData.put(AippConst.INST_FLOW_INST_ID_KEY, flowTraceId);
    }

    /**
     * 获取流程id.
     *
     * @return {@link String} 流程id.
     */
    public String getFlowTraceId() {
        return ObjectUtils.cast(this.businessData.get(AippConst.INST_FLOW_INST_ID_KEY));
    }

    /**
     * 获取原始应用会话id.
     *
     * @return 原始应用会话id.
     */
    public String getOriginChatId() {
        return Optional.ofNullable(this.businessData.get(AippConst.BS_ORIGIN_CHAT_ID))
                .map(ObjectUtils::<String>cast)
                .orElseGet(this::getChatId);
    }

    /**
     * 设置产品线的id信息
     *
     * @param dimensionId 产品线的id信息
     */
    public void setDimensionId(String dimensionId) {
        this.businessData.put(AippConst.BS_DIMENSION_ID_KEY, dimensionId);
    }

    /**
     * 获取产品线的id信息
     *
     * @return {@link String} 产品线的id信息.
     */
    public String getDimensionId() {
        return ObjectUtils.cast(this.businessData.get(AippConst.BS_DIMENSION_ID_KEY));
    }

    /**
     * 获取文件描述信息列表.
     *
     * @return {@link String} 对象
     */
    public List<Map<String, String>> getFileDescriptions() {
        return Optional.ofNullable(this.businessData.get(BS_AIPP_FILE_DESC_KEY))
                .map(ObjectUtils::<List<Map<String, String>>>cast)
                .orElse(Collections.emptyList());
    }

    /**
     * 判断restart模式是否是INCREMENT模式.
     *
     * @return {@link String} 对象
     */
    public boolean isIncrementMode() {
        return StringUtils.equals(RestartModeEnum.INCREMENT.getMode(),
                ObjectUtils.cast(this.businessData.get(RESTART_MODE)));
    }

    /**
     * 设置 resume duration.
     *
     * @param duration 间隔.
     */
    public void setResumeDuration(long duration) {
        this.businessData.put(AippConst.INST_RESUME_DURATION_KEY, String.valueOf(duration));
    }

    /**
     * 设置上下文的任务实例id.
     *
     * @param taskInstanceId {@link String} 对象
     */
    public void setContextTaskInstanceId(String taskInstanceId) {
        this.businessData.put(AippConst.CONTEXT_INSTANCE_ID, taskInstanceId);
    }

    /**
     * 获取上下文的任务实例id.
     *
     * @return 表示上下文的任务实例id的 {@link String} 对象
     */
    public String getContextTaskInstanceId() {
        return ObjectUtils.cast(this.businessData.get(AippConst.CONTEXT_INSTANCE_ID));
    }
}
