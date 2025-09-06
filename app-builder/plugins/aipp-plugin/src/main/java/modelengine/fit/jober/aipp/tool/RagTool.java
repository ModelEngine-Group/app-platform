/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool;

import modelengine.fel.core.document.Document;
import modelengine.fitframework.annotation.Genericable;

import java.util.List;

/**
 * Rag 工具。
 *
 * @author 刘信宏
 * @since 2024-06-26
 */
public interface RagTool {
    /**
     * 提取文件内容。
     *
     * @param content 表示检索内容的 {@link String}。
     * @return 表示文档列表的 {@link List}{@code <}{@link Document}{@code >}。
     */
    @Genericable("modelengine.fit.jober.aipp.tool.rag.contentToDocs")
    List<Document> contentToDocs(String content);
}
