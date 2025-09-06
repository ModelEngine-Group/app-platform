/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 服务上用户模型列表的传输类。
 *
 * @author 李智超
 * @since 2025-04-15
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModelDetailDto {
    /**
     * 表示创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 表示模型标识。
     */
    private String modelId;

    /**
     * 表示用户标识。
     */
    private String userId;

    /**
     * 表示模型名称。
     */
    private String modelName;

    /**
     * 表示模型访问地址。
     */
    private String baseUrl;

    /**
     * 表示是否为默认模型（1表示默认，0表示非默认）。
     */
    private int isDefault;

    /**
     * 表示模型类型。
     */
    private String type;
}
