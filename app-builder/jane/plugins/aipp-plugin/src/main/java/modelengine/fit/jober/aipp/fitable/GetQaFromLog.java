/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.fitable;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.aipplog.AippInstLogDataDto;
import modelengine.fit.jober.aipp.enums.AippInstLogType;
import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AppInspirationService;
import modelengine.fit.jober.aipp.util.JsonUtils;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 灵感大全获取历史记录表单中的QA对
 *
 * @author 孙怡菲
 * @since 2024-04-25
 */
@Component
public class GetQaFromLog implements AppInspirationService {
    private static final Logger log = Logger.get(GetQaFromLog.class);

    private final AippLogService aippLogService;

    public GetQaFromLog(AippLogService aippLogService) {
        this.aippLogService = aippLogService;
    }

    @Override
    @Fitable(id = "GetQAFromLog")
    public List<Map<String, Object>> getCustomizedLogs(Map<String, Object> params, String aippId,
            String appType, OperationContext context) {
        List<AippInstLogDataDto> logs = aippLogService.queryRecentLogsSinceResume(aippId, appType, context);
        List<Map<String, Object>> res = new ArrayList<>();
        for (AippInstLogDataDto log : logs) {
            List<Map<String, Object>> qaList = new ArrayList<>();
            log.getInstanceLogBodies()
                    .stream()
                    .filter(l -> StringUtils.equals(l.getLogType(), AippInstLogType.FORM.name()))
                    .forEach(logBody -> {
                        extractQA(logBody, qaList);
                    });
            res.addAll(qaList);
        }
        return res;
    }

    private void extractQA(AippInstLogDataDto.AippInstanceLogBody logBody, List<Map<String, Object>> qaList) {
        Map<String, String> data = JsonUtils.parseObject(logBody.getLogData(), Map.class);
        String formArgsStr = data.get("form_args");
        if (StringUtils.isEmpty(formArgsStr)) {
            return;
        }
        Map<String, Object> formArgs = JsonUtils.parseObject(formArgsStr, Map.class);
        if (!formArgs.containsKey("interviewResult")) {
            return;
        }
        if (formArgs.get("interviewResult") instanceof List) {
            qaList.addAll((List<Map<String, Object>>) formArgs.get("interviewResult"));
        }
    }
}
