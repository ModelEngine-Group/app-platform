/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.dto.aipplog.AippLogCreateDto;
import modelengine.fit.jober.aipp.entity.AippInstLog;
import modelengine.fit.jober.aipp.entity.AippLogData;
import modelengine.fit.jober.aipp.enums.AippInstLogType;

import modelengine.fit.jober.aipp.service.AippLogService;
import modelengine.fit.jober.aipp.service.AopAippLogService;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * AippLog 操作工具类
 *
 * @author 方誉州
 * @since 2024-06-14
 */
public class AippLogUtils {
    /**
     * aipp log path分割符
     */
    public static final String PATH_DELIMITER = "/";

    /**
     * 检查FORM和MSG类型的logData是否有效
     *
     * @param logData 表示待检查的日志数据的 {@link AippLogData}
     * @param logType 表示待检查日志的日志类型的 {@link String}
     * @return 返回logData的有效性。
     **/
    public static boolean validFormMsg(AippLogData logData, String logType) {
        if (!StringUtils.equals(AippInstLogType.FORM.name(), logType)
                && !StringUtils.equals(AippInstLogType.HIDDEN_FORM.name(), logType)) {
            return true;
        }
        if (!isFormIdValid(logData.getFormId())) {
            return false;
        }
        return isFormVersionValid(logData.getFormVersion());
    }

    private static boolean isFormIdValid(String formId) {
        return !(StringUtils.isBlank(formId) || StringUtils.equals(AippConst.INVALID_FORM_ID, formId));
    }

    private static boolean isFormVersionValid(String formVersion) {
        return !(StringUtils.isBlank(formVersion)
                || StringUtils.equals(AippConst.INVALID_FORM_VERSION_ID, formVersion));
    }

    /**
     * 检查business log enable key，默认为true。
     *
     * @param contextData 表示上下文数据contextData的 {@link Map}。
     * @return 返回business log enable key检查结果。
     **/
    public static boolean isLogEnabled(Map<String, Object> contextData) {
        if (!contextData.containsKey(AippConst.BS_EXTRA_CONFIG_KEY)) {
            return true;
        }
        Map<String, Object> configKeyObj = ObjectUtils.cast(contextData.get(AippConst.BS_EXTRA_CONFIG_KEY));
        if (configKeyObj.containsKey(AippConst.BS_LOG_ENABLE_KEY)) {
            Object logEnableKeyObject = configKeyObj.get(AippConst.BS_LOG_ENABLE_KEY);
            String logEnableKey = logEnableKeyObject instanceof String ? ObjectUtils.cast(logEnableKeyObject) : null;
            return "true".equalsIgnoreCase(logEnableKey);
        }
        return true;
    }

    public static String writePromptLog(String instanceId, String prompt, AippLogService aippLogService,
            AopAippLogService aopAippLogService) {
        List<AippInstLog> aippInstLogs = aippLogService.queryInstanceLogSince(instanceId, null);
        AippInstLog aippInstLog = aippInstLogs.get(0);
        Map<String, Object> map = JsonUtils.parseObject(aippInstLog.getLogData());
        map.put("msg", prompt);

        AippLogCreateDto aippLogCreateDto = AippLogCreateDto.builder()
                .aippId(aippInstLog.getAippId())
                .version(aippInstLog.getVersion())
                .aippType(aippInstLog.getAippType())
                .instanceId(aippInstLog.getInstanceId())
                .logType(AippInstLogType.HIDDEN_QUESTION.name())
                .logData(JsonUtils.toJsonString(map))
                .createUserAccount(aippInstLog.getCreateUserAccount())
                .path(aippInstLog.getPath())
                .build();
        return aopAippLogService.insertLog(aippLogCreateDto);
    }
}
