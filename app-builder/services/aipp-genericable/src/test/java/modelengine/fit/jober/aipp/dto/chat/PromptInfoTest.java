/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.chat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * {@link PromptInfo} 的单元测试。
 *
 * @author 曹嘉美
 * @since 2024-12-31
 */
@DisplayName("测试 PromptInfo")
class PromptInfoTest {
    @Test
    @DisplayName("用构建器构建灵感大全元数据类时，返回成功")
    void constructAppBuilderPromptDtoAdapter() {
        PromptInfo.AppBuilderPromptVarDataDtoAdapter varData = PromptInfo.AppBuilderPromptVarDataDtoAdapter.builder()
                .key("var1")
                .var("variable1")
                .varType("String")
                .sourceType("API")
                .sourceInfo("some source info")
                .multiple(true)
                .build();

        PromptInfo.AppBuilderInspirationDtoAdapter inspiration = PromptInfo.AppBuilderInspirationDtoAdapter.builder()
                .name("inspiration1")
                .id("1")
                .prompt("Sample prompt")
                .promptTemplate("Template")
                .category("Category1")
                .description("Description1")
                .auto(true)
                .promptVarData(Collections.singletonList(varData))
                .build();

        PromptInfo promptInfo =
                PromptInfo.builder().categories(null).inspirations(Collections.singletonList(inspiration)).build();
        assertThat(promptInfo).isNotNull();
        assertThat(promptInfo.getInspirations()).isNotNull();
        assertThat(promptInfo.getInspirations()).hasSize(1);
        PromptInfo.AppBuilderInspirationDtoAdapter firstInspiration = promptInfo.getInspirations().get(0);
        assertThat(firstInspiration.getName()).isEqualTo("inspiration1");
        assertThat(firstInspiration.getId()).isEqualTo("1");
        assertThat(firstInspiration.getPrompt()).isEqualTo("Sample prompt");
        assertThat(firstInspiration.getPromptVarData()).isNotEmpty();
        assertThat(firstInspiration.getPromptVarData().get(0).getKey()).isEqualTo("var1");
    }
}
