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
 * {@link AippStringUtils} 的单元测试类
 *
 * @author 姚江
 * @since 2024-07-15
 */
@ExtendWith(MockitoExtension.class)
public class AippStringUtilsTest {
    @Test
    @DisplayName("AippStringUtils.isPreview()测试")
    void testIsPreview() {
        String version = "1.0.0";
        String versionPreview = "1.1.1-abcdab";
        Assertions.assertTrue(AippStringUtils.isPreview(versionPreview));
        Assertions.assertFalse(AippStringUtils.isPreview(version));
    }

    @Test
    @DisplayName("AippStringUtils.textLenLimit()测试")
    void testTextLenLimit() {
        String text1 = "123456";
        String text2 = "123456789";
        String r1 = AippStringUtils.textLenLimit(text1, 7);
        String r2 = AippStringUtils.textLenLimit(text2, 7);
        Assertions.assertEquals("123456", r1);
        Assertions.assertEquals("1234567", r2);
    }

    @Test
    @DisplayName("AippStringUtils.outlineLenLimit()测试")
    void testOutlineLenLimit() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < AippStringUtils.MAX_OUTLINE_LINE; i++) {
            sb.append("12\n");
        }
        String excepted1 = sb.toString();
        String outline1 = sb.toString();
        String excepted2 = sb.append("12").toString();
        String outline2 = sb.append("\n12\n12\n12\n").toString();
        String r1 = AippStringUtils.outlineLenLimit(outline1);
        String r2 = AippStringUtils.outlineLenLimit(outline2);
        Assertions.assertEquals(excepted1, r1);
        Assertions.assertEquals(excepted2, r2);
    }

    @Test
    @DisplayName("AippStringUtils.getIntegerFromStr()测试")
    void testGetIntegerFromStr() {
        Integer i = Assertions.assertDoesNotThrow(() -> AippStringUtils.getIntegerFromStr("12"));
        Assertions.assertEquals(12, i);

        Integer i1 = Assertions.assertDoesNotThrow(() -> AippStringUtils.getIntegerFromStr("pp"));
        Assertions.assertNull(i1);
    }

    @Test
    @DisplayName("AippStringUtils.trimLine()测试")
    void testTrimLine() {
        String excepted = "123456\\\"\\\\";
        String line = " 1\n2\t3\r4\b5\f6\"\\";
        String result = Assertions.assertDoesNotThrow(() -> AippStringUtils.trimLine(line));
        Assertions.assertEquals(excepted, result);
    }
}
