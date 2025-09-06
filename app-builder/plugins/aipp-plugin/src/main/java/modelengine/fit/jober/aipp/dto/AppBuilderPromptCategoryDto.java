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
 * 应用构建器提示类别Dto
 *
 * @author 姚江
 * @since 2024-04-26
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppBuilderPromptCategoryDto {
    private String title;
    private String id;
    private String parent;
    private Boolean disable;
    private List<AppBuilderPromptCategoryDto> children;
}