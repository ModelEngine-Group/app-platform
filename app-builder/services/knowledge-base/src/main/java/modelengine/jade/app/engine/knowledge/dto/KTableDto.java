/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.jade.app.engine.knowledge.dto.enums.KStorageType;
import modelengine.jade.app.engine.knowledge.dto.enums.TableFormat;

import java.util.Date;

/**
 * 知识表。
 *
 * @since 2024-05-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KTableDto {
    private Long id;

    private String name;

    private Long repositoryId;

    private KStorageType serviceType;

    private Long serviceId;

    private TableFormat format;

    private Long recordNum;

    private Integer status;

    private Date createdAt;

    private Date updatedAt;
}
