/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.service;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fit.jade.aipp.domain.division.service.DomainDivisionService;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.jade.carver.ListResult;
import modelengine.jade.store.entity.query.PluginToolQuery;
import modelengine.jade.store.entity.query.QueryUtils;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.repository.pgsql.entity.PluginToolDo;
import modelengine.jade.store.repository.pgsql.repository.PluginToolRepository;
import modelengine.jade.store.service.DefinitionGroupService;
import modelengine.jade.store.service.PluginToolService;
import modelengine.jade.store.service.TagService;
import modelengine.jade.store.service.ToolGroupService;
import modelengine.jade.store.service.ToolService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * 插件工具的 Http 请求的服务层实现。
 *
 * @author 鲁为
 * @since 2024-06-15
 */
@Component
public class DefaultPluginToolService implements PluginToolService {
    private static final Logger logger = Logger.get(DefaultPluginToolService.class);
    private static final String FITABLE_ID = "store-repository-pgsql";

    private final ToolService toolService;
    private final TagService tagService;
    private final DefinitionGroupService defGroupService;
    private final ToolGroupService toolGroupService;
    private final PluginToolRepository pluginToolRepository;
    private final DomainDivisionService domainDivisionService;
    private final boolean isEnableDomainDivision;

    /**
     * 通过工具服务接口来初始化 {@link DefaultPluginToolService} 的实例。
     *
     * @param toolService 表示持久层实例的 {@link ToolService}。
     * @param tagService 表示标签服务的 {@link TagService}。
     * @param defGroupService 表示定义组服务的 {@link DefinitionGroupService}。
     * @param toolGroupService 表示工具组服务的 {@link ToolGroupService}。
     * @param pluginToolRepository 表示插件工具的仓库的 {@link PluginToolRepository}。
     */
    public DefaultPluginToolService(ToolService toolService, TagService tagService,
            DefinitionGroupService defGroupService, ToolGroupService toolGroupService,
            PluginToolRepository pluginToolRepository, DomainDivisionService domainDivisionService,
            @Value("${domain-division.isEnable}") boolean isEnableDomainDivision) {
        this.toolService = notNull(toolService, "The tool service cannot be null.");
        this.pluginToolRepository = notNull(pluginToolRepository, "The plugin tool repository cannot be null.");
        this.defGroupService = notNull(defGroupService, "The definition group service cannot be null.");
        this.toolGroupService = notNull(toolGroupService, "The tool group service cannot be null.");
        this.tagService = notNull(tagService, "The tag service cannot be null.");
        this.domainDivisionService = notNull(domainDivisionService, "The domain division service cannot be null.");
        this.isEnableDomainDivision = isEnableDomainDivision;
    }

