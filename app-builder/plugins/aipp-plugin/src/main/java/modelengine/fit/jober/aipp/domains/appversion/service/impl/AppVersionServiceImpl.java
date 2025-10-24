/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.appversion.service.impl;

import static modelengine.fit.jober.aipp.service.impl.UploadedFileMangeServiceImpl.IRREMOVABLE;
import static modelengine.fit.jober.aipp.service.impl.UploadedFileMangeServiceImpl.REMOVABLE;

import com.alibaba.fastjson.JSONObject;

import io.opentelemetry.api.trace.Span;
import modelengine.fit.jade.aipp.domain.division.service.DomainDivisionService;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.enums.DirectionEnum;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.condition.AppQueryCondition;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.domain.AppTemplate;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.AppVersionDecorator;
import modelengine.fit.jober.aipp.domains.appversion.AppVersionFactory;
import modelengine.fit.jober.aipp.domains.appversion.repository.AppVersionRepository;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.domains.business.RunContext;
import modelengine.fit.jober.aipp.domains.chat.repository.AppChatRepository;
import modelengine.fit.jober.aipp.domains.log.AppLog;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.AppBuilderFlowGraphDto;
import modelengine.fit.jober.aipp.dto.AppBuilderSaveConfigDto;
import modelengine.fit.jober.aipp.dto.chat.CreateAppChatRequest;
import modelengine.fit.jober.aipp.dto.chat.QueryChatRsp;
import modelengine.fit.jober.aipp.entity.ChatSession;
import modelengine.fit.jober.aipp.enums.AippSortKeyEnum;
import modelengine.fit.jober.aipp.enums.AppState;
import modelengine.fit.jober.aipp.enums.AppTypeEnum;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigPropertyRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.service.UploadedFileManageService;
import modelengine.fit.jober.aipp.util.AippFileUtils;
import modelengine.fit.jober.aipp.util.TemplateUtils;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.flowable.Choir;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.common.locale.LocaleUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * {@link AppVersionService} 服务.
 *
 * @author 张越
 * @since 2025-01-14
 */
@Component
public class AppVersionServiceImpl implements AppVersionService {
    private static final Logger LOGGER = Logger.get(AppVersionServiceImpl.class);
    private static final String APP_NAME_FORMAT = "^[\\u4E00-\\u9FA5A-Za-z0-9][\\u4E00-\\u9FA5A-Za-z0-9-_]*$";
    private static final String DEFAULT_APP_VERSION = "1.0.0";

    private final AppVersionRepository repository;
    private final AppChatRepository appChatRepository;
    private final AppTaskInstanceService appTaskInstanceService;
    private final UploadedFileManageService uploadedFileManageService;
    private final AppBuilderConfigRepository configRepository;
    private final AppBuilderFlowGraphRepository flowGraphRepository;
    private final AppBuilderFormPropertyRepository formPropertyRepository;
    private final AppBuilderConfigPropertyRepository configPropertyRepository;
    private final AppTaskService appTaskService;
    private final AppVersionFactory appVersionFactory;
    private final DomainDivisionService domainDivisionService;
    private final boolean isEnableDomainDivision;
    private final int nameLengthMaximum;

    public AppVersionServiceImpl(AppVersionRepository repository, AppChatRepository appChatRepository,
            AppTaskInstanceService appTaskInstanceService, UploadedFileManageService uploadedFileManageService,
            AppBuilderConfigRepository configRepository, AppBuilderFlowGraphRepository flowGraphRepository,
            AppBuilderFormPropertyRepository formPropertyRepository,
            AppBuilderConfigPropertyRepository configPropertyRepository, AppTaskService appTaskService,
            AppVersionFactory appVersionFactory, DomainDivisionService domainDivisionService,
            @Value("${validation.task.name.length.maximum:64}") int nameLengthMaximum,
            @Value("${domain-division.isEnable}") boolean isEnableDomainDivision) {
        this.repository = repository;
        this.appChatRepository = appChatRepository;
        this.appTaskInstanceService = appTaskInstanceService;
        this.uploadedFileManageService = uploadedFileManageService;
        this.configRepository = configRepository;
        this.flowGraphRepository = flowGraphRepository;
        this.formPropertyRepository = formPropertyRepository;
        this.configPropertyRepository = configPropertyRepository;
        this.appTaskService = appTaskService;
        this.appVersionFactory = appVersionFactory;
        this.nameLengthMaximum = nameLengthMaximum;
        this.domainDivisionService = domainDivisionService;
        this.isEnableDomainDivision = isEnableDomainDivision;
    }

