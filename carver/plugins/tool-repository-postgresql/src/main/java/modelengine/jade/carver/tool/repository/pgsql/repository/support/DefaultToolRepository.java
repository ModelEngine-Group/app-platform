/*
 * Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

package modelengine.jade.carver.tool.repository.pgsql.repository.support;

import static modelengine.fel.tool.info.schema.PluginSchema.TYPE;
import static modelengine.fel.tool.info.schema.ToolsSchema.PROPERTIES;
import static modelengine.fel.tool.info.schema.ToolsSchema.REQUIRED;
import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.core.tool.ToolInfo;
import modelengine.fel.tool.Tool;
import modelengine.fel.tool.ToolInfoEntity;
import modelengine.fel.tool.info.entity.ParameterEntity;
import modelengine.fel.tool.info.entity.SchemaEntity;
import modelengine.fel.tool.info.entity.ToolEntity;
import modelengine.fel.tool.info.schema.PluginSchema;
import modelengine.fel.tool.info.schema.ToolsSchema;
import modelengine.fel.tool.service.ToolRepository;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.carver.tool.repository.pgsql.repository.ToolRepositoryInner;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 提供工具的存储服务默认实现
 *
 * @author 邬涨财
 * @since 2025-05-12
 */
@Component
public class DefaultToolRepository implements ToolRepository {
    private final ToolRepositoryInner toolRepositoryInner;

    /**
     * 通过 mapper 接口来初始化 {@link DefaultToolRepository} 的实例。
     *
     * @param toolRepositoryInner 表示序列化对象的 {@link ObjectSerializer}。
     */
    public DefaultToolRepository(ToolRepositoryInner toolRepositoryInner) {
        this.toolRepositoryInner = notNull(toolRepositoryInner, "The inner tool repository cannot be null.");
    }

    @Override
    @Fitable(id = "default")
    public void addTool(ToolInfoEntity tool) {
        ToolInfoEntity entity = this.getTool(tool.namespace(), tool.name());
        if (entity != null) {
            return;
        }
        Tool.Info info = this.tool2Info(tool);
        this.toolRepositoryInner.addTool(info);
    }

    private Tool.Info tool2Info(ToolInfoEntity tool) {
        return Tool.Info.custom()
                .definitionGroupName(tool.definitionGroupName())
                .groupName(tool.namespace())
                .name(tool.name())
                .definitionName(tool.definitionName())
                .schema(tool.schema())
                .runnables(tool.runnables())
                .extensions(tool.extensions())
                .uniqueName(tool.uniqueName())
                .version(tool.version())
                .isLatest(tool.isLatest())
                .namespace(tool.namespace())
                .description(tool.description())
                .parameters(tool.parameters())
                .defaultParameterValues(tool.defaultParameterValues())
                .returnConverter(tool.returnConverter())
                .build();
    }

    @Override
    @Fitable(id = "default")
    public void deleteTool(String namespace, String toolName) {
        String uniqueName = ToolInfo.identify(namespace, toolName);
        this.toolRepositoryInner.deleteTool(uniqueName);
    }

    @Override
    @Fitable(id = "default")
    public ToolInfoEntity getTool(String namespace, String toolName) {
        String uniqueName = ToolInfo.identify(namespace, toolName);
        Optional<Tool.Info> infoOptional = this.toolRepositoryInner.getTool(uniqueName);
        return infoOptional.map(this::tool2Info).orElse(null);
    }

    private ToolInfoEntity tool2Info(Tool.Info info) {
        ToolEntity infoEntity = new ToolEntity();
        infoEntity.setNamespace(info.namespace());
        SchemaEntity schemaEntity = new SchemaEntity();
        schemaEntity.setName(ObjectUtils.cast(info.schema().get(ToolsSchema.NAME)));
        schemaEntity.setDescription(ObjectUtils.cast(info.schema().get(PluginSchema.DESCRIPTION)));
        schemaEntity.setParameters(this.getParameters(info));
        schemaEntity.setOrder(ObjectUtils.cast(info.schema().get(ToolsSchema.ORDER)));
        schemaEntity.setRet(ObjectUtils.cast(info.schema().get(ToolsSchema.RETURN)));
        schemaEntity.setParameterExtensions(ObjectUtils.cast(info.schema().get(ToolsSchema.PARAMETER_EXTENSIONS)));
        infoEntity.setSchema(schemaEntity);
        infoEntity.setRunnables(info.runnables());
        infoEntity.setExtensions(info.extensions());
        infoEntity.setDefinitionName(ObjectUtils.cast(info.definitionName()));
        return new ToolInfoEntity(infoEntity);
    }

    private ParameterEntity getParameters(Tool.Info info) {
        Map<String, Object> paramsMap = ObjectUtils.cast(info.schema().get(ToolsSchema.PARAMETERS));
        ParameterEntity parameterEntity = new ParameterEntity();
        parameterEntity.setType(ObjectUtils.cast(paramsMap.get(TYPE)));
        parameterEntity.setProperties(ObjectUtils.cast(paramsMap.get(PROPERTIES)));
        parameterEntity.setRequired(ObjectUtils.cast(paramsMap.get(REQUIRED)));
        return parameterEntity;
    }

    @Override
    @Fitable(id = "default")
    public List<ToolInfoEntity> listTool(String namespace) {
        List<Tool.Info> infos = this.toolRepositoryInner.getTools(StringUtils.EMPTY, namespace);
        return infos.stream().map(this::tool2Info).collect(Collectors.toList());
    }
}
