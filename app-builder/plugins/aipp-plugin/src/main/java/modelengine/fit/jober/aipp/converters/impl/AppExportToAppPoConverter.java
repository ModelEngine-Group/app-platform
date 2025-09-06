/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.converters.impl;

import modelengine.fit.jober.aipp.converters.EntityConverter;
import modelengine.fit.jober.aipp.dto.export.AppExportApp;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.util.JsonUtils;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Optional;

/**
 * {@link AppExportApp} -> {@link AppBuilderAppPo}.
 *
 * @author 张越
 * @since 2025-02-14
 */
@Component
public class AppExportToAppPoConverter implements EntityConverter {
    @Override
    public Class<AppExportApp> source() {
        return AppExportApp.class;
    }

    @Override
    public Class<AppBuilderAppPo> target() {
        return AppBuilderAppPo.class;
    }

    @Override
    public AppBuilderAppPo convert(Object appExportApp) {
        return Optional.ofNullable(appExportApp)
                .map(ObjectUtils::<AppExportApp>cast)
                .map(s -> AppBuilderAppPo.builder()
                        .name(s.getName())
                        .type(s.getType())
                        .appBuiltType(s.getAppBuiltType())
                        .version(s.getVersion())
                        .attributes(JsonUtils.toJsonString(s.getAttributes()))
                        .appCategory(s.getAppCategory())
                        .appType(s.getAppType())
                        .build())
                .orElse(null);
    }
}
