/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.flow.graph.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 流程graph定义
 *
 * @author 孙怡菲
 * @since 2023-10-28
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlowGraphDefinition {
    /**
     * 流程Id
     */
    private String flowId;

    /**
     * 流程graph版本
     */
    private String version;

    /**
     * 租户
     */
    private String tenant;

    /**
     * 状态
     */
    private String status;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 流程graph名称
     */
    private String name;

    /**
     * 流程graph全量数据
     */
    private String graphData;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 来源信息
     */
    private String previous;

    /**
     * 是否删除
     */
    private Boolean isDeleted;
}
