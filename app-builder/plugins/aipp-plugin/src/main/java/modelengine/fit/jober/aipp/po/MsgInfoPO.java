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
 * 查询instance id对应Msg的结构体
 *
 * @author 孙怡菲
 * @since 2024-12-09
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgInfoPO {
    private String instanceId;
    private String logData;
}
