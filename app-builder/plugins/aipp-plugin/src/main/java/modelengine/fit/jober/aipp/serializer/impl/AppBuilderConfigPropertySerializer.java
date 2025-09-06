/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.serializer.impl;

import modelengine.fit.jober.aipp.domain.AppBuilderConfigProperty;
import modelengine.fit.jober.aipp.po.AppBuilderConfigPropertyPo;
import modelengine.fit.jober.aipp.serializer.BaseSerializer;

/**
 * 应用属性表单序列化与反序列化实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
public class AppBuilderConfigPropertySerializer
        implements BaseSerializer<AppBuilderConfigProperty, AppBuilderConfigPropertyPo> {
    @Override
    public AppBuilderConfigPropertyPo serialize(AppBuilderConfigProperty appBuilderConfigProperty) {
        if (appBuilderConfigProperty == null) {
            return null;
        }
        return AppBuilderConfigPropertyPo.builder()
                .id(appBuilderConfigProperty.getId())
                .nodeId(appBuilderConfigProperty.getNodeId())
                .formPropertyId(appBuilderConfigProperty.getFormPropertyId())
                .configId(appBuilderConfigProperty.getConfigId())
                .build();
    }

    @Override
    public AppBuilderConfigProperty deserialize(AppBuilderConfigPropertyPo appBuilderConfigPropertyPO) {
        if (appBuilderConfigPropertyPO == null) {
            return null;
        }
        return AppBuilderConfigProperty.builder()
                .id(appBuilderConfigPropertyPO.getId())
                .nodeId(appBuilderConfigPropertyPO.getNodeId())
                .formPropertyId(appBuilderConfigPropertyPO.getFormPropertyId())
                .configId(appBuilderConfigPropertyPO.getConfigId())
                .build();
    }
}
