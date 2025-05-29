/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jober.aipp.dto.AppBuilderPromptCategoryDto;
import modelengine.fit.jober.aipp.dto.AppBuilderPromptDto;

import java.util.List;

/**
 * 灵感大全的两个查询接口service
 *
 * @author 姚江
 * @since 2024-04-26
 */
public interface AppBuilderPromptService {
    /**
     * 查询灵感大全
     *
     * @param appId 应用id
     * @param categoryId 分类id
     * @param context 操作上下文
     * @param isDebug 是否是调试状态
     * @return 返回查询结果
     */
    Rsp<AppBuilderPromptDto> queryInspirations(String appId, String categoryId, OperationContext context,
            boolean isDebug);

    /**
     * 查询灵感大全的分类
     *
     * @param appId 应用id
     * @param context 操作上下文
     * @param isDebug 是否是调试状态
     * @return 返回查询结果
     */
    Rsp<List<AppBuilderPromptCategoryDto>> listPromptCategories(String appId, OperationContext context,
            boolean isDebug);

    /**
     * 添加我的灵感
     *
     * @param appId 应用id
     * @param parentId 父类目id
     * @param inspirationDto 灵感
     * @param context 操作上下文
     */
    void addCustomInspiration(String appId, String parentId,
            AppBuilderPromptDto.AppBuilderInspirationDto inspirationDto, OperationContext context);

    /**
     * 更新我的灵感
     *
     * @param appId 应用id
     * @param categoryId “我的”类目id
     * @param inspirationId 灵感id
     * @param inspirationDto 灵感
     * @param context 操作上下文
     */
    void updateCustomInspiration(String appId, String categoryId, String inspirationId,
            AppBuilderPromptDto.AppBuilderInspirationDto inspirationDto, OperationContext context);

    /**
     * 删除我的灵感
     *
     * @param appId 应用id
     * @param categoryId “我的”类目id
     * @param inspirationId 灵感id
     * @param context 操作上下文
     */
    void deleteCustomInspiration(String appId, String categoryId, String inspirationId, OperationContext context);
}
