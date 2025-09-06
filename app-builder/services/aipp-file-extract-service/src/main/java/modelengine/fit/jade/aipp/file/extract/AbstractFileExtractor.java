/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.file.extract;

import modelengine.fit.jober.aipp.service.OperatorService;
import modelengine.fitframework.annotation.Genericable;

/**
 * Excel文件提取器的抽象接口。
 *
 * @author jsbjfkbsjk
 * @since 2025-9-6
 */
public interface AbstractFileExtractor {
    /**
     * 提取文件函数
     *
     * @param fileUrl 文件路径
     * @return 表示提取的文件信息的 {@link String}。
     */
    @Genericable(id = "extract-file")
    String extractFile(String fileUrl);

    /**
     * 返回提取器支持文件类型
     *
     * @return 表示返回的文件枚举类型 {@link OperatorService.FileType}
     */
    @Genericable(id = "get-fileType")
    OperatorService.FileType supportedFileType();
}
