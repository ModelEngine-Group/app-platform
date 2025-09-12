/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.base.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.jade.app.engine.base.dto.AppBuilderRecommendDto;

import java.util.List;

/**
 * 猜你想问的查询接口的服务类。
 *
 * @author 杨海波
 * @since 2024-05-24
 */
public interface AppBuilderRecommendService {
    /**
     * 猜你想问推荐问题查询。
     *
     * @param recommendDto 表示上次对话问答及模型信息的 {@link AppBuilderRecommendDto}。
     * @param context 表示系统上下文的 {@link OperationContext}。
     * @param isGuest 表示是否为游客模式的 {@code boolean}。
     * @return 表示推荐问题列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> queryRecommends(AppBuilderRecommendDto recommendDto, OperationContext context, boolean isGuest);
}
