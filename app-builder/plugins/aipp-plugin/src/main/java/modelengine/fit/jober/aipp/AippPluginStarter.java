/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.fit.jober.aipp;

import modelengine.fit.jober.aipp.mapper.AppChatNumMapper;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.plugin.PluginStartedObserver;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

/**
 * 插件启动器
 *
 * @author 邬涨财
 * @since 2025-06-04
 */
@Component
public class AippPluginStarter implements PluginStartedObserver {
    @Override
    public void onPluginStarted(Plugin plugin) {
        if (!StringUtils.equals(plugin.metadata().name(), "aipp-plugin")) {
            return;
        }
        plugin.container().all(AppChatNumMapper.class).stream().findAny().ifPresent(beanFactory -> {
            AppChatNumMapper appChatNumMapper = ObjectUtils.cast(beanFactory.get());
            appChatNumMapper.clearNum();
        });
    }
}
