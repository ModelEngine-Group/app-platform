/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.dto.AippCreateDto;
import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.genericable.entity.AippCreate;

import modelengine.fit.appbuilder.security.util.XssUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * 转换工具类
 *
 * @author 邬涨财
 * @since 2024-04-20
 */
public class ConvertUtils {
    /**
     * 把{@link AppBuilderAppDto}实例转换为{@link AippDto}实例 后续是否可以把 aipp和 app结合在一起
     *
     * @param appDto {@link AppBuilderAppDto}对象
     * @return {@link AippDto}实例
     */
    public static AippDto convertToAippDtoFromAppBuilderAppDto(AppBuilderAppDto appDto) {
        Map<String, Object> attributes = appDto.getAttributes();
        String description = String.valueOf(attributes.getOrDefault("description", StringUtils.EMPTY));
        String icon = String.valueOf(attributes.getOrDefault("icon", StringUtils.EMPTY));
        return AippDto.builder()
                .name(appDto.getName())
                .description(description)
                .flowViewData(appDto.getFlowGraph().getAppearance())
                .publishUrl(appDto.getPublishUrl())
                .icon(icon)
                .appId(appDto.getId())
                .version(appDto.getVersion())
                .type(appDto.getType())
                .appType(appDto.getAppType())
                .publishedDescription(appDto.getPublishedDescription())
                .publishedUpdateLog(XssUtils.filter(appDto.getPublishedUpdateLog()))
                .appCategory(appDto.getAppCategory())
                .build();
    }

    /**
     * 将app转换为aippDto
     *
     * @param app 待转换的app
     * @return aippDTO
     */
    public static AippDto convertToAippDtoFromAppBuilderApp(AppBuilderApp app) {
        String description = String.valueOf(app.getAttributes().getOrDefault("description", StringUtils.EMPTY));
        String icon = String.valueOf(app.getAttributes().getOrDefault("icon", StringUtils.EMPTY));
        return AippDto.builder()
                .name(app.getName())
                .description(description)
                .icon(icon)
                .appId(app.getId())
                .version(app.getVersion())
                .type(app.getType())
                .appCategory(app.getAppCategory())
                .build();
    }

    /**
     * 将 {@link AippCreateDto} 类转化成 {@link AippCreate}。
     *
     * @param aippCreateDto 表示创建 Aipp 响应体的 {@link AippCreateDto}。
     * @return 表示转化后的创建 Aipp 响应体实体类对象的 {@link AippCreate}。
     */
    public static AippCreate toAippCreate(AippCreateDto aippCreateDto) {
        return AippCreate.builder()
                .aippId(aippCreateDto.getAippId())
                .toolUniqueName(aippCreateDto.getToolUniqueName())
                .version(aippCreateDto.getVersion())
                .build();
    }

    /**
     * 将 {@link AippCreate} 类转化成 {@link AippCreateDto}。
     *
     * @param aippCreate 表示创建 Aipp 响应体的 {@link AippCreate}。
     * @return 表示转化后的创建 Aipp 响应体实体类对象的 {@link AippCreateDto}。
     */
    public static AippCreateDto toAippCreateDto(AippCreate aippCreate) {
        return AippCreateDto.builder()
                .aippId(aippCreate.getAippId())
                .toolUniqueName(aippCreate.getToolUniqueName())
                .version(aippCreate.getVersion())
                .appId(aippCreate.getAppId())
                .build();
    }

    /**
     * 将 {@link LocalDateTime} 类转化成 {@code long}。
     *
     * @param time 表示需要转换的 {@link LocalDateTime}。
     * @return 表示转换化后的 {@code long}。
     */
    public static long toLong(LocalDateTime time) {
        return time.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    }
}
