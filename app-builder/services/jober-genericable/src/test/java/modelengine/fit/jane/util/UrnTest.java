/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import modelengine.fit.jane.util.Urn;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * UrnTest
 *
 * @author 梁济时
 * @since 2023/11/28
 */
@DisplayName("测试 URN 工具类")
class UrnTest {
    private static final String TYPE = "rooms";

    private static final String ID = "33";

    private static final String CHILD_TYPE = "tables";

    private static final String CHILD_ID = "44";

    private static final String URN = "urn:rooms:33";

    private static final String CHILD_URN = "urn:rooms:33:tables:44";

    @Test
    @DisplayName("从文本中解析简单 URN")
    void should_parse_simple_urn_from_text() {
        Urn urn = Urn.parse(URN);
        assertNull(urn.parent());
        assertEquals(TYPE, urn.type());
        assertEquals(ID, urn.id());
    }

    @Test
    @DisplayName("从文本中解析复杂 URN")
    void should_parse_complex_urn_from_text() {
        Urn urn = Urn.parse(CHILD_URN);
        assertNull(urn.parent().parent());
        assertEquals(TYPE, urn.parent().type());
        assertEquals(ID, urn.parent().id());
        assertEquals(CHILD_TYPE, urn.type());
        assertEquals(CHILD_ID, urn.id());
    }

    @Test
    @DisplayName("将简单 URN 转为字符串表现形式")
    void should_return_text_of_simple_urn() {
        Urn urn = Urn.create(TYPE, ID);
        String text = urn.toString();
        assertEquals(URN, text);
    }

    @Test
    @DisplayName("将复杂 URN 转为字符串表现形式")
    void should_return_text_of_complex_urn() {
        Urn parentUrn = Urn.create(TYPE, ID);
        Urn urn = Urn.create(parentUrn, CHILD_TYPE, CHILD_ID);
        String text = urn.toString();
        assertEquals(CHILD_URN, text);
    }
}