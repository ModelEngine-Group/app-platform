/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 运行时信息.
 *
 * @author 张越
 * @since 2024-07-29
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppBuilderRuntimeInfoPo {
    private String traceId;
    private String flowDefinitionId;
    private String instanceId;
    private String nodeId;
    private String nodeType;
    private long startTime;
    private long endTime;
    private String status;
    private int published;
    private String errorMsg;
    private String nextPositionId;
    private String parameters;

    /* ------------ 公共字段 ------------ */
    private Long id;
    private String createBy;
    private String updateBy;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
