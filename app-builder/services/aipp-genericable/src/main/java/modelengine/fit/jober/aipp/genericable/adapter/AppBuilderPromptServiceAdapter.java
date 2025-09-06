/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.genericable.adapter;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.chat.PromptCategory;
import modelengine.fit.jober.aipp.dto.chat.PromptInfo;

import java.util.List;

/**
 * 灵感大全查询接口。
 *
 * @author 曹嘉美
 * @since 2024-12-19
 */
public interface AppBuilderPromptServiceAdapter {
    /**
     * 查询灵感大全的分类。
     *
     * @param appId 表示应用唯一标识符的 {@link String}。
     * @param operationContext 表示操作上下文的 {@link OperationContext}。
     * @param isDebug 表示是否是调试状态的 {@link Boolean}。
     * @return 表示获取到的应用列表信息的
     * {@link List}{@code <}{@link PromptCategory}{@code >}。
     */
    List<PromptCategory> listPromptCategories(String appId, OperationContext operationContext, boolean isDebug);

    /**
     * 查询灵感大全。
     *
     * @param appId 表示应用唯一标识符的 {@link String}。
     * @param categoryId 表示分类唯一标识符的 {@link String}。
     * @param operationContext 表示操作上下文的 {@link OperationContext}。
     * @param isDebug 表示是否是调试状态的 {@link Boolean}。
     * @return 表示获取到的灵感大全信息的 {@link PromptInfo}。
     */
    PromptInfo queryInspirations(String appId, String categoryId, OperationContext operationContext, boolean isDebug);
}
