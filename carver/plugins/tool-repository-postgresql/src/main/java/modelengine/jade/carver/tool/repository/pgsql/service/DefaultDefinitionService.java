/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.service;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.tool.Tool;
import modelengine.fel.tool.model.transfer.DefinitionData;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.transaction.Transactional;
import modelengine.jade.carver.tool.repository.pgsql.repository.DefinitionRepository;
import modelengine.jade.store.service.DefinitionService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 提供工具定义的默认服务。
 *
 * @author 王攀博
 * @since 2024-10-29
 */
@Component
public class DefaultDefinitionService implements DefinitionService {
    private static final String FITABLE_ID = "tool-repository-pgsql";

    private final DefinitionRepository definitionRepository;

    public DefaultDefinitionService(DefinitionRepository definitionRepository) {
        this.definitionRepository = notNull(definitionRepository, "The definition repository cannot be null.");
    }

    @Fitable(id = FITABLE_ID)
    @Override
    @Transactional
    public String add(DefinitionData definition) {
        return this.definitionRepository.add(Tool.Metadata.fromSchema(definition.getGroupName(),
                definition.getSchema()));
    }

    @Fitable(id = FITABLE_ID)
    @Override
    @Transactional
    public void add(List<DefinitionData> definitions) {
        this.definitionRepository.add(definitions.stream()
                .map(definition -> Tool.Metadata.fromSchema(definition.getGroupName(), definition.getSchema()))
                .collect(Collectors.toList()));
    }

    @Fitable(id = FITABLE_ID)
    @Override
    @Transactional
    public String delete(String groupName, String name) {
        this.definitionRepository.delete(groupName, name);
        return name;
    }

    @Fitable(id = FITABLE_ID)
    @Override
    @Transactional
    public void delete(String groupName) {
        this.definitionRepository.delete(groupName);
    }

    @Override
    public DefinitionData get(String groupName, String name) {
        Tool.Metadata metadata = this.definitionRepository.get(groupName, name);
        if (metadata == null) {
            return null;
        }
        return DefinitionData.from(metadata);
    }

    @Fitable(id = FITABLE_ID)
    @Override
    public List<DefinitionData> get(String groupName) {
        List<Tool.Metadata> metadataList = this.definitionRepository.get(groupName);
        return metadataList.stream().map(DefinitionData::from).collect(Collectors.toList());
    }
}