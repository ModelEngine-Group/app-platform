/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户自定义灵感大全的结构体
 *
 * @author 陈潇文
 * @since 2024-10-19
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InspirationPo {
    private String aippId;
    private String parentId;
    private String categoryId;
    private String inspirationId;
    private String value;
    private String createUser;
}
