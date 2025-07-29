/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.dto;

import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.jade.store.entity.transfer.PluginToolData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * 组合 tool 和 default model 的类。
 *
 * @author 李金绪
 * @since 2024/6/12
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolModelDto {
    private String creator;
    private String modifier;
    private String name;
    private String description;
    private String uniqueName;
    private Map<String, Object> schema;
    private Map<String, Object> runnables;
    private Map<String, Object> extensions;
    private String source;
    private String icon;
    private Set<String> tags;
    private String defaultModel;

    /**
     * 将 ToolData 和 default model 组合成一个 ToolModelDtod。
     *
     * @param toolData 表示工具的 {@link ToolData}。
     * @param defaultModel 表示默认模型的 {@link String}。
     * @return 表示组合后的工具数据的 {@link ToolModelDto}。
     */
    public static ToolModelDto combine2ToolModelDto(PluginToolData toolData, String defaultModel) {
        return ToolModelDto.builder()
                .creator(toolData.getCreator())
                .modifier(toolData.getModifier())
                .name(toolData.getName())
                .description(toolData.getDescription())
                .uniqueName(toolData.getUniqueName())
                .schema(toolData.getSchema())
                .runnables(toolData.getRunnables())
                .extensions(toolData.getExtensions())
                .source(toolData.getSource())
                .icon(toolData.getIcon())
                .tags(toolData.getTags())
                .defaultModel(defaultModel)
                .build();
    }
}
