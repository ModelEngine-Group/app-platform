/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.file.extract;

import modelengine.fitframework.annotation.Genericable;

import java.util.List;

/**
 * Excel文件提取器的抽象接口。
 *
 * @author 黄政炫
 * @since 2025-09-06
 */
public interface FileExtractor {
    /**
     * 提取文件函数。
     *
     * @param fileUrl 表示文件路径 {@link String}。
     * @return 表示提取的文件信息的 {@link String}。
     */
    @Genericable(id = "modelengine.fit.jade.file.extractFile")
    String extractFile(String fileUrl);

    /**
     * 返回提取器支持文件类型。
     *
     * @return 支持的枚举常量类型列表 {@link List}{@code <}{@link String}{@code >}。
     */
    @Genericable(id = "modelengine.fit.jade.file.getFileTypes")
    List<String> supportedFileTypes();
}
