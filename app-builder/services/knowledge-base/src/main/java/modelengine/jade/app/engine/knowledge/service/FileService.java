/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.knowledge.service;

import modelengine.fitframework.annotation.Genericable;
import modelengine.jade.app.engine.knowledge.dto.FileUploadRequest;

import java.io.IOException;

/**
 * 文件服务接口类
 *
 * @since 2024/05/17
 */
public interface FileService {
    /**
     * 文件导入接口
     *
     * @param knowledgeId 知识库id
     * @param knowledgeTableId 知识表id
     * @param fileUploadRequest 文件上传请求
     * @throws IOException IO异常
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.file.import")
    void importFiles(Long knowledgeId, Long knowledgeTableId, FileUploadRequest fileUploadRequest) throws IOException;

    /**
     * 删除特定知识表文件接口
     *
     * @param knowledgeId 知识库id
     * @param knowledgeTableId 知识表id
     * @param filename 文件名称
     * @return true-删除成功
     * @throws IllegalArgumentException 参数异常
     */
    @Genericable(id = "modelengine.jade.app.engine.knowledge.service.file.delete")
    boolean deleteFiles(Long knowledgeId, Long knowledgeTableId, String filename) throws IllegalArgumentException;
}
