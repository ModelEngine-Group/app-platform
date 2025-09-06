/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.northbound;

import lombok.AllArgsConstructor;
import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.http.entity.PartitionedEntity;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.dto.chat.FileUploadInfo;
import modelengine.fit.jober.aipp.genericable.adapter.FileServiceAdapter;
import modelengine.fit.jober.aipp.service.FileService;
import modelengine.fit.jober.aipp.util.AippFileUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.beans.BeanUtils;

import java.io.IOException;
import java.util.List;

/**
 * {@link FileService} 的适配器类的实现类。
 *
 * @author 曹嘉美
 * @since 2024-12-20
 */
@Component
@AllArgsConstructor
public class FileServiceAdapterImpl implements FileServiceAdapter {
    private final FileService fileService;
    private final AppVersionService appVersionService;

    @Override
    public FileUploadInfo uploadFile(OperationContext context, String tenantId, String fileName, String appId,
            PartitionedEntity receivedFile) throws IOException {
        AppVersion appVersion = this.appVersionService.retrieval(appId);
        List<FileEntity> files = AippFileUtils.getFileEntity(receivedFile);
        if (files.isEmpty()) {
            throw new AippException(AippErrCode.UPLOAD_FAILED);
        }
        return BeanUtils.copyProperties(
                this.fileService.uploadFile(context, tenantId, fileName, appVersion.getData().getAppSuiteId(),
                        files.get(0)), FileUploadInfo.class);
    }
}