    @Override
    public Optional<AppVersion> getByAppId(String appId) {
        return this.repository.selectById(appId);
    }

    @Override
    public Optional<AppVersion> getByPath(String path) {
        return this.repository.selectByPath(path);
    }

    @Override
    public AppVersion retrieval(String appId) {
        return this.getByAppId(appId).orElseThrow(() -> {
            LOGGER.error("The app version is not found. [version={}]", appId);
            return new AippException(AippErrCode.APP_NOT_FOUND);
        });
    }

    @Override
    public List<AppVersion> getByAppSuiteId(String appSuiteId) {
        return this.repository.selectByAppSuiteId(appSuiteId);
    }

    @Override
    @Transactional
    public Choir<Object> run(CreateAppChatRequest request, OperationContext context) {
        AppVersion appVersion = this.retrieval(request.getAppId());
        RunContext runContext = RunContext.from(request, context);
        appVersion.validate(runContext, false);
        runContext.setIsGuest(request.getContext().isGuest());
        runContext.setAppCreateBy(appVersion.getData().getCreateBy());
        Locale locale = LocaleUtil.getLocale();
        return Choir.create(emitter -> {
            ChatSession<Object> session = new ChatSession<>(emitter, request.getAppId(), false, locale);
            AppVersionDecorator.decorate(appVersion, this.appChatRepository).run(runContext, session);
        });
    }

    @Override
    @Transactional
    public Choir<Object> debug(CreateAppChatRequest request, OperationContext context) {
        AppVersion appVersion = this.retrieval(request.getAppId());
        appVersion.updateFlows(context);
        RunContext runContext = RunContext.from(request, context);
        appVersion.validate(runContext, true);
        runContext.setIsGuest(request.getContext().isGuest());
        runContext.setAppCreateBy(appVersion.getData().getCreateBy());
        Locale locale = LocaleUtil.getLocale();
        return Choir.create(emitter -> {
            ChatSession<Object> session = new ChatSession<>(emitter, request.getAppId(), true, locale);
            AppVersionDecorator.decorate(appVersion, this.appChatRepository).debug(runContext, session);
        });
    }

    @Override
    @Transactional
    public Choir<Object> restart(String instanceId, Map<String, Object> restartParams, OperationContext context) {
        String taskId = this.appTaskInstanceService.getTaskId(instanceId);
        AppTask task = this.appTaskService.getTaskById(taskId, context)
                .orElseThrow(() -> {
                    LOGGER.error("The task is not found. [taskId={}]", taskId);
                    return new AippException(AippErrCode.TASK_NOT_FOUND);});

        // 这边instance的获取暂时没有放在 Choir.create 里：Choir 会把异常吞掉
        AppTaskInstance instance = this.appTaskInstanceService.getInstanceById(instanceId, context)
                .orElseThrow(() -> new AippException(AippErrCode.CHAT_NOT_FOUND_BY_INSTANCE_ID, instanceId));
        String parentInstanceId = instance.getParentInstanceId();
        List<QueryChatRsp> chatList = instance.getChats();
        if (CollectionUtils.isEmpty(chatList)) {
            LOGGER.error("Chat list are empty. [parentInstanceId={}]", parentInstanceId);
            throw new AippParamException(AippErrCode.RE_CHAT_FAILED);
        }
        List<AppLog> instanceLogs = instance.getLogs();
        if (CollectionUtils.isEmpty(instanceLogs)) {
            LOGGER.error("Instance logs are empty. [parentInstanceId={}]", parentInstanceId);
            throw new AippParamException(AippErrCode.AIPP_INSTANCE_LOG_IS_NULL);
        }
        String appId = task.getEntity().getAppId();
        Locale locale = LocaleUtil.getLocale();
        return Choir.create(emitter -> {
            ChatSession<Object> session = new ChatSession<>(emitter, appId, false, locale);
            AppVersion appVersion = this.retrieval(appId);
            AppVersionDecorator.decorate(appVersion, this.appChatRepository)
                    .restart(instance, restartParams, session, context);
        });
    }

