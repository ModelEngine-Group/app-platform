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
 * END节点状态记录.
 *
 * @author 沈维枫
 * @since 2026-04-01
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndNodeStatusPo {
    private Long id;
    private String traceId;
    private String endNodeId;
    private String status;
    private long startTime;
    private long endTime;
    private String instanceId;
    private String flowDefinitionId;

    /* ------------ 公共字段 ------------ */
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
