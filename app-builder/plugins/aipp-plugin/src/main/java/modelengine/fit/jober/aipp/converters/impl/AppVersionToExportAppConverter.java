/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.converters.impl;

import lombok.RequiredArgsConstructor;
import modelengine.fit.jober.aipp.converters.EntityConverter;
import modelengine.fit.jober.aipp.converters.IconConverter;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.dto.export.AppExportApp;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * {@link AppVersion} -> {@link AppExportApp}.
 *
 * @author 张越
 * @since 2025-02-14
 */
@Component
@RequiredArgsConstructor
public class AppVersionToExportAppConverter implements EntityConverter {
    private final IconConverter iconConverter;

    @Override
    public Class<AppVersion> source() {
        return AppVersion.class;
    }

    @Override
    public Class<AppExportApp> target() {
        return AppExportApp.class;
    }

    @Override
    public AppExportApp convert(Object appVersion) {
        return Optional.ofNullable(appVersion).map(ObjectUtils::<AppVersion>cast).map(s -> {
            AppBuilderAppPo appBuilderAppPo = s.getData();
            Map<String, Object> attributes = Optional.ofNullable(appBuilderAppPo.getAttributes())
                    .map(JsonUtils::parseObject)
                    .orElseGet(HashMap::new);
            attributes.computeIfPresent("icon",
                    (k, v) -> this.iconConverter.toFrontend(String.valueOf(v)));
            return AppExportApp.builder()
                    .name(appBuilderAppPo.getName())
                    .tenantId(appBuilderAppPo.getTenantId())
                    .type(appBuilderAppPo.getType())
                    .appBuiltType(appBuilderAppPo.getAppBuiltType())
                    .version(appBuilderAppPo.getVersion())
                    .attributes(attributes)
                    .appCategory(appBuilderAppPo.getAppCategory())
                    .appType(appBuilderAppPo.getAppType())
                    .build();
        }).orElse(null);
    }
}
