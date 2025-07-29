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
import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fit.jober.aipp.util.JsonUtils;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Optional;

/**
 * {@link AppVersion} -> {@link AippDto}.
 *
 * @author 张越
 * @since 2025-02-18
 */
@Component
@RequiredArgsConstructor
public class AppVersionToAippDtoConverter implements EntityConverter {
    private final IconConverter iconConverter;

    @Override
    public Class<AppVersion> source() {
        return AppVersion.class;
    }

    @Override
    public Class<AippDto> target() {
        return AippDto.class;
    }

    @Override
    public AippDto convert(Object appVersion) {
        return Optional.ofNullable(appVersion).map(ObjectUtils::<AppVersion>cast).map(s -> {
            String description = ObjectUtils.nullIf(s.getDescription(), StringUtils.EMPTY);
            String icon = this.iconConverter.toFrontend(ObjectUtils.nullIf(s.getIcon(), StringUtils.EMPTY));
            return AippDto.builder()
                    .name(s.getData().getName())
                    .description(description)
                    .flowViewData(JsonUtils.parseObject(s.getFlowGraph().getAppearance()))
                    .icon(icon)
                    .appId(s.getData().getId())
                    .version(s.getData().getVersion())
                    .type(s.getData().getType())
                    .appCategory(s.getData().getAppCategory())
                    .build();
        }).orElse(null);
    }
}
