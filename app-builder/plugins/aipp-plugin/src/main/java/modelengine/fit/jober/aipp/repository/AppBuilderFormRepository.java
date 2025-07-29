/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository;

import modelengine.fit.jober.aipp.condition.FormQueryCondition;
import modelengine.fit.jober.aipp.domain.AppBuilderForm;

import java.util.List;

/**
 * 表单数据层服务
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
public interface AppBuilderFormRepository {
    /**
     * 根据id查询表单
     *
     * @param id 表单id
     * @return 表单结构体
     */
    AppBuilderForm selectWithId(String id);

    /**
     * 根据类型和租户查询表单
     *
     * @param type 类型
     * @param tenantId 租户id
     * @return 表单结构体
     */
    List<AppBuilderForm> selectWithType(String type, String tenantId);

    /**
     * 插入一个表单
     *
     * @param appBuilderForm 表单结构体
     */
    void insertOne(AppBuilderForm appBuilderForm);

    /**
     * 更新表单
     *
     * @param appBuilderForm 表单结构体
     */
    void updateOne(AppBuilderForm appBuilderForm);

    /**
     * 根据id删除表单
     *
     * @param ids 表单id集合
     */
    void delete(List<String> ids);

    /**
     * 根据类型和租户获取表单数量。
     *
     * @param type 表示表单类型的 {@link String}。
     * @param tenantId 表示租户Id的 {@link String}。
     * @return 表示表单数量的 {@link Long}。
     */
    long countWithType(String type, String tenantId);

    /**
     * 根据名称获取表单数据。
     *
     * @param name 表示表单名称的 {@link String}。
     * @param tenantId 示租户Id的 {@link String}。
     * @return 表示表单结构体的 {@link AppBuilderForm}。
     */
    AppBuilderForm selectWithName(String name, String tenantId);

    /**
     * 根据查询条件获取表单数据。
     *
     * @param cond 表示表单查询条件的 {@link FormQueryCondition}。
     * @return 表示表单结构体列表的 {@link List}{@code <}{@link AppBuilderForm}{@code >}。
     */
    List<AppBuilderForm> selectWithCondition(FormQueryCondition cond);

    /**
     * 根据查询条件获取表单个数。
     *
     * @param cond 表示表单查询条件的 {@link FormQueryCondition}。
     * @return 表示表单数量的 {@link Long}。
     */
    long countWithCondition(FormQueryCondition cond);
}
