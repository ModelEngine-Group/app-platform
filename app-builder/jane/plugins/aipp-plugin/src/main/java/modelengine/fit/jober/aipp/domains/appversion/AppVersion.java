/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.appversion;

import static modelengine.fit.jober.aipp.common.exception.AippErrCode.APP_CHAT_ERROR;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.APP_CHAT_PUBLISHED_META_NOT_FOUND;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.APP_HAS_PUBLISHED;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.APP_NOT_FOUND_WHEN_CHAT;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.APP_VERSION_HAS_ALREADY;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.INPUT_PARAM_IS_INVALID;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.NEW_VERSION_IS_LOWER;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.UPDATE_APP_CONFIGURATION_FAILED;
import static modelengine.fit.jober.aipp.constants.AippConst.ATTR_AIPP_TYPE_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.ATTR_APP_IS_UPDATE;
import static modelengine.fit.jober.aipp.constants.AippConst.ATTR_META_STATUS_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.BS_AIPP_QUESTION_KEY;
import static modelengine.fit.jober.aipp.constants.AippConst.RESTART_MODE;
import static modelengine.fit.jober.aipp.enums.AippMetaStatusEnum.ACTIVE;
import static modelengine.fit.jober.aipp.enums.AippTypeEnum.NORMAL;
import static modelengine.fit.jober.aipp.enums.AippTypeEnum.PREVIEW;
import static modelengine.fit.jober.aipp.enums.AppTypeEnum.APP;
import static modelengine.fit.jober.aipp.enums.RestartModeEnum.OVERWRITE;
import static modelengine.fit.jober.aipp.util.UsefulUtils.doIfNotBlank;
import static modelengine.fit.jober.aipp.util.UsefulUtils.doIfNull;
import static modelengine.fitframework.util.ObjectUtils.cast;

import com.alibaba.fastjson.JSON;

import lombok.Getter;
import modelengine.fel.tool.service.ToolService;
import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;
import modelengine.fit.jade.aipp.model.dto.ModelListDto;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fit.jade.waterflow.AippFlowDefinitionService;
import modelengine.fit.jade.waterflow.FlowsService;
import modelengine.fit.jade.waterflow.dto.FlowInfo;
import modelengine.fit.jade.waterflow.entity.FlowDefinitionResult;
import modelengine.fit.jade.waterflow.service.FlowDefinitionService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.converters.ConverterFactory;
import modelengine.fit.jober.aipp.domain.AppBuilderConfig;
import modelengine.fit.jober.aipp.domain.AppBuilderFlowGraph;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.domain.AppTemplate;
import modelengine.fit.jober.aipp.domains.appversion.publish.FormProperyPublisher;
import modelengine.fit.jober.aipp.domains.appversion.publish.FlowPublisher;
import modelengine.fit.jober.aipp.domains.appversion.publish.GraphPublisher;
import modelengine.fit.jober.aipp.domains.appversion.publish.Publisher;
import modelengine.fit.jober.aipp.domains.appversion.publish.StorePublisher;
import modelengine.fit.jober.aipp.domains.appversion.publish.TaskPublisher;
import modelengine.fit.jober.aipp.domains.appversion.repository.AppVersionRepository;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.chat.repository.AppChatRepository;
import modelengine.fit.jober.aipp.domains.definition.service.AppDefinitionService;
import modelengine.fit.jober.aipp.domains.log.AppLog;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.TaskDecorator;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.dto.AippCreateDto;
import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fit.jober.aipp.dto.AippNodeForms;
import modelengine.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto;
import modelengine.fit.jober.aipp.dto.AppInputParam;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRsp;
import modelengine.fit.jober.aipp.dto.export.AppExportApp;
import modelengine.fit.jober.aipp.dto.export.AppExportConfig;
import modelengine.fit.jober.aipp.dto.export.AppExportDto;
import modelengine.fit.jober.aipp.dto.export.AppExportFlowGraph;
import modelengine.fit.jober.aipp.dto.template.TemplateAppCreateDto;
import modelengine.fit.jober.aipp.dto.template.TemplateInfoDto;
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.entity.ChatSession;
import modelengine.fit.jober.aipp.enums.AippMetaStatusEnum;
import modelengine.fit.jober.aipp.enums.AippTypeEnum;
import modelengine.fit.jober.aipp.enums.AppState;
import modelengine.fit.jober.aipp.enums.AppStatus;
import modelengine.fit.jober.aipp.factory.AppTemplateFactory;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormRepository;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.UploadedFileManageService;
import modelengine.fit.jober.aipp.util.AippFileUtils;
import modelengine.fit.jober.aipp.util.AippStringUtils;
import modelengine.fit.jober.aipp.util.AppImExportUtil;
import modelengine.fit.jober.aipp.util.FlowInfoUtil;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fit.jober.aipp.util.RandomPathUtils;
import modelengine.fit.jober.aipp.util.Retryable;
import modelengine.fit.jober.aipp.util.TemplateUtils;
import modelengine.fit.jober.aipp.util.UUIDUtil;
import modelengine.fit.jober.aipp.util.UsefulUtils;
import modelengine.fit.jober.aipp.util.VersionUtils;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.common.exceptions.JobberException;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.merge.ConflictResolutionPolicy;
import modelengine.fitframework.transaction.DataAccessException;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.common.globalization.LocaleService;
import modelengine.jade.knowledge.KnowledgeCenterService;
import modelengine.jade.knowledge.dto.KnowledgeDto;
import modelengine.jade.store.service.AppService;
import modelengine.jade.store.service.PluginService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用版本.
 *
 * @author 张越
 * @since 2025-01-14
 */
