/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.converters.impl;

import lombok.RequiredArgsConstructor;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.converters.EntityConverter;
import modelengine.fit.jober.aipp.converters.IconConverter;
import modelengine.fit.jober.aipp.domain.AppBuilderConfig;
import modelengine.fit.jober.aipp.domain.AppBuilderFlowGraph;
import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigFormDto;
import modelengine.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto;
import modelengine.fit.jober.aipp.dto.AppBuilderFlowGraphDto;
import modelengine.fit.jober.aipp.util.JsonUtils;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * {@link AppVersion} -> {@link AppBuilderAppDto}.
 *
 * @author 张越
 * @since 2025-02-18
 */
@Component
@RequiredArgsConstructor
public class AppVersionToAppDtoConverter implements EntityConverter {
    private static final String FORM_PROPERTY_GROUP_NULL = "null";
    private final IconConverter iconConverter;

    @Value("${app-engine.chat-path.format}")
    private String chatPathFormat;

    @Override
    public Class<AppVersion> source() {
        return AppVersion.class;
    }

    @Override
    public Class<AppBuilderAppDto> target() {
        return AppBuilderAppDto.class;
    }

    @Override
    public AppBuilderAppDto convert(Object appVersion) {
        return Optional.ofNullable(appVersion).map(ObjectUtils::<AppVersion>cast).map(s -> {
            Map<String, Object> attributes = new HashMap<>(s.getAttributes());
            attributes.computeIfPresent("icon",
                    (k, v) -> this.iconConverter.toFrontend(String.valueOf(v)));
            AppBuilderAppDto.AppBuilderAppDtoBuilder appDtoBuilder = AppBuilderAppDto.builder()
                    .id(s.getData().getId())
                    .name(s.getData().getName())
                    .type(s.getData().getType())
                    .state(s.getData().getState())
                    .attributes(attributes)
                    .appType(s.getData().getAppType())
                    .version(s.getData().getVersion())
                    .appCategory(s.getData().getAppCategory())
                    .createBy(s.getData().getCreateBy())
                    .updateBy(s.getData().getUpdateBy())
                    .createAt(s.getData().getCreateAt())
                    .updateAt(s.getData().getUpdateAt())
                    .appBuiltType(s.getData().getAppBuiltType())
                    .config(this.buildAppBuilderConfig(s.getConfig()))
                    .flowGraph(this.buildFlowGraph(s.getFlowGraph()))
                    .aippId(s.getData().getAppSuiteId())
                    .configFormProperties(this.buildConfigFormProperties(s.getFormProperties()));
            Optional.ofNullable(s.getData().getPath())
                    .filter(path -> !path.isEmpty())
                    .ifPresent(path -> appDtoBuilder.chatUrl(String.format(this.chatPathFormat, path)));
            return appDtoBuilder.build();
        }).orElse(null);
    }

    private AppBuilderFlowGraphDto buildFlowGraph(AppBuilderFlowGraph flowGraph) {
        return AppBuilderFlowGraphDto.builder()
                .id(flowGraph.getId())
                .name(flowGraph.getName())
                .appearance(JsonUtils.parseObject(flowGraph.getAppearance()))
                .createBy(flowGraph.getCreateBy())
                .updateBy(flowGraph.getUpdateBy())
                .createAt(flowGraph.getCreateAt())
                .updateAt(flowGraph.getUpdateAt())
                .build();
    }

    private AppBuilderConfigDto buildAppBuilderConfig(AppBuilderConfig config) {
        return AppBuilderConfigDto.builder()
                .id(config.getId())
                .tenantId(config.getTenantId())
                .createBy(config.getCreateBy())
                .updateBy(config.getUpdateBy())
                .createAt(config.getCreateAt())
                .updateAt(config.getUpdateAt())
                .form(buildAppBuilderConfigFormDto(config))
                .build();
    }

    private AppBuilderConfigFormDto buildAppBuilderConfigFormDto(AppBuilderConfig config) {
        Validation.notNull(config.getForm(), "Form can not be null.");
        return AppBuilderConfigFormDto.builder()
                .id(config.getFormId())
                .name(config.getForm().getName())
                .appearance(config.getForm().getAppearance())
                .build();
    }

    private List<AppBuilderConfigFormPropertyDto> buildConfigFormProperties(
            List<AppBuilderFormProperty> formProperties) {
        LinkedHashMap<String, AppBuilderConfigFormPropertyDto> formPropertyMapping = formProperties.stream()
                .map(AppBuilderFormProperty::toAppBuilderConfigFormPropertyDto)
                .collect(Collectors.toMap(AppBuilderConfigFormPropertyDto::getName, Function.identity(), (k1, k2) -> k1,
                        LinkedHashMap::new));
        String root = "";
        for (Map.Entry<String, AppBuilderConfigFormPropertyDto> entry : formPropertyMapping.entrySet()) {
            AppBuilderConfigFormPropertyDto dto = entry.getValue();
            String group = entry.getValue().getGroup();
            if (group.equals(FORM_PROPERTY_GROUP_NULL)) {
                root = dto.getName();
            } else {
                group = dto.getGroup();
                AppBuilderConfigFormPropertyDto parent = formPropertyMapping.get(group);
                if (parent == null) {
                    throw new AippException(AippErrCode.FORM_PROPERTY_PARENT_NOT_EXIST);
                }
                parent.addChild(dto);
            }
        }
        AppBuilderConfigFormPropertyDto rootProperty = formPropertyMapping.get(root);
        return rootProperty == null ? Collections.emptyList() : Collections.singletonList(rootProperty);
    }
}
