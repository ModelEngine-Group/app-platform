/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.mapper;

import modelengine.fit.jober.aipp.condition.TemplateQueryCondition;
import modelengine.fit.jober.aipp.domain.AppTemplate;
import modelengine.fit.jober.aipp.dto.template.TemplateInfoDto;
import modelengine.fit.jober.aipp.po.AppTemplatePo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 应用模板的数据库操作接口。
 *
 * @author 方誉州
 * @since 2024-12-31
 */
@Mapper
public interface AppTemplateMapper {
    /**
     * 跟据筛选条件，查询所有的符合的应用模板信息。
     *
     * @param cond 表示筛选条件的 {@link TemplateQueryCondition}。
     * @return 表示符合筛选条件的应用模板信息的列表额 {@link List}{@code <}{@link TemplateInfoDto}{@code >}。
     */
    List<AppTemplatePo> selectWithCondition(@Param("cond") TemplateQueryCondition cond);

    /**
     * 根据条件进行计数。
     *
     * @param cond 表示筛选条件的 {@link TemplateQueryCondition}。
     * @return 表示符合筛选条件的记录数量的 {@code int}。
     */
    int countWithCondition(@Param("cond") TemplateQueryCondition cond);

    /**
     * 根据模板的 id 获取对应模板的基本信息。
     *
     * @param templateId 表示模板 id 的 {@link String}。
     * @return 表示数据库中模板基本信息的 {@link AppTemplate}。
     */
    AppTemplatePo selectWithId(@Param("id") String templateId);

    /**
     * 插入一条应用模板数据。
     *
     * @param appTemplate 表示应用模板数据的 {@link AppTemplate}。
     */
    void insertOne(AppTemplatePo appTemplate);

    /**
     * 删除一条应用模板数据。
     *
     * @param templateId 表示应用模板唯一 id 的 {@link String}。
     */
    void deleteOne(@Param("id") String templateId);

    /**
     * 模板的使用数量加 1。
     *
     * @param templateId 表示应用模板唯一 id 的 {@link String}。
     */
    void increaseUsage(@Param("id") String templateId);

    /**
     * 更新应用模板喜欢数量。
     *
     * @param templateId 表示应用模板唯一 id 的 {@link String}。
     * @param delta 表示应用模板喜欢数量变化的值的 {@code long}。
     */
    void updateLike(@Param("id") String templateId, @Param("delta") long delta);

    /**
     * 更新应用模板收藏数量。
     *
     * @param templateId 表示应用模板唯一 id 的 {@link String}。
     * @param delta 表示应用模板收藏数量变化的值的 {@code long}。
     */
    void updateCollection(@Param("id") String templateId, @Param("delta") long delta);
}
