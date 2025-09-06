/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * {@link AippFlowData} 指标上报数据传输类。
 *
 * @author 高嘉乐
 * @since 2025-01-06
 */
@Data
@Builder
public class AippFlowData {
    private String appId;
    private String username;
    private LocalDateTime createTime;
    private LocalDateTime finishTime;
}
