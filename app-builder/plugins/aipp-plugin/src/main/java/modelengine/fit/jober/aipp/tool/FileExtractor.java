/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool;

import modelengine.fitframework.annotation.Genericable;

import java.io.File;

/**
 * 文件内容提取
 *
 * @author 孙怡菲
 * @since 2024-06-08
 */
public interface FileExtractor {
    /**
     * 文件提取genericable接口gid
     */
    String FILE_EXTRACTOR_GID = "modelengine.fit.jober.aipp.tool.file.extractor";

    /**
     * 提取文件内容
     *
     * @param file 待提取的文件
     * @return 文件内容。
     */
    @Genericable(FILE_EXTRACTOR_GID)
    String extractFile(File file);
}
