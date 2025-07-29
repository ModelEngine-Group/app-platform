/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.mapper;

import modelengine.fit.jober.aipp.aop.Locale;
import modelengine.fit.jober.aipp.po.AppBuilderFormPropertyPo;

import java.util.List;

/**
 * Aipp表单属性映射器
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
public interface AppBuilderFormPropertyMapper {
    /**
     * 通过表单id查询表单属性
     *
     * @param formId 要查询表单属性的表单id
     * @return 表单属性信息集合
     */
    @Locale
    List<AppBuilderFormPropertyPo> selectWithFormId(String formId);

    /**
     * 通过应用id查询表单属性
     *
     * @param appId 要查询的应用id
     * @return 表单属性信息集合
     */
    @Locale
    List<AppBuilderFormPropertyPo> selectWithAppId(String appId);

    /**
     * 根据表单属性id查询表单属性
     *
     * @param id 要查询的表单属性的id
     * @return 表单属性信息
     */
    @Locale
    AppBuilderFormPropertyPo selectWithId(String id);

    /**
     * 插入一条表单属性
     *
     * @param insert 要插入的表单属性
     */
    void insertOne(AppBuilderFormPropertyPo insert);

    /**
     * 插入多条表单属性
     *
     * @param jadeFormProperties 要插入的表单属性的集合
     */
    void insertMore(List<AppBuilderFormPropertyPo> jadeFormProperties);

    /**
     * 更新一条表单属性
     *
     * @param update 要更新的表单属性
     */
    void updateOne(AppBuilderFormPropertyPo update);

    /**
     * 更新多条表单属性
     *
     * @param formProperties 要更新的表单属性
     */
    void updateMany(List<AppBuilderFormPropertyPo> formProperties);

    /**
     * 通过表单属性id删除多条表单属性
     *
     * @param ids 被删除的表单属性id的集合
     * @return 被删除的表单属性的数量
     */
    int deleteMore(List<String> ids);

    /**
     * 通过表单id删除表单属性
     *
     * @param formIds 被删除的表单属性的表单id集合
     */
    void deleteByFormIds(List<String> formIds);

    /**
     * 通过应用id集合删除表单属性
     *
     * @param appIds 被删除的表单属性的表单id集合
     */
    void deleteByAppIds(List<String> appIds);
}
