/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.app;

import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.AppVersionFactory;
import modelengine.fit.jober.aipp.domains.appversion.repository.AppVersionRepository;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.mapper.AippChatMapper;
import modelengine.fit.jober.aipp.mapper.AippLogMapper;
import modelengine.fit.jober.aipp.repository.AppBuilderConfigRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;
import modelengine.fit.jober.aipp.repository.AppBuilderFormPropertyRepository;
import modelengine.jade.store.service.AppService;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.jade.store.service.PluginService;
import modelengine.jade.store.service.PluginToolService;

import java.util.Map;

/**
 * {@link App} 工厂类.
 *
 * @author 张越
 * @since 2025-01-14
 */
@Component
public class AppFactory {
    private final AppVersionService appVersionService;
    private final AppBuilderConfigRepository configRepository;
    private final AppBuilderFlowGraphRepository flowGraphRepository;
    private final AppBuilderFormPropertyRepository formPropertyRepository;
    private final AippLogMapper aippLogMapper;
    private final AppService appService;
    private final AippChatMapper aippChatMapper;
    private final AppVersionRepository appVersionRepository;
    private final AppVersionFactory appVersionFactory;
    private final Map<String, String> exportMeta;
    private final PluginToolService pluginToolService;
    private final PluginService pluginService;

    public AppFactory(AppVersionService appVersionService, AppBuilderConfigRepository configRepository,
            AppBuilderFlowGraphRepository flowGraphRepository, AppBuilderFormPropertyRepository formPropertyRepository,
            AippLogMapper aippLogMapper, AppService appService, AippChatMapper aippChatMapper,
            AppVersionRepository appVersionRepository, AppVersionFactory appVersionFactory,
            @Value("${export-meta}") Map<String, String> exportMeta, PluginToolService pluginToolService,
            PluginService pluginService) {
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
     * 创建 {@link App} 对象.
     *
     * @param appSuiteId app的唯一标识.
     * @return {@link AppVersion} 对象.
     */
    public App create(String appSuiteId) {
        return new App(appSuiteId,
                this.appVersionService,
                this.configRepository,
                this.flowGraphRepository,
                this.formPropertyRepository,
                this.aippLogMapper,
                this.appService,
                this.aippChatMapper,
                this.appVersionRepository,
                this.appVersionFactory,
                this.exportMeta,
                this.pluginToolService,
                this.pluginService);
    }
}