    @Override
    @Transactional
    public AppVersion create(String templateId, AppBuilderAppCreateDto dto, OperationContext context) {
        this.validateAppName(dto.getName(), context);
        if (dto.getDescription() != null) {
            this.validateAppDescription(dto, context);
        }
        this.validateAppCategory(dto, context);
        if (this.isNameExists(dto.getName(), context)) {
            LOGGER.error("Create aipp failed, [name={}, tenantId={}]", dto.getName(), context.getTenantId());
            throw new AippException(context, AippErrCode.AIPP_NAME_IS_DUPLICATE);
        }
        AppVersion template = this.retrieval(templateId);
        template.create();
        template.cloneVersion(dto, DEFAULT_APP_VERSION, AppTypeEnum.APP.name(), context);
        if (this.isEnableDomainDivision) {
            template.getData().setUserGroupId(this.domainDivisionService.getUserGroupId());
        }
        this.save(template);
        return template;
    }

    private void validateAppDescription(AppBuilderAppCreateDto dto, OperationContext context) {
        if (dto.getDescription().length() > 300) {
            LOGGER.error("Create aipp failed, [name={}, tenantId={}], app description is larger than 300.",
                    dto.getName(), context.getTenantId());
            throw new AippException(context, AippErrCode.APP_DESCRIPTION_LENGTH_OUT_OF_BOUNDS);
        }
    }

    private void validateAppCategory(AppBuilderAppCreateDto dto, OperationContext context) {
        if (dto.getAppCategory() == null) {
            LOGGER.error("Create aipp failed, [name={}, tenantId={}], app category is null.",
                    dto.getName(), context.getTenantId());
            throw new AippException(context, AippErrCode.APP_CATEGORY_IS_NULL);
        }
    }

    @Override
    public AppVersion createByTemplate(AppTemplate template, OperationContext context) {
        this.validateAppName(template.getName(), context);
        if (this.isNameExists(template.getName(), context)) {
            LOGGER.error("Create aipp by template failed, [name={}, tenantId={}]", template.getName(),
                    context.getTenantId());
            throw new AippException(context, AippErrCode.AIPP_NAME_IS_DUPLICATE);
        }
        AppVersion appVersion = this.appVersionFactory.create(new AppBuilderAppPo(), this.repository);
        appVersion.getData().setConfigId(template.getConfigId());
        appVersion.getData().setFlowGraphId(template.getFlowGraphId());
        appVersion.getData().setId(template.getId());
        appVersion.cloneVersion(TemplateUtils.toAppCreateDTO(template), DEFAULT_APP_VERSION, AppTypeEnum.APP.name(),
                context);
        appVersion.getData().setState(AppState.INACTIVE.getName());
        if (this.isEnableDomainDivision) {
            appVersion.getData().setUserGroupId(this.domainDivisionService.getUserGroupId());
        }
        this.save(appVersion);
        return appVersion;
    }

    @Override
    @Transactional
    public AppVersion upgrade(String appId, AppBuilderAppCreateDto dto, OperationContext context) {
        AppVersion template = this.retrieval(appId);
        template.upgrade(dto, AppTypeEnum.APP.name(), context);
        this.save(template);
        return template;
    }

