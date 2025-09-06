/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;
import modelengine.fit.jober.aipp.dto.AppBuilderFormDto;
import modelengine.fit.jober.common.RangedResultSet;

import modelengine.fit.http.server.HttpClassicServerRequest;

import java.util.List;

/**
 * 表单服务
 *
 * @author 邬涨财
 * @since 2024-04-19
 */
public interface AppBuilderFormService {
    /**
     * 根据类型查询
     *
     * @param httpRequest http请求
     * @param type 类型
     * @param tenantId 租户id
     * @return 表单列表
     */
    Rsp<List<AppBuilderFormDto>> queryByType(HttpClassicServerRequest httpRequest, String type, String tenantId);

    /**
     * 根据id查找表单
     *
     * @param id 唯一id
     * @return 表单
     */
    AppBuilderForm selectWithId(String id);

    /**
     * 创建智能表单
     *
     * @param dto 表示表单创建结构体的 {@link AppBuilderFormDto}。
     * @param context 表示操作者上下文的 {@link OperationContext}。
     * @return 表单结构体
     */
    AppBuilderFormDto create(AppBuilderFormDto dto, OperationContext context);

    /**
     * 更新智能表单
     *
     * @param dto 表示表单创建结构体的 {@link AppBuilderFormDto}。
     * @param formId 表示表单Id的 {@link String}。
     * @param context 表示操作者上下文的 {@link OperationContext}。
     * @return 表单结构体
     */
    AppBuilderFormDto update(AppBuilderFormDto dto, String formId, OperationContext context);

    /**
     * 分页查询智能表单
     *
     * @param pageNum 表示分页页数的 {@link Long}。
     * @param pageSize 表示分页大小的 {@link Integer}。
     * @param name 表示模糊查询表单名称的 {@link String}。
     * @param operationContext 表示操作者上下文的 {@link OperationContext}。
     * @return 表示查询结果的 {@link RangedResultSet}{@code <}{@link AppBuilderFormDto}{@code >}。
     */
    RangedResultSet<AppBuilderFormDto> query(long pageNum, int pageSize, String name,
            OperationContext operationContext);

    /**
     * 删除智能表单
     *
     * @param formId 表示表单Id的 {@link String}。
     * @param operationContext 表示操作者上下文的 {@link OperationContext}。
     * @return void
     */
    Void delete(String formId, OperationContext operationContext);

    /**
     * 根据类型查询表单数量
     *
     * @param type 表示表单类型的 {@link String}。
     * @param tenantId 表示租户标识的 {@link String}。
     * @return 表示表单数量的 {@code long}。
     */
    long countByType(String type, String tenantId);
}
