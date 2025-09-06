/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.mapper;

import modelengine.fit.jober.aipp.po.I18nPo;

import java.util.List;

/**
 * 国际化相关的数据库操作。
 *
 * @author 陈潇文
 * @since 2024-08-20
 */
public interface I18nMapper {
    /**
     * 获取国际化资源。
     *
     * @return 表示国际化资源列表的 {@link List}{@code <}{@link I18nPo}{@code >}
     */
    List<I18nPo> selectResource();
}
