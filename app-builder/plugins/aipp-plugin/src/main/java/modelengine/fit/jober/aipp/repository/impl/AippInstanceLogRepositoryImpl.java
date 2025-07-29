/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository.impl;

import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.mapper.AippLogMapper;
import modelengine.fit.jober.aipp.repository.AippInstanceLogRepository;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.CollectionUtils;

import java.util.List;

/**
 * {@link AippInstanceLogRepository} 对应实现类。
 *
 * @author 杨祥宇
 * @since 2025-04-09
 */
@Component
public class AippInstanceLogRepositoryImpl implements AippInstanceLogRepository {
    private final AippLogMapper aippLogMapper;

    /**
     * 表示用日志持久层构造 {@link AippInstanceLogRepositoryImpl} 的实例。
     *
     * @param aippLogMapper 表示日志持久层实例的 {@link AippLogMapper}。
     */
    public AippInstanceLogRepositoryImpl(AippLogMapper aippLogMapper) {this.aippLogMapper = aippLogMapper;}

    @Override
    public List<Long> getExpireInstanceLogIds(String aippType, int expiredDays, int limit) {
        return this.aippLogMapper.getExpireInstanceLogIds(aippType, expiredDays, limit);
    }

    @Override
    public void forceDeleteInstanceLogs(List<Long> logIds) {
        if (CollectionUtils.isEmpty(logIds)) {
            return;
        }
        this.aippLogMapper.forceDeleteInstanceLogsByIds(logIds);
    }

    @Override
    public List<AippInstLog> selectByLogIds(List<Long> logIds) {
        return this.aippLogMapper.selectByLogIds(logIds);
    }
}