public class AppVersion {
    private static final Logger LOGGER = Logger.get(AppVersion.class);
    private static final String PUBLISH_UPDATE_DESCRIPTION_KEY = "publishedDescription";
    private static final String PUBLISH_UPDATE_LOG_KEY = "publishedUpdateLog";
    private static final int RETRY_PATH_GENERATION_TIMES = 3;
    private static final int PATH_LENGTH = 16;
    private static final String VERSION_FORMAT = "{0}.{1}.{2}";
    private static final int VERSION_LENGTH = 8;
    private static final int RETRY_PREVIEW_TIMES = 5;
    private static final Set<String> TEMPLATE_DEFAULT_ATTRIBUTE_KEYS = new HashSet<>(
            List.of("icon", "description", "greeting", "app_type"));

    @Getter
    private AppBuilderAppPo data;

    @Getter
    private Map<String, Object> attributes;
    private final Integer maxQuestionLen;
    private final Integer maxUserContextLen;

    // 注入属性.
    private final AppBuilderFormPropertyRepository formPropertyRepository;
    private final AppBuilderConfigRepository configRepository;
    private final AppBuilderFormRepository formRepository;
    private final AppBuilderConfigPropertyRepository configPropertyRepository;
    private final AppBuilderFlowGraphRepository flowGraphRepository;
    private final AppVersionRepository appVersionRepository;
    private final AppTaskService appTaskService;
    private final AppTaskInstanceService appTaskInstanceService;
    private final FlowsService flowsService;
    private final AppService appService;
    private final PluginService pluginService;
    private final ToolService toolService;
    private final AppChatRepository appChatRepository;
    private final AppDefinitionService appDefinitionService;
    private final AippLogService aippLogService;
    private final UploadedFileManageService uploadedFileManageService;
    private final AppTemplateFactory templateFactory;
    private final LocaleService localeService;
    private final AippModelCenter aippModelCenter;
    private final ConverterFactory converterFactory;

    // 加载属性.
    private List<AppBuilderFormProperty> formProperties;
    private List<AppTask> tasks;
    private AppBuilderConfig config;
    private AppBuilderFlowGraph flowGraph;
    private LocalDateTime baselineCreateTime;
    private final AippFlowDefinitionService aippFlowDefinitionService;
    private final FlowDefinitionService flowDefinitionService;
    private final KnowledgeCenterService knowledgeCenterService;
    private final String resourcePath;

    AppVersion(AppBuilderAppPo data, Dependencies dependencies) {
        this.data = data;
        this.attributes = StringUtils.isBlank(data.getAttributes())
                ? new HashMap<>()
                : JsonUtils.parseObject(data.getAttributes());
        this.formPropertyRepository = dependencies.getFormPropertyRepository();
        this.appTaskService = dependencies.getAppTaskService();
        this.configRepository = dependencies.getConfigRepository();
        this.formRepository = dependencies.getFormRepository();
        this.configPropertyRepository = dependencies.getConfigPropertyRepository();
        this.flowGraphRepository = dependencies.getFlowGraphRepository();
        this.flowsService = dependencies.getFlowsService();
        this.appService = dependencies.getAppService();
        this.pluginService = dependencies.getPluginService();
        this.toolService = dependencies.getToolService();
        this.appVersionRepository = dependencies.getAppVersionRepository();
        this.appChatRepository = dependencies.getAppChatRepository();
        this.appDefinitionService = dependencies.getAppDefinitionService();
        this.aippLogService = dependencies.getAippLogService();
        this.uploadedFileManageService = dependencies.getUploadedFileManageService();
        this.templateFactory = dependencies.getTemplateFactory();
        this.appTaskInstanceService = dependencies.getAppTaskInstanceService();
        this.localeService = dependencies.getLocaleService();
        this.aippModelCenter = dependencies.getAippModelCenter();
        this.converterFactory = dependencies.getConverterFactory();
        this.aippFlowDefinitionService = dependencies.getAippFlowDefinitionService();
        this.flowDefinitionService = dependencies.getFlowDefinitionService();
        this.maxQuestionLen = dependencies.getMaxQuestionLen();
        this.maxUserContextLen = dependencies.getMaxUserContextLen();
        this.knowledgeCenterService = dependencies.getKnowledgeCenterService();
        this.resourcePath = dependencies.getResourcePath();
    }

    /**
     * 获取baseLine创建时间.
     *
     * @param context 操作人上下文信息.
     * @return {LocalDateTime} 对象.
     */
    public LocalDateTime getBaselineCreateTime(OperationContext context) {
        return UsefulUtils.lazyGet(this.baselineCreateTime, () -> {
            List<AppTask> appTaskList = this.getTasks(context);
            if (CollectionUtils.isEmpty(appTaskList)) {
                return null;
            }
            return appTaskList.get(0).getEntity().getCreationTime();
        }, b -> this.baselineCreateTime = b);
    }

    /**
     * 获取表单配置项集合.
     *
     * @return {@link List}{@code <}{@link AppBuilderFormProperty}{@code >} 集合.
     */
    public List<AppBuilderFormProperty> getFormProperties() {
        return UsefulUtils.lazyGet(this.formProperties,
                () -> this.formPropertyRepository.selectWithAppId(this.data.getId()), ps -> this.formProperties = ps);
    }

    /**
     * 通过id获取 {@link AppBuilderFormProperty}.
     *
     * @param id {@link AppBuilderFormProperty} 唯一标识.
     * @return {@link AppBuilderFormProperty} 对象.
     */
    public AppBuilderFormProperty getFormProperty(String id) {
        List<AppBuilderFormProperty> appBuilderFormPropertyList = this.getFormProperties();
        return appBuilderFormPropertyList.stream()
                .filter(p -> StringUtils.equals(id, p.getId()))
                .findFirst()
                .orElse(new AppBuilderFormProperty());
    }

