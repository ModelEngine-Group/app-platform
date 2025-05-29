/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.converters.impl;

import modelengine.fit.jober.aipp.converters.EntityConverter;
import modelengine.fit.jober.aipp.domain.AppBuilderConfig;
import modelengine.fit.jober.aipp.domain.AppBuilderConfigProperty;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.dto.export.AppExportConfig;
import modelengine.fit.jober.aipp.dto.export.AppExportConfigProperty;
import modelengine.fit.jober.aipp.dto.export.AppExportForm;
import modelengine.fit.jober.aipp.dto.export.AppExportFormProperty;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link AppBuilderConfig} -> {@link AppExportConfig}.
 *
 * @author 张越
 * @since 2025-02-14
 */
@Component
public class AppConfigToExportConfigConverter implements EntityConverter {
    @Override
    public Class<AppBuilderConfig> source() {
        return AppBuilderConfig.class;
    }

    @Override
    public Class<AppExportConfig> target() {
        return AppExportConfig.class;
    }

    @Override
    public AppExportConfig convert(Object config) {
        return Optional.ofNullable(config).map(ObjectUtils::<AppBuilderConfig>cast).map(this::convert0).orElse(null);
    }

    private AppExportConfig convert0(AppBuilderConfig s) {
        List<AppBuilderConfigProperty> configProperties = s.getConfigProperties();
        List<AppExportConfigProperty> exportProperties = configProperties.stream()
                .map(cp -> this.buildExportConfigProperty(cp, s.getAppVersion()))
                .collect(Collectors.toList());
        return AppExportConfig.builder()
                .form(this.buildExportForm(s.getForm()))
                .configProperties(exportProperties)
                .build();
    }

    private AppExportForm buildExportForm(AppBuilderForm appBuilderForm) {
        return AppExportForm.builder()
                .id(appBuilderForm.getId())
                .name(appBuilderForm.getName())
                .appearance(appBuilderForm.getAppearance())
                .type(appBuilderForm.getType())
                .formSuiteId(appBuilderForm.getFormSuiteId())
                .version(appBuilderForm.getVersion())
                .build();
    }

    private AppExportConfigProperty buildExportConfigProperty(AppBuilderConfigProperty configProperty,
            AppVersion appVersion) {
        AppBuilderFormProperty appBuilderFormProperty = appVersion.getFormProperty(configProperty.getFormPropertyId());
        return AppExportConfigProperty.builder()
                .nodeId(configProperty.getNodeId())
                .formProperty(this.buildExportFormProperty(appBuilderFormProperty))
                .build();
    }

    private AppExportFormProperty buildExportFormProperty(AppBuilderFormProperty appBuilderFormProperty) {
        return AppExportFormProperty.builder()
                .name(appBuilderFormProperty.getName())
                .dataType(appBuilderFormProperty.getDataType())
                .defaultValue(JsonUtils.toJsonString(appBuilderFormProperty.getDefaultValue()))
                .from(appBuilderFormProperty.getFrom())
                .group(appBuilderFormProperty.getGroup())
                .description(appBuilderFormProperty.getDescription())
                .index(appBuilderFormProperty.getIndex())
                .build();
    }
}
