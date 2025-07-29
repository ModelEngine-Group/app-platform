/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

import java.util.List;

/**
 * 灵感大全的提示词信息。
 *
 * @author 曹嘉美
 * @since 2024-12-19
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromptInfo {
    private List<PromptCategory> categories;
    private List<AppBuilderInspirationDtoAdapter> inspirations;

    /**
     * 构建器中的提示数据
     */
    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AppBuilderInspirationDtoAdapter {
        @Property(description = "灵感名称")
        private String name;
        @Property(description = "灵感的唯一标识符")
        private String id;
        @Property(description = "提示词")
        private String prompt;
        @Property(description = "提示词模板")
        private String promptTemplate;
        @Property(description = "分类")
        private String category;
        @Property(description = "简介")
        private String description;
        @Property(description = "是否自动执行")
        private Boolean auto;
        @Property(description = "提示词变量")
        private List<AppBuilderPromptVarDataDtoAdapter> promptVarData;
    }

    /**
     * 构建器中的提示数据
     */
    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AppBuilderPromptVarDataDtoAdapter {
        @Property(description = "提示词变量的唯一key")
        private String key;
        @Property(description = "提示词变量")
        private String var;
        @Property(description = "提示词变量值的展示形式")
        private String varType;
        @Property(description = "提示词变量值的来源类型")
        private String sourceType;
        @Property(description = "提示词变量值的来源信息")
        private String sourceInfo;
        @Property(description = "是否多选")
        private Boolean multiple;
    }
}
