/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.service;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fel.tool.model.transfer.ToolGroupData;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.carver.tool.repository.pgsql.repository.ToolGroupRepository;
import modelengine.jade.store.service.ToolGroupService;
import modelengine.jade.store.service.ToolService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 提工具组的默认服务。
 *
 * @author 王攀博
 * @since 2024-10-29
 */
@Component
public class DefaultToolGroupService implements ToolGroupService {
    private static final String FITABLE_ID = "tool-repository-pgsql";
    private static final String DOT = ".";

    private final ToolService toolService;
    private final ToolGroupRepository toolGroupRepository;

    public DefaultToolGroupService(ToolService toolService, ToolGroupRepository toolGroupRepository) {
        this.toolService = notNull(toolService, "The tool service cannot be null.");
        this.toolGroupRepository = notNull(toolGroupRepository, "The tool group repository cannot be null.");
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public void add(ToolGroupData toolGroup) {
        if (toolGroup == null) {
            return;
        }
        this.toolService.addTools(toolGroup.getDefGroupName(), toolGroup.getName(), toolGroup.getTools());
        this.toolGroupRepository.add(toolGroup);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public void add(List<ToolGroupData> toolGroupDataList) {
        if (toolGroupDataList == null) {
            return;
        }
        toolGroupDataList.forEach(this::add);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public void delete(String definitionGroupName, String toolGroupName) {
        this.toolService.deleteTools(definitionGroupName, toolGroupName);
        this.toolGroupRepository.delete(definitionGroupName, toolGroupName);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public void deleteByDefinitionGroupName(String definitionGroupName) {
        this.toolService.deleteToolsByDefinitionGroupName(definitionGroupName);
        this.toolGroupRepository.deleteByDefGroupName(definitionGroupName);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public List<ToolGroupData> get(String definitionGroupName, List<String> toolGroupNames) {
        return toolGroupNames.stream().map(toolGroupName -> {
            Optional<ToolGroupData> toolGroupData = this.toolGroupRepository.get(definitionGroupName, toolGroupName);
            List<ToolData> toolDataList = this.toolService.getTools(definitionGroupName, toolGroupName);
            if (!toolGroupData.isPresent() || toolDataList == null || toolDataList.isEmpty()) {
                return null;
            }
            ToolGroupData res = toolGroupData.get();
            res.setTools(toolDataList);
            return res;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public List<ToolGroupData> get(String definitionGroupName) {
        List<ToolGroupData> toolGroupDatas = this.toolGroupRepository.getByDefGroupName(definitionGroupName);
        for (ToolGroupData toolGroupData : toolGroupDatas) {
            List<ToolData> toolDataList = this.toolService.getTools(definitionGroupName, toolGroupData.getName());
            toolGroupData.setTools(toolDataList);
        }
        return toolGroupDatas;
    }

    @Deprecated
    @Override
    @Fitable(id = FITABLE_ID)
    public boolean isExistAnyToolInToolGroup(String defGroupName, String toolGroupName, Set<String> toolNames) {
        ToolGroupData existToolGroupData = this.get(defGroupName)
                .stream()
                .filter(toolGroupData -> Objects.equals(toolGroupData.getName(), toolGroupName))
                .findFirst()
                .orElse(null);
        if (existToolGroupData == null) {
            return false;
        }
        return existToolGroupData.getTools().stream().anyMatch(toolData -> toolNames.contains(toolData.getName()));
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public String findFirstExistToolGroupInDefGroup(String toolGroupName, String defGroupName) {
        return this.get(defGroupName)
                .stream()
                .filter(toolGroupData -> Objects.equals(toolGroupData.getName(), toolGroupName))
                .map(toolGroupData -> toolGroupData.getDefGroupName() + DOT + toolGroupData.getName())
                .findFirst()
                .orElse(StringUtils.EMPTY);
    }
}