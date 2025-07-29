/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.appversion.serializer;

import modelengine.fit.jober.aipp.converters.IconConverter;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.AppVersionFactory;
import modelengine.fit.jober.aipp.domains.appversion.repository.AppVersionRepository;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.serializer.BaseSerializer;
import modelengine.fit.jober.aipp.util.JsonUtils;

import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Optional;

/**
 * 应用版本序列化与反序列化实现类
 *
 * @author 张越
 * @since 2025-01-14
 */
@AllArgsConstructor
public class AppVersionSerializer implements BaseSerializer<AppVersion, AppBuilderAppPo> {
    private final AppVersionFactory factory;
    private final AppVersionRepository appVersionRepository;
    private final IconConverter iconConverter;

    @Override
    public AppBuilderAppPo serialize(AppVersion appVersion) {
        return Optional.ofNullable(appVersion)
                .map(av -> {
                    AppBuilderAppPo data = appVersion.getData();
                    Map<String, Object> attrs = appVersion.getAttributes();
                    attrs.computeIfPresent("icon", (k, v) -> this.iconConverter.toStorage(String.valueOf(v)));
                    data.setAttributes(JsonUtils.toJsonString(attrs));
                    return data;
                })
                .orElseGet(() -> AppBuilderAppPo.builder().build());
    }

    @Override
    public AppVersion deserialize(AppBuilderAppPo dataObject) {
        return this.factory.create(dataObject, this.appVersionRepository);
    }
}
