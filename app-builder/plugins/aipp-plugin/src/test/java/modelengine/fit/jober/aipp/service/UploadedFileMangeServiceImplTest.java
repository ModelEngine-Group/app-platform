/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service;

import modelengine.fit.jober.aipp.mapper.AippUploadedFileMapper;
import modelengine.fit.jober.aipp.service.impl.UploadedFileMangeServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

/**
 * 上传文件相关接口测试。
 *
 * @author 孙怡菲
 * @since 2024-09-19
 */
@ExtendWith(MockitoExtension.class)
class UploadedFileMangeServiceImplTest {
    @Mock
    private AippUploadedFileMapper aippUploadedFileMapper;

    private UploadedFileManageService uploadedFileManageService;

    @BeforeEach
    public void before() {
        uploadedFileManageService = new UploadedFileMangeServiceImpl(aippUploadedFileMapper);
    }

    @Nested
    @DisplayName("测试清理文件记录功能")
    class TestCleanFiles {
        @Test
        @DisplayName("appid列表为空时，执行成功")
        void testCleanFilesWhenAppIdIsEmpty() {
            Assertions.assertDoesNotThrow(() -> uploadedFileManageService.cleanAippFiles(Collections.emptyList()));
        }

        @Test
        @DisplayName("appid列表不为空时，执行成功")
        void testCleanFilesWhenAppIdIsNotEmpty() {
            Assertions.assertDoesNotThrow(
                    () -> uploadedFileManageService.cleanAippFiles(Collections.singletonList("app_id")));
        }
    }
}