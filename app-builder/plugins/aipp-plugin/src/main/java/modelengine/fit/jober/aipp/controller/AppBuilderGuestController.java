/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jober.aipp.controller;

import io.opentelemetry.api.trace.Span;
import modelengine.fit.http.annotation.DeleteMapping;
import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.PatchMapping;
import modelengine.fit.http.annotation.PathVariable;
import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.PutMapping;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.http.annotation.RequestParam;
import modelengine.fit.http.entity.PartitionedEntity;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.jane.common.controller.AbstractController;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jane.task.gateway.Authenticator;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.common.exception.AippTaskNotFoundException;
import modelengine.fit.jober.aipp.dto.AippCreateDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.AppBuilderPromptCategoryDto;
import modelengine.fit.jober.aipp.dto.AppBuilderPromptDto;
import modelengine.fit.jober.aipp.dto.FileRspDto;
import modelengine.fit.jober.aipp.dto.ResumeAippDto;
import modelengine.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRspDto;
import modelengine.fit.jober.aipp.genericable.AppBuilderAppService;
import modelengine.fit.jober.aipp.service.AippChatService;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AippRunTimeService;
import modelengine.fit.jober.aipp.service.AppBuilderPromptService;
import modelengine.fit.jober.aipp.service.AppChatService;
import modelengine.fit.jober.aipp.service.FileService;
import modelengine.fit.jober.aipp.util.AippFileUtils;
import modelengine.fit.jober.aipp.util.ConvertUtils;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.app.engine.base.dto.AppBuilderRecommendDto;
import modelengine.jade.app.engine.base.dto.UsrFeedbackDto;
import modelengine.jade.app.engine.base.service.AppBuilderRecommendService;
import modelengine.jade.app.engine.base.service.UsrFeedbackService;
import modelengine.jade.service.annotations.CarverSpan;
import modelengine.jade.service.annotations.SpanAttr;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 免登录接口。
 *
 * @author 陈潇文
 * @since 2025-08-27
 */
@Component
@RequestMapping(path = "/v1/api/guest")
public class AppBuilderGuestController extends AbstractController {
    private static final Logger LOGGER = Logger.get(AppBuilderGuestController.class);
    private final modelengine.fit.jober.aipp.genericable.AppBuilderAppService appGenericable;
    private final AppChatService appChatService;
    private final AippLogService aippLogService;
    private final AppBuilderPromptService appBuilderPromptService;
    private final AippRunTimeService aippRunTimeService;
    private final UsrFeedbackService usrFeedbackService;
    private final AppBuilderRecommendService recommendService;
    private final FileService fileService;
    private final AippChatService aippChatService;

    /**
     * 用限校验认的证器对象 {@link Authenticator}， 应用通用服务 {@link AppBuilderAppService}，对话服务 {@link AppChatService}，实例历史记录服务
     * {@link AippLogService}，灵感大全服务类 {@link AppBuilderPromptService}，运行时服务类 {@link AippRunTimeService}，用户反馈服务类
     * {@link UsrFeedbackService}，
     * 猜你想问服务类 {@link AppBuilderRecommendService}，文件服务类 {@link FileService} 和应用聊天服务类 {@link AippChatService} 构造
     * {@link AppBuilderGuestController}。
     *
     * @param authenticator 表示权限校验认的证器对象的 {@link Authenticator}。
     * @param appGenericable 表示应用通用服务的 {@link AppBuilderAppService}。
     * @param appChatService 表示对话服务的 {@link AppChatService}。
     * @param aippLogService 表示实例历史记录服务的 {@link AippLogService}。
     * @param appBuilderPromptService 表示灵感大全服务的 {@link AppBuilderPromptService}。
     * @param aippRunTimeService 表示运行时服务的 {@link AippRunTimeService}。
     * @param usrFeedbackService 表示用户反馈服务的 {@link UsrFeedbackService}。
     * @param recommendService 表示猜你想问服务的 {@link AppBuilderRecommendService}。
     * @param fileService 表示文件服务的 {@link FileService}。
     * @param aippChatService 表示应用聊天服务的 {@link AippChatService}。
     */
    public AppBuilderGuestController(Authenticator authenticator, AppBuilderAppService appGenericable,
            AppChatService appChatService, AippLogService aippLogService,
            AppBuilderPromptService appBuilderPromptService, AippRunTimeService aippRunTimeService,
            UsrFeedbackService usrFeedbackService, AppBuilderRecommendService recommendService, FileService fileService,
            AippChatService aippChatService) {
        super(authenticator);
        this.appGenericable = appGenericable;
        this.appChatService = appChatService;
        this.aippLogService = aippLogService;
        this.appBuilderPromptService = appBuilderPromptService;
        this.aippRunTimeService = aippRunTimeService;
        this.usrFeedbackService = usrFeedbackService;
        this.recommendService = recommendService;
        this.fileService = fileService;
        this.aippChatService = aippChatService;
    }

