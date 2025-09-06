/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.model.entity;

import static modelengine.jade.carver.util.SerializeUtils.json2obj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fel.tool.Tool;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 存入数据库的工具的实体类。
 *
 * @author 李金绪
 * @since 2024/5/10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolDo {
    public static final String SCHEMA_PARAMETERS_KEY = "parameters";
    public static final String SCHEMA_DESCRIPTION_KEY = "description";
    /**
     * 表示工具的名字。
     */
    private String name;

    /**
     * 表示工具组的名字。
     */
    private String groupName;

    /**
     * 表示定义的名字。
     */
    private String definitionName;

    /**
     * 表示定义组的名字。
     */
    private String definitionGroupName;

    /**
     * 表示工具的结构。
     */
    private String schema;

    /**
     * 表示工具的运行描述。
     */
    private String runnables;

    /**
     * 表示工具的扩展描述。
     */
    private String extensions;

    /**
     * 表示工具的唯一标识。
     */
    private String uniqueName;

    /**
     * 表示工具的版本。
     */
    private String version;

    /**
     * 表示当前版本工具是否最新。
     */
    private Boolean isLatest;

    /**
     * 将领域对象转换为数据对象实体类。
     *
     * @param definitionGroupName 表示待注册工具定义组名的 {@link String}。
     * @param groupName 表示工具组名称的 {@link String}。
     * @param serializer 表示序列化对象的 {@link ObjectSerializer}。
     * @param info 表示领域类的 {@link Tool.Info}。
     * @return 工具实体类的 {@link ToolDo}。
     */
    public static ToolDo info2Do(String definitionGroupName, String groupName, Tool.Info info,
            ObjectSerializer serializer) {
        ToolDo toolDo = new ToolDo();
        toolDo.setName(info.name());
        toolDo.setGroupName(groupName);
        toolDo.setDefinitionName(info.definitionName());
        toolDo.setDefinitionGroupName(info.definitionGroupName());
        toolDo.setSchema(serializer.serialize(info.schema()));
        toolDo.setRunnables(serializer.serialize(upperKeys(info.runnables())));
        toolDo.setExtensions(serializer.serialize(info.extensions()));
        toolDo.setUniqueName(info.uniqueName());
        toolDo.setVersion(info.version());
        toolDo.setIsLatest(info.isLatest());
        return toolDo;
    }

    /**
     * 将数据对象实体类象转换为领域对象。
     *
     * @param toolDo 表示工具实体类的 {@link ToolDo}。
     * @param serializer 表示序列化对象的 {@link ObjectSerializer}。
     * @return 领域类的 {@link Tool.Info}。
     */
    public static Tool.Info do2Info(ToolDo toolDo, ObjectSerializer serializer) {
        Map<String, Object> schema = ObjectUtils.nullIf(json2obj(toolDo.getSchema(), serializer), new HashMap<>());
        return Tool.Info.custom()
                .namespace("")
                .name(toolDo.getName())
                .description(ObjectUtils.cast(schema.getOrDefault(SCHEMA_DESCRIPTION_KEY, StringUtils.EMPTY)))
                .schema(schema)
                .runnables(upperKeys(json2obj(toolDo.getRunnables(), serializer)))
                .parameters(ObjectUtils.cast(schema.getOrDefault(SCHEMA_PARAMETERS_KEY, new HashMap<>())))
                .extensions(json2obj(toolDo.getExtensions(), serializer))
                .uniqueName(toolDo.getUniqueName())
                .version(toolDo.getVersion())
                .isLatest(toolDo.getIsLatest())
                .groupName(toolDo.getGroupName())
                .definitionName(toolDo.getDefinitionName())
                .definitionGroupName(toolDo.getDefinitionGroupName())
                .build();
    }

    private static Map<String, Object> upperKeys(Map<String, Object> mapData) {
        if (MapUtils.isEmpty(mapData)) {
            return Collections.emptyMap();
        }
        return mapData.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> StringUtils.toUpperCase(entry.getKey()), Map.Entry::getValue));
    }
}
