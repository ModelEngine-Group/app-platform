/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.serializer.impl;

import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.po.AppBuilderFormPo;
import modelengine.fit.jober.aipp.serializer.BaseSerializer;
import modelengine.fit.jober.aipp.util.JsonUtils;

import java.util.Map;

/**
 * 应用表单序列化与反序列化实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
public class AppBuilderFormSerializer implements BaseSerializer<AppBuilderForm, AppBuilderFormPo> {
    @Override
    public AppBuilderFormPo serialize(AppBuilderForm appBuilderForm) {
        if (appBuilderForm == null) {
            return null;
        }
        return AppBuilderFormPo.builder()
                .id(appBuilderForm.getId())
                .name(appBuilderForm.getName())
                .tenantId(appBuilderForm.getTenantId())
                .appearance(JsonUtils.toJsonString(appBuilderForm.getAppearance()))
                .type(appBuilderForm.getType())
                .createAt(appBuilderForm.getCreateAt())
                .updateAt(appBuilderForm.getUpdateAt())
                .createBy(appBuilderForm.getCreateBy())
                .updateBy(appBuilderForm.getUpdateBy())
                .version(appBuilderForm.getVersion())
                .formSuiteId(appBuilderForm.getFormSuiteId())
                .build();
    }

    @Override
    public AppBuilderForm deserialize(AppBuilderFormPo appBuilderFormPO) {
        if (appBuilderFormPO == null) {
            return null;
        }
        Map<String, Object> appearance = JsonUtils.parseObject(appBuilderFormPO.getAppearance());
        return AppBuilderForm.builder()
                .id(appBuilderFormPO.getId())
                .name(appBuilderFormPO.getName())
                .tenantId(appBuilderFormPO.getTenantId())
                .appearance(appearance)
                .type(appBuilderFormPO.getType())
                .createAt(appBuilderFormPO.getCreateAt())
                .updateAt(appBuilderFormPO.getUpdateAt())
                .createBy(appBuilderFormPO.getCreateBy())
                .updateBy(appBuilderFormPO.getUpdateBy())
                .version(appBuilderFormPO.getVersion())
                .formSuiteId(appBuilderFormPO.getFormSuiteId())
                .build();
    }
}
