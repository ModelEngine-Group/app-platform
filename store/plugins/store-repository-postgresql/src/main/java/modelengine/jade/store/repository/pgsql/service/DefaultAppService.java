/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.service;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.jade.store.entity.transfer.AppData.toDefGroup;
import static modelengine.jade.store.entity.transfer.AppData.toToolGroup;

import modelengine.fel.tool.ToolSchema;
import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fit.jade.aipp.domain.division.service.DomainDivisionService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.carver.ListResult;
import modelengine.jade.store.entity.query.AppQuery;
import modelengine.jade.store.entity.query.QueryUtils;
import modelengine.jade.store.entity.transfer.AppData;
import modelengine.jade.store.entity.transfer.AppPublishData;
import modelengine.jade.store.repository.pgsql.entity.AppDo;
import modelengine.jade.store.repository.pgsql.repository.AppRepository;
import modelengine.jade.store.service.AppService;
import modelengine.jade.store.service.DefinitionGroupService;
import modelengine.jade.store.service.TagService;
import modelengine.jade.store.service.ToolGroupService;
import modelengine.jade.store.service.ToolService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 应用的 Http 请求的服务层实现。
 *
 * @author 鲁为
 * @since 2024-06-19
 */
@Component
public class DefaultAppService implements AppService {
    private static final Logger logger = Logger.get(DefaultAppService.class);
    private static final String FITABLE_ID = "store-repository-pgsql";
    private static final String APP = "APP";
    private static final String APP_TYPE = "APP_TYPE";

    private final ToolService toolService;
    private final AppRepository appRepository;
    private final TagService tagService;
    private final ToolGroupService toolGroupService;
    private final DefinitionGroupService defGroupService;
    private final DomainDivisionService domainDivisionService;
    private final boolean isEnableDomainDivision;

