/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import modelengine.fel.tool.model.transfer.ToolData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AppBuilder waterFlow列表返回数据结构
 *
 * @author 陈潇文
 * @since 2024-05-15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderWaterFlowInfoDto {
    /**
     * itemData store里waterFlow的元数据
     */
    private ToolData itemData;

    /**
     * tenantId 租户id
     */
    private String tenantId;

    /**
     * appId appId
     */
    private String appId;

    /**
     * version 版本号
     */
    private String version;
}
