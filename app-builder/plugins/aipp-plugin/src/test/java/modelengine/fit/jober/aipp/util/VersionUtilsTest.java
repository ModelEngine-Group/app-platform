/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * {@link VersionUtils} 的单元测试
 *
 * @author 姚江
 * @since 2024-07-17
 */
@ExtendWith(MockitoExtension.class)
public class VersionUtilsTest {
    @Test
    @DisplayName("测试isValidVersion方法：全部场景")
    void testIsValidVersion() {
        Assertions.assertTrue(() -> VersionUtils.isValidVersion("1.2.3"));
        Assertions.assertFalse(() -> VersionUtils.isValidVersion("1.3.c"));
        Assertions.assertFalse(() -> VersionUtils.isValidVersion("1.3"));
        Assertions.assertFalse(() -> VersionUtils.isValidVersion("1..2"));
    }

    @Test
    @DisplayName("测试buildPreviewVersion方法")
    void testBuildPreviewVersion() {
        String baseVersion = "1.2.3";
        String version = Assertions.assertDoesNotThrow(() -> VersionUtils.buildPreviewVersion(baseVersion));
        Assertions.assertEquals(12, version.length());
        Assertions.assertTrue(version.contains(VersionUtils.CONNECTION_SIGN));
    }
}