    /**
     * 获取任务列表，默认按创建时间降序排列.
     *
     * @param context 操作人上下文信息.
     * @return {@link List}{@code <}{@link AppTask}{@code >} 任务列表.
     */
    public List<AppTask> getTasks(OperationContext context) {
        return UsefulUtils.lazyGet(this.tasks, () -> this.appTaskService.getTasksByAppId(this.data.getAppId(), context)
                .stream()
                .peek(t -> t.setAppVersion(this))
                .toList(), ts -> this.tasks = ts);
    }

    /**
     * 获取配置.
     *
     * @return {@link AppBuilderConfig} 对象.
     */
    public AppBuilderConfig getConfig() {
        return UsefulUtils.lazyGet(this.config, this::loadConfig, v -> this.config = v);
    }

    private AppBuilderConfig loadConfig() {
        String configId = Validation.notNull(this.data.getConfigId(), "App config id can not be null.");
        AppBuilderConfig appBuilderConfig = this.configRepository.selectWithId(configId);
        Validation.notNull(appBuilderConfig, "App builder config can not be null.");
        appBuilderConfig.setFormRepository(this.formRepository);
        appBuilderConfig.setFormPropertyRepository(this.formPropertyRepository);
        appBuilderConfig.setConfigPropertyRepository(this.configPropertyRepository);
        appBuilderConfig.setAppVersion(this);
        return appBuilderConfig;
    }

    /**
     * 获取画布数据.
     *
     * @return {@link AppBuilderFlowGraph} 数据.
     */
    public AppBuilderFlowGraph getFlowGraph() {
        return UsefulUtils.lazyGet(this.flowGraph, this::loadFlowGraph, g -> this.flowGraph = g);
    }

    private AppBuilderFlowGraph loadFlowGraph() {
        String flowGraphId = Validation.notNull(this.data.getFlowGraphId(), "App flow graph id can not be null.");
        return this.flowGraphRepository.selectWithId(flowGraphId);
    }

    /**
     * app是否已经发布.
     *
     * @return true/false, true表示已发布; 否则, 未发布.
     */
    public boolean isPublished() {
        return StringUtils.equals(AppStatus.PUBLISHED.getName(), this.data.getStatus());
    }

    /**
     * 发布一个应用
     *
     * @param context 发布上下文.
     */
    public void publish(PublishContext context) {
        if (this.isPublished()) {
            throw new AippException(APP_HAS_PUBLISHED);
        }

        // 判断版本是否已存在.
        this.validateVersion(context);

        // 发布.
        List<Publisher> publishers = new ArrayList<>();
        publishers.add(new GraphPublisher(this.flowGraphRepository));
        publishers.add(new FormProperyPublisher(this.formPropertyRepository));
        publishers.add(new FlowPublisher(this.flowsService));
        publishers.add(new StorePublisher(this.appService, this.pluginService, this.toolService));
        publishers.add(new TaskPublisher(this.appTaskService));
        publishers.forEach(p -> p.publish(context, this));

        // 修改appVersion的状态等属性并保存.
        this.data.setState(AppState.PUBLISHED.getName());
        this.data.setStatus(AppStatus.PUBLISHED.getName());
        this.data.setIsActive(true);
        this.data.setUpdateAt(LocalDateTime.now());
        this.data.setUpdateBy(context.getOperationContext().getOperator());
        this.data.setVersion(context.getPublishData().getVersion());
        this.data.setPublishAt(LocalDateTime.now());
        this.attributes.put(PUBLISH_UPDATE_DESCRIPTION_KEY, context.getPublishData().getPublishedDescription());
        this.attributes.put(PUBLISH_UPDATE_LOG_KEY, context.getPublishData().getPublishedUpdateLog());
        this.attributes.put(ATTR_APP_IS_UPDATE, true);
        if (StringUtils.isBlank(this.data.getPath())) {
            this.data.setPath(this.generateUniquePath());
        }
        this.appVersionRepository.update(this);
    }

    private String generateUniquePath() {
        String path;
        int retryTimes = RETRY_PATH_GENERATION_TIMES;
        do {
            path = RandomPathUtils.generateRandomString(PATH_LENGTH);
            if (!this.appVersionRepository.checkPathExists(path)) {
                return path;
            }
            LOGGER.warn("Path already exists, retrying... {} times left", retryTimes - 1);
        } while (retryTimes-- > 0);

        LOGGER.error("Failed to generate a unique path for app after {} retries.", RETRY_PATH_GENERATION_TIMES);
        throw new AippException(UPDATE_APP_CONFIGURATION_FAILED);
    }

    /*
     * 1、校验版本号的大小，若当前版本号比发布的版本号大，抛出AippErrCode.NEW_VERSION_IS_LOWER异常
     * 2、去任务表中查询，若已存在该版本的任务，抛出AippErrCode.APP_VERSION_HAS_ALREADY异常.
     */
    private void validateVersion(PublishContext context) {
        if (VersionUtils.compare(this.data.getVersion(), context.getPublishData().getVersion()) > 0) {
            throw new AippParamException(NEW_VERSION_IS_LOWER);
        }
        RangedResultSet<AppTask> resultSet = this.appTaskService.getTasks(AppTask.asQueryEntity(0, 1)
                .latest()
                .addVersion(context.getPublishData().getVersion())
                .addAppSuiteId(this.data.getAppSuiteId())
                .putQueryAttribute(ATTR_AIPP_TYPE_KEY, AippTypeEnum.NORMAL.type())
                .putQueryAttribute(ATTR_META_STATUS_KEY, AippMetaStatusEnum.ACTIVE.getCode())
                .build(), context.getOperationContext());
        if (!resultSet.isEmpty()) {
            throw new AippException(APP_VERSION_HAS_ALREADY);
        }
    }

