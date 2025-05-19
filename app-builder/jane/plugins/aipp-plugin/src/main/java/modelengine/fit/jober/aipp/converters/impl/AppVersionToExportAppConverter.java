/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.converters.impl;

import modelengine.fit.jober.aipp.converters.EntityConverter;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.dto.export.AppExportApp;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Optional;

/**
 * {@link AppVersion} -> {@link AppExportApp}.
 *
 * @author 张越
 * @since 2025-02-14
 */
@Component
public class AppVersionToExportAppConverter implements EntityConverter {
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
            return AppExportApp.builder()
                    .name(appBuilderAppPo.getName())
                    .tenantId(appBuilderAppPo.getTenantId())
                    .type(appBuilderAppPo.getType())
                    .appBuiltType(appBuilderAppPo.getAppBuiltType())
                    .version(appBuilderAppPo.getVersion())
                    .attributes(JsonUtils.parseObject(appBuilderAppPo.getAttributes()))
                    .appCategory(appBuilderAppPo.getAppCategory())
                    .appType(appBuilderAppPo.getAppType())
                    .build();
        }).orElse(null);
    }
}
