/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.jadeconfig;

import modelengine.fitframework.util.IoUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * {@link JadeConfig} 的测试类。
 *
 * @author 孙怡菲
 * @since 2025-02-20
 */
class JadeConfigTest {
    @Test
    @DisplayName("测试获取节点信息")
    public void TestJadeConfig() throws IOException {
        ClassLoader classLoader = JadeConfigTest.class.getClassLoader();
        String appearance = IoUtils.content(classLoader, "appearance.txt");
        String startNodeId = "jade6qm5eg";
        Map<String, Object> input = Map.of("Question", "");

        JadeConfig jadeConfig = new JadeConfig(appearance);
        JadeShape startNode = jadeConfig.getShapeById(startNodeId).get();

        Assertions.assertEquals(startNodeId, startNode.getId());
        Assertions.assertEquals(Optional.of(input), startNode.getValue("input"));
    }
}