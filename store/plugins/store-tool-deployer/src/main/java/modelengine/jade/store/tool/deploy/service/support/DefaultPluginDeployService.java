/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.deploy.service.support;

import static modelengine.fel.tool.info.schema.PluginSchema.PLUGIN_FULL_NAME;
import static modelengine.fel.tool.info.schema.ToolsSchema.FIT;
import static modelengine.fel.tool.info.schema.ToolsSchema.FITABLE_ID;
import static modelengine.fel.tool.info.schema.ToolsSchema.GENERICABLE_ID;
import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.runtime.FitRuntime;
import modelengine.fitframework.runtime.FitRuntimeStartedObserver;
import modelengine.fitframework.schedule.Task;
import modelengine.fitframework.schedule.ThreadPoolExecutor;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.common.exception.ModelEngineException;
import modelengine.jade.store.code.PluginRetCode;
import modelengine.jade.store.entity.query.PluginQuery;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.service.DeployService;
import modelengine.jade.store.service.PluginService;
import modelengine.jade.store.service.support.DeployStatus;
import modelengine.jade.store.tool.deploy.config.PluginDeployQueryConfig;
import modelengine.jade.store.tool.deploy.config.RegistryQueryPoolConfig;
import modelengine.jade.store.tool.deploy.service.PluginDeployService;
import modelengine.jade.store.tool.deploy.util.PluginDeployManagementUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 插件部署服务实现类。
 *
 * @author 罗帅
 * @author 杭潇
 * @since 2024-8-13
 */
@Component
public class DefaultPluginDeployService implements PluginDeployService, FitRuntimeStartedObserver {
    private static final Logger log = Logger.get(DefaultPluginDeployService.class);
    private static final String BUILTIN = "BUILTIN";
    private static final String AND = "AND";
    private static final int MIN_FIT_LENGTH = 1;
    private static final int MAX_FIT_OR_TAG_LENGTH = 64;

    private final PluginService pluginService;
    private final ThreadPoolExecutor registerQueryThread;
    private final PluginDeployQueryConfig pluginDeployQueryConfig;
    private final DeployService deployService;

