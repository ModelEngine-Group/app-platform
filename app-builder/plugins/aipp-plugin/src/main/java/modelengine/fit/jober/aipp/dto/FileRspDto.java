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

/**
 * 文件传输响应Dto
 *
 * @author: 孙怡菲
 * @since 2024-09-01
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileRspDto {
    @Property(name = "file_name")
    private String fileName;

    @Property(name = "file_path")
    private String filePath;

    @Property(name = "file_type")
    private String fileType;
}
