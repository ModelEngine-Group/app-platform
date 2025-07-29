/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.app;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.AppVersionFactory;
import modelengine.fit.jober.aipp.domains.appversion.repository.AppVersionRepository;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.domains.task.AppTask;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.dto.export.AppExportDto;
import modelengine.fit.jober.aipp.enums.AppCategory;
import modelengine.fit.jober.aipp.mapper.AippChatMapper;
import modelengine.fit.jober.aipp.mapper.AippLogMapper;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.fit.jober.aipp.util.UsefulUtils;
import modelengine.fitframework.log.Logger;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.service.AppService;

import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.store.service.PluginService;
import modelengine.jade.store.service.PluginToolService;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 应用.
 *
 * @author 张越
 * @since 2025-01-14
 */
public class App {
    private static final Logger LOGGER = Logger.get(App.class);

    private final String appSuiteId;

    // 注入.
    private final AppVersionService appVersionService;
    private final AppVersionRepository appVersionRepository;
    private final AppVersionFactory appVersionFactory;
    private final AppBuilderConfigRepository configRepository;
    private final AppBuilderFlowGraphRepository flowGraphRepository;
    private final AppBuilderFormPropertyRepository formPropertyRepository;
    private final AippLogMapper aippLogMapper;
    private final AppService appService;
    private final AippChatMapper aippChatMapper;
    private final Map<String, String> exportMeta;
    private final PluginToolService pluginToolService;
    private final PluginService pluginService;

    // 懒加载数据.
    private List<AppVersion> appVersionList;

    App(String appSuiteId, AppVersionService appVersionService, AppBuilderConfigRepository configRepository,
            AppBuilderFlowGraphRepository flowGraphRepository, AppBuilderFormPropertyRepository formPropertyRepository,
            AippLogMapper aippLogMapper, AppService appService, AippChatMapper aippChatMapper,
            AppVersionRepository appVersionRepository, AppVersionFactory appVersionFactory,
            Map<String, String> exportMeta, PluginToolService pluginToolService, PluginService pluginService) {
        this.appSuiteId = appSuiteId;
        this.appVersionService = appVersionService;
        this.configRepository = configRepository;
        this.flowGraphRepository = flowGraphRepository;
        this.formPropertyRepository = formPropertyRepository;
        this.aippLogMapper = aippLogMapper;
        this.appService = appService;
        this.aippChatMapper = aippChatMapper;
        this.appVersionRepository = appVersionRepository;
        this.appVersionFactory = appVersionFactory;
        this.exportMeta = exportMeta;
        this.pluginToolService = pluginToolService;
        this.pluginService = pluginService;
    }

    /**
     * 获取版本数据.
     *
     * @return {@link List}{@code <}{@link AppVersion}{@code >} 列表.
     */
    public List<AppVersion> getVersions() {
        return UsefulUtils.lazyGet(this.appVersionList,
                () -> this.appVersionService.getByAppSuiteId(this.appSuiteId),
                vs -> this.appVersionList = vs);
    }

    /**
     * 导出应用。
     *
     * @param context 操作上下文。
     * @return {@link AppExportDto} 应用导出对象。
     */
    public AppExportDto export(OperationContext context) {
        AppVersion latestVersion = this.getVersions()
                .stream()
                .max(Comparator.comparing(version -> version.getData().getUpdateAt()))
                .orElseThrow(() -> {
                    LOGGER.error("The app version is not found. [appSuiteId={}]", this.appSuiteId);
                    return new AippException(AippErrCode.APP_NOT_FOUND);
                });
        return latestVersion.export(context, this.exportMeta);
    }

    /**
     * 导入版本数据。
     *
     * @param appDto 应用导入导出基本信息。
     * @param contextRoot 请求上下文根
     * @param context 操作上下文。
     * @return {@link AppVersion} 版本对象。
     */
    public AppVersion importData(AppExportDto appDto, String contextRoot, OperationContext context) {
        AppVersion appVersion = this.appVersionFactory.create(new AppBuilderAppPo(), this.appVersionRepository);
        appVersion.importData(appDto, this.appSuiteId, contextRoot, context, this.exportMeta);
        this.appVersionService.validateAppName(appVersion.getData().getName(), context);
        this.appVersionService.save(appVersion);
        return appVersion;
    }

    /**
     * 获取最新的版本对象.
     *
     * @return {@link Optional}{@code <}{@link AppVersion}{@code >} 应用版本op.
     */
    public Optional<AppVersion> getLatestVersion() {
        return this.appVersionService.getLatestCreatedByAppSuiteId(this.appSuiteId);
    }

    /**
     * 删除整个app.
     *
     * @param context 操作人上下文信息.
     */
    public void delete(OperationContext context) {
        List<String> configIds = this.getVersions().stream().map(a -> a.getData().getConfigId()).toList();
        this.configRepository.delete(configIds);

        List<String> flowGraphIds = this.getVersions().stream().map(a -> a.getData().getFlowGraphId()).toList();
        this.flowGraphRepository.delete(flowGraphIds);

        List<String> appIds = this.getVersions().stream().map(a -> a.getData().getAppId()).toList();
        this.appVersionService.deleteByIds(appIds);
        this.formPropertyRepository.deleteByAppIds(appIds);

        List<AppTask> appTasks = this.getVersions().stream().flatMap(a -> a.getTasks(context).stream()).toList();
        if (CollectionUtils.isEmpty(appTasks)) {
            return;
        }
        List<AppTaskInstance> instances = appTasks.stream().flatMap(t -> t.getInstances(context).stream()).toList();
        List<String> instanceIds = instances.stream().map(i -> i.getEntity().getInstanceId()).toList();
        if (!CollectionUtils.isEmpty(instanceIds)) {
            this.aippLogMapper.deleteByInstanceIds(instanceIds);
        }
        appTasks.forEach(t -> t.delete(context));
        this.aippChatMapper.deleteAppByAippId(this.appSuiteId);
        Optional<AppVersion> optionalAppVersion = this.getVersions().stream().findAny();
        if (optionalAppVersion.isEmpty()) {
            return;
        }
        String type = optionalAppVersion.get().getData().getType();
        AppCategory appCategory = AppCategory.findByType(type).orElse(null);
        if (appCategory == null) {
            return;
        }
        List<String> uniqueNames = appTasks.stream()
                .filter(Objects::nonNull)
                .map(t -> t.getEntity().getUniqueName())
                .filter(StringUtils::isNotBlank)
                .distinct()
                .toList();
        if (appCategory == AppCategory.WATER_FLOW) {
            List<PluginToolData> pluginTools = this.pluginToolService.getPluginTools(uniqueNames);
            pluginTools.forEach(pluginTool -> this.pluginService.deletePlugin(pluginTool.getPluginId()));
        } else {
            uniqueNames.forEach(this.appService::deleteApp);
        }
    }
}
