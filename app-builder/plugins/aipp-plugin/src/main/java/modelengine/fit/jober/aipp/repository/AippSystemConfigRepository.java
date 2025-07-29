/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository;

import modelengine.fit.jober.aipp.domain.AippSystemConfig;

import java.util.Optional;

/**
 * 系统配置数据库操作接口
 *
 * @author 张越
 * @since 2024-11-30
 */
public interface AippSystemConfigRepository {
    /**
     * 查询系统配置.
     *
     * @param group 组.
     * @param key 键值.
     * @return {@link Optional}{@code <}{@link AippSystemConfig}{@code >} 对象.
     */
    Optional<AippSystemConfig> find(String group, String key);
}
