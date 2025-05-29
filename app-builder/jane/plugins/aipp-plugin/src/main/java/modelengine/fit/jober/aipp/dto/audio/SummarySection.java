/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.audio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * SummarySection
 *
 * @author 易文渊
 * @since 2024/1/8
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummarySection {
    @Property(description = "音频坐标, 格式HH:mm:ss")
    private String position;
    @Property(description = "摘要标题")
    private String title;
    @Property(description = "片段摘要")
    private String text;
}