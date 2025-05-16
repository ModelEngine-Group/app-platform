/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.service;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.jade.store.repository.pgsql.entity.PluginDo.convertToPluginData;

import modelengine.fel.tool.model.transfer.DefinitionGroupData;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.transaction.Transactional;
import modelengine.jade.carver.ListResult;
import modelengine.jade.store.entity.query.PluginQuery;
import modelengine.jade.store.entity.query.PluginToolQuery;
import modelengine.jade.store.entity.query.QueryUtils;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.repository.pgsql.entity.PluginDo;
import modelengine.jade.store.repository.pgsql.repository.PluginRepository;
import modelengine.jade.store.service.DefinitionGroupService;
import modelengine.jade.store.service.PluginService;
import modelengine.jade.store.service.PluginToolService;
import modelengine.jade.store.service.ToolGroupService;
import modelengine.jade.store.service.support.DeployStatus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 插件的 Http 请求的服务层实现。
 *
 * @author 鲁为
 * @since 2024-07-25
 */
@Component
public class DefaultPluginService implements PluginService {
    private static final Logger logger = Logger.get(DefaultPluginService.class);
    private static final String FITABLE_ID = "store-repository-pgsql";

    private final PluginRepository pluginRepository;
    private final PluginToolService pluginToolService;
    private final DefinitionGroupService defGroupService;
    private final ToolGroupService toolGroupService;
    private final ObjectSerializer serializer;

    /**
     * 通过插件仓库、插件工具服务和序列化器来初始化 {@link DefaultPluginService} 的实例。
     *
     * @param pluginRepository 表示插件的仓库的 {@link PluginRepository}。
     * @param pluginToolService 表示插件工具的服务的 {@link PluginToolService}。
     * @param defGroupService 表示定义组的服务的 {@link DefinitionGroupService}。
     * @param toolGroupService 表示工具组的服务的 {@link ToolGroupService}。
     * @param serializer 表示序列化器的 {@link ObjectSerializer}。
     */
    public DefaultPluginService(PluginRepository pluginRepository, PluginToolService pluginToolService,
            DefinitionGroupService defGroupService, ToolGroupService toolGroupService,
            @Fit(alias = "json") ObjectSerializer serializer) {
        this.pluginRepository = notNull(pluginRepository, "The plugin repository cannot be null.");
        this.pluginToolService = notNull(pluginToolService, "The plugin tool service cannot be null.");
        this.toolGroupService = notNull(toolGroupService, "The tool group service cannot be null.");
        this.defGroupService = notNull(defGroupService, "The definition group service cannot be null.");
        this.serializer = notNull(serializer, "The serializer cannot be null.");
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public String addPlugin(PluginData pluginData) {
        try {
            this.defGroupService.add(pluginData.getDefinitionGroupDataList());
            this.toolGroupService.add(pluginData.getToolGroupDataList());
            logger.info("Succeeded to add groups.");
        } catch (FitException e) {
            logger.error("Failed to add groups.");
            throw e;
        }
        try {
            this.pluginToolService.addPluginTools(pluginData.getPluginToolDataList());
            this.pluginRepository.addPlugin(pluginData);
        } catch (FitException e) {
            logger.error("Failed to add plugin tools and tags.");
            try {
                // 回滚之前的数据库插入操作。
                this.defGroupService.delete(pluginData.getDefinitionGroupDataList()
                        .stream()
                        .map(DefinitionGroupData::getName)
                        .collect(Collectors.toList()));
                pluginData.getToolGroupDataList()
                        .stream()
                        .forEach(toolGroupData -> this.toolGroupService.delete(toolGroupData.getDefGroupName(),
                                toolGroupData.getName()));
            } catch (FitException rollbackEx) {
                e.addSuppressed(rollbackEx);
            }
            throw e;
        }
        return pluginData.getPluginId();
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public PluginData getPlugin(String pluginId) {
        return convertToPluginData(this.pluginRepository.getPluginByPluginId(pluginId),
                serializer,
                this.pluginToolService.getPluginTools(pluginId));
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public ListResult<PluginData> getPlugins(PluginQuery pluginQuery) {
        if (pluginQuery == null || QueryUtils.isPageInvalid(pluginQuery.getOffset(), pluginQuery.getLimit())) {
            return ListResult.empty();
        }
        PluginToolQuery.toUpperCase(pluginQuery);
        List<PluginDo> pluginDos = this.pluginRepository.getPlugins(pluginQuery);
        List<PluginData> pluginDataList = pluginDos.stream()
                .map(pluginDo -> convertToPluginData(pluginDo, serializer, null))
                .collect(Collectors.toList());
        pluginQuery.setLimit(null);
        pluginQuery.setOffset(null);
        int count = this.pluginRepository.getPluginsCount(pluginQuery);
        return ListResult.create(pluginDataList, count);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public List<PluginData> getPlugins(DeployStatus deployStatus) {
        List<PluginDo> pluginDos = this.pluginRepository.getPlugins(deployStatus);
        return pluginDos.stream()
                .map(pluginDo -> PluginDo.convertToPluginData(pluginDo, serializer, null))
                .collect(Collectors.toList());
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public int getPluginsCount(DeployStatus deployStatus) {
        return this.pluginRepository.getPluginsCount(deployStatus);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public void updateDeployStatus(List<String> pluginIdList, DeployStatus deployStatus) {
        this.pluginRepository.updateDeployStatus(pluginIdList, deployStatus);
        logger.info("Succeed in updating deploy status. [deployStatus={}]", deployStatus);
    }

    /**
     * 删除插件，并删除插件下的每个工具。
     *
     * @param pluginId 表示待删除插件唯一标识的 {@link String}。
     * @return 表示删除插件唯一标识的 {@link String}。
     */
    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public String deletePlugin(String pluginId) {
        List<PluginToolData> pluginToolDataList = this.pluginToolService.getPluginTools(pluginId);
        for (PluginToolData pluginToolData : pluginToolDataList) {
            this.pluginToolService.deletePluginTool(pluginToolData.getUniqueName());
        }
        this.pluginRepository.deletePlugin(pluginId);
        return pluginId;
    }
}
