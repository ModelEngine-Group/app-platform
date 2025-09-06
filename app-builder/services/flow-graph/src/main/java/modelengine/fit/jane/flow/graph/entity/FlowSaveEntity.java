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

import java.util.List;

/**
 * 流程入参信息
 *
 * @author 杨祥宇
 * @since 2023/12/13
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowSaveEntity {
    /**
     * 流程定义图json字符串
     */
    private String graphData;

    /**
     * 流程定义引擎json字符串
     */
    private String definitionData;

    /**
     * 流程所属标签
     */
    private List<String> tags;

    /**
     * 流程id
     */
    private String id;

    /**
     * 流程版本
     */
    private String version;

    /**
     * 流程来源模板id
     */
    private String previous;

    /**
     * 流程状态
     */
    private String status;
}