    /**
     * 查询应用是否打开游客模式。
     *
     * @param httpRequest 表示 Http 请求对象的 {@link HttpClassicServerRequest}。
     * @param path 表示待查询应用的 path 的 {@link String}。
     * @return 表示是否打开游客模式的 {@link Rsp}{@code <}{@link Boolean}{@code >}。
     */
    @GetMapping(value = "/{path}/is_open", description = "查询应用是否打开游客模式")
    public Rsp<Boolean> queryAppIsOpen(HttpClassicServerRequest httpRequest, @PathVariable("path") String path) {
        AppBuilderAppDto appDto = this.appGenericable.queryByPath(path);
        return Rsp.ok(ObjectUtils.cast(appDto.getAttributes().getOrDefault("allow_guest", false)));
    }

    /**
     * 查询应用详情。
     *
     * @param httpRequest 表示 Http 请求对象的 {@link HttpClassicServerRequest}。
     * @param path 表示待查询应用的 Path {@link String}。
     * @return 表示查询应用的最新可编排版本的实体类的 {@link Rsp}{@code <}{@link AppBuilderAppDto}{@code >}。
     */
    @GetMapping(value = "/{path}", description = "查询应用详情")
    public Rsp<AppBuilderAppDto> queryByPath(HttpClassicServerRequest httpRequest, @PathVariable("path") String path) {
        return Rsp.ok(this.appGenericable.queryByPath(path));
    }

    /**
     * 对话接口。
     *
     * @param httpRequest 表示 Http 请求对象的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param body 表示会话参数的 {@link CreateAppChatRequest}。
     * @return 表示 SSE 流的 {@link Choir}{@code <}{@link Object}{@code >}。
     * @throws AippTaskNotFoundException 表示任务不存在异常的 {@link AippTaskNotFoundException}。
     */
    @PostMapping(value = "/{tenant_id}/app_chat", description = "会话接口，传递会话信息")
    public Choir<Object> chat(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestBody CreateAppChatRequest body) throws AippTaskNotFoundException {
        this.validateChat(httpRequest, body);
        return this.appChatService.chat(body, this.contextOf(httpRequest, tenantId), false);
    }

