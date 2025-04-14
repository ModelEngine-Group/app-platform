/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.model.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import modelengine.jade.common.po.BasePo;

import java.time.LocalDateTime;

/**
 * 用户模型关系信息用于前端表单展示。
 *
 * @author lizhichao
 * @since 2025/4/8
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class UserModelDetailPo extends BasePo {
    /**
     * 表示创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 模型 ID。
     */
    private String modelId;

    /**
     * 用户标识。
     */
    private String userId;

    /**
     * 模型名称。
     */
    private String modelName;

    /**
     * 模型访问地址。
     */
    private String baseUrl;

    /**
     * 是否为默认模型（1表示默认，0表示非默认）。
     */
    private int isDefault;
}