    /**
     * 运行 AppVersion，只能运行发布过的任务.
     *
     * @param context 运行上下文信息.
     * @param session 会话对象.
     */
    public void run(RunContext context, ChatSession<Object> session) {
        // chatId不存在，创建一个新的chatId.
        String appId = this.getAppIdByChatId(context);

        // 若chatId不存在，则创建个新的.
        context.setChatId(StringUtils.blankIf(context.getChatId(), UUIDUtil.uuid()));

        // 如果是当前的appVersion，直接启动task
        // 否则，执行被艾特的appVersion.
        if (StringUtils.equals(appId, this.data.getAppId())) {
            this.startTask(context, session);
        } else {
            AppVersion appVersion = this.appVersionRepository.selectById(appId)
                    .orElseThrow(() -> new AippException(APP_NOT_FOUND_WHEN_CHAT));
            RunContext clonedContext = context.businessDeepClone();
            clonedContext.setOriginAppId(this.data.getAppId());
            clonedContext.setOriginChatId(context.getChatId());
            clonedContext.setAppId(appId);
            clonedContext.setChatId(context.getAtChatId());
            AppVersionDecorator.decorate(appVersion, this, this.appChatRepository).run(clonedContext, session);
        }
    }


    public void validate(RunContext context, boolean isDebug) {
        // 校验问题是否符合规范.
        this.validateQuestion(context);

        // 添加用户上下文数据，输入参数校验.
        OperationContext ctx = context.getOperationContext();
        AppTask task = isDebug ? this.getLatestTask(ctx) : this.getLatestPublishedTask(ctx);
        this.validateUserContext(task, context.getUserContext(), context.getOperationContext());
    }

    /**
     * 调试 AppVersion，和运行的唯一区别是不需要运行发布过的任务.
     *
     * @param context 运行上下文信息.
     * @param session 会话对象.
     */
    public void debug(RunContext context, ChatSession<Object> session) {
        context.setDebug(true);
        this.run(context, session);
    }

    private void validateQuestion(RunContext context) {
        if (!this.isApp()) {
            return;
        }
        if (context.getQuestion() == null || !StringUtils.lengthBetween(context.getQuestion(), 0, this.maxQuestionLen,
                true, true)) {
            throw new AippParamException(INPUT_PARAM_IS_INVALID, BS_AIPP_QUESTION_KEY);
        }
    }

    private String getAppIdByChatId(RunContext context) {
        String atChatId = context.getAtChatId();
        if (StringUtils.isNotBlank(atChatId)) {
            return this.appChatRepository.getChatById(atChatId, context.getOperationContext().getAccount())
                    .orElseThrow(() -> new AippException(APP_CHAT_ERROR))
                    .getAppId();
        }
        return StringUtils.isNotBlank(context.getAtAppId()) ? context.getAtAppId() : this.getData().getAppId();
    }

    private void startTask(RunContext context, ChatSession<Object> session) {
        LOGGER.info("[perf] [{}] chat updateFlow end, appId={}", System.currentTimeMillis(), this.data.getAppId());

        // 获取将要运行的任务对象.
        OperationContext ctx = context.getOperationContext();
        AppTask task = context.isDebug() ? this.getLatestTask(ctx) : this.getLatestPublishedTask(ctx);

        // 执行任务.
        context.initStartParams();
        doIfNull(context.getRestartMode(), () -> context.setRestartMode(OVERWRITE.getMode()));
        context.setStartTime(LocalDateTime.now());
        TaskDecorator.create(task, this.aippLogService, this.appTaskInstanceService, this.localeService)
                .exceptionLog()
                .run(context, session);

        LOGGER.info("[perf] [{}] chat createInstanceByApp end, appId={}", System.currentTimeMillis(),
                this.data.getAppId());
    }

    private void validateUserContext(AppTask task, Map<String, Object> userContext, OperationContext context) {
        String flowDefinitionId = task.getEntity().getFlowDefinitionId();
        FlowInfo flowInfo = this.flowsService.getFlows(flowDefinitionId, context);
        List<AppInputParam> inputParams = flowInfo.getInputParamsByName("input")
                .stream()
                .peek(map -> map.put("stringMaxLength", this.maxUserContextLen))
                .map(AppInputParam::from)
                .toList();

        if (this.isApp()) {
            inputParams = inputParams.stream()
                    .filter(param -> !StringUtils.equals("Question", param.getName()))
                    .toList();
        }
        if (MapUtils.isEmpty(userContext)) {
            if (inputParams.stream().noneMatch((AppInputParam::isRequired))) {
                return;
            }
            LOGGER.error("No user context when starting a chat.");
            throw new AippParamException(INPUT_PARAM_IS_INVALID, "user context");
        }
        inputParams.forEach(ip -> ip.validate(userContext));
    }

    /**
     * 更新 flow
     *
     * @param context 操作上下文
     */
    public void updateFlows(OperationContext context) {
        if (!this.isUpdated()) {
            return;
        }
        this.preview(this.data.getVersion(), this.converterFactory.convert(this, AippDto.class), context);
        this.attributes.put(ATTR_APP_IS_UPDATE, false);
        this.appVersionRepository.update(this);
    }

    /**
     * 获取最新创建的任务.
     *
     * @param ctx 操作人上下文信息.
     * @return {@link AppTask} 任务对象.
     */
    public AppTask getLatestTask(OperationContext ctx) {
        return this.appTaskService.getTasksByAppId(this.data.getAppId(), ctx)
                .stream()
                .peek(t -> t.setAppVersion(this))
                .findFirst()
                .orElseThrow(() -> new AippException(AippErrCode.APP_CHAT_DEBUG_META_NOT_FOUND));
    }

    /**
     * 获取任意已发布的任务，默认最新创建.
     *
     * @param ctx 操作人上下文信息.
     * @return {@link AppTask} 任务对象.
     */
    public AppTask getLatestPublishedTask(OperationContext ctx) {
        return this.getPublishedTasks(ctx)
                .stream()
                .findFirst()
                .orElseThrow(() -> new AippException(APP_CHAT_PUBLISHED_META_NOT_FOUND));
    }

