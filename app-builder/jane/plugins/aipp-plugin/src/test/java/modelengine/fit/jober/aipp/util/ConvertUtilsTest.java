/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.util;

import modelengine.fit.jober.aipp.domain.AppBuilderApp;
import modelengine.fit.jober.aipp.dto.AippCreateDto;
import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fit.jober.aipp.dto.AppBuilderAppDto;
import modelengine.fit.jober.aipp.dto.AppBuilderFlowGraphDto;
import modelengine.fit.jober.aipp.genericable.entity.AippCreate;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link ConvertUtils} 的单元测试类
 *
 * @author 姚江
 * @since 2024-07-16
 */
@ExtendWith(MockitoExtension.class)
public class ConvertUtilsTest {
    @Test
    @DisplayName("convertToAippDtoFromAppBuilderAppDto")
    void testConvertToAippDtoFromAppBuilderAppDto() {
        Map<String, Object> attributes = MapBuilder.get(() -> new HashMap<String, Object>())
                .put("description", "h")
                .put("icon", "i")
                .put("app_type", "写作助手")
                .build();
        String appCategory = "chatbot";
        AppBuilderAppDto build = AppBuilderAppDto.builder()
                .config(null)
                .attributes(attributes)
                .flowGraph(AppBuilderFlowGraphDto.builder().appearance(new HashMap<>()).build())
                .publishUrl("publishUrl")
                .id("id")
                .version("1.0.0")
                .type("app")
                .publishedDescription("pd")
                .appCategory(appCategory)
                .build();
        AippDto aippDto = Assertions.assertDoesNotThrow(() -> ConvertUtils.convertToAippDtoFromAppBuilderAppDto(build));
        Assertions.assertEquals("h", aippDto.getDescription());
        Assertions.assertEquals("1.0.0", aippDto.getVersion());
        Assertions.assertEquals(appCategory, aippDto.getAppCategory());
    }

    @Test
    @DisplayName("convertToAippDtoFromAppBuilderApp")
    void testConvertToAippDtoFromAppBuilderApp() {
        String appCategory = "agent";
        AppBuilderApp app = AppBuilderApp.builder()
                .attributes(MapBuilder.get(() -> new HashMap<String, Object>())
                        .put("description", "h")
                        .put("icon", "i")
                        .put("app_type", "写作助手")
                        .build())
                .version("1.0.0")
                .name("app")
                .type("type")
                .id("id")
                .appCategory(appCategory)
                .build();
        AippDto aippDto = Assertions.assertDoesNotThrow(() -> ConvertUtils.convertToAippDtoFromAppBuilderApp(app));

        Assertions.assertEquals("h", aippDto.getDescription());
        Assertions.assertEquals("1.0.0", aippDto.getVersion());
        Assertions.assertEquals(appCategory, aippDto.getAppCategory());
    }

    @Test
    @DisplayName("toAippCreate")
    void testToAippCreate() {
        AippCreateDto dto =
                AippCreateDto.builder().aippId("aippId").toolUniqueName("uniqueName").version("1.0.0").build();

        AippCreate aippCreate = Assertions.assertDoesNotThrow(() -> ConvertUtils.toAippCreate(dto));

        Assertions.assertEquals("uniqueName", aippCreate.getToolUniqueName());
        Assertions.assertEquals("aippId", aippCreate.getAippId());
        Assertions.assertEquals("1.0.0", aippCreate.getVersion());
    }

    @Test
    @DisplayName("toAippCreateDto")
    void testToAippCreateDto() {
        AippCreate create = AippCreate.builder().aippId("aippId").toolUniqueName("uniqueName").version("1.0.0").build();

        AippCreateDto dto = Assertions.assertDoesNotThrow(() -> ConvertUtils.toAippCreateDto(create));

        Assertions.assertEquals("uniqueName", dto.getToolUniqueName());
        Assertions.assertEquals("aippId", dto.getAippId());
        Assertions.assertEquals("1.0.0", dto.getVersion());
    }
}
