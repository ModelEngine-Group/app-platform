/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.serializer.impl;

import modelengine.fit.jober.aipp.domain.AppBuilderFormProperty;
import modelengine.fit.jober.aipp.enums.FormPropertyTypeEnum;
import modelengine.fit.jober.aipp.po.AppBuilderFormPropertyPo;
import modelengine.fit.jober.aipp.serializer.BaseSerializer;
import modelengine.fit.jober.aipp.util.JsonUtils;

/**
 * 应用表单属性序列化与反序列化实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
public class AppBuilderFormPropertySerializer
        implements BaseSerializer<AppBuilderFormProperty, AppBuilderFormPropertyPo> {
    @Override
    public AppBuilderFormPropertyPo serialize(AppBuilderFormProperty appBuilderFormProperty) {
        if (appBuilderFormProperty == null) {
            return null;
        }
        return AppBuilderFormPropertyPo.builder()
                .id(appBuilderFormProperty.getId())
                .formId(appBuilderFormProperty.getFormId())
                .name(appBuilderFormProperty.getName())
                .dataType(appBuilderFormProperty.getDataType())
                .defaultValue(JsonUtils.toJsonString(appBuilderFormProperty.getDefaultValue()))
                .from(appBuilderFormProperty.getFrom())
                .group(appBuilderFormProperty.getGroup())
                .description(appBuilderFormProperty.getDescription())
                .index(appBuilderFormProperty.getIndex())
                .appId(appBuilderFormProperty.getAppId())
                .build();
    }

    @Override
    public AppBuilderFormProperty deserialize(AppBuilderFormPropertyPo appBuilderFormPropertyPO) {
        if (appBuilderFormPropertyPO == null) {
            return null;
        }
        return AppBuilderFormProperty.builder()
                .id(appBuilderFormPropertyPO.getId())
                .formId(appBuilderFormPropertyPO.getFormId())
                .name(appBuilderFormPropertyPO.getName())
                .dataType(appBuilderFormPropertyPO.getDataType())
                .defaultValue(this.getDefaultValue(appBuilderFormPropertyPO))
                .from(appBuilderFormPropertyPO.getFrom())
                .group(appBuilderFormPropertyPO.getGroup())
                .description(appBuilderFormPropertyPO.getDescription())
                .index(appBuilderFormPropertyPO.getIndex())
                .appId(appBuilderFormPropertyPO.getAppId())
                .build();
    }

    private Object getDefaultValue(AppBuilderFormPropertyPo po) {
        return JsonUtils.parseObject(po.getDefaultValue(), FormPropertyTypeEnum.getClazz(po.getDataType()));
    }
}
