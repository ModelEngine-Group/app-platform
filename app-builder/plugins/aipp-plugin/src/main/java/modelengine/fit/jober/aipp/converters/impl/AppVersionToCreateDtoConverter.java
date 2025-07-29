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
import modelengine.fit.jober.aipp.dto.AppBuilderAppCreateDto;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Optional;

/**
 * {@link AppVersion} -> {@link AppBuilderAppCreateDto}.
 *
 * @author 张越
 * @since 2025-02-18
 */
@Component
@RequiredArgsConstructor
public class AppVersionToCreateDtoConverter implements EntityConverter {
    private final IconConverter iconConverter;

    @Override
    public Class<AppVersion> source() {
        return AppVersion.class;
    }

    @Override
    public Class<AppBuilderAppCreateDto> target() {
        return AppBuilderAppCreateDto.class;
    }

    @Override
    public AppBuilderAppCreateDto convert(Object appVersion) {
        return Optional.ofNullable(appVersion)
                .map(ObjectUtils::<AppVersion>cast)
                .map(s -> AppBuilderAppCreateDto.builder()
                        .name(s.getData().getName())
                        .description(s.getDescription())
                        .icon(this.iconConverter.toFrontend(s.getIcon()))
                        .greeting(s.getGreeting())
                        .appType(s.getData().getAppType())
                        .type(s.getData().getType())
                        .storeId(s.getData().getUniqueName())
                        .appBuiltType(s.getData().getAppBuiltType())
                        .appCategory(s.getData().getAppCategory())
                        .build())
                .orElse(null);
    }
}