    /**
     * 根据聊天唯一标识查询历史记录。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param appId 表示待发布应用的唯一标识的 {@link String}。
     * @param chatId 表示会话唯一标识的 {@link String}。
     * @return 表示会话历史记录的 {@link Rsp}{@code <}{@link List}{@code <}{@link AippInstLogDataDto}{@code >>}。
     */
    @GetMapping(value = "/{tenant_id}/log/app/{app_id}/chat/{chat_id}",
            description = "指定chatId查询实例历史记录（查询最近10个实例）")
    public Rsp<List<AippInstLogDataDto>> queryChatRecentChatLog(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @PathVariable("chat_id") String chatId) {
        return Rsp.ok(this.aippLogService.queryChatRecentChatLog(chatId, appId, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 获取应用最新发布版本信息。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param appId 表示待发布应用的唯一标识的 {@link String}。
     * @return 表示返回结果的 {@link Rsp}{@code <}{@link AippCreateDto}{@code >}。
     */
    @GetMapping(path = "/{tenant_id}/app/{app_id}/latest_published", description = "获取 app 最新发布版本信息")
    public Rsp<AippCreateDto> latestPublished(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId) {
        return Rsp.ok(ConvertUtils.toAippCreateDto(this.appGenericable.queryLatestPublished(appId,
                this.contextOf(httpRequest, tenantId))));
    }

    /**
     * 查询单个应用。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param appId 表示待查询应用的唯一标识的 {@link String}。
     * @return 表示查询应用的最新可编排版本的实体类的 {@link Rsp}{@code <}{@link AppBuilderAppDto}{@code >}。
     */
    @GetMapping(value = "/{tenant_id}/{app_id}", description = "查询 app ")
    public Rsp<AppBuilderAppDto> query(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("app_id") String appId) {
        return Rsp.ok(this.appGenericable.query(appId, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 查询所有的灵感类别。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param isDebug 表示是否是调试状态的 {@link Boolean}。
     * @return 表示返回所有的灵感类别的 {@link Rsp}{@code <}{@link List}{@code <}{@link AppBuilderPromptCategoryDto}{@code >>}。
     */
    @GetMapping(path = "/{tenant_id}/app/{app_id}/prompt")
    public Rsp<List<AppBuilderPromptCategoryDto>> listCategories(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @RequestParam(value = "isDebug", defaultValue = "true", required = false) boolean isDebug) {
        return appBuilderPromptService.listPromptCategories(appId, this.contextOf(httpRequest, tenantId), isDebug);
    }

    /**
     * 查询指定类别的所有灵感。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param categoryId 表示“我的”类别唯一标识的 {@link String}。
     * @param isDebug 表示是否是调试状态的 {@link Boolean}。
     * @return 表示返回指定类别的所有灵感 {@link Rsp}{@code <}{@link AppBuilderPromptDto}{@code >}。
     */
    @GetMapping("/{tenant_id}/app/{app_id}/prompt/{category_id}")
    public Rsp<AppBuilderPromptDto> queryInspirations(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("app_id") String appId,
            @PathVariable("category_id") String categoryId,
            @RequestParam(value = "isDebug", defaultValue = "true", required = false) boolean isDebug) {
        return this.appBuilderPromptService.queryInspirations(appId,
                categoryId,
                this.contextOf(httpRequest, tenantId),
                isDebug);
    }

    /**
     * 添加我的灵感。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param parentId 表示父类别标识的 {@link String}。
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param inspirationDto 表示待添加的灵感内容的 {@link AppBuilderPromptDto.AppBuilderInspirationDto}。
     * @return 表示空返回的 {@link Rsp}{@code <}{@link Void}{@code >}。
     */
    @CarverSpan(value = "operation.inspiration.addMy")
    @PostMapping("/{tenant_id}/app/{app_id}/prompt/{parent_id}")
    public Rsp<Void> addMyInspiration(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("parent_id") String parentId, @SpanAttr("app_id") @PathVariable("app_id") String appId,
            @RequestBody AppBuilderPromptDto.AppBuilderInspirationDto inspirationDto) {
        this.appBuilderPromptService.addCustomInspiration(appId,
                parentId,
                inspirationDto,
                this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }

    /**
     * 更新我的灵感。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param categoryId 表示“我的”类别唯一标识的 {@link String}。
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param inspirationId 表示要修改的灵感唯一标识的 {@link String}。
     * @param inspirationDto 表示待添加的灵感内容的 {@link AppBuilderPromptDto.AppBuilderInspirationDto}。
     * @return 表示空返回的 {@link Rsp}{@code <}{@link Void}{@code >}。
     */
    @CarverSpan(value = "operation.inspiration.updateMy")
    @PutMapping("/{tenant_id}/app/{app_id}/prompt/{category_id}/inspiration/{inspiration_id}")
    public Rsp<Void> updateMyInspiration(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("category_id") String categoryId,
            @SpanAttr("app_id") @PathVariable("app_id") String appId,
            @SpanAttr("inspiration_id") @PathVariable("inspiration_id") String inspirationId,
            @RequestBody AppBuilderPromptDto.AppBuilderInspirationDto inspirationDto) {
        this.appBuilderPromptService.updateCustomInspiration(appId,
                categoryId,
                inspirationId,
                inspirationDto,
                this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }

    /**
     * 删除我的灵感。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param categoryId 表示“我的”类别唯一标识的 {@link String}。
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param inspirationId 表示要删除的灵感唯一标识的 {@link String}。
     * @return 表示空返回的 {@link Rsp}{@code <}{@link Void}{@code >}。
     */
    @CarverSpan(value = "operation.inspiration.delete")
    @DeleteMapping("/{tenant_id}/app/{app_id}/prompt/{category_id}/inspiration/{inspiration_id}")
    public Rsp<Void> deleteMyInspiration(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @PathVariable("category_id") String categoryId,
            @SpanAttr("app_id") @PathVariable("app_id") String appId,
            @SpanAttr("inspiration_id") @PathVariable("inspiration_id") String inspirationId) {
        this.appBuilderPromptService.deleteCustomInspiration(appId,
                categoryId,
                inspirationId,
                this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }

    /**
     * 终止实例任务。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param instanceId 表示实例标识的 {@link String}。
     * @param msgArgs 表示用于终止时返回的信息的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @return 表示返回终止信息的 {@link Rsp}{@code <}{@link String}{@code >}。
     */
    @CarverSpan(value = "operation.appRuntime.terminate")
    @PutMapping(path = "/{tenant_id}/instances/{instance_id}/terminate", description = "终止实例任务")
    public Rsp<String> terminateAippInstance(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId,
            @PathVariable("instance_id") @SpanAttr("instance_id") String instanceId,
            @RequestBody Map<String, Object> msgArgs) {
        return Rsp.ok(this.aippRunTimeService.terminateInstance(instanceId,
                msgArgs,
                this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 用于表单的终止实例任务。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param instanceId 表示实例标识的 {@link String}。
     * @param msgArgs 表示用于终止时返回的信息的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @param logId 表示日志标识的 {@link Long}。
     * @return 表示返回终止信息的 {@link Rsp}{@code <}{@link String}{@code >}。
     */
    @CarverSpan(value = "operation.appRuntime.terminate.form")
    @PutMapping(path = "/{tenant_id}/instances/{instance_id}/terminate/log/{log_id}",
            description = "用于表单的终止实例任务")
    public Rsp<String> terminateAippInstance(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId,
            @PathVariable("instance_id") @SpanAttr("instance_id") String instanceId,
            @RequestBody Map<String, Object> msgArgs, @PathVariable("log_id") Long logId) {
        return Rsp.ok(this.aippRunTimeService.terminateInstance(instanceId,
                msgArgs,
                logId,
                this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 删除指定的应用对话记录。
     *
     * @param logIds 表示需要删除的对话记录列表的 {@link List}{@code <}{@link Long}{@code >}。
     * @return 表示返回空回复的 {@link Rsp}{@code <}{@link Void}{@code >}。
     */
    @CarverSpan(value = "operation.aippLog.deleteHistory")
    @DeleteMapping(path = "/{app_id}/log/logs", description = "删除指定的应用对话记录")
    public Rsp<Void> deleteLogs(@RequestBody List<Long> logIds) {
        Span.current().setAttribute("logIds", logIds.toString());
        this.aippLogService.deleteLogs(logIds);
        return Rsp.ok();
    }

    /**
     * 创建用户反馈记录。
     *
     * @param usrFeedbackDto 表示用户反馈消息体的 {@link UsrFeedbackDto}。
     */
    @PostMapping("/feedback")
    public void createUsrFeedback(@RequestBody UsrFeedbackDto usrFeedbackDto) {
        this.usrFeedbackService.create(usrFeedbackDto);
    }

    /**
     * 更新用户反馈信息。
     *
     * @param instanceId 表示对话实例标识的 {@link String}。
     * @param usrFeedbackDto 表示用户反馈消息体的 {@link UsrFeedbackDto}。
     */
    @PatchMapping("/feedback/{instanceId}")
    public void updateUsrFeedback(@PathVariable("instanceId") String instanceId,
            @RequestBody UsrFeedbackDto usrFeedbackDto) {
        this.usrFeedbackService.updateOne(instanceId, usrFeedbackDto);
    }

    /**
     * 删除用户反馈信息。
     *
     * @param instanceId 表示对话实例标识的 {@link String}。
     */
    @DeleteMapping("/feedback/{instanceId}")
    public void deleteByLogId(@PathVariable("instanceId") String instanceId) {
        this.usrFeedbackService.deleteByLogId(instanceId);
    }

    /**
     * 获取用户反馈信息列表。
     *
     * @return 表示用户反馈信息列表的 {@link List}{@code <}{@link UsrFeedbackDto}{@code >}。
     */
    @GetMapping("/feedbacks")
    public List<UsrFeedbackDto> getAllUsrFeedbacks() {
        return this.usrFeedbackService.getAllUsrFeedbacks();
    }

    /**
     * 通过日志唯一标识获取对话信息列表。
     *
     * @param instanceId 对话实例唯一标识的 {@link String}。
     * @return 表示对话信息的 {@link UsrFeedbackDto}。
     */
    @GetMapping("/feedback/{instanceId}")
    public UsrFeedbackDto getAllAnswerByInstanceId(@PathVariable("instanceId") String instanceId) {
        return this.usrFeedbackService.getUsrFeedbackByInstanceId(instanceId);
    }

    /**
     * 重新发起会话。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param currentInstanceId 表示需要重新发起会话的实例标识的 {@link String}。
     * @param additionalContext
     * 表示重新会话需要的信息，如是否使用多轮对话等等的{@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @return 表示会话相应体的 sse 流的 {@link Choir}{@code <}{@link Object}{@code >}。
     */
    @CarverSpan(value = "operation.appChat.restartChat")
    @PostMapping(path = "/{tenant_id}/instances/{current_instance_id}", description = "重新发起会话接口")
    public Choir<Object> restartChat(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("current_instance_id") @SpanAttr("current_instance_id") String currentInstanceId,
            @RequestBody Map<String, Object> additionalContext) {
        return this.appChatService.restartChat(currentInstanceId,
                additionalContext,
                this.contextOf(httpRequest, tenantId));
    }

    /**
     * 获取猜你想问推荐列表。
     *
     * @param request 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param recommendDto 表示上次对话用户提问及模型回答的 {@link AppBuilderRecommendDto}。
     * @return 表示返回三个推荐问题列表的 {@link Rsp}{@code <}{@link List}{@code <}{@link String}{@code >>}。
     */
    @PostMapping(path = "/recommend")
    public Rsp<List<String>> queryRecommends(HttpClassicServerRequest request,
            @RequestBody AppBuilderRecommendDto recommendDto) {
        return Rsp.ok(this.recommendService.queryRecommends(recommendDto, this.contextOf(request, "")));
    }

    /**
     * 批量上传文件。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param receivedFiles 表示接收到的文件的 {@link PartitionedEntity}。
     * @return 表示文件返回列表的 {@link Rsp}{@code <}{@link List}{@code <}{@link FileRspDto}{@code >>}。
     */
    @CarverSpan(value = "operation.file.batch.upload")
    @PostMapping(path = "/{tenant_id}/files", description = "批量上传文件")
    public Rsp<List<FileRspDto>> batchUploadFile(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestParam(value = "app_id", required = false) String appId,
            PartitionedEntity receivedFiles) {
        OperationContext context = this.contextOf(httpRequest, tenantId);
        List<FileRspDto> fileRspDtos = AippFileUtils.getFileEntity(receivedFiles).stream().map(fileEntity -> {
            try {
                return this.fileService.uploadFile(context, tenantId, fileEntity.filename(), appId, fileEntity);
            } catch (IOException e) {
                throw new AippException(AippErrCode.UPLOAD_FAILED);
            }
        }).toList();
        return Rsp.ok(fileRspDtos);
    }

    /**
     * 清空应用的历史对话。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param chatIds 表示会话标识的字符串的 {@link String}。当传入多个标识时，以“,”进行分隔。
     * @return 表示清空应用的返回的 {@link Rsp}{@code <}{@link Void}{@code >}。
     */
    @CarverSpan(value = "operation.aippChat.delete")
    @DeleteMapping(path = "/{tenant_id}/chat", description = "删除会话接口")
    public Rsp<Void> deleteChat(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @RequestParam(value = "app_id", required = false) String appId,
            @RequestBody(value = "chat_id", required = false) @SpanAttr("chat_id") String chatIds) {
        this.aippChatService.deleteChat(chatIds, appId, this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }

    /**
     * 查询会话列表。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param body 标识消息体的 {@link QueryChatRequest}。
     * @return 表示会话列表返回的 {@link Rsp}{@code <}{@link RangedResultSet}{@code <}{@link QueryChatRspDto}{@code >>}。
     */
    @CarverSpan(value = "operation.aippChat.queryList")
    @PostMapping(path = "/{tenant_id}/chat/chat_list", description = "查询会话列表接口")
    public Rsp<RangedResultSet<QueryChatRspDto>> queryChatList(HttpClassicServerRequest httpRequest,
            @PathVariable("tenant_id") String tenantId, @RequestBody QueryChatRequest body) {
        if (StringUtils.isEmpty(body.getAppState())) {
            body.setAppState("active");
        }
        return Rsp.ok(this.aippChatService.queryChatList(body, this.contextOf(httpRequest, tenantId)));
    }

    /**
     * 更新表单数据，并恢复实例任务执行。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param resumeAippDto 表示恢复实例运行的启动参数类的 {@link ResumeAippDto}。
     * @param formArgs 用于填充表单的数据的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     * @return 表示返回流式回复的 {@link Choir}{@code <}{@link Object}{@code >}。
     */
    @CarverSpan(value = "operation.appRuntime.updateResume")
    @PutMapping(path = "/{tenant_id}/app/instances/{instance_id}/log/{log_id}",
            description = "更新表单数据，并恢复实例任务执行")
    public Choir<Object> resumeAndUpdateAippInstance(HttpClassicServerRequest httpRequest,
            @RequestBean ResumeAippDto resumeAippDto,
            @Property(description = "用户填写的表单信息", example = "用户选择的大模型信息") @RequestBody
            Map<String, Object> formArgs) {
        return this.aippRunTimeService.resumeAndUpdateAippInstance(resumeAippDto.getInstanceId(),
                formArgs,
                resumeAippDto.getLogId(),
                this.contextOf(httpRequest, resumeAippDto.getTenantId()),
                resumeAippDto.isDebug());
    }

    /**
     * 清除应用唯一标识查询实例的全部历史记录。
     *
     * @param httpRequest 表示 Http 请求体的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param type 表示类型的 {@link String}。
     * @return 表示空返回的 {@link Rsp}{@code <}{@link Void}{@code >}。
     */
    @CarverSpan(value = "operation.aippLog.eraseHistory")
    @DeleteMapping(path = "/{tenant_id}/log/app/{app_id}", description = "清除appId查询实例的全部历史记录")
    public Rsp<Void> deleteInstanceLog(HttpClassicServerRequest httpRequest, @PathVariable("tenant_id") String tenantId,
            @PathVariable("app_id") @SpanAttr("app_id") String appId, @RequestParam("type") String type) {
        this.aippLogService.deleteAippInstLog(appId, type, this.contextOf(httpRequest, tenantId));
        return Rsp.ok();
    }

    private void validateChatBody(CreateAppChatRequest body) {
        if (body == null || body.getContext() == null || StringUtils.isEmpty(body.getAppId())) {
            LOGGER.error("The input chat body is incorrect.");
            throw new AippParamException(AippErrCode.APP_CHAT_REQUEST_IS_NULL);
        }
    }

    private void validateChatQuestion(CreateAppChatRequest body) {
        if (StringUtils.isEmpty(body.getQuestion())) {
            LOGGER.error("The input chat body is incorrect.");
            throw new AippParamException(AippErrCode.APP_CHAT_QUESTION_IS_NULL);
        }
    }

    private void validateChat(HttpClassicServerRequest httpRequest, CreateAppChatRequest body) {
        this.validateChatBody(body);
        if (httpRequest.headers().contains("Auto-Chat-On-Upload") && !Objects.equals(httpRequest.headers()
                .require("Auto-Chat-On-Upload"), "true")) {
            this.validateChatQuestion(body);
        }
    }
}
