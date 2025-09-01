/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static modelengine.fit.jober.aipp.common.exception.AippErrCode.INVALID_PATH_ERROR;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.OBTAIN_APP_ORCHESTRATION_INFO_FAILED;
import static modelengine.fit.jober.aipp.common.exception.AippErrCode.QUERY_PUBLICATION_HISTORY_FAILED;

import io.opentelemetry.api.trace.Span;
import lombok.RequiredArgsConstructor;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.condition.AppQueryCondition;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.converters.ConverterFactory;
import modelengine.fit.jober.aipp.domain.AppTemplate;
import modelengine.fit.jober.aipp.domains.app.App;
import modelengine.fit.jober.aipp.domains.app.AppFactory;
import modelengine.fit.jober.aipp.domains.app.service.AppDomainService;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.PublishContext;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.task.AppTaskUtils;
import modelengine.fit.jober.aipp.domains.task.service.AppTaskService;
import modelengine.fit.jober.aipp.dto.AippCreateDto;
import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppMetadataDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto;
import modelengine.fit.jober.aipp.dto.AppBuilderFlowGraphDto;
import modelengine.fit.jober.aipp.dto.AppBuilderSaveConfigDto;
import modelengine.fit.jober.aipp.dto.PublishedAppResDto;
import modelengine.fit.jober.aipp.dto.check.AppCheckDto;
import modelengine.fit.jober.aipp.dto.check.CheckResult;
import modelengine.fit.jober.aipp.dto.template.TemplateAppCreateDto;
import modelengine.fit.jober.aipp.dto.template.TemplateInfoDto;
import modelengine.fit.jober.aipp.factory.AppTemplateFactory;
import modelengine.fit.jober.aipp.factory.CheckerFactory;
import modelengine.fit.jober.aipp.genericable.entity.AippCreate;
import modelengine.fit.jober.aipp.repository.AppBuilderAppRepository;
import modelengine.fit.jober.aipp.service.AppBuilderAppService;
import modelengine.fit.jober.aipp.service.Checker;
import modelengine.fit.jober.aipp.service.UploadedFileManageService;
import modelengine.fit.jober.aipp.util.ConvertUtils;
import modelengine.fit.jober.aipp.util.RandomPathUtils;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.knowledge.KnowledgeCenterService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用开发接口实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
@Component
@RequiredArgsConstructor
public class AppBuilderAppServiceImpl
        implements AppBuilderAppService, modelengine.fit.jober.aipp.genericable.AppBuilderAppService {
    private static final Logger log = Logger.get(AppBuilderAppServiceImpl.class);
    private static final int PATH_LENGTH = 16;
    private static final String APP_BUILDER_DEFAULT_KNOWLEDGE_SET = "#app_builder_default_knowledge_set#";

    private final AppTemplateFactory templateFactory;
    private final UploadedFileManageService uploadedFileManageService;
    private final AppTaskService appTaskService;
    private final AppVersionService appVersionService;
    private final AppDomainService appDomainService;
    private final AppFactory appDomainFactory;
    private final ConverterFactory converterFactory;
    private final KnowledgeCenterService knowledgeCenterService;
    private final AppBuilderAppRepository appBuilderAppRepository;

    @Override
    @Fitable(id = "default")
    public AppBuilderAppDto query(String appId, OperationContext context) {
        AppBuilderAppDto appBuilderAppDto = this.appVersionService.getByAppId(appId)
                .map(v -> this.converterFactory.convert(v, AppBuilderAppDto.class))
                .orElseThrow(() -> new AippException(AippErrCode.APP_NOT_FOUND));
        AppVersion firstAppVersion = this.appVersionService.getFirstCreatedByAppSuiteId(appBuilderAppDto.getAippId())
                .orElseThrow(() -> new AippException(AippErrCode.APP_NOT_FOUND));
        appBuilderAppDto.setBaselineCreateAt(firstAppVersion.getData().getCreateAt());
        return appBuilderAppDto;
    }

    @Override
    @Fitable(id = "default")
    public AppBuilderAppDto queryByPath(String path) {
        if (!RandomPathUtils.validatePath(path, PATH_LENGTH)) {
            log.error("Invalid path format path: {}.", path);
            throw new AippException(INVALID_PATH_ERROR);
        }
        return this.appVersionService.getByPath(path)
                .map(v -> this.converterFactory.convert(v, AppBuilderAppDto.class))
                .orElseThrow(() -> new AippException(AippErrCode.APP_NOT_FOUND));
    }

    @Override
    @Fitable(id = "default")
    @Transactional
    public Rsp<AippCreateDto> publish(AppBuilderAppDto appDto, OperationContext contextOf) {
        AppVersion appVersion = this.appVersionService.getByAppId(appDto.getId()).orElseThrow(() -> {
            log.error("The app version is not found. [version={}]", appDto.getId());
            return new AippException(AippErrCode.APP_NOT_FOUND);
        });
        if (appVersion.isPublished()) {
            throw new AippException(AippErrCode.APP_HAS_PUBLISHED);
        }
        appVersion.publish(new PublishContext(appDto, contextOf));
        return Rsp.ok(AippCreateDto.builder()
                .aippId(appVersion.getData().getAppSuiteId())
                .version(appVersion.getData().getVersion())
                .toolUniqueName(appVersion.getData().getUniqueName())
                .build());
    }

    @Override
    @Fitable(id = "default")
    public AippCreate debug(AppBuilderAppDto appDto, OperationContext contextOf) {
        AippDto aippDto = ConvertUtils.convertToAippDtoFromAppBuilderAppDto(appDto);
        // Rsp 得统一整改下
        return ConvertUtils.toAippCreate(this.appVersionService.retrieval(aippDto.getAppId())
                .preview(appDto.getVersion(), aippDto, contextOf));
    }

    @Override
    @Fitable(id = "default")
    public void updateFlow(String appId, OperationContext contextOf) {
        AppVersion appVersion = this.appVersionService.retrieval(appId);
        if (appVersion.isUpdated()) {
            AippDto aippDto = ConvertUtils.convertToAippDtoFromAppBuilderAppDto(
                    this.converterFactory.convert(appVersion, AppBuilderAppDto.class));
            appVersion.preview(appVersion.getData().getVersion(), aippDto, contextOf);
            appVersion.getAttributes().put(AippConst.ATTR_APP_IS_UPDATE, false);
            this.appVersionService.update(appVersion);
        }
    }

    @Override
    public AppBuilderAppDto queryLatestOrchestration(String appId, OperationContext context) {
        AppVersion appVersion = this.appVersionService.retrieval(appId);
        App app = this.appDomainFactory.create(appVersion.getData().getAppSuiteId());
        AppVersion latestVersion = app.getLatestVersion()
                .orElseThrow(() -> new AippException(OBTAIN_APP_ORCHESTRATION_INFO_FAILED));
        if (latestVersion.isPublished()) {
            AppVersion upgraded = this.appVersionService.upgrade(latestVersion.getData().getId(),
                    this.converterFactory.convert(latestVersion, AppBuilderAppCreateDto.class), context);
            return this.converterFactory.convert(upgraded, AppBuilderAppDto.class);
        }
        return this.query(latestVersion.getData().getId(), context);
    }

    @Override
    public AippCreate queryLatestPublished(String appId, OperationContext context) {
        AppVersion appVersion = this.appVersionService.retrieval(appId);
        AppTask appTask = appVersion.getLatestPublishedTask(context);
        String latestAppId = appTask.getEntity().getAppId();
        return AippCreate.builder()
                .version(appTask.getEntity().getVersion())
                .aippId(appVersion.getData().getAppSuiteId())
                .appId(latestAppId)
                .build();
    }

    @Override
    @Fitable(id = "default")
    public Optional<AppBuilderConfigFormPropertyDto> getPropertyByName(String appId, String name) {
        AppVersion appVersion = this.appVersionService.retrieval(appId);
        AppBuilderAppDto dto = this.converterFactory.convert(appVersion, AppBuilderAppDto.class);
        List<AppBuilderConfigFormPropertyDto> formPropertyDtoList = dto.getConfigFormProperties();
        return formPropertyDtoList.stream().filter(prop -> StringUtils.equals(prop.getName(), name)).findFirst();
    }

    @Override
    @Fitable(id = "default")
    public Rsp<RangedResultSet<AppBuilderAppMetadataDto>> list(AppQueryCondition cond, OperationContext context,
            long offset, int limit) {
        if (cond == null) {
            cond = new AppQueryCondition();
        }
        cond.setCreateBy(context.getOperator());
        RangedResultSet<AppVersion> result =
                this.appVersionService.pageListByTenantId(cond, context.getTenantId(), offset, limit);
        List<AppBuilderAppMetadataDto> metaDtoList = result.getResults()
                .stream()
                .map(v -> this.converterFactory.convert(v, AppBuilderAppMetadataDto.class))
                .toList();
        return Rsp.ok(RangedResultSet.create(metaDtoList, offset, limit, result.getRange().getTotal()));
    }

    @Override
    @Transactional
    @Fitable(id = "default")
    public AppBuilderAppDto create(String appId, AppBuilderAppCreateDto dto, OperationContext context,
            boolean isUpgrade) {
        if (isUpgrade) {
            return this.converterFactory.convert(this.appVersionService.upgrade(appId, dto, context),
                    AppBuilderAppDto.class);
        } else {
            return this.converterFactory.convert(this.appVersionService.create(appId, dto, context),
                    AppBuilderAppDto.class);
        }
    }

    @Override
    @Fitable(id = "default")
    public long getAppCount(String tenantId, AppQueryCondition cond) {
        return this.appVersionService.countByTenantId(cond, tenantId);
    }

    @Override
    @Transactional
    @Fitable(id = "default")
    public Rsp<AppBuilderAppDto> updateApp(String appId, AppBuilderAppDto appDto, OperationContext context) {
        if (appDto == null) {
            throw new AippException(AippErrCode.INVALID_OPERATION);
        }
        AppVersion appVersion = this.appVersionService.update(appId, appDto, context);
        return Rsp.ok(this.converterFactory.convert(appVersion, AppBuilderAppDto.class));
    }

    @Override
    @Transactional
    @Fitable(id = "default")
    public Rsp<AppBuilderAppDto> updateConfig(String appId, AppBuilderConfigDto configDto,
            List<AppBuilderConfigFormPropertyDto> properties, OperationContext context) {
        AppVersion appVersion = this.appVersionService.retrieval(appId);
        if (appVersion.isPublished()) {
            throw new AippException(AippErrCode.APP_HAS_ALREADY);
        }
        Span.current().setAttribute("name", appVersion.getData().getName());
        appVersion.updateConfig(configDto, properties, context);
        appVersion.updateGraph(properties, context);
        appVersion.getData().setUpdateAt(LocalDateTime.now());
        appVersion.getData().setUpdateBy(context.getOperator());
        appVersion.putAttributes(new HashMap<>());
        this.appVersionService.update(appVersion);
        return Rsp.ok(this.converterFactory.convert(appVersion, AppBuilderAppDto.class));
    }

    @Override
    @Transactional
    @Fitable(id = "default")
    public Rsp<AppBuilderAppDto> saveConfig(String appId, AppBuilderSaveConfigDto appBuilderSaveConfigDto,
            OperationContext context) {
        AppVersion appVersion = this.appVersionService.update(appId, appBuilderSaveConfigDto, context);
        return Rsp.ok(this.converterFactory.convert(appVersion, AppBuilderAppDto.class));
    }

    @Override
    @Transactional
    @Fitable(id = "default")
    public Rsp<AppBuilderAppDto> updateFlowGraph(String appId, AppBuilderFlowGraphDto graphDto,
            OperationContext context) {
        AppVersion appVersion = this.appVersionService.update(appId, graphDto, context);
        return Rsp.ok(this.converterFactory.convert(appVersion, AppBuilderAppDto.class));
    }

    @Override
    @Fitable(id = "default")
    public void delete(String appId, OperationContext context) {
        this.appDomainService.deleteByAppId(appId, context);
    }

    @Override
    public PublishedAppResDto published(String uniqueName, OperationContext context) {
        return this.appTaskService.getLatest(uniqueName, context)
                .map(appTask -> PublishedAppResDto.builder()
                        .appId(appTask.getEntity().getAppId())
                        .appVersion(appTask.getEntity().getVersion())
                        .publishedAt(appTask.getEntity().getCreationTime())
                        .publishedBy(appTask.getEntity().getCreator())
                        .publishedDescription(appTask.getEntity().getPublishDescription())
                        .publishedUpdateLog(appTask.getEntity().getPublishLog())
                        .build())
                .orElseThrow(() -> new AippParamException(QUERY_PUBLICATION_HISTORY_FAILED));
    }

    @Override
    @Transactional
    public TemplateInfoDto publishTemplateFromApp(TemplateAppCreateDto createDto, OperationContext context) {
        this.appVersionService.validateAppName(createDto.getName(), context);
        AppVersion appVersion = this.appVersionService.retrieval(createDto.getId());
        return appVersion.publishTemplate(createDto, context);
    }

    @Override
    @Transactional
    public AppBuilderAppDto createAppByTemplate(TemplateAppCreateDto createDto, OperationContext context) {
        AppTemplate template = this.templateFactory.create(createDto.getId());
        template.setName(createDto.getName());
        template.setDescription(createDto.getDescription());
        template.setAppType(createDto.getAppType());
        String icon = template.getIcon();
        if (StringUtils.isNotBlank(icon) && StringUtils.equals(icon, createDto.getIcon())) {
            try {
                String copiedIcon = this.uploadedFileManageService.copyIconFiles(icon, null, context.getAccount());
                template.setIcon(copiedIcon);
            } catch (IOException e) {
                log.warn("Failed to create a copy of icon when create app.", e);
                template.setIcon(StringUtils.EMPTY);
            }
        } else {
            template.setIcon(createDto.getIcon());
        }
        return this.converterFactory.convert(this.appVersionService.createByTemplate(template, context),
                AppBuilderAppDto.class);
    }

    @Override
    @Transactional
    public void deleteTemplate(String templateId, OperationContext context) {
        this.templateFactory.delete(templateId);
        this.uploadedFileManageService.cleanAippFiles(Collections.singletonList(templateId));
    }

    @Override
    public RangedResultSet<AppBuilderAppDto> recentPublished(AppQueryCondition cond, long offset, int limit, String appId,
            OperationContext context) {
        AppVersion appVersion = this.appVersionService.retrieval(appId);
        String aippId = appVersion.getData().getAppSuiteId();
        List<AppTask> publishedTasks = this.appTaskService.getPublishedByPage(aippId, offset, limit, context)
                .stream()
                .toList();
        Map<String, AppVersion> appIdKeyAppVersionMap = publishedTasks.stream()
                .map(appTask -> this.appVersionService.retrieval(appTask.getEntity().getAppId()))
                .collect(Collectors.toMap(version -> version.getData().getAppId(), Function.identity()));
        List<AppBuilderAppDto> appBuilderAppDtos = publishedTasks.stream()
                .map(t -> AppTaskUtils.toPublishedAppBuilderAppDto(t,
                        appIdKeyAppVersionMap.get(t.getEntity().getAppId()),
                        this.converterFactory))
                .toList();
        return RangedResultSet.create(appBuilderAppDtos, offset, limit, appBuilderAppDtos.size());
    }

    @Override
    public List<CheckResult> checkAvailable(List<AppCheckDto> appCheckDtos, OperationContext context) {
        List<CheckResult> results = new ArrayList<>();
        appCheckDtos.forEach(dto -> {
            Checker nodeChecker = CheckerFactory.getChecker(dto.getType());
            results.addAll(nodeChecker.validate(dto, context));
        });
        return results.stream().filter(result -> !result.isValid()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppBuilderAppDto recoverApp(String appId, String resetId, OperationContext context) {
        AppVersion resetApp = this.appVersionService.retrieval(resetId);
        AppVersion currentApp = this.appVersionService.retrieval(appId);
        currentApp.cloneVersion(resetApp);
        this.appVersionService.update(currentApp);
        return this.converterFactory.convert(currentApp, AppBuilderAppDto.class);
    }

    @Override
    @Transactional
    @Fitable(id = "default")
    public void updateGuestMode(String path, Boolean isGuest) {
        this.appBuilderAppRepository.updateGuestMode(path, isGuest);
    }
}
