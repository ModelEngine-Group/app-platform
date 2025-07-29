/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流式日志的参数类.
 *
 * @author 张越
 * @since 2024-05-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamLogParam {
    private String aippInstanceId;
}