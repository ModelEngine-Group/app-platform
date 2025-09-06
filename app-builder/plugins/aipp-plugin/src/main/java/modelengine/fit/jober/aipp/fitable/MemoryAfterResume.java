/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import static modelengine.fit.jober.aipp.service.impl.AippRunTimeServiceImpl.getLogMaps;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AppLogService;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;

import java.util.List;
import java.util.Map;

/**
 * 获取简历之后的memory
 *
 * @author 孙怡菲
 * @since 2024-04-21
 */
@Component
public class MemoryAfterResume implements AppLogService {
    private final AippLogService aippLogService;

    public MemoryAfterResume(AippLogService aippLogService) {
        this.aippLogService = aippLogService;
    }

    @Fitable(id = "MemoryAfterResume")
    @Override
    public List<Map<String, Object>> getCustomizedLogs(Map<String, Object> params, String aippId, String aippType,
            OperationContext context) {
        List<AippInstLogDataDto> logs = this.aippLogService.queryRecentLogsSinceResume(aippId, aippType, context);
        return getLogMaps(logs);
    }
}
