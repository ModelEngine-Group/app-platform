/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.mapper;

import modelengine.fit.jober.aipp.po.AippSystemConfigPo;

import org.apache.ibatis.annotations.Param;

/**
 * 系统配置Mapper
 *
 * @author 张越
 * @since 2024-11-30
 */
public interface AippSystemConfigMapper {
    /**
     * 通过group和key查找配置.
     *
     * @param group 分组.
     * @param key 键值.
     * @return {@link AippSystemConfigPo} 对象.
     */
    AippSystemConfigPo findOne(@Param("configGroup") String group, @Param("configKey") String key);
}