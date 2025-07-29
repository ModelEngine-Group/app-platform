/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.common.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import modelengine.fit.jane.common.enums.DirectionEnum;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fit.jober.common.exceptions.JobberParamException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * 排序条件解析器测试类
 *
 * @author 刘信宏
 * @since 2023-12-08
 */
@DisplayName("排序条件解析测试集合")
public class SortConditionParserTest {
    @Test
    @DisplayName("测试解析正常排序条件的场景")
    public void testWhenParserNormalStrThenOk() {
        SortConditionParser parser = SortConditionParser.builder()
                .encodeSorts(Arrays.asList("create_at,ascend", "update_at,DESCEND"))
                .build();
        assertEquals(parser.getDecodeSorts().size(), 2);
        assertEquals(parser.getDecodeSorts().get(0).getName(), "create_at");
        assertEquals(parser.getDecodeSorts().get(0).getDir(), DirectionEnum.ASCEND);
        assertEquals(parser.getDecodeSorts().get(1).getName(), "update_at");
        assertEquals(parser.getDecodeSorts().get(1).getDir(), DirectionEnum.DESCEND);
    }

    @Test
    @DisplayName("测试指定无效排序方向的场景")
    public void testWhenParserUnknownDirectionThenFail() {
        Assertions.assertThrows(JobberParamException.class, () -> SortConditionParser.builder()
                .encodeSorts(Collections.singletonList("create_at,unknown_direct"))
                .build());
    }

    @Test
    @DisplayName("测试无效格式的场景")
    public void testWhenParserUnknownFormatThenFail() {
        Assertions.assertThrows(JobberParamException.class,
                () -> SortConditionParser.builder().encodeSorts(Collections.singletonList("create_at,")).build());
        Assertions.assertThrows(JobberParamException.class,
                () -> SortConditionParser.builder().encodeSorts(Collections.singletonList("create_at")).build());
        Assertions.assertThrows(JobberParamException.class,
                () -> SortConditionParser.builder().encodeSorts(Collections.singletonList(",create_at")).build());
        Assertions.assertThrows(JobberParamException.class,
                () -> SortConditionParser.builder().encodeSorts(Collections.singletonList(",ascend")).build());
        Assertions.assertThrows(JobberParamException.class,
                () -> SortConditionParser.builder().encodeSorts(Collections.singletonList("descend")).build());
        Assertions.assertThrows(JobberParamException.class,
                () -> SortConditionParser.builder().encodeSorts(Collections.singletonList(",")).build());
    }

    @Test
    @DisplayName("测试builder同时指定解码前和解码后的场景")
    public void testWhenBuilderWithEncodeAndDecodeSortsThenFail() {
        Assertions.assertThrows(BadRequestException.class, () -> SortConditionParser.builder()
                .encodeSorts(Collections.singletonList("create_time,ascend"))
                .decodeSorts(Collections.singletonList(
                        SortConditionParser.Sorter.builder().name("update_at").dir(DirectionEnum.ASCEND).build()))
                .build());

        Assertions.assertThrows(BadRequestException.class, () -> SortConditionParser.builder()
                .encodeSorts(Collections.singletonList("create_at,ascend"))
                .decodeSorts(Collections.emptyList())
                .build());

        Assertions.assertThrows(BadRequestException.class, () -> SortConditionParser.builder()
                .decodeSorts(Collections.emptyList())
                .encodeSorts(Collections.singletonList("create_at,ascend"))
                .build());
    }
}
