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
 * 系统配置结构体
 *
 * @author 张越
 * @since 2024-11-30
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AippSystemConfigPo {
    private Long id;
    private String configKey;
    private String configValue;
    private String configGroup;
    private String configParent;
}
