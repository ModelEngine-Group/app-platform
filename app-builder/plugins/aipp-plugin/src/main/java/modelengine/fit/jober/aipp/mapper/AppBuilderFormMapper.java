/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.mapper;

import modelengine.fit.jober.aipp.condition.FormQueryCondition;
import modelengine.fit.jober.aipp.po.AppBuilderFormPo;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 表单数据库服务
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
public interface AppBuilderFormMapper {
    /**
     * 通过表单id查询表单信息
     *
     * @param id 要查询的表单id
     * @return 表单结构体
     */
    AppBuilderFormPo selectWithId(String id);

    /**
     * 插入一条表单信息
     *
     * @param insert 要插入的表单信息
     */
    void insertOne(AppBuilderFormPo insert);

    /**
     * 更新一条表单信息
     *
     * @param update 被更新的表单信息
     */
    void updateOne(AppBuilderFormPo update);

    /**
     * 根据类型和租户查询表单
     *
     * @param type 类型
     * @param tenantId 租户id
     * @return 表单结构体
     */
    List<AppBuilderFormPo> selectWithType(String type, String tenantId);

    /**
     * 通过表单id删除表单信息
     *
     * @param ids 被删除的表单id
     */
    void delete(List<String> ids);

    /**
     * 根据类型和租户查询表单数量
     *
     * @param type 表示表单类型的 {@link String}。
     * @param tenantId 表示租户Id的 {@link String}。
     * @return 表单数量
     */
    long countWithType(String type, String tenantId);

    /**
     * 根据名称获取表单数据。
     *
     * @param name 表示表单名称的 {@link String}。
     * @param tenantId 示租户Id的 {@link String}。
     * @return 表示表单结构体列表的 {@link AppBuilderFormPo}。
     */
    AppBuilderFormPo selectWithName(String name, String tenantId);

    /**
     * 分页查询智能表单数据。
     *
     * @param cond 表示表单查询条件的 {@link FormQueryCondition}。
     * @return 表示表单结构体列表的 {@link AppBuilderFormPo}。
     */
    List<AppBuilderFormPo> selectWithCondition(@Param("cond") FormQueryCondition cond);

    /**
     * 根据查询条件查询表单数量。
     *
     * @param cond 表示表单查询条件的 {@link FormQueryCondition}。
     * @return 表示表单数量的 {@link Long}。
     */
    long countWithCondition(@Param("cond") FormQueryCondition cond);
}