    @Override
    public void validateAppName(String name, OperationContext context) throws AippException {
        String trimName = StringUtils.trim(name);
        if (StringUtils.isEmpty(trimName)) {
            LOGGER.error("Create aipp failed: name can not be empty.");
            throw new AippParamException(context, AippErrCode.APP_NAME_IS_INVALID);
        }
        if (!name.matches(APP_NAME_FORMAT)) {
            LOGGER.error("Create aipp failed: the name format is incorrect. [name={}]", name);
            throw new AippParamException(context, AippErrCode.APP_NAME_IS_INVALID);
        }
        if (name.length() > this.nameLengthMaximum) {
            LOGGER.error("Create aipp failed: the length of task name is out of bounds. [name={}]", name);
            throw new AippParamException(context, AippErrCode.AIPP_NAME_LENGTH_OUT_OF_BOUNDS);
        }
    }

    @Override
    public Optional<AppVersion> getLatestCreatedByAppSuiteId(String appSuiteId) {
        List<AppVersion> appVersionList = this.repository.selectByCondition(AppQueryCondition.builder()
                .appSuiteId(appSuiteId)
                .orderBy(AippSortKeyEnum.CREATE_AT.name())
                .sort(DirectionEnum.DESCEND.name())
                .offset(0L)
                .limit(1)
                .build());
        return appVersionList.stream().findFirst();
    }

    @Override
    public Optional<AppVersion> getFirstCreatedByAppSuiteId(String appSuiteId) {
        List<AppVersion> appVersionList = this.repository.selectByCondition(AppQueryCondition.builder()
                .appSuiteId(appSuiteId)
                .orderBy(AippSortKeyEnum.CREATE_AT.name())
                .sort(DirectionEnum.ASCEND.name())
                .offset(0L)
                .limit(1)
                .build());
        return appVersionList.stream().findFirst();
    }

    @Override
    public RangedResultSet<AppVersion> pageListByTenantId(AppQueryCondition cond, String tenantId, long offset,
            int limit) {
        List<AppVersion> versions = this.repository.pageListByTenantId(cond, tenantId, offset, limit);
        long total = this.repository.countByTenantId(cond, tenantId);
        return RangedResultSet.create(versions, offset, limit, total);
    }

    @Override
    public long countByTenantId(AppQueryCondition cond, String tenantId) {
        return this.repository.countByTenantId(cond, tenantId);
    }

    @Override
    public AppVersion update(String appId, AppBuilderAppDto appDto, OperationContext context) {
        AppVersion appVersion = this.retrieval(appId);
        if (appVersion.isPublished()) {
            throw new AippException(AippErrCode.APP_HAS_ALREADY);
        }
        List<AppTask> tasks = appVersion.getPublishedTasks(context);
        if (CollectionUtils.isNotEmpty(tasks) && !StringUtils.equals(tasks.get(0).getEntity().getName(),
                appDto.getName())) {
            throw new AippException(AippErrCode.APP_NAME_HAS_PUBLISHED);
        }
        this.validateAppName(appDto.getName(), context);
        appVersion.getData().setName(appDto.getName());
        appVersion.getData().setUpdateBy(context.getOperator());
        appVersion.getData().setUpdateAt(LocalDateTime.now());
        appVersion.getData().setType(appDto.getType());
        appVersion.getData().setAppType(appDto.getAppType());

        // 避免前端更新将app表的attributes覆盖了
        String oldIcon = appVersion.getIcon();
        appVersion.putAttributes(appDto.getAttributes());

        // 更新状态.
        if (StringUtils.equals(appVersion.getData().getState(), AppState.IMPORTING.getName())
                && StringUtils.equals(appDto.getState(), AppState.INACTIVE.getName())) {
            appVersion.getData().setState(AppState.INACTIVE.getName());
        }

        appVersion.getData().setVersion(appDto.getVersion());
        this.repository.update(appVersion);

        String newIcon = appVersion.getIcon();
        if (StringUtils.isNotBlank(newIcon) && !StringUtils.equals(oldIcon, newIcon)) {
            this.uploadedFileManageService.changeRemovable(AippFileUtils.getFileNameFromIcon(oldIcon), REMOVABLE);
            this.uploadedFileManageService.changeRemovable(AippFileUtils.getFileNameFromIcon(newIcon), IRREMOVABLE);
        }
        return appVersion;
    }

