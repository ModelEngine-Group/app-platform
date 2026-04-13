/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * END节点状态领域对象.
 *
 * @author 张越
 * @since 2026-04-01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class EndNodeStatus extends BaseDomain {
    private Long id;
    private String traceId;
    private String endNodeId;
    private String status;
    private long startTime;
    private long endTime;
    private String instanceId;
    private String flowDefinitionId;

    /**
     * 获取节点执行时间.
     *
     * @return 执行时间（毫秒）.
     */
    public long getExecutionCost() {
        return this.endTime - this.startTime;
    }
}
