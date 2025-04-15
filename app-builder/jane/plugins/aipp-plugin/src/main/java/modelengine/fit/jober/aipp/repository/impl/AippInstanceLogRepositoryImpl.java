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

import java.time.LocalDateTime;
import java.util.List;

/**
 * {@link AippInstanceLogRepository}对应实现类。
 *
 * @author 杨祥宇
 * @since 2025-04-09
 */
@Component
public class AippInstanceLogRepositoryImpl implements AippInstanceLogRepository {
    private final AippLogMapper aippLogMapper;

    public AippInstanceLogRepositoryImpl(AippLogMapper aippLogMapper) {this.aippLogMapper = aippLogMapper;}

    @Override
    public List<Long> getExpireInstanceLogIds(String aippType, int expiredDays, int limit) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusDays(expiredDays);
        return aippLogMapper.getExpireInstanceLogIds(aippType, expired, limit);
    }

    @Override
    public void forceDeleteInstanceLogs(List<Long> logIds) {
        if (CollectionUtils.isEmpty(logIds)) {
            return;
        }
        aippLogMapper.forceDeleteInstanceLogsByIds(logIds);
    }

    @Override
    public List<AippInstLog> selectByLogIds(List<Long> logIds) {
        return aippLogMapper.selectByLogIds(logIds);
    }
}