    /**
     * 获取任意已发布的任务集合
     *
     * @param ctx 操作人上下文信息.
     * @return {@link List}{@code <}{@link AppTask}{@code >} 任务对象.
     */
    public List<AppTask> getPublishedTasks(OperationContext ctx) {
        return this.appTaskService.getTaskList(this.data.getAppSuiteId(), NORMAL.name(), ACTIVE.getCode(), ctx)
                .stream()
                .peek(t -> t.setAppVersion(this))
                .toList();
    }

    /**
     * 通过指定任务id，以及任务实例id的方式，重新启动流程.
     *
     * @param instance 任务实例.
     * @param restartParams 重启参数.
     * @param session SSE会话.
     * @param context 操作人上下文对象.
     * @param onFinished 完成时的回调.
     */
    public void restart(AppTaskInstance instance, Map<String, Object> restartParams, ChatSession<Object> session,
            OperationContext context, Consumer<RunContext> onFinished) {
        List<AppLog> instanceLogs = instance.getLogs();
        List<QueryChatRsp> chatList = instance.getChats();
        if (CollectionUtils.isEmpty(chatList)) {
            LOGGER.error("ChatList is empty. [InstanceId={}]", instance.getId());
            throw new AippParamException(AippErrCode.RE_CHAT_FAILED);
        }

        // 合并参数.
        AppLog appLog = instanceLogs.iterator().next();
        Map<String, Object> mergedRestartParams = MapUtils.merge(restartParams,
                appLog.getInput().orElseGet(HashMap::new), ConflictResolutionPolicy.OVERRIDE);

        RunContext runContext = new RunContext(new HashMap<>(), context);
        runContext.setRestartMode(cast(mergedRestartParams.getOrDefault(RESTART_MODE, OVERWRITE.getMode())));

        QueryChatRsp mostRecentRsp = chatList.get(0);
        runContext.setAppId(mostRecentRsp.getAppId());
        runContext.setChatId(mostRecentRsp.getChatId());
        runContext.setUserContext(mergedRestartParams);
        runContext.putAllToBusiness(mergedRestartParams);
        runContext.setQuestion(this.getQuestion(appLog.getLogData()));
        if (chatList.size() == 2) {
            runContext.setAtChatId(chatList.get(1).getChatId());
        }
        if (runContext.isOverWriteMode()) {
            instance.overWrite();
        }
        boolean isDebug =
                StringUtils.equals(AppState.INACTIVE.getName(), this.getState(mostRecentRsp.getAttributes()));
        if (isDebug) {
            this.debug(runContext, session);
        } else {
            this.run(runContext, session);
        }
        onFinished.accept(runContext);
    }

    private String getState(String attributes) {
        return ObjectUtils.cast(JsonUtils.parseObject(attributes).get(AippConst.ATTR_CHAT_STATE_KEY));
    }

    private String getQuestion(AippInstLog instLog) {
        return JSON.parseObject(instLog.getLogData()).getString("msg");
    }

    /**
     * 获取 icon.
     *
     * @return {@link String} icon路径.
     */
    public String getIcon() {
        return cast(this.attributes.get("icon"));
    }

    /**
     * 获取描述.
     *
     * @return {@link String} 描述信息.
     */
    public String getDescription() {
        return cast(this.attributes.get("description"));
    }

    /**
     * 设置图标.
     *
     * @param icon 图标.
     */
    public void setIcon(String icon) {
        this.attributes.put("icon", icon);
    }

    /**
     * 设置描述.
     *
     * @param description 描述信息.
     */
    public void setDescription(String description) {
        this.attributes.put("description", description);
    }

    /**
     * 获取开场白.
     *
     * @return {@link String} 开场白.
     */
    public String getGreeting() {
        return cast(this.attributes.getOrDefault("greeting", StringUtils.EMPTY));
    }

    /**
     * 获取分类.
     *
     * @return {@link String} 分类信息.
     */
    public String getClassification() {
        return cast(this.attributes.get("app_type"));
    }

    /**
     * 是否是App.
     *
     * @return true/false.
     */
    public boolean isApp() {
        return StringUtils.equals(APP.code(), this.data.getType());
    }

    /**
     * 是否被修改过.
     *
     * @return true/false.
     */
    public boolean isUpdated() {
        return ObjectUtils.cast(this.attributes.getOrDefault(AippConst.ATTR_APP_IS_UPDATE, true));
    }

    /**
     * 当新建appVersion时调用.
     *
     * @param dto 创建时的数据集合.
     * @param version 版本号.
     * @param type 应用类型.
     * @param context 操作人上下文信息.
     */
    public void cloneVersion(AppBuilderAppCreateDto dto, String version, String type, OperationContext context) {
        String newAppId = Entities.generateId();

        // 画布数据.
        AppBuilderFlowGraph graph = this.getFlowGraph();
        graph.setModelInfo(this.getFirstModelInfo(context));
        graph.setKnowledgeInfo(this.getFirstKnowledgeInfo(context));
        graph.clone(context);
        this.data.setFlowGraphId(graph.getId());

        // 配置.
        AppBuilderConfig appBuilderConfig = this.getConfig();
        appBuilderConfig.clone(this.getFormProperties(), context);
        appBuilderConfig.setAppId(newAppId);
        this.data.setConfigId(appBuilderConfig.getId());

        LocalDateTime now = LocalDateTime.now();
        this.data.setId(newAppId);
        this.data.setType(type);
        this.data.setTenantId(context.getTenantId());
        this.data.setCreateBy(context.getOperator());
        this.data.setCreateAt(now);
        this.data.setUpdateBy(context.getOperator());
        this.data.setUpdateAt(now);
        this.data.setAppId(newAppId);
        this.data.setIsActive(false);
        this.data.setStatus(AppStatus.DRAFT.getName());
        this.data.setVersion(version);
        this.getFormProperties().forEach(p -> p.setAppId(newAppId));

        if (Objects.nonNull(dto)) {
            this.attributes.clear();
            this.attributes.put("description", dto.getDescription());
            this.attributes.put("icon", dto.getIcon());
            this.attributes.put("greeting", dto.getGreeting());
            this.attributes.put("app_type", dto.getAppType());
            if (StringUtils.isNotBlank(dto.getStoreId())) {
                this.attributes.put("store_id", dto.getStoreId());
                this.data.setUniqueName(dto.getStoreId());
            }
            this.data.setName(dto.getName());
            this.data.setType(dto.getType());
            this.data.setAppCategory(dto.getAppCategory());
            this.data.setAppBuiltType(dto.getAppBuiltType());
            this.data.setAppType(dto.getAppType());
        }

        AippCreateDto aippCreateDto = this.preview(version, this.converterFactory.convert(this, AippDto.class),
                context);
        this.data.setAppSuiteId(aippCreateDto.getAippId());
    }

