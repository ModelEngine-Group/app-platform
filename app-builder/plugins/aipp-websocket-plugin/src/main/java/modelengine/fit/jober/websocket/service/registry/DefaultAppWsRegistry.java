/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.websocket.service.registry;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.jober.aipp.service.AppWsCommand;
import modelengine.fit.jober.aipp.service.AppWsRegistryService;

import modelengine.fitframework.annotation.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link AppWsRegistryService} 的默认实现。
 *
 * @author 曹嘉美
 * @since 2025-01-14
 */
@Component
public class DefaultAppWsRegistry implements AppWsRegistryService {
    private final Map<String, AppWsCommand<?>> router = new HashMap<>();

    @Override
    public void register(String method, AppWsCommand<?> command) {
        this.router.put(method, command);
    }

    @Override
    public void unregister(String method) {
        this.router.remove(method);
    }

    @Override
    public AppWsCommand<?> getCommand(String method) {
        return this.router.getOrDefault(method, null);
    }

    @Override
    public Class<?> getParamClass(String method) {
        notNull(this.getCommand(method), "The command can not be null.");
        return this.getCommand(method).paramClass();
    }
}
