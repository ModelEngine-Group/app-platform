/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool;

import modelengine.fel.tool.annotation.Group;
import modelengine.fel.tool.annotation.ToolMethod;
import modelengine.fit.jober.aipp.service.OperatorService;
import modelengine.fitframework.annotation.Genericable;

/**
 * 文件内容提取
 *
 * @author 孙怡菲
 * @since 2024-06-08
 */
@Group(name = "defGroup-aipp-file-extract-tool")
public interface FileExtractor {
    /**
     * 文件提取genericable接口gid
     */
    String FILE_EXTRACTOR_GID = "modelengine.fit.jober.aipp.tool.file.extractor";

    /**
     * 提取文件内容
     *
     * @param fileUrl 待提取的文件地址
     * @return 文件内容。
     */
    @ToolMethod(name = "file_extract", description = "提取文件信息")
    @Genericable(FILE_EXTRACTOR_GID)
    String extractFile(String fileUrl);

    OperatorService.FileType supportedType();
}