    private KnowledgeDto getFirstKnowledgeInfo(OperationContext context) {
        return this.knowledgeCenterService.getSupportKnowledges(context.getOperator()).get(0);
    }

    private String[] getFirstModelInfo(OperationContext context) {
        ModelListDto modelList = this.aippModelCenter.fetchModelList(AippConst.CHAT_MODEL_TYPE, null, context);
        if (modelList != null && modelList.getModels() != null && !modelList.getModels().isEmpty()) {
            ModelAccessInfo firstModel = modelList.getModels().get(0);
            return new String[] {firstModel.getServiceName(), firstModel.getTag()};
        } else {
            return new String[] {StringUtils.EMPTY, StringUtils.EMPTY};
        }
    }

    /**
     * 将当前应用版本发布为模板.
     *
     * @param createDto 模板创建参数.
     * @param context 操作人上下文信息.
     * @return {@link TemplateInfoDto} 对象.
     */
    public TemplateInfoDto publishTemplate(TemplateAppCreateDto createDto, OperationContext context) {
        String newAppID = Entities.generateId();

        AppBuilderFlowGraph graph = this.getFlowGraph();
        graph.setId(Entities.generateId());
        this.data.setFlowGraphId(graph.getId());

        // 配置.
        AppBuilderConfig appBuilderConfig = this.getConfig();
        appBuilderConfig.clone(this.getFormProperties(), context);
        appBuilderConfig.setAppId(newAppID);
        this.data.setConfigId(appBuilderConfig.getId());

        this.data.setId(newAppID);
        this.data.setAppId(newAppID);

        // 只保留模板相关的属性.
        this.attributes.keySet().retainAll(TEMPLATE_DEFAULT_ATTRIBUTE_KEYS);

        // 创建参数设置.
        if (createDto != null) {
            this.data.setName(createDto.getName());
            this.data.setAppType(createDto.getAppType());
            this.setDescription(createDto.getDescription());
            String icon = this.getIcon();
            if (this.isLegalIcon(createDto, icon)) {
                try {
                    String copiedIcon = this.uploadedFileManageService.copyIconFiles(icon, this.getData().getId(),
                            context.getAccount());
                    this.setIcon(copiedIcon);
                } catch (IOException e) {
                    LOGGER.warn("Failed to create a copy of icon when publish template.", e);
                    this.setIcon(StringUtils.EMPTY);
                }
            }
        }

        LocalDateTime now = LocalDateTime.now();
        this.data.setCreateBy(context.getOperator());
        this.data.setCreateAt(now);
        this.data.setUpdateBy(context.getOperator());
        this.data.setUpdateAt(now);

        graph.setCreateBy(context.getOperator());
        graph.setCreateAt(now);
        graph.setUpdateBy(context.getOperator());
        graph.setUpdateAt(now);

        AppTemplate template = this.converterFactory.convert(this, AppTemplate.class);
        this.templateFactory.setRepositories(template);
        this.templateFactory.save(template);
        String icon = this.getIcon();
        if (StringUtils.isNotBlank(icon)) {
            this.uploadedFileManageService.updateRecord(this.data.getId(), AippFileUtils.getFileNameFromIcon(icon), 0);
        }
        return TemplateUtils.convertToTemplateDto(template);
    }

    private boolean isLegalIcon(TemplateAppCreateDto createDto, String icon) {
        return StringUtils.isNotBlank(icon) && StringUtils.equals(icon, createDto.getIcon());
    }

    /**
     * 升级应用.
     *
     * @param dto 创建时的数据集合.
     * @param appType 应用类型.
     * @param context 操作人上下文信息.
     */
    public void upgrade(AppBuilderAppCreateDto dto, String appType, OperationContext context) {
        // 构建新的版本号.
        String preVersion = this.data.getVersion();
        String[] parts = preVersion.split("\\.");
        parts[2] = String.valueOf(Integer.parseInt(parts[2]) + 1);
        String newVersion = StringUtils.format(VERSION_FORMAT, parts[0], parts[1], parts[2]);
        String nextVersion = newVersion.length() > VERSION_LENGTH ? preVersion : newVersion;

        this.cloneVersion(dto, nextVersion, appType, context);
        this.data.setState(AppState.INACTIVE.getName());
        this.data.setIsActive(false);
        this.data.setStatus(AppStatus.DRAFT.getName());
        this.attributes.put("latest_version", preVersion);
    }

    /**
     * 创建应用.
     */
    public void create() {
        this.data.setState(AppState.INACTIVE.getName());
    }

