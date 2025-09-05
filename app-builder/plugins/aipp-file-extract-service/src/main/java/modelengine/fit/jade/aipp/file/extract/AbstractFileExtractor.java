/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.file.extract;

import modelengine.fitframework.annotation.Genericable;

public interface AbstractFileExtractor {
    /**
     *
     * @param fileUrl 文件路径
     * @return 表示提取的文件信息的 {@link String}。
     */
    @Genericable(id = "extract-file")
    String extractFile(String fileUrl);

    /**
     *
     * @return 表示返回的文件枚举类型
     */
    @Genericable(id = "get-fileType")
    FileTypeConstant.FileType supportedFileType();

}
