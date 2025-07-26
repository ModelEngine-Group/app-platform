/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 应用标识信息。
 *
 * @author 宋永坦
 * @since 2025-07-24
 */
@Data
@AllArgsConstructor
public class AppIdentifier {
    private String tenantId;
    private String aippId;
    private String version;
}