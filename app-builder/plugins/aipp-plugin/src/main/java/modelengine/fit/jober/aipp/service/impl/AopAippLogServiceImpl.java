/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import modelengine.fit.jober.aipp.aop.AippLogInsert;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import modelengine.fit.jober.aipp.mapper.AippLogMapper;
import modelengine.fit.jober.aipp.service.AopAippLogService;
import modelengine.fit.jober.aipp.util.SensitiveFilterTools;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;

/**
 * 用于 aop 的 aipp 实例历史记录服务接口实现
 *
 * @author 邬涨财
 * @since 2024-07-03
 */
@Component
public class AopAippLogServiceImpl implements AopAippLogService {
    private static final Logger log = Logger.get(AopAippLogServiceImpl.class);

    private final AippLogMapper aippLogMapper;

    private final SensitiveFilterTools sensitiveFilterTools;

    public AopAippLogServiceImpl(AippLogMapper aippLogMapper, SensitiveFilterTools sensitiveFilterTools) {
        this.aippLogMapper = aippLogMapper;
        this.sensitiveFilterTools = sensitiveFilterTools;
    }

    /**
     * 插入aipp的历史记录
     *
     * @param logDto 插入数据
     * @return 返回插入的历史记录的唯一标识符
     * @throws IllegalArgumentException 不合法的参数时抛出
     * @throws AippParamException 当参数校验失败时抛出此异常
     */
    @Override
    @AippLogInsert
    public String insertLog(AippLogCreateDto logDto) throws IllegalArgumentException {
        if (logDto.allFieldsNotNull()) {
            logDto.setLogData(this.sensitiveFilterTools.filterString(logDto.getLogData()));
            aippLogMapper.insertOne(logDto);
            return logDto.getLogId();
        }
        log.error("null field exists in req {}", logDto);
        // 待各个参数独立校验
        throw new AippParamException(AippErrCode.UNKNOWN);
    }
}
