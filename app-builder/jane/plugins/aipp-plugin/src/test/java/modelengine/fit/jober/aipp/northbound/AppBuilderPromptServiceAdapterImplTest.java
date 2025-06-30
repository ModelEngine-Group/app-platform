/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.northbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.response.Rsp;
import modelengine.fit.jober.aipp.dto.AppBuilderPromptCategoryDto;
import modelengine.fit.jober.aipp.dto.AppBuilderPromptDto;
import modelengine.fit.jober.aipp.dto.chat.PromptCategory;
import modelengine.fit.jober.aipp.dto.chat.PromptInfo;
import modelengine.fit.jober.aipp.service.AppBuilderPromptService;
import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.serialization.ObjectSerializer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link AippChatServiceAdapterImpl} 的单元测试。
 *
 * @author 曹嘉美
 * @since 2024-12-31
 */
@DisplayName("测试 AppBuilderPromptServiceAdapterImpl")
public class AppBuilderPromptServiceAdapterImplTest {
    private final ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null, true);
    private final AppBuilderPromptService appBuilderPromptService = mock(AppBuilderPromptService.class);
    private final AppBuilderPromptServiceAdapterImpl appBuilderPromptServiceAdapter =
            new AppBuilderPromptServiceAdapterImpl(appBuilderPromptService, serializer);

    @Test
    @DisplayName("当查询灵感类别时返回正确结果。")
    void shouldReturnOkListPromptCategories() {
        String appId = "app123";
        OperationContext operationContext = new OperationContext();
        boolean isDebug = false;
        AppBuilderPromptCategoryDto categoryDto1 = new AppBuilderPromptCategoryDto();
        categoryDto1.setId("Id1");
        categoryDto1.setTitle("Title1");
        categoryDto1.setDisable(false);
        categoryDto1.setChildren(Collections.singletonList(new AppBuilderPromptCategoryDto("Title1",
                "Id1",
                null,
                false,
                null)));
        AppBuilderPromptCategoryDto categoryDto2 = new AppBuilderPromptCategoryDto();
        categoryDto2.setId("Id2");
        categoryDto2.setTitle("Title2");
        categoryDto2.setDisable(true);
        categoryDto2.setChildren(null);
        List<AppBuilderPromptCategoryDto> categoryDtos = Arrays.asList(categoryDto1, categoryDto2);
        Rsp<List<AppBuilderPromptCategoryDto>> mockResponse = Rsp.ok(categoryDtos);

        when(appBuilderPromptService.listPromptCategories(appId, operationContext, isDebug)).thenReturn(mockResponse);
        List<PromptCategory> result =
                appBuilderPromptServiceAdapter.listPromptCategories(appId, operationContext, isDebug);
        assertThat(result).isNotNull().hasSize(2);
        PromptCategory adapter1 = result.get(0);
        assertThat(adapter1).extracting(PromptCategory::getId, PromptCategory::getTitle)
                .containsExactly("Id1", "Title1");
        assertThat(adapter1.getChildren()).hasSize(1);
        PromptCategory adapter2 = result.get(1);
        assertThat(adapter2).extracting(PromptCategory::getId, PromptCategory::getTitle)
                .containsExactly("Id2", "Title2");
        assertThat(adapter2.getChildren()).isNull();
        verify(appBuilderPromptService, times(1)).listPromptCategories(appId, operationContext, isDebug);
    }

    @Test
    @DisplayName("当灵感大全数据类转换成适配器类时返回正确结果。")
    void shouldReturnOkWhenDtoConvertToAdapter() {
        AppBuilderPromptDto dto = new AppBuilderPromptDto();
        dto.setInspirations(Collections.singletonList(new AppBuilderPromptDto.AppBuilderInspirationDto("name",
                "id",
                "prompt",
                "promptTemplate",
                "category",
                "description",
                true,
                Arrays.asList(new AppBuilderPromptDto.AppBuilderPromptVarDataDto("key",
                                "var",
                                "varType",
                                "sourceType",
                                "sourceInfo",
                                true),
                        new AppBuilderPromptDto.AppBuilderPromptVarDataDto("key2",
                                "var2",
                                "varType2",
                                "sourceType2",
                                "sourceInfo2",
                                true)))));
        dto.setCategories(new ArrayList<>());
        PromptInfo result = appBuilderPromptServiceAdapter.appBuilderPromptDtoConvertToAdapter(dto);
        assertThat(result.getInspirations().get(0).getId()).isEqualTo("id");
        assertThat(result.getInspirations().get(0).getPrompt()).isEqualTo("prompt");
        assertThat(result.getInspirations().get(0).getPromptVarData().get(0).getKey()).isEqualTo("key");
        assertThat(result.getInspirations().get(0).getPromptVarData().get(1).getKey()).isEqualTo("key2");
        assertThat(result.getInspirations().get(0).getPromptVarData().get(0).getVar()).isEqualTo("var");
        assertThat(result.getInspirations().get(0).getPromptVarData().get(1).getVar()).isEqualTo("var2");
    }
}
