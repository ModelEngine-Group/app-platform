/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.serializer.impl;

import lombok.RequiredArgsConstructor;
import modelengine.fit.jober.aipp.converters.IconConverter;
import modelengine.fit.jober.aipp.domain.AppTemplate;
import modelengine.fit.jober.aipp.po.AppTemplatePo;
import modelengine.fit.jober.aipp.serializer.BaseSerializer;
import modelengine.fit.jober.aipp.util.JsonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 应用模板领域数据与存储数据转换工具。
 *
 * @author 方誉州
 * @since 2025-01-02
 */
@RequiredArgsConstructor
public class AppTemplateSerializer implements BaseSerializer<AppTemplate, AppTemplatePo> {
    private final IconConverter iconConverter;

    @Override
    public AppTemplatePo serialize(AppTemplate appTemplate) {
        if (appTemplate == null) {
            return null;
        }
        return AppTemplatePo.builder()
                .id(appTemplate.getId())
                .name(appTemplate.getName())
                .builtType(appTemplate.getBuiltType())
                .category(appTemplate.getCategory())
                .attributes(JsonUtils.toJsonString(appTemplate.getAttributes()
                        .computeIfPresent("icon", (k, v) -> this.iconConverter.toStorage(String.valueOf(v)))))
                .appType(appTemplate.getAppType())
                .like(appTemplate.getLike())
                .collection(appTemplate.getCollection())
                .usage(appTemplate.getUsage())
                .version(appTemplate.getVersion())
                .configId(appTemplate.getConfigId())
                .flowGraphId(appTemplate.getFlowGraphId())
                .createBy(appTemplate.getCreateBy())
                .createAt(appTemplate.getCreateAt())
                .updateBy(appTemplate.getUpdateBy())
                .updateAt(appTemplate.getUpdateAt())
                .build();
    }

    @Override
    public AppTemplate deserialize(AppTemplatePo dataObject) {
        if (dataObject == null) {
            return AppTemplate.builder().build();
        }
        Map<String, Object> attributes = this.modifyIconValue(dataObject);
        return AppTemplate.builder()
                .id(dataObject.getId())
                .name(dataObject.getName())
                .builtType(dataObject.getBuiltType())
                .category(dataObject.getCategory())
                .attributes(attributes)
                .appType(dataObject.getAppType())
                .like(dataObject.getLike())
                .collection(dataObject.getCollection())
                .usage(dataObject.getUsage())
                .version(dataObject.getVersion())
                .configId(dataObject.getConfigId())
                .flowGraphId(dataObject.getFlowGraphId())
                .createBy(dataObject.getCreateBy())
                .createAt(dataObject.getCreateAt())
                .updateBy(dataObject.getUpdateBy())
                .updateAt(dataObject.getUpdateAt())
                .build();
    }

    private Map<String, Object> modifyIconValue(AppTemplatePo appTemplatePo) {
        Map<String, Object> attributes = Optional.ofNullable(appTemplatePo.getAttributes())
                .map(JsonUtils::parseObject)
                .orElseGet(HashMap::new);
        attributes.computeIfPresent("icon", (k, v) -> this.iconConverter.toFrontend(String.valueOf(v)));
        return attributes;
    }
}
