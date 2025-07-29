/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link ContentProcessUtils} 的单元测试。
 *
 * @author 孙怡菲
 * @since 2025-04-29
 */
@DisplayName("测试ContentProcessUtils")
class ContentProcessUtilsTest {
    @Test
    @DisplayName("测试去除模型生成内容中的内部推理内容")
    void filterReasoningContent() {
        String input = "<think>This is some reasoning</think> some content";
        String expected = "some content";
        assertEquals(expected, ContentProcessUtils.filterReasoningContent(input));
    }
}