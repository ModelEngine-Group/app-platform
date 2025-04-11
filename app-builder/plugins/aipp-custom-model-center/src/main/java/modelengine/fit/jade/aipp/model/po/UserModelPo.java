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

/**
 * 用户模型关系信息 ORM 对象。
 *
 * @author lixin
 * @since 2025/3/11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class UserModelPo extends BasePo {
    private String userId;
    private String modelId;
    private String apiKey;
    private int isDefault;
}
