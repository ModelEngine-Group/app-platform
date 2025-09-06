/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * AppUtils测试类
 *
 * @author 陈潇文
 * @since 2024-08-26
 */
public class AppUtilsTest {
    @Test
    void testReplaceAsterisks() {
        List<String> excludeNames = Arrays.asList("*name*", "age");
        List<String> results = AppUtils.replaceAsterisks(excludeNames);
        Assertions.assertEquals(results.get(0), "{name}");
        Assertions.assertEquals(results.get(1), "age");
    }
}
