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

/**
 * 根据store_id从app_builder_app获取waterFlow部分信息的格式
 *
 * @author 陈潇文
 * @since 2024-05-15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreWaterFlowDto {
    /**
     * id appId
     */
    String id;

    /**
     * version 版本
     */
    String version;

    /**
     * tenantId 租户id
     */
    String tenantId;
}
