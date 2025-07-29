/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.mapper;

import modelengine.fit.jober.aipp.aop.Locale;
import modelengine.fit.jober.aipp.po.AppBuilderAppTypePo;

import java.util.List;

/**
 * 应用业务分类 Mapper。
 *
 * @author songyongtan
 * @since 2025-01-04
 */
public interface AppBuilderAppTypeMapper {
    /**
     * 查询所有分类信息。
     *
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @return 表示分类列表的 {@link List}{@code <}{@link AppBuilderAppTypePo}{@code >}。
     */
    @Locale
    List<AppBuilderAppTypePo> queryAll(String tenantId);

    /**
     * 插入一条分类信息。
     *
     * @param appBuilderAppType 表示待插入的应用分类信息的 {@link AppBuilderAppTypePo}。
     */
    void insert(AppBuilderAppTypePo appBuilderAppType);

    /**
     * 更新一条分类信息。
     *
     * @param appBuilderAppType 表示待插入的应用分类信息的 {@link AppBuilderAppTypePo}。
     */
    void update(AppBuilderAppTypePo appBuilderAppType);

    /**
     * 根据应用分类唯一标识删除一条数据。
     *
     * @param id 表示应用分类唯一标识的 {@link String}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     */
    void delete(String id, String tenantId);

    /**
     * 查询一条数据。
     *
     * @param id 表示应用分类唯一标识的 {@link String}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @return 表示应用分类信息的 {@link AppBuilderAppTypePo}。
     */
    @Locale
    AppBuilderAppTypePo query(String id, String tenantId);
}
