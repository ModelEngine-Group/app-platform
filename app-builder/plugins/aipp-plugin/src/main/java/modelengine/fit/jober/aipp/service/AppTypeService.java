/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jober.aipp.dto.AppTypeDto;

import java.util.List;

/**
 * 应用业务分类服务接口。
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
public interface AppTypeService {
    /**
     * 查询租户下所有分类定义信息。
     *
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @return 表示分类定义信息列表的 {@link List}{@code <}{@link AppTypeDto}{@code >}。
     */
    List<AppTypeDto> queryAll(String tenantId);

    /**
     * 根据应用分类唯一标识和租户唯一标识查询分类信息。
     *
     * @param id 表示应用分类唯一标识的 {@link String}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @return 表示分类定义信息列表的 {@link AppTypeDto}。
     */
    AppTypeDto query(String id, String tenantId);

    /**
     * 新增一条分类信息。
     *
     * @param dto 表示应用分类信息的 {@link AppTypeDto}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @return 表示分类定义信息列表的 {@link AppTypeDto}。
     */
    AppTypeDto add(AppTypeDto dto, String tenantId);

    /**
     * 根据应用分类唯一标识删除应用分类。
     *
     * @param id 表示应用分类唯一标识的 {@link String}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     */
    void delete(String id, String tenantId);

    /**
     * 更新应用分类信息。
     *
     * @param dto 表示分类定义信息列表的 {@link AppTypeDto}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     */
    void update(AppTypeDto dto, String tenantId);
}
