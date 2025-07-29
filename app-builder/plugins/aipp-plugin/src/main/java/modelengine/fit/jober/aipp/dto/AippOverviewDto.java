/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

import java.time.LocalDateTime;

/**
 * Aipp概况
 *
 * @author 刘信宏
 * @since 2023-12-08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AippOverviewDto {
    @Property(description = "aipp id", name = "aipp_id")
    private String aippId;

    @Property(description = "aipp 名称", example = "aipp")
    private String name;

    @Property(description = "aipp 版本", example = "1.0.0")
    private String version;

    @Property(description = "发布时间", name = "publish_at")
    private LocalDateTime publishAt;

    @Property(description = "创建时间", name = "created_at")
    private LocalDateTime createdAt;

    @Property(description = "更新时间", name = "updated_at")
    private LocalDateTime updatedAt;

    @Property(description = "创建人")
    private String updater;

    @Property(description = "创建人")
    private String creator;

    @Property(description = "aipp状态")
    private String status;
}
