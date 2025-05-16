/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.service;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.tool.model.transfer.DefinitionData;
import modelengine.fel.tool.model.transfer.DefinitionGroupData;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.carver.tool.repository.pgsql.repository.DefinitionGroupRepository;
import modelengine.jade.store.service.DefinitionGroupService;
import modelengine.jade.store.service.DefinitionService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 提供定义组的默认服务。
 *
 * @author 王攀博
 * @since 2024-10-29
 */
@Component
public class DefaultDefinitionGroupService implements DefinitionGroupService {
    private static final String FITABLE_ID = "tool-repository-pgsql";
    private static final String DOT = ".";

    private final DefinitionService definitionService;
    private final DefinitionGroupRepository defGroupRepo;

    public DefaultDefinitionGroupService(DefinitionService defService, DefinitionGroupRepository defGroupRepo) {
        this.definitionService = notNull(defService, "The definition service cannot be null.");
        this.defGroupRepo = notNull(defGroupRepo, "The definition group repo cannot be null.");
    }

    @Fitable(id = FITABLE_ID)
    @Override
    @Transactional
    public String add(DefinitionGroupData definitionGroup) {
        this.definitionService.add(definitionGroup.getDefinitions());
        this.defGroupRepo.add(definitionGroup);
        return definitionGroup.getName();
    }

    @Fitable(id = FITABLE_ID)
    @Override
    @Transactional
    public void add(List<DefinitionGroupData> definitionGroups) {
        definitionGroups.forEach(this::add);
    }

    @Fitable(id = FITABLE_ID)
    @Override
    @Transactional
    public String delete(String definitionGroupName) {
        this.definitionService.delete(definitionGroupName);
        this.defGroupRepo.delete(definitionGroupName);
        return definitionGroupName;
    }

    @Fitable(id = FITABLE_ID)
    @Override
    @Transactional
    public void delete(List<String> definitionGroupNames) {
        definitionGroupNames.forEach(this::delete);
    }

    @Fitable(id = FITABLE_ID)
    @Override
    public DefinitionGroupData get(String name) {
        Optional<DefinitionGroupData> definitionGroupData = this.defGroupRepo.get(name);
        if (!definitionGroupData.isPresent()) {
            return null;
        }
        List<DefinitionData> definitionDataList = this.definitionService.get(name);
        DefinitionGroupData res = definitionGroupData.get();
        res.setDefinitions(definitionDataList);
        return res;
    }

    @Fitable(id = FITABLE_ID)
    @Override
    public String findFirstExistDefGroup(Set<String> defGroupNames) {
        return this.findExistDefGroups(defGroupNames).stream().findFirst().orElse(StringUtils.EMPTY);
    }

    @Fitable(id = FITABLE_ID)
    @Override
    public List<String> findExistDefGroups(Set<String> defGroupNames) {
        return defGroupNames.stream().filter(groupName -> {
            DefinitionGroupData defGroup = this.get(groupName);
            return defGroup != null && !defGroup.getDefinitions().isEmpty();
        }).collect(Collectors.toList());
    }

    @Fitable(id = FITABLE_ID)
    @Override
    public String findFirstExistDefNameInDefGroup(String defGroupName, Set<String> defNames) {
        DefinitionGroupData defGroupData = this.get(defGroupName);
        if (defGroupData == null) {
            return StringUtils.EMPTY;
        }
        return defGroupData.getDefinitions()
                .stream()
                .filter(definitionData -> defNames.contains(definitionData.getName()))
                .map(defData -> defData.getGroupName() + DOT + defData.getName())
                .findFirst()
                .orElse(StringUtils.EMPTY);
    }
}
