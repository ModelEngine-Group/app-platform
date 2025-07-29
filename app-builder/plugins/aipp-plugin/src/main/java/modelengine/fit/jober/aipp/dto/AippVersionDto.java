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
import modelengine.fitframework.annotation.Property;

import java.time.LocalDateTime;

/**
 * Aipp版本信息
 *
 * @author 刘信宏
 * @since 2024-01-26
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AippVersionDto {
    @Property(description = "aipp 版本", example = "1.0.0")
    private String version;

    @Property(description = "aipp状态")
    private String status;

    @Property(description = "创建人")
    private String creator;

    @Property(description = "创建时间", name = "created_at")
    private LocalDateTime createdAt;
}
