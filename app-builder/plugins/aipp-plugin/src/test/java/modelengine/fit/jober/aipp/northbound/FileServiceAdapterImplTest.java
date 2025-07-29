/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.northbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.http.entity.FileEntity;
import modelengine.fit.http.entity.PartitionedEntity;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.common.exception.AippTaskNotFoundException;
import modelengine.fit.jober.aipp.domains.appversion.service.AppVersionService;
import modelengine.fit.jober.aipp.dto.FileRspDto;
import modelengine.fit.jober.aipp.dto.chat.FileUploadInfo;
import modelengine.fit.jober.aipp.genericable.adapter.FileServiceAdapter;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.service.AppBuilderAppServiceImplTest;
import modelengine.fit.jober.aipp.service.FileService;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.util.UUID;

/**
 * {@link FileServiceAdapterImpl} 的单元测试。
 *
 * @author 曹嘉美
 * @since 2024-12-31
 */
@DisplayName("测试 FileServiceAdapterImpl")
public class FileServiceAdapterImplTest {
    private final FileService fileService = mock(FileService.class);
    private final String tenantId = UUID.randomUUID().toString();
    private final PartitionedEntity partitionedEntity = mock(PartitionedEntity.class);
    private final AppVersionService appVersionService = mock(AppVersionService.class);
    private final FileServiceAdapter fileServiceAdapterImpl = new FileServiceAdapterImpl(fileService,
            appVersionService);

    private final FileEntity fileEntity = mock(FileEntity.class);

    private OperationContext operationContext;

    @BeforeEach
    void setUp() {
        operationContext = new OperationContext();
    }

    @Test
    @DisplayName("测试上传文件。")
    public void testUploadFile() throws IOException, AippTaskNotFoundException {
        String appSuiteId = "appSuiteId";
        String appId = "testApp";
        AppBuilderAppPo appPo = AppBuilderAppPo.builder().appId(appId).appSuiteId(appSuiteId).build();
        when(appVersionService.retrieval(anyString())).thenReturn(AppBuilderAppServiceImplTest.mockAppVersion(appPo));
        FileRspDto fileRspDto = new FileRspDto();
        when(fileService.uploadFile(any(), any(), any(), any(), any())).thenReturn(fileRspDto);
        String fileName = "testFile";
        FileUploadInfo result = fileServiceAdapterImpl.uploadFile(operationContext, tenantId, fileName, appId,
            partitionedEntity);
        assertThat(result.getFileName()).isEqualTo(fileRspDto.getFileName());
        assertThat(result.getFilePath()).isEqualTo(fileRspDto.getFilePath());
        verify(fileService, times(1)).uploadFile(operationContext, tenantId, fileName, appSuiteId, fileEntity);
    }
}