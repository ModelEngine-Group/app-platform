/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.knowledge.dto;

import lombok.Data;

import java.sql.Timestamp;

/**
 * Edm 知识库单条数据。
 *
 * @author 何嘉斌
 * @since 2024-09-25
 */
@Data
public class EdmRepoRecord {
    /**
     * 知识库唯一标识。
     */
    private Long id;

    /**
     * 知识库名称。
     */
    private String name;

    /**
     * 知识库描述。
     */
    private String description;

    /**
     * 创建时间。
     */
    private Timestamp createdAt;

    /**
     * 知识库状态。
     */
    private String status;

    /**
     * 知识库类型。
     */
    private String type;

    /**
     * 错误消息。
     */
    private String errMsg;
}