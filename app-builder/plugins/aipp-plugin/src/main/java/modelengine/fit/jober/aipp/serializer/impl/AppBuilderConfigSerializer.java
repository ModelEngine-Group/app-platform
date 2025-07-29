/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.serializer.impl;

import modelengine.fit.jober.aipp.domain.AppBuilderConfig;
import modelengine.fit.jober.aipp.po.AppBuilderConfigPo;
import modelengine.fit.jober.aipp.serializer.BaseSerializer;

/**
 * 应用属性序列化与反序列化实现类
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
public class AppBuilderConfigSerializer implements BaseSerializer<AppBuilderConfig, AppBuilderConfigPo> {
    @Override
    public AppBuilderConfigPo serialize(AppBuilderConfig appBuilderConfig) {
        if (appBuilderConfig == null) {
            return null;
        }
        return AppBuilderConfigPo.builder()
                .id(appBuilderConfig.getId())
                .formId(appBuilderConfig.getFormId())
                .tenantId(appBuilderConfig.getTenantId())
                .appId(appBuilderConfig.getAppId())
                .createAt(appBuilderConfig.getCreateAt())
                .updateAt(appBuilderConfig.getUpdateAt())
                .createBy(appBuilderConfig.getCreateBy())
                .updateBy(appBuilderConfig.getUpdateBy())
                .build();
    }

    @Override
    public AppBuilderConfig deserialize(AppBuilderConfigPo appBuilderConfigPO) {
        if (appBuilderConfigPO == null) {
            return null;
        }
        return AppBuilderConfig.builder()
                .id(appBuilderConfigPO.getId())
                .formId(appBuilderConfigPO.getFormId())
                .tenantId(appBuilderConfigPO.getTenantId())
                .appId(appBuilderConfigPO.getAppId())
                .createAt(appBuilderConfigPO.getCreateAt())
                .updateAt(appBuilderConfigPO.getUpdateAt())
                .createBy(appBuilderConfigPO.getCreateBy())
                .updateBy(appBuilderConfigPO.getUpdateBy())
                .build();
    }
}
