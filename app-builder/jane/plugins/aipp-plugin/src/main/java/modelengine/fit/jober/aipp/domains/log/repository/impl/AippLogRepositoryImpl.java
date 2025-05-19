/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.log.repository.impl;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.domains.log.AppLog;
import modelengine.fit.jober.aipp.domains.log.AppLogFactory;
import modelengine.fit.jober.aipp.domains.log.repository.AippLogRepository;
import modelengine.fit.jober.aipp.enums.AippTypeEnum;
import modelengine.fit.jober.aipp.mapper.AippLogMapper;

import lombok.AllArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 日志仓库实现类
 *
 * @author 张越
 * @since 2025-02-07
 */
@Component
@AllArgsConstructor
public class AippLogRepositoryImpl implements AippLogRepository {
    private static final Logger log = Logger.get(AippLogRepositoryImpl.class);

    private final AippLogMapper aippLogMapper;
    private final AppLogFactory appLogFactory;

    @Override
    public List<AppLog> selectAllLogsByInstanceId(String instanceId) {
        return this.aippLogMapper.getFullLogsByInstanceIds(Collections.singletonList(instanceId))
                .stream()
                .map(this.appLogFactory::create)
                .toList();
    }

    @Override
    public String getParentPath(String parentInstId) {
        return Optional.ofNullable(parentInstId).map(this.aippLogMapper::getParentPath).orElse(StringUtils.EMPTY);
    }

    @Override
    public List<AppLog> selectByInstanceIdAndLogTypes(String instanceId, List<String> logTypes) {
        if (StringUtils.isEmpty(instanceId)) {
            log.error("When queryLogsByInstanceIdAndLogTypes input instance id is empty.");
            throw new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID);
        }
        return this.aippLogMapper.getLogsByInstanceIdAndLogTypes(instanceId, logTypes)
                .stream()
                .map(this.appLogFactory::create)
                .toList();
    }

    @Override
    public void deleteByInstanceId(String instanceId) {
        if (StringUtils.isEmpty(instanceId)) {
            log.error("Instance id is null or empty.");
            throw new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID);
        }
        this.aippLogMapper.deleteInstanceLog(instanceId);
    }

    @Override
    public void deleteAippPreviewLog(String previewAippId, OperationContext context) {
        this.aippLogMapper.deleteByType(previewAippId, AippTypeEnum.PREVIEW.name(), context.getAccount(), null);
    }

    @Override
    public void updateDataAndType(Long logId, String newLogType, String newLogData) {
        this.aippLogMapper.updateDataAndType(logId, newLogType, newLogData);
    }
}