    @Deprecated
    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public String addPluginTool(PluginToolData pluginToolData) {
        notNull(pluginToolData, "The plugin data cannot be null.");
        String uniqueName = UUID.randomUUID().toString();
        pluginToolData.setUniqueName(uniqueName);
        try {
            this.toolService.addTool(pluginToolData);
            logger.info("Succeeded to add tool. [toolUniqueName={}]", pluginToolData.getUniqueName());
        } catch (FitException e) {
            logger.error("Failed to add tool.");
            throw e;
        }
        try {
            this.pluginToolRepository.addPluginTool(pluginToolData);
            this.tagService.addTags(pluginToolData.getTags(), pluginToolData.getUniqueName());
            logger.info("Succeeded to add plugin tools and tags. [toolUniqueName={}]", pluginToolData.getUniqueName());
        } catch (FitException e) {
            logger.error("Failed to add plugin tools and tags.");
            try {
                // 回滚之前的数据库插入操作。
                String version = pluginToolData.getVersion();
                this.toolService.deleteToolByVersion(uniqueName, version == null ? "1.0.0" : version);
            } catch (FitException rollbackEx) {
                e.addSuppressed(rollbackEx);
            }
            throw e;
        }
        return uniqueName;
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public List<String> addPluginTools(List<PluginToolData> pluginToolDataList) {
        notNull(pluginToolDataList, "The plugin data list cannot be null.");
        this.pluginToolRepository.addPluginTools(pluginToolDataList);
        List<Set<String>> tagsList = new ArrayList<>();
        List<String> uniqueNameList = new ArrayList<>();
        pluginToolDataList.forEach(pluginToolData -> {
            tagsList.add(pluginToolData.getTags());
            uniqueNameList.add(pluginToolData.getUniqueName());
        });
        this.tagService.addTagsList(tagsList, uniqueNameList);
        logger.info("Succeeded to add plugin tools and tags. [toolUniqueNames={}]", uniqueNameList);
        return uniqueNameList;
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public ListResult<PluginToolData> getPluginTools(PluginToolQuery pluginToolQuery) {
        if (pluginToolQuery == null || QueryUtils.isPageInvalid(pluginToolQuery.getOffset(),
                pluginToolQuery.getLimit())) {
            return ListResult.empty();
        }
        if (this.isEnableDomainDivision) {
            pluginToolQuery.setUserGroupId(this.domainDivisionService.getUserGroupId());
        }
        PluginToolQuery.toUpperCase(pluginToolQuery);
        List<PluginToolData> data =
                this.getPluginToolDataList(this.pluginToolRepository.getPluginTools(pluginToolQuery));
        pluginToolQuery.setLimit(null);
        pluginToolQuery.setOffset(null);
        int count = this.pluginToolRepository.getPluginToolsCount(pluginToolQuery);
        return ListResult.create(data, count);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public List<PluginToolData> getPluginTools(String pluginId) {
        List<PluginToolDo> dos = this.pluginToolRepository.getPluginTools(pluginId);
        return this.getPluginToolDataList(dos);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public List<Boolean> hasPluginTools(List<String> uniqueNames) {
        if (CollectionUtils.isEmpty(uniqueNames)) {
            return Collections.emptyList();
        }
        return this.pluginToolRepository.hasPluginTools(uniqueNames);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public List<PluginToolData> getPluginTools(List<String> uniqueNames) {
        if (uniqueNames.isEmpty()) {
            return Collections.emptyList();
        }
        List<PluginToolDo> dos = this.pluginToolRepository.getPluginTools(uniqueNames);
        return this.getPluginToolDataList(dos);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public PluginToolData getPluginTool(String toolUniqueName) {
        PluginToolDo pluginToolDo = this.pluginToolRepository.getPluginToolByUniqueName(toolUniqueName);
        ToolData toolData = this.toolService.getTool(toolUniqueName);
        Set<String> tags = this.tagService.getTags(pluginToolDo.getToolUniqueName());
        return PluginToolDo.convertToPluginToolData(toolData, pluginToolDo, tags);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public String deletePluginTool(String toolUniqueName) {
        PluginToolData oldPluginToolData = this.getPluginTool(toolUniqueName);
        String defGroupName = oldPluginToolData.getDefGroupName();
        String toolGroupName = oldPluginToolData.getGroupName();
        try {
            this.pluginToolRepository.deletePluginTool(toolUniqueName);
            this.tagService.deleteTagByUniqueName(toolUniqueName);
            logger.info("Succeeded to delete plugin and tags. [toolUniqueName={}]", toolUniqueName);
        } catch (FitException e) {
            logger.error("Failed to delete plugin and tags.");
            throw e;
        }
        try {
            this.toolService.deleteTool(toolUniqueName);
            if (this.toolService.getTools(defGroupName, toolGroupName).isEmpty()) {
                this.toolGroupService.delete(defGroupName, toolGroupName);
            }
            if (this.toolGroupService.get(defGroupName).isEmpty()) {
                this.defGroupService.delete(defGroupName);
            }
            logger.info("Succeeded to delete existing tool. [toolUniqueName={}]", toolUniqueName);
        } catch (FitException e) {
            logger.error("Failed to delete existing tool.");
            try {
                // 回滚之前的数据库插入操作。
                this.pluginToolRepository.addPluginTool(oldPluginToolData);
                this.tagService.addTags(oldPluginToolData.getTags(), oldPluginToolData.getUniqueName());
            } catch (FitException rollbackEx) {
                e.addSuppressed(rollbackEx);
            }
            throw e;
        }
        return toolUniqueName;
    }

    private List<PluginToolData> getPluginToolDataList(List<PluginToolDo> list) {
        List<PluginToolData> data = new ArrayList<>();
        for (PluginToolDo pluginToolDo : list) {
            ToolData toolData = this.toolService.getTool(pluginToolDo.getToolUniqueName());
            Set<String> tags = this.tagService.getTags(pluginToolDo.getToolUniqueName());
            PluginToolData pluginToolData = PluginToolDo.convertToPluginToolData(toolData, pluginToolDo, tags);
            data.add(pluginToolData);
        }
        return data;
    }
}
