/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.serializer.impl;

import lombok.RequiredArgsConstructor;
import modelengine.fit.jober.aipp.converters.IconConverter;
import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.serializer.BaseSerializer;
import modelengine.fit.jober.aipp.util.JsonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 应用数据序列化与反序列化实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
@RequiredArgsConstructor
public class AppBuilderAppSerializer implements BaseSerializer<AppBuilderApp, AppBuilderAppPo> {
    private final IconConverter iconConverter;

    @Override
    public AppBuilderAppPo serialize(AppBuilderApp appBuilderApp) {
        if (appBuilderApp == null) {
            return null;
        }
        return AppBuilderAppPo.builder()
                .id(appBuilderApp.getId())
                .name(appBuilderApp.getName())
                .appId(appBuilderApp.getAppId())
                .appSuiteId(appBuilderApp.getAppSuiteId())
                .tenantId(appBuilderApp.getTenantId())
                .configId(appBuilderApp.getConfigId())
                .flowGraphId(appBuilderApp.getFlowGraphId())
                .type(appBuilderApp.getType())
                .version(appBuilderApp.getVersion())
                .attributes(JsonUtils.toJsonString(appBuilderApp.getAttributes()
                        .computeIfPresent("icon", (k, v) -> this.iconConverter.toStorage(String.valueOf(v)))))
                .path(appBuilderApp.getPath())
                .state(appBuilderApp.getState())
                .appType(appBuilderApp.getAppType())
                .appBuiltType(appBuilderApp.getAppBuiltType())
                .appCategory(appBuilderApp.getAppCategory())
                .createAt(appBuilderApp.getCreateAt())
                .updateAt(appBuilderApp.getUpdateAt())
                .createBy(appBuilderApp.getCreateBy())
                .updateBy(appBuilderApp.getUpdateBy())
                .isActive(appBuilderApp.getIsActive())
                .status(appBuilderApp.getStatus())
                .uniqueName(appBuilderApp.getUniqueName())
                .publishAt(appBuilderApp.getPublishAt())
                .build();
    }

    @Override
    public AppBuilderApp deserialize(AppBuilderAppPo appBuilderAppPO) {
        if (appBuilderAppPO == null) {
            return AppBuilderApp.builder().build();
        }
        Map<String, Object> attributes = this.modifyIconValue(appBuilderAppPO);
        return AppBuilderApp.builder()
                        .id(appBuilderAppPO.getId())
                        .name(appBuilderAppPO.getName())
                        .appId(appBuilderAppPO.getAppId())
                        .appSuiteId(appBuilderAppPO.getAppSuiteId())
                        .tenantId(appBuilderAppPO.getTenantId())
                        .configId(appBuilderAppPO.getConfigId())
                        .flowGraphId(appBuilderAppPO.getFlowGraphId())
                        .type(appBuilderAppPO.getType())
                        .version(appBuilderAppPO.getVersion())
                        .attributes(attributes)
                        .path(appBuilderAppPO.getPath())
                        .state(appBuilderAppPO.getState())
                        .appType(appBuilderAppPO.getAppType())
                        .appBuiltType(appBuilderAppPO.getAppBuiltType())
                        .appCategory(appBuilderAppPO.getAppCategory())
                        .createAt(appBuilderAppPO.getCreateAt())
                        .updateAt(appBuilderAppPO.getUpdateAt())
                        .createBy(appBuilderAppPO.getCreateBy())
                        .updateBy(appBuilderAppPO.getUpdateBy())
                        .isActive(appBuilderAppPO.getIsActive())
                        .status(appBuilderAppPO.getStatus())
                        .uniqueName(appBuilderAppPO.getUniqueName())
                        .publishAt(appBuilderAppPO.getPublishAt())
                        .build();
    }

    private Map<String, Object> modifyIconValue(AppBuilderAppPo appBuilderAppPO) {
        Map<String, Object> attributes = Optional.ofNullable(appBuilderAppPO.getAttributes())
                .map(JsonUtils::parseObject)
                .orElseGet(HashMap::new);
        attributes.computeIfPresent("icon", (k, v) -> this.iconConverter.toFrontend(String.valueOf(v)));
        return attributes;
    }
}
