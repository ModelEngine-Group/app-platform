/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository;

import modelengine.fit.jober.aipp.domain.AppBuilderComponent;

/**
 * AppBuilder组件持久化层
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
public interface AppBuilderComponentRepository {
    /**
     * 通过组件id查询组件
     *
     * @param id 要查询的组件的id
     * @return 组件结构体
     */
    AppBuilderComponent selectWithId(String id);
}
