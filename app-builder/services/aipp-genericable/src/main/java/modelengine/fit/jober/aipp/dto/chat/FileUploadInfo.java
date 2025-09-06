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
import modelengine.fitframework.annotation.Property;

/**
 * 文件传输数据。
 *
 * @author: 曹嘉美
 * @since 2024-09-01
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadInfo {
    @Property(name = "文件名")
    private String fileName;

    @Property(name = "文件路径")
    private String filePath;

    @Property(name = "文件类型")
    private String fileType;
}
