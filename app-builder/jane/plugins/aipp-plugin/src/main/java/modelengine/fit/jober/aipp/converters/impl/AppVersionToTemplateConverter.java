/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.converters.impl;

import modelengine.fit.jober.aipp.converters.EntityConverter;
import modelengine.fit.jober.aipp.domain.AppTemplate;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Optional;

/**
 * {@link AppVersion} -> {@link AppTemplate}.
 *
 * @author 张越
 * @since 2025-02-18
 */
@Component
public class AppVersionToTemplateConverter implements EntityConverter {
    @Override
    public Class<AppVersion> source() {
        return AppVersion.class;
    }

    @Override
    public Class<AppTemplate> target() {
        return AppTemplate.class;
    }

    @Override
    public AppTemplate convert(Object appVersion) {
        return Optional.ofNullable(appVersion)
                .map(ObjectUtils::<AppVersion>cast)
                .map(appVersionObj -> AppTemplate.builder()
                        .id(appVersionObj.getData().getId())
                        .name(appVersionObj.getData().getName())
                        .builtType(appVersionObj.getData().getAppBuiltType())
                        .appType(appVersionObj.getData().getAppType())
                        .category(appVersionObj.getData().getAppCategory())
                        .attributes(appVersionObj.getAttributes())
                        .like(0)
                        .collection(0)
                        .usage(0)
                        .version("1.0.0")
                        .configId(appVersionObj.getData().getConfigId())
                        .flowGraphId(appVersionObj.getData().getFlowGraphId())
                        .createBy(appVersionObj.getData().getCreateBy())
                        .createAt(appVersionObj.getData().getCreateAt())
                        .updateBy(appVersionObj.getData().getUpdateBy())
                        .updateAt(appVersionObj.getData().getUpdateAt())
                        .config(appVersionObj.getConfig())
                        .flowGraph(appVersionObj.getFlowGraph())
                        .formProperties(appVersionObj.getFormProperties())
                        .build())
                .orElse(null);
    }
}
