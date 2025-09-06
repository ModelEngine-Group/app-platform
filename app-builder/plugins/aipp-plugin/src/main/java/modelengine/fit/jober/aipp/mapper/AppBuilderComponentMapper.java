/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.mapper;

import modelengine.fit.jober.aipp.po.AppBuilderComponentPo;

/**
 * AppBuilder组件映射器
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
public interface AppBuilderComponentMapper {
    /**
     * 通过id查询AppBuilder组件
     *
     * @param id 组件id
     * @return AppBuilder组件信息
     */
    AppBuilderComponentPo selectWithId(String id);
}
