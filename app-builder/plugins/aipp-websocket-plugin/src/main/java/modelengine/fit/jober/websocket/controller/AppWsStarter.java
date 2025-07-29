/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.controller;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.jober.aipp.service.AppWsCommand;
import modelengine.fit.jober.aipp.service.AppWsRegistryService;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.ioc.BeanFactory;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginStartedObserver;
import modelengine.fitframework.plugin.PluginStoppingObserver;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表示观察所有插件启动的观察者的实现。
 *
 * @author 曹嘉美
 * @since 2025-01-14
 */
@Component
public class AppWsStarter implements PluginStartedObserver, PluginStoppingObserver {
    private final AppWsRegistryService registry;

    public AppWsStarter(AppWsRegistryService registry) {
        this.registry = notNull(registry, "The registry cannot be null.");
    }

    @Override
    public void onPluginStarted(Plugin plugin) {
        for (AppWsCommand<?> service : this.getAppWsCommands(plugin)) {
            this.registry.register(service.method(), service);
        }
    }

    @Override
    public void onPluginStopping(Plugin plugin) {
        for (AppWsCommand<?> service : this.getAppWsCommands(plugin)) {
            this.registry.unregister(service.method());
        }
    }

    private List<AppWsCommand<?>> getAppWsCommands(Plugin plugin) {
        return plugin.container()
                .factories(AppWsCommand.class)
                .stream()
                .map(BeanFactory::<AppWsCommand<?>>get)
                .collect(Collectors.toList());
    }
}
