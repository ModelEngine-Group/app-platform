/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.serializer.impl;

import modelengine.fit.jober.aipp.domain.AippSystemConfig;
import modelengine.fit.jober.aipp.po.AippSystemConfigPo;
import modelengine.fit.jober.aipp.serializer.BaseSerializer;

/**
 * 系统配置序列化与反序列化实现类
 *
 * @author 张越
 * @since 2024-11-30
 */
public class AippSystemConfigSerializer implements BaseSerializer<AippSystemConfig, AippSystemConfigPo> {
    @Override
    public AippSystemConfigPo serialize(AippSystemConfig aippSystemConfig) {
        if (aippSystemConfig == null) {
            return null;
        }
        return AippSystemConfigPo.builder()
                .id(aippSystemConfig.getId())
                .configKey(aippSystemConfig.getConfigKey())
                .configValue(aippSystemConfig.getConfigValue())
                .configGroup(aippSystemConfig.getConfigGroup())
                .configParent(aippSystemConfig.getConfigParent())
                .build();
    }

    @Override
    public AippSystemConfig deserialize(AippSystemConfigPo aippSystemConfigPo) {
        if (aippSystemConfigPo == null) {
            return null;
        }
        return AippSystemConfig.builder()
                .id(aippSystemConfigPo.getId())
                .configKey(aippSystemConfigPo.getConfigKey())
                .configValue(aippSystemConfigPo.getConfigValue())
                .configGroup(aippSystemConfigPo.getConfigGroup())
                .configParent(aippSystemConfigPo.getConfigParent())
                .build();
    }
}
