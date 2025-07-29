/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fit.http.annotation.RequestQuery;
import modelengine.fitframework.annotation.Property;

/**
 * 文字转语音的参数。
 *
 * @author 曹嘉美
 * @since 2025-01-13
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextTranslationParams {
    @RequestQuery(value = "voicePath", required = false)
    @Property(description = "语音文件的路径")
    private String voicePath;

    @RequestQuery(value = "fileName", required = false)
    @Property(description = "语音文件的名称")
    private String fileName;
}