    /**
     * 导入应用数据。
     *
     * @param appDto 导入应用的基础信息。
     * @param appSuiteId app唯一标识。
     * @param contextRoot 请求上下文根
     * @param context 操作上下文。
     * @param exportMeta 应用导入导出元数据。
     */
    public void importData(AppExportDto appDto, String appSuiteId, String contextRoot, OperationContext context,
            Map<String, String> exportMeta) {
        // 检查导入应用配置是否合法
        if (!StringUtils.equals(appDto.getVersion(), exportMeta.get("version"))) {
            throw new AippException(AippErrCode.IMPORT_CONFIG_UNMATCHED_VERSION, exportMeta.get("version"),
                    appDto.getVersion());
        }
        AppImExportUtil.checkAppExportDto(appDto);

        this.data = this.converterFactory.convert(appDto.getApp(), AppBuilderAppPo.class);
        this.data.setAppSuiteId(appSuiteId);
        this.data.setState(AppState.IMPORTING.getName());

        // 设置应用名称
        String initAppName = appDto.getApp().getName();
        List<String> similarNames = this.appVersionRepository.selectWithSimilarName(initAppName);
        String newName = AppImExportUtil.generateNewAppName(similarNames, initAppName);
        this.data.setName(newName);

        this.attributes = AppImExportUtil.resetAppAttributes(JsonUtils.parseObject(this.data.getAttributes()));
        this.config = AppImExportUtil.convertToAppBuilderConfig(appDto.getConfig(), context);
        this.flowGraph = AppImExportUtil.convertToAppBuilderFlowGraph(appDto.getFlowGraph(), context);
        this.formProperties = AppImExportUtil.getFormProperties(this.config.getConfigProperties());

        // 对于有头像的应用数据，需要保存头像文件
        String iconPath = appDto.getIconPath(contextRoot, this.resourcePath, context);
        if (!StringUtils.isBlank(iconPath)) {
            this.setIcon(iconPath);
            this.uploadedFileManageService.addFileRecord(this.getData().getAppId(), context.getAccount(),
                    AippFileUtils.getFileNameFromIcon(iconPath), Entities.generateId());
        }
        this.cloneVersion(null, "1.0.0", appDto.getType(), context);
    }

    /**
     * 导出.
     *
     * @param context 操作人上下文信息.
     * @param exportMeta 导出元数据.
     * @return {@link AppExportDto} 导出的数据.
     */
    public AppExportDto export(OperationContext context, Map<String, String> exportMeta) {
        if (!StringUtils.equals(this.getData().getCreateBy(), context.getName())) {
            throw new AippException(AippErrCode.EXPORT_CONFIG_UNAUTHED);
        }
        // 校验流程编排合法性
        try {
            String flowDefinitionData =
                    this.aippFlowDefinitionService.getParsedGraphData(this.getFlowGraph().getAppearance(),
                            this.getData().getVersion());
            this.flowDefinitionService.validateDefinitionData(flowDefinitionData);
        } catch (Exception e) {
            LOGGER.error("app config export failed", e);
            throw new AippException(AippErrCode.EXPORT_INVALID_FLOW_EXCEPTION);
        }
        try {
            AppExportApp exportAppInfo = this.converterFactory.convert(this, AppExportApp.class);
            String icon = this.getIcon();
            doIfNotBlank(icon, exportAppInfo::setIcon);

            return AppExportDto.builder()
                    .version(exportMeta.get("version"))
                    .app(exportAppInfo)
                    .config(this.converterFactory.convert(this.getConfig(), AppExportConfig.class))
                    .flowGraph(this.converterFactory.convert(this.getFlowGraph(), AppExportFlowGraph.class))
                    .build();
        } catch (DataAccessException e) {
            LOGGER.error("app config export failed", e);
            throw new AippException(AippErrCode.EXPORT_CONFIG_DB_EXCEPTION);
        }
    }

    /**
     * 判断两个应用是否相同.
     *
     * @param appVersion 应用版本对象.
     * @return true/false.
     */
    public boolean isEqual(AppVersion appVersion) {
        return StringUtils.equals(this.getData().getAppId(), appVersion.getData().getAppId());
    }

    /**
     * put所有属性.
     *
     * @param attributes 属性集合.
     */
    public void putAttributes(Map<String, Object> attributes) {
        this.attributes.putAll(attributes);
        this.attributes.put(AippConst.ATTR_APP_IS_UPDATE, true);
    }

    /**
     * 配置配置.
     *
     * @param configDto 待更新数据.
     * @param properties 新的属性列表.
     * @param context 操作人上下文信息.
     */
    public void updateConfig(AppBuilderConfigDto configDto, List<AppBuilderConfigFormPropertyDto> properties,
            OperationContext context) {
        this.getConfig().updateByProperties(properties);
        this.getConfig().setUpdateBy(context.getOperator());
        this.getConfig().setUpdateAt(LocalDateTime.now());
        this.configRepository.updateOne(this.getConfig());
        AppBuilderForm form = this.getConfig().getForm();
        form.setUpdateBy(context.getOperator());
        form.setUpdateAt(LocalDateTime.now());
        form.setName(configDto.getForm().getName());
        form.setAppearance(configDto.getForm().getAppearance());
        this.formRepository.updateOne(form);
    }

    /**
     * 更新graph.
     *
     * @param properties 新的form属性列表.
     * @param context 操作人上下文信息.
     */
    public void updateGraph(List<AppBuilderConfigFormPropertyDto> properties, OperationContext context) {
        this.getFlowGraph().setUpdateBy(context.getOperator());
        this.getFlowGraph().setUpdateAt(LocalDateTime.now());
        this.getFlowGraph().updateByProperties(properties);
        this.flowGraphRepository.updateOne(this.getFlowGraph());
    }

