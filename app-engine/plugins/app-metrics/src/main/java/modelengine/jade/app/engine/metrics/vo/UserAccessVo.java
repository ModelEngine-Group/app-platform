/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.metrics.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserAccessVO类消息处理策略
 *
 * @author 陈霄宇
 * @since 2024/05/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccessVo {
    private String createUser;
    private int accessCount;
}
