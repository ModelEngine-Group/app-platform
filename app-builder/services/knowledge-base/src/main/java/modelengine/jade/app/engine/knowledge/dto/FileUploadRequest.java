/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.dto;

import modelengine.jade.app.engine.knowledge.utils.DecodeUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.InputStream;

/**
 * 文件上传请求体
 *
 * @since 2024-05-21
 */
@Getter
@AllArgsConstructor
public class FileUploadRequest {
    private InputStream entity;

    private String fileName;

    /**
     * 获取文件名称
     *
     * @return 解码后的文件名称
     */
    public String getFileName() {
        return DecodeUtil.decodeStr(fileName);
    }
}
