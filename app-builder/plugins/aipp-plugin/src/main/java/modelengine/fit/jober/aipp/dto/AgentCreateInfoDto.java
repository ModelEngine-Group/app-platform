/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import lombok.Data;

/**
 * 表示用于生成智能体信息的信息，
 *
 * @author 兰宇晨
 * @since 2024-12-3
 */
@Data
public class AgentCreateInfoDto {
    /**
     * 用于生成智能体信息的描述。
     */
    private String description;
}