    /**
     * 预览appVersion.
     *
     * @param baselineVersion 基线版本.
     * @param aippDto 数据.
     * @param context 操作人上下文信息.
     * @return {@link AippCreateDto} 对象.
     * @throws AippException 流程异常.
     */
    public AippCreateDto preview(String baselineVersion, AippDto aippDto, OperationContext context)
            throws AippException {
        List<AppTask> appTasks = this.getTasks(context);
        if (CollectionUtils.isNotEmpty(appTasks)) {
            AppTask task = appTasks.get(0);
            if (task.isPublished()) {
                return AippCreateDto.builder()
                        .aippId(task.getEntity().getAppSuiteId())
                        .version(task.getEntity().getVersion())
                        .build();
            }
        }
        FlowDefinitionResult definitionResult = this.appDefinitionService.getSameFlowDefinition(aippDto);
        if (definitionResult != null) {
            RangedResultSet<AppTask> resultSet = this.appTaskService.getTasks(AppTask.asQueryEntity(0, 1)
                    .latest()
                    .putQueryAttribute(AippConst.ATTR_FLOW_DEF_ID_KEY, definitionResult.getFlowDefinitionId())
                    .putQueryAttribute(AippConst.ATTR_FLOW_CONFIG_ID_KEY, definitionResult.getMetaId())
                    .build(), context);
            if (!resultSet.isEmpty()) {
                AppTask task = resultSet.getResults().get(0);
                return AippCreateDto.builder()
                        .aippId(task.getEntity().getAppSuiteId())
                        .version(task.getEntity().getVersion())
                        .build();
            }
        }
        // 过滤预览版本
        if (AippStringUtils.isPreview(baselineVersion)) {
            throw new AippParamException(context, AippErrCode.INPUT_PARAM_IS_INVALID, "version is preview");
        }

        // 创建预览版本
        Retryable<AippCreateDto, JobberException> retryable = new Retryable<>(
                () -> this.createPreviewAipp(baselineVersion, aippDto, context), RETRY_PREVIEW_TIMES);
        retryable.setObserveException(JobberException.class);
        retryable.setBreakCondition(e -> e.getCode() != ErrorCodes.FLOW_ALREADY_EXIST.getErrorCode());
        retryable.setExceptionConsumer((e, times) -> this.handleException(e, times, aippDto));
        return retryable.retry().orElseThrow(e -> this.handleException(context, e));
    }

    private void handleException(JobberException e, int times, AippDto aippDto) {
        LOGGER.warn("create preview aipp failed, times {} aippId {} version {}, error {}", times,
                this.getData().getAppSuiteId(), aippDto.getPreviewVersion(), e.getMessage());
    }

    private AippCreateDto createPreviewAipp(String baselineVersion, AippDto aippDto, OperationContext context) {
        String previewVersion = VersionUtils.buildPreviewVersion(baselineVersion);
        aippDto.setPreviewVersion(previewVersion);

        // 创建、发布流程定义
        FlowInfo flowInfo = this.flowsService.publishFlowsWithoutElsa(aippDto.getFlowId(),
                previewVersion,
                JsonUtils.toJsonString(aippDto.getFlowViewData()),
                context);

        // 预览时，aipp 的 version 用的是 flowInfo 的 version，是否合理待确认
        aippDto.setVersion(flowInfo.getVersion());
        List<AippNodeForms> aippNodeForms = FlowInfoUtil.buildAippNodeForms(flowInfo, this.getFormProperties());

        // 构建创建参数.
        AppTask createArgs = AppTask.asCreateEntity()
                .fetch(aippDto)
                .setBaseLineVersion(baselineVersion)
                .setAppSuiteId(this.getData().getAppSuiteId())
                .fetch(aippNodeForms)
                .setFlowConfigId(flowInfo.getFlowId())
                .setFlowDefinitionId(flowInfo.getFlowDefinitionId())
                .setAippType(PREVIEW.name())
                .setStatus(ACTIVE.getCode())
                .setPublishTime(LocalDateTime.now().toString())
                .build();
        LOGGER.debug("create aipp, task info {}", createArgs.getEntity().toString());
        AppTask appTask = this.appTaskService.createTask(createArgs, context);
        return AippCreateDto.builder().aippId(appTask.getEntity().getAppSuiteId()).version(previewVersion).build();
    }

    private AippException handleException(OperationContext context, JobberException exception) {
        LOGGER.error("Failed to preview aipp.[errorMsg={}]", exception.getMessage());
        switch (ErrorCodes.getErrorCodes(exception.getCode())) {
            case INVALID_FLOW_NODE_SIZE:
                return new AippException(context, AippErrCode.INVALID_FLOW_NODE_SIZE);
            case INVALID_START_NODE_EVENT_SIZE:
                return new AippException(context, AippErrCode.INVALID_START_NODE_EVENT_SIZE);
            case INVALID_EVENT_CONFIG:
            case INVALID_STATE_NODE_EVENT_SIZE:
                return new AippException(context, AippErrCode.INVALID_EVENT_CONFIG);
            default:
                return new AippException(context, AippErrCode.INVALID_FLOW_CONFIG);
        }
    }

    /**
     * 克隆应用版本
     *
     * @param cloneApp 需要克隆的应用版本的 {@link AppVersion}。
     */
    public void cloneVersion(AppVersion cloneApp) {
        List<AppBuilderFormProperty> resetFormProperties = cloneApp.getFormProperties();
        List<AppBuilderFormProperty> currentFormProperties = this.getFormProperties();
        Map<String, AppBuilderFormProperty> currentPropMap = currentFormProperties.stream()
                .collect(Collectors.toMap(AppBuilderFormProperty::getName, Function.identity()));
        resetFormProperties.forEach(resetProp -> {
            AppBuilderFormProperty currentProp = currentPropMap.get(resetProp.getName());
            if (currentProp != null) {
                currentProp.setDefaultValue(resetProp.getDefaultValue());
            }
        });
        this.formPropertyRepository.updateMany(currentFormProperties);
        AppBuilderFlowGraph resetGraph = cloneApp.getFlowGraph();
        AppBuilderFlowGraph currentGraph = this.getFlowGraph();
        String currentGraphId = cloneApp.getFlowGraph().getId();
        resetGraph.setId(currentGraphId);
        resetGraph.resetGraphId();

        currentGraph.setAppearance(resetGraph.getAppearance());
        this.flowGraphRepository.updateOne(currentGraph);
    }
}
