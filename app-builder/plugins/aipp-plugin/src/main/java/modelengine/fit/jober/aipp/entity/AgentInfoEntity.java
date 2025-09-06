/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.entity;

import lombok.Data;

import java.util.List;

/**
 * 表示自动生成的智能体信息。
 *
 * @author 兰宇晨
 * @since 2024-12-03
 */
@Data
public class AgentInfoEntity {
    /**
     * 智能体名称。
     */
    private String name;

    /**
     * 智能体开场白。
     */
    private String greeting;

    /**
     * 智能体 Prompt。
     */
    private String prompt;

    /**
     * 工具 UniqueName 列表。
     */
    private List<String> tools;
}