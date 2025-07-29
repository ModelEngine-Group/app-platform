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
 * AudioSplitInfo
 *
 * @author 易文渊
 * @since 2024/1/9
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AudioSplitInfo {
    @Property(description = "音频目录")
    private String dirPath;
    @Property(description = "音频分段大小")
    private int segmentSize;
}