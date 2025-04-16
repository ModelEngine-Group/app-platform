/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.enums;

import modelengine.fit.waterflow.ErrorCodes;
import modelengine.fit.waterflow.exceptions.WaterflowParamException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 流程实例状态枚举
 * 注意：增加状态时，修改TRACE_EXCLUSIVE_STATUS_MAP状态互斥表，否则状态更新会异常
 *
 * @author 刘海洋
 * @since 2023/11/01
 */
public enum FlowTraceStatus {
    READY,          // 未执行
    RUNNING,        // 执行中
    ARCHIVED,       // 执行完成
    ERROR,          // 执行失败
    PARTIAL_ERROR,   // 部分失败
    TERMINATE;      // 已终止

    private static final Set<FlowTraceStatus> END_STATUS = new HashSet<>(
            Arrays.asList(ARCHIVED, PARTIAL_ERROR, ERROR, TERMINATE));

    /**
     * 根据字符串获取FlowTraceStatus
     *
     * @param status status
     * @return FlowTraceStatus
     */
    public static FlowTraceStatus getFlowTraceStatus(String status) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new WaterflowParamException(ErrorCodes.ENUM_CONVERT_FAILED, "FlowTraceStatus", status));
    }

    /**
     * isEndStatus
     *
     * @param status status
     * @return boolean
     */
    public static boolean isEndStatus(FlowTraceStatus status) {
        return END_STATUS.contains(status);
    }
}
