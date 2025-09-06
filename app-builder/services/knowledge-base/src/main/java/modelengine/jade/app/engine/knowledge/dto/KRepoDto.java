/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 知识库。
 *
 * @since 2024-05-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KRepoDto {
    private Long id;
    private String name;
    private Long ownerId;
    private String ownerName;
    private String description;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}