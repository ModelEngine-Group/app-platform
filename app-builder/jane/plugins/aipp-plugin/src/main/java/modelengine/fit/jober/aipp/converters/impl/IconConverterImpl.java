/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.converters.impl;

import modelengine.fit.jober.aipp.converters.IconConverter;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.util.StringUtils;

/**
 * {@link IconConverter} 的默认实现类。
 *
 * @author 陈镕希
 * @since 2025-07-16
 */
@Component
public class IconConverterImpl implements IconConverter {
    private final String contextRoot;

    public IconConverterImpl(@Value("${app-engine.contextRoot}") String contextRoot) {
        this.contextRoot = contextRoot;
    }

    @Override
    public String toFrontend(String storedValue) {
        if (StringUtils.isBlank(storedValue)) {
            return storedValue;
        }
        String API_PREFIX = "/v1/api";
        if (!storedValue.contains(API_PREFIX)) {
            return storedValue;
        }
        return this.contextRoot + storedValue.substring(storedValue.indexOf(API_PREFIX));
    }

    @Override
    public String toStorage(String frontendValue) {
        if (StringUtils.isBlank(frontendValue)) {
            return frontendValue;
        }
        if (!frontendValue.startsWith(this.contextRoot)) {
            return frontendValue;
        }
        return frontendValue.substring(this.contextRoot.length());
    }
}
