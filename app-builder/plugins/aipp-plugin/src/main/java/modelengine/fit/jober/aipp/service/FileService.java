/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jober.aipp.dto.FileRspDto;
import modelengine.fit.jober.aipp.dto.FormFileDto;
import modelengine.fit.jober.aipp.dto.GenerateImageDto;

import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.http.entity.PartitionedEntity;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.server.HttpClassicServerResponse;

import java.io.IOException;

/**
 * 文件服务接口。
 *
 * @author 陈潇文
 * @author yangxiangyu
 * @since 2024/11/18
 */
public interface FileService {
    /**
     * 生成图片接口
     *
     * @param imageDto 图片参数
     * @return base64编码字符串图片
     */
    Rsp<String> generateImage(GenerateImageDto imageDto);

    /**
     * 从远端下载文件或者从nas下载文件
     *
     * @param context 操作上下文的 {@link OperationContext}
     * @param fileCanonicalPath 文件的规范路径
     * @param fileName 文件名
     * @param httpClassicServerResponse Http响应
     * @return 文件实体
     * @throws IOException 文件读取异常
     */
    FileEntity getFile(OperationContext context, String fileCanonicalPath, String fileName,
            HttpClassicServerResponse httpClassicServerResponse) throws IOException;

    /**
     * 上传文件
     *
     * @param context 操作上下文的 {@link OperationContext}
     * @param tenantId 租户ID
     * @param fileName 文件名
     * @param aippId AIPP ID
     * @param receivedFile 接收到的文件
     * @return 文件响应DTO
     */
    FileRspDto uploadFile(OperationContext context, String tenantId, String fileName, String aippId,
            FileEntity receivedFile) throws IOException;

    /**
     * 上传表单文件。
     *
     * @param receivedFile 表示表单文件的 {@link PartitionedEntity}。
     * @param fileName 表示表单文件名称的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示表单文件DTO的 {@link FormFileDto}。
     * @throws IOException 表示IO异常的 {@link IOException}。
     */
    FormFileDto uploadSmartForm(PartitionedEntity receivedFile, String fileName, OperationContext context)
            throws IOException;

    /**
     * 获取表单模板
     *
     * @param httpRequest 表示Http请求的 {@link HttpClassicServerRequest}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示表单模板文件的 {@link FileEntity}。
     * @throws IOException 文件读取异常。
     */
    FileEntity getSmartFormTemplate(HttpClassicServerRequest httpRequest, OperationContext context) throws IOException;
}
