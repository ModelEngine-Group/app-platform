/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * {@link AppBuilderSaveConfigDto} 的测试类。
 *
 * @author 鲁为
 * @since 2024-10-29
 */
public class AppBuilderSaveConfigDtoTest {
    @Test
    @DisplayName("测试无参构造传输类")
    public void testNoArgsConstruction() {
        AppBuilderSaveConfigDto dto = new AppBuilderSaveConfigDto();
        List<AppBuilderConfigFormPropertyDto> list = dto.getInput();
        String graph = dto.getGraph();
        Assertions.assertNull(list);
        Assertions.assertNull(graph);
    }

    @Test
    @DisplayName("测试满参构造传输类")
    public void testAllArgsConstruction() {
        AppBuilderSaveConfigDto dto = new AppBuilderSaveConfigDto(Collections.EMPTY_LIST, "");
        Assertions.assertEquals(Collections.EMPTY_LIST, dto.getInput());
    }

    @Test
    @DisplayName("测试构建器构造传输类")
    public void testBuilderConstruction() {
        AppBuilderSaveConfigDto dto = AppBuilderSaveConfigDto.builder()
                .input(Collections.EMPTY_LIST)
                .graph("")
                .build();
        Assertions.assertEquals(Collections.EMPTY_LIST, dto.getInput());
    }
}
