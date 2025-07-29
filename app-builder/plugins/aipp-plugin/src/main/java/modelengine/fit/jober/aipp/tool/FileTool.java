/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool;

import modelengine.fel.tool.annotation.Group;
import modelengine.fel.tool.annotation.ToolMethod;

import modelengine.fitframework.annotation.Genericable;

/**
 * 文件工具类。
 *
 * @author 孙怡菲
 * @since 2024-05-30
 */
@Group(name = "defGroup-aipp-file-tool")
public interface FileTool {
    /**
     * 提取文件内容。
     *
     * @param filePath 表示文件路径 {@link String}。
     * @return 表示提取的文件内容 {@link String}。
     */
    @ToolMethod(name = "def-aipp-file-tool-extract", description = "该方法解析文件内容")
    @Genericable("modelengine.fit.jober.aipp.tool.extract.file")
    String extractFile(String filePath);
}
