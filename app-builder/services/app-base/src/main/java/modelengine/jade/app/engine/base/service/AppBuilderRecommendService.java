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
 * 猜你想问的查询接口service
 *
 * @author 杨海波
 * @since 2024-05-24
 */
public interface AppBuilderRecommendService {
    /**
     * queryRecommends猜你想问推荐问题查询
     *
     * @param recommendDto 包含上次对话问答及模型信息
     * @param context
     * @return List<String> 3个推荐问题列表
     */
    List<String> queryRecommends(AppBuilderRecommendDto recommendDto, OperationContext context);
}
