/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.condition.TemplateQueryCondition;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.template.TemplateAppCreateDto;
import modelengine.fit.jober.aipp.dto.template.TemplateInfoDto;
import modelengine.fit.jober.common.RangedResultSet;

/**
 * 应用模板 Service 接口定义。
 *
 * @author 方誉州
 * @since 2024-12-30
 */
public interface AppTemplateService {
    /**
     * 根据筛选条件查询应用模板。
     *
     * @param cond 表示应用模板查询条件的 {@link TemplateQueryCondition}。
     * @param context 表示接口操作上下文的 {@link OperationContext}。
     * @return 表示符合筛选条件的模板的列表的 {@link RangedResultSet}{@code <}{@link TemplateInfoDto}{@code >}。
     */
    RangedResultSet<TemplateInfoDto> query(TemplateQueryCondition cond, OperationContext context);

    /**
     * 将应用发布为应用模板。
     *
     * @param createDto 表示发布应用模板的基础信息的 {@link TemplateAppCreateDto}。
     * @param context 表示接口操作上下文的 {@link OperationContext}。
     * @return 表示发布后的应用模板信息的 {@link TemplateInfoDto}。
     */
    TemplateInfoDto publish(TemplateAppCreateDto createDto, OperationContext context);

    /**
     * 跟据应用模板创建应用。
     *
     * @param createDto 表示根据模板创建应用的基础信息的 {@link TemplateAppCreateDto}。
     * @param context 表示接口操作上下文的 {@link OperationContext}。
     * @return 表示应用创建完成后应用详情的 {@link AppBuilderAppDto}。
     */
    AppBuilderAppDto createAppByTemplate(TemplateAppCreateDto createDto, OperationContext context);

    /**
     * 删除指定的应用模板。
     *
     * @param templateId 表示待删除模板的唯一的 id 的 {@link String}。
     * @param context 表示接口操作上下文的 {@link OperationContext}。
     */
    void delete(String templateId, OperationContext context);
}