    /**
     * 通过应用服务接口来初始化 {@link DefaultAppService} 的实例。
     *
     * @param toolService 表示持久层实例的 {@link ToolService}。
     * @param appRepository 表示应用的仓库的 {@link AppRepository}。
     * @param tagService 表示标签服务的 {@link TagService}。
     * @param toolGroupService 表示工具组服务的 {@link ToolGroupService}。
     * @param defGroupService 表示定义组服务的 {@link DefinitionGroupService}。
     * @param domainDivisionService 表示定义组服务的 {@link DefinitionGroupService}。
     */
    public DefaultAppService(ToolService toolService, AppRepository appRepository, TagService tagService,
            ToolGroupService toolGroupService, DefinitionGroupService defGroupService,
            DomainDivisionService domainDivisionService,
            @Value("${domain-division.isEnable}") boolean isEnableDomainDivision) {
        this.toolService = notNull(toolService, "The tool service cannot be null.");
        this.appRepository = notNull(appRepository, "The app repository cannot be null.");
        this.tagService = notNull(tagService, "The tag service cannot be null.");
        this.toolGroupService = notNull(toolGroupService, "The tool group service cannot be null.");
        this.defGroupService = notNull(defGroupService, "The definition group service cannot be null.");
        this.domainDivisionService = notNull(domainDivisionService, "The domain division service cannot be null.");
        this.isEnableDomainDivision = isEnableDomainDivision;
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public String publishApp(AppPublishData appData) {
        notNull(appData, "The app data cannot be null.");
        String uniqueName = appData.getUniqueName();
        AppData.fillAppData(appData);
        fillSchema(appData);
        if (StringUtils.isBlank(uniqueName)) {
            return this.addApp(appData);
        }
        return this.upgradeApp(appData);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public ListResult<AppPublishData> getApps(AppQuery appQuery) {
        if (appQuery == null || QueryUtils.isPageInvalid(appQuery.getOffset(), appQuery.getLimit())) {
            return ListResult.empty();
        }
        AppQuery.toUpperCase(appQuery);
        if (this.isEnableDomainDivision) {
            appQuery.setUserGroupId(this.domainDivisionService.getUserGroupId());
        }
        List<AppPublishData> data = this.getAppDataList(this.appRepository.getApps(appQuery));
        appQuery.setLimit(null);
        appQuery.setOffset(null);
        return ListResult.create(data, this.appRepository.getAppsCount(appQuery));
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public AppPublishData getApp(String toolUniqueName) {
        AppDo appDo = this.appRepository.getApp(toolUniqueName);
        ToolData toolData = this.toolService.getTool(toolUniqueName);
        Set<String> tags = this.tagService.getTags(appDo.getToolUniqueName());
        return AppDo.convertToAppPublishData(toolData, appDo, tags);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public String deleteApp(String toolUniqueName) {
        AppPublishData oldAppData = this.getApp(toolUniqueName);
        try {
            this.appRepository.deleteApp(toolUniqueName);
            this.tagService.deleteTagByUniqueName(toolUniqueName);
            logger.info("Succeeded in deleting app and tags. [toolUniqueName={}]", toolUniqueName);
        } catch (FitException e) {
            logger.error("Failed to delete app and tags.");
            throw e;
        }
        try {
            this.toolService.deleteTool(toolUniqueName);
            logger.info("Succeeded in deleting existing tool. [toolUniqueName={}]", toolUniqueName);
        } catch (FitException e) {
            logger.error("Failed to delete existing tool.");
            try {
                // 回滚之前的数据库插入操作。
                this.appRepository.addApp(oldAppData);
                this.tagService.addTags(oldAppData.getTags(), oldAppData.getUniqueName());
            } catch (FitException rollbackEx) {
                e.addSuppressed(rollbackEx);
            }
            throw e;
        }
        return toolUniqueName;
    }

    private String addApp(AppPublishData appData) {
        String uniqueName = appData.getUniqueName();
        try {
            this.defGroupService.add(toDefGroup(appData));
            this.toolGroupService.add(toToolGroup(appData));
            logger.info("Succeeded in adding tool. [toolUniqueName={}]", appData.getUniqueName());
        } catch (FitException e) {
            logger.error("Failed to add tool.");
            throw e;
        }
        try {
            this.appRepository.addApp(appData);
            this.tagService.addTags(appData.getTags(), appData.getUniqueName());
            logger.info("Succeeded in adding app and tags. [toolUniqueName={}]", appData.getUniqueName());
        } catch (FitException e) {
            logger.error("Failed to add app and tags.");
            try {
                // 回滚之前的数据库插入操作。
                this.toolService.deleteToolByVersion(uniqueName, appData.getVersion());
            } catch (FitException rollbackEx) {
                e.addSuppressed(rollbackEx);
            }
            throw e;
        }
        return uniqueName;
    }

    private void fillSchema(AppData appData) {
        Map<String, Object> schema = appData.getSchema();
        if (schema == null) {
            schema = new HashMap<>();
        }
        if (schema.get(ToolSchema.PARAMETERS) == null) {
            schema.put(ToolSchema.PARAMETERS,
                    MapBuilder.<String, Object>get()
                            .put(ToolSchema.PROPERTIES_TYPE, "object")
                            .put(ToolSchema.PARAMETERS_PROPERTIES, Collections.emptyMap())
                            .put(ToolSchema.PARAMETERS_REQUIRED, "")
                            .build());
        }
        if (schema.get(ToolSchema.RETURN_SCHEMA) == null) {
            schema.put(ToolSchema.RETURN_SCHEMA,
                    MapBuilder.<String, Object>get()
                            .put(ToolSchema.DESCRIPTION, "")
                            .put(ToolSchema.PROPERTIES_TYPE, "string")
                            .build());
        }
        if (schema.get(ToolSchema.PARAMETERS_ORDER) == null) {
            schema.put(ToolSchema.PARAMETERS_ORDER, Collections.emptyList());
        }
    }

    private String upgradeApp(AppPublishData appData) {
        this.updateAppTag(appData);
        this.updateAppData(appData);
        return this.toolService.upgradeTool(appData);
    }

    private void updateAppData(AppPublishData appData) {
        this.appRepository.updateApp(appData);
    }

    private void updateAppTag(AppPublishData appData) {
        Optional<String> filteredTag = appData.getTags().stream().filter(tag -> tag.startsWith(APP_TYPE)).findFirst();
        String targetTag = filteredTag.orElse(APP);
        this.tagService.updateAppTag(targetTag, appData.getUniqueName());
    }

    private List<AppPublishData> getAppDataList(List<AppDo> list) {
        List<AppPublishData> data = new ArrayList<>();
        for (AppDo appDo : list) {
            ToolData toolData = this.toolService.getTool(appDo.getToolUniqueName());
            Set<String> tags = this.tagService.getTags(appDo.getToolUniqueName());
            AppPublishData appData = AppDo.convertToAppPublishData(toolData, appDo, tags);
            data.add(appData);
        }
        return data;
    }
}