    @Override
    public AppVersion update(String appId, AppBuilderFlowGraphDto graphDto, OperationContext context) {
        AppVersion appVersion = this.retrieval(appId);
        if (appVersion.isPublished()) {
            throw new AippException(AippErrCode.APP_HAS_ALREADY);
        }
        this.updateGraph(appVersion, graphDto, context);
        return appVersion;
    }

    @Override
    public void updateGraph(AppVersion appVersion, AppBuilderFlowGraphDto graphDto, OperationContext context) {
        Span.current().setAttribute("name", appVersion.getData().getName());
        LocalDateTime operateTime = LocalDateTime.now();
        appVersion.getFlowGraph().setUpdateAt(operateTime);
        appVersion.getFlowGraph().setUpdateBy(context.getOperator());
        appVersion.getFlowGraph().setName(graphDto.getName());
        appVersion.getFlowGraph().setAppearance(JSONObject.toJSONString(graphDto.getAppearance()));
        this.flowGraphRepository.updateOne(appVersion.getFlowGraph());

        appVersion.getConfig().updateByAppearance(appVersion.getFlowGraph().getAppearance());
        appVersion.getConfig().setUpdateAt(operateTime);
        appVersion.getConfig().setUpdateBy(context.getOperator());
        this.configRepository.updateOne(appVersion.getConfig());

        appVersion.getData().setUpdateAt(operateTime);
        appVersion.getData().setUpdateBy(context.getOperator());
        appVersion.putAttributes(new HashMap<>());
        this.repository.update(appVersion);
    }

    @Override
    @Transactional
    public AppVersion update(String appId, AppBuilderSaveConfigDto appBuilderSaveConfigDto, OperationContext context) {
        AppVersion appVersion = this.retrieval(appId);
        List<AppBuilderFormProperty> formProperties = appBuilderSaveConfigDto.getInput().stream()
                .map(formPropertyDto -> AppBuilderFormProperty.builder()
                        .id(formPropertyDto.getId())
                        .formId("null")
                        .name(formPropertyDto.getName())
                        .dataType(formPropertyDto.getDataType())
                        .defaultValue(formPropertyDto.getDefaultValue())
                        .from(formPropertyDto.getFrom())
                        .group(formPropertyDto.getGroup())
                        .description(formPropertyDto.getDescription())
                        .build())
                .toList();
        appVersion.putAttributes(new HashMap<>());
        this.repository.update(appVersion);
        this.formPropertyRepository.updateMany(formProperties);
        appVersion.getFlowGraph().setAppearance(appBuilderSaveConfigDto.getGraph());
        this.flowGraphRepository.updateOne(appVersion.getFlowGraph());
        return appVersion;
    }

    @Override
    public void update(AppVersion appVersion) {
        Optional.ofNullable(appVersion).ifPresent(this.repository::update);
    }

    @Override
    public void deleteByIds(List<String> appIds) {
        if (CollectionUtils.isEmpty(appIds)) {
            return;
        }
        this.repository.deleteByIds(appIds);
    }

    @Override
    public boolean isNameExists(String appName, OperationContext context) {
        AppQueryCondition queryCondition = AppQueryCondition.builder()
                .tenantId(context.getTenantId())
                .name(appName)
                .build();
        return !this.repository.selectByCondition(queryCondition).isEmpty();
    }

    @Override
    public void save(AppVersion appVersion) {
        if (appVersion == null) {
            return;
        }
        this.repository.save(appVersion);
        String icon = appVersion.getIcon();
        if (StringUtils.isNotBlank(icon)) {
            this.uploadedFileManageService.updateRecord(appVersion.getData().getId(),
                    AippFileUtils.getFileNameFromIcon(icon),
                    IRREMOVABLE);
        }
        this.flowGraphRepository.insertOne(appVersion.getFlowGraph());
        this.configRepository.insertOne(appVersion.getConfig());
        this.configPropertyRepository.insertMore(appVersion.getConfig().getConfigProperties());
        List<AppBuilderFormProperty> formProperties = appVersion.getFormProperties();
        formProperties.forEach(property -> property.setAppId(appVersion.getData().getId()));
        this.formPropertyRepository.insertMore(formProperties);
    }
}
