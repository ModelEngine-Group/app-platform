/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.genericable.adapter;

import modelengine.fit.http.entity.PartitionedEntity;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.dto.chat.FileUploadInfo;

import java.io.IOException;

/**
 * 文件服务接口。
 *
 * @author 陈潇文
 * @author yangxiangyu
 * @since 2024/11/18
 */
public interface FileServiceAdapter {
    /**
     * 上传文件。
     *
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @param tenantId 表示租户唯一标识符的 {@link String}。
     * @param fileName 表示文件名的 {@link String}。
     * @param appId 表示应用唯一标识符的 {@link String}。
     * @param receivedFile 表示接收到的文件实体的 {@link PartitionedEntity}，
     * 此处由于底层服务使用到，因此需要传入参数，后续需改为在 {@code Http} 层面逻辑中处理。
     * @return 表示上传文件后的响应信息的 {@link FileUploadInfo}。
     * @throws IOException 当发生 I/O 异常时。
     */
    FileUploadInfo uploadFile(OperationContext context, String tenantId, String fileName, String appId,
            PartitionedEntity receivedFile) throws IOException;
}
