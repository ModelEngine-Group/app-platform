/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jober.aipp.dto.aipplog.AippLogCreateDto;

/**
 * 用于 aop 的 aipp 实例历史记录服务接口
 *
 * @author 邬涨财
 * @since 2024-07-03
 */
public interface AopAippLogService {
    /**
     * 插入aipp的历史记录。
     *
     * @param logDto 历史记录的数据传输对象。
     * @return 返回插入的历史记录的唯一标识符。
     * @throws IllegalArgumentException 不合法的参数时抛出。
     */
    String insertLog(AippLogCreateDto logDto) throws IllegalArgumentException;
}
