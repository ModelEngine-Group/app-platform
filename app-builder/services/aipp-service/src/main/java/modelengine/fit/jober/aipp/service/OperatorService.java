/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * 提供操作服务
 *
 * @author 孙怡菲
 * @since 2024/5/10
 */
public interface OperatorService {
    /**
     * 文件类型枚举
     */
    enum FileType {
        PDF,
        WORD,
        EXCEL,
        IMAGE,
        AUDIO,
        TXT,
        HTML,
        MARKDOWN,
        CSV
    }

    /**
     * 文件提取
     *
     * @param fileUrl 表示文件路径的 {@link String}.
     * @param optionalFileType 表示可选文件类型的 {@link FileType}。
     * @return 文件字节内容
     */
    String fileExtractor(String fileUrl, Optional<FileType> optionalFileType);

    /**
     * 提取大纲
     *
     * @param file 文件
     * @param fileType 文件类型
     * @return 大纲
     */
    String outlineExtractor(File file, FileType fileType);

    /**
     * 创建文档
     *
     * @param instanceId 实例id
     * @param fileName 文件名
     * @param txt 文本
     * @return 文件
     * @throws IOException IO异常
     */
    File createDoc(String instanceId, String fileName, String txt) throws IOException;
}
