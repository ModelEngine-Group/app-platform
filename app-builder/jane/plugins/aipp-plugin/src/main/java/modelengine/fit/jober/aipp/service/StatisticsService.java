/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.StatisticsDTO;

/**
 * Statistics相关服务
 *
 * @author 陈潇文
 * @since 2024-12-26
 */
public interface StatisticsService {
    /**
     * 获取统计数据。
     *
     * @param operationContext 表示操作上下文的 {@link OperationContext}。
     * @return 表示统计数据的 {@link StatisticsDTO}。
     */
    StatisticsDTO getStatistics(OperationContext operationContext);
}