    /**
     * 通过插件服务来初始化 {@link DefaultPluginDeployService} 的新实例。
     *
     * @param pluginService 表示插件服务的 {@link PluginService}。
     * @param registryQueryPoolConfig 表示查询注册中心的线程池配置参数的 {@link RegistryQueryPoolConfig}。
     * @param pluginDeployQueryConfig 表示插件部署状态查询配置参数的 {@link PluginDeployQueryConfig}。
     * @param deployService 表示插件部署服务的 {@link DeployService}。
     */
    public DefaultPluginDeployService(PluginService pluginService, RegistryQueryPoolConfig registryQueryPoolConfig,
            PluginDeployQueryConfig pluginDeployQueryConfig, DeployService deployService) {
        this.pluginService = notNull(pluginService, "The plugin service cannot be null.");
        RegistryQueryPoolConfig queryPoolConfig =
                notNull(registryQueryPoolConfig, "The registry query pool config cannot be null.");
        this.pluginDeployQueryConfig =
                notNull(pluginDeployQueryConfig, "The plugin deploy query config cannot be null.");
        this.deployService = deployService;
        this.registerQueryThread = ThreadPoolExecutor.custom()
                .threadPoolName("registry-query-pool")
                .awaitTermination(500L, TimeUnit.MILLISECONDS)
                .isImmediateShutdown(false)
                .corePoolSize(queryPoolConfig.getCorePoolSize())
                .maximumPoolSize(queryPoolConfig.getMaximumPoolSize())
                .keepAliveTime(60L, TimeUnit.SECONDS)
                .workQueueCapacity(queryPoolConfig.getWorkQueueCapacity())
                .isDaemonThread(false)
                .exceptionHandler((thread, throwable) -> {})
                .rejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.AbortPolicy())
                .build();
    }

    private void initDeployStatus() {
        List<String> expiredStatusIds = this.pluginService.getPlugins(DeployStatus.DEPLOYING)
                .stream()
                .map(PluginData::getPluginId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(expiredStatusIds)) {
            expiredStatusIds.forEach(pluginId -> PluginDeployManagementUtils.undeployPlugin(pluginId,
                    this.pluginService,
                    this.pluginDeployQueryConfig));
            this.pluginService.updateDeployStatus(expiredStatusIds, DeployStatus.UNDEPLOYED);
        }
        // 内置工具修改为已部署
        PluginQuery pluginQuery = new PluginQuery();
        pluginQuery.setExcludeTags(new HashSet<>());
        pluginQuery.setIncludeTags(new HashSet<>(Collections.singletonList(BUILTIN)));
        pluginQuery.setMode(AND);
        List<String> builtInPluginIds = this.pluginService.getPlugins(pluginQuery)
                .getData()
                .stream()
                .map(PluginData::getPluginId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(builtInPluginIds)) {
            this.pluginService.updateDeployStatus(builtInPluginIds, DeployStatus.DEPLOYED);
        }
    }

    @Override
    public void deployPlugins(List<String> toDeployPluginIds) {
        if (toDeployPluginIds.size() > this.pluginDeployQueryConfig.getMaxToolSize()) {
            throw new ModelEngineException(PluginRetCode.PLUGIN_DEPLOY_FAILED,
                    StringUtils.format("The number of plugin deployments exceeds the limit'. [number={0}]",
                            this.pluginDeployQueryConfig.getMaxToolSize()));
        }
        this.validatePluginIds(toDeployPluginIds);
        List<PluginData> deployedPlugins = this.pluginService.getPlugins(DeployStatus.DEPLOYED);
        List<String> deployedPluginIds = deployedPlugins.stream()
                .filter(pluginData -> !pluginData.getBuiltin())
                .map(PluginData::getPluginId)
                .collect(Collectors.toList());
        List<String> toUnDeployedIds =
                new ArrayList<>(CollectionUtils.difference(deployedPluginIds, toDeployPluginIds));
        List<String> newDeployedIds = new ArrayList<>(CollectionUtils.difference(toDeployPluginIds, deployedPluginIds));
        if (CollectionUtils.isNotEmpty(toUnDeployedIds)) {
            this.pluginService.updateDeployStatus(toUnDeployedIds, DeployStatus.UNDEPLOYED);
            toUnDeployedIds.forEach(pluginId -> PluginDeployManagementUtils.undeployPlugin(pluginId,
                    this.pluginService,
                    this.pluginDeployQueryConfig));
        }
        if (CollectionUtils.isNotEmpty(newDeployedIds)) {
            this.pluginService.updateDeployStatus(newDeployedIds, DeployStatus.DEPLOYING);
            newDeployedIds.forEach(this::deployPlugin);
        }
    }

    private void validatePluginIds(List<String> toDeployPluginIds) {
        Set<String> toDeployTool = new HashSet<>();
        for (String toDeployPluginId : toDeployPluginIds) {
            PluginData plugin = this.pluginService.getPlugin(toDeployPluginId);
            if (plugin == null) {
                throw new ModelEngineException(PluginRetCode.PLUGIN_NOT_EXISTS);
            }
            List<PluginToolData> pluginToolDataList =
                    notNull(plugin.getPluginToolDataList(), "The tools of plugin cannot be null.");
            for (PluginToolData pluginToolData : pluginToolDataList) {
                try {
                    this.validateRunnable(pluginToolData.getRunnables(), toDeployTool);
                } catch (IllegalStateException exception) {
                    throw new ModelEngineException(PluginRetCode.PLUGIN_DEPLOY_FAILED, exception.getMessage());
                }
            }
        }
    }

    @Override
    public int queryCountByDeployStatus(DeployStatus deployStatus) {
        return this.pluginService.getPluginsCount(deployStatus);
    }

    @Override
    public List<PluginData> queryPluginsByDeployStatus(DeployStatus deployStatus) {
        return this.pluginService.getPlugins(deployStatus);
    }

    private void validateRunnable(Map<String, Object> runnables, Set<String> set) {
        if (runnables.containsKey(FIT)) {
            Map<String, Object> fit = cast(runnables.get(FIT));
            if (!fit.containsKey(FITABLE_ID)) {
                throw new ModelEngineException(PluginRetCode.JSON_PARSE_ERROR,
                        "The field 'runnables' in tools.json must contain key: 'fitableId'.");
            }
            this.validateLength(fit, FITABLE_ID);
            if (!fit.containsKey(GENERICABLE_ID)) {
                throw new ModelEngineException(PluginRetCode.JSON_PARSE_ERROR,
                        "The field 'runnables' in tools.json must contain key: 'genericableId'.");
            }
            this.validateLength(fit, GENERICABLE_ID);
            String fitableId = cast(fit.get(FITABLE_ID));
            String genericableId = cast(fit.get(GENERICABLE_ID));
            if (!set.add(fitableId + genericableId)) {
                throw new IllegalStateException(StringUtils.format(
                        "The current operation has duplicate fitable id and genericable id. "
                                + "[fitableId={0}, genericableId={1}]",
                        fitableId,
                        genericableId));
            }
        }
    }

    private void validateLength(Map<String, Object> fit, String fitKey) {
        Object fitValue = fit.get(fitKey);
        if (!(fitValue instanceof String)) {
            throw new ModelEngineException(PluginRetCode.JSON_PARSE_ERROR,
                    StringUtils.format("The type of field value in 'runnables' must be String. [field={0}]", fitKey));
        }
        int length = ((String) fitValue).length();
        if (length < MIN_FIT_LENGTH || length > MAX_FIT_OR_TAG_LENGTH) {
            throw new ModelEngineException(PluginRetCode.JSON_PARSE_ERROR,
                    StringUtils.format("The length of field value in 'runnables' be compliant. "
                                    + "[field={0}, minLength={1}, maxLength={2}]",
                            fitKey,
                            MIN_FIT_LENGTH,
                            MAX_FIT_OR_TAG_LENGTH));
        }
    }

    /**
     * 根据插件 Id 部署插件。
     *
     * @param pluginId 表示待部署的插件 Id 的 {@link String}。
     */
    private void deployPlugin(String pluginId) {
        PluginData pluginData = this.pluginService.getPlugin(pluginId);
        Optional<String> pluginFullName = this.getPluginFullName(pluginData);
        if (!pluginFullName.isPresent()) {
            return;
        }
        this.registerQueryThread.execute(Task.builder()
                .runnable(() -> this.deployService.deploy(pluginData, pluginFullName.get(), pluginId))
                .uncaughtExceptionHandler((thread, cause) -> this.exceptionCaught(cause,
                        pluginData.getPluginName(),
                        pluginId))
                .buildDisposable());
    }

    private void exceptionCaught(Throwable cause, String pluginName, String pluginId) {
        log.error("Failed to deploy file. [pluginFile={}]", pluginName, cause);
        PluginDeployManagementUtils.undeployPlugin(pluginId, this.pluginService, this.pluginDeployQueryConfig);
        this.pluginService.updateDeployStatus(Collections.singletonList(pluginId), DeployStatus.DEPLOYMENT_FAILED);
    }

    private Optional<String> getPluginFullName(PluginData pluginData) {
        Map<String, Object> extension = pluginData.getExtension();
        if (extension.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(PluginDeployManagementUtils.requireStringInMapObject(extension.get(PLUGIN_FULL_NAME)));
    }

    @Override
    public void onRuntimeStarted(FitRuntime runtime) {
        this.initDeployStatus();
    }
}