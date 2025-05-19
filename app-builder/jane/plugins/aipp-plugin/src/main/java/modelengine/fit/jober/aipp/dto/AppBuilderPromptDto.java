/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 构建器中的提示数据
 *
 * @author 姚江
 * @since 2024-04-25
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderPromptDto {
    private List<AppBuilderPromptCategoryDto> categories;
    private List<AppBuilderInspirationDto> inspirations;

    /**
     * 构建器中的提示数据
     */
    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AppBuilderInspirationDto {
        private String name;
        private String id;
        private String prompt;
        private String promptTemplate;
        private String category;
        private String description;
        private Boolean auto;
        private List<AppBuilderPromptVarDataDto> promptVarData;
    }

    /**
     * 构建器中的提示数据
     */
    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AppBuilderPromptVarDataDto {
        private String key;
        private String var;
        private String varType;
        private String sourceType;
        private String sourceInfo;
        private Boolean multiple;
    }
}
