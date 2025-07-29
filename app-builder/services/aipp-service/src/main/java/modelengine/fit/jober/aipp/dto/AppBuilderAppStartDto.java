/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fit.jober.aipp.genericable.entity.AippCreate;

/**
 * 启动Aipp实例响应体
 *
 * @author 陈潇文
 * @since 2024-05-24
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderAppStartDto {
    /**
     * aippCreateDto 创建Aipp响应体
     */
    private AippCreate aippCreate;

    /**
     * instanceId 实例Id
     */
    private String instanceId;
}
