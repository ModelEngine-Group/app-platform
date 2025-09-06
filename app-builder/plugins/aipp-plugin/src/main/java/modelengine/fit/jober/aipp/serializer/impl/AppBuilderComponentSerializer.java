/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.serializer.impl;

import modelengine.fit.jober.aipp.domain.AppBuilderComponent;
import modelengine.fit.jober.aipp.po.AppBuilderComponentPo;
import modelengine.fit.jober.aipp.serializer.BaseSerializer;

/**
 * 应用属性组件序列化与反序列化实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
public class AppBuilderComponentSerializer implements BaseSerializer<AppBuilderComponent, AppBuilderComponentPo> {
    @Override
    public AppBuilderComponentPo serialize(AppBuilderComponent appBuilderComponent) {
        if (appBuilderComponent == null) {
            return null;
        }
        return AppBuilderComponentPo.builder()
                .id(appBuilderComponent.getId())
                .name(appBuilderComponent.getName())
                .type(appBuilderComponent.getType())
                .description(appBuilderComponent.getDescription())
                .formId(appBuilderComponent.getFormId())
                .serviceId(appBuilderComponent.getServiceId())
                .tenantId(appBuilderComponent.getTenantId())
                .createAt(appBuilderComponent.getCreateAt())
                .updateAt(appBuilderComponent.getUpdateAt())
                .createBy(appBuilderComponent.getCreateBy())
                .updateBy(appBuilderComponent.getUpdateBy())
                .build();
    }

    @Override
    public AppBuilderComponent deserialize(AppBuilderComponentPo appBuilderComponentPO) {
        if (appBuilderComponentPO == null) {
            return null;
        }
        return AppBuilderComponent.builder()
                .id(appBuilderComponentPO.getId())
                .name(appBuilderComponentPO.getName())
                .type(appBuilderComponentPO.getType())
                .description(appBuilderComponentPO.getDescription())
                .formId(appBuilderComponentPO.getFormId())
                .serviceId(appBuilderComponentPO.getServiceId())
                .tenantId(appBuilderComponentPO.getTenantId())
                .createAt(appBuilderComponentPO.getCreateAt())
                .updateAt(appBuilderComponentPO.getUpdateAt())
                .createBy(appBuilderComponentPO.getCreateBy())
                .updateBy(appBuilderComponentPO.getUpdateBy())
                .build();
    }
}
