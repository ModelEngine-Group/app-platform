/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.repository.pgsql.service;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fel.tool.Tool;
import modelengine.fel.tool.model.ListResult;
import modelengine.fel.tool.model.entity.ToolIdentifier;
import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.transaction.Transactional;
import modelengine.jade.carver.tool.repository.pgsql.repository.ToolRepositoryInner;
import modelengine.jade.store.service.DefinitionService;
import modelengine.jade.store.service.ToolService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 工具的 Http 请求的服务层实现。
 *
 * @author 李金绪
 * @since 2024/5/10
 */
@Component
public class DefaultToolService implements ToolService {
    private static final Logger log = Logger.get(DefaultToolService.class);
    private static final String FITABLE_ID = "tool-repository-pgsql";

    private final ToolRepositoryInner toolRepo;
    private final DefinitionService definitionService;

    /**
     * 通过持久层接口来初始化 {@link DefaultToolService} 的实例。
     *
     * @param toolRepo 表示持久层实例的 {@link ToolRepositoryInner}。
     * @param definitionService 表示定义服务的 {@link DefinitionService}。
     */
    public DefaultToolService(ToolRepositoryInner toolRepo, DefinitionService definitionService) {
        this.toolRepo = toolRepo;
        this.definitionService = definitionService;
        this.toolRepo.getTool("123");
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public String addTool(ToolData toolData) {
        ToolData.transform(toolData);
        Tool.Info info = ToolData.convertToInfo(toolData);
        this.toolRepo.addTool(info);
        return toolData.getUniqueName();
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public void addTools(List<ToolData> toolDataList) {
        List<Tool.Info> infos = toolDataList.stream()
                .map(ToolData::transform)
                .map(ToolData::convertToInfo)
                .collect(Collectors.toList());
        this.toolRepo.addTools(infos);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public void addTools(String definitionGroupName, String groupName, List<ToolData> toolDataList) {
        List<Tool.Info> infos = toolDataList.stream()
                .map(ToolData::transform)
                .map(ToolData::convertToInfo)
                .collect(Collectors.toList());
        this.toolRepo.addTools(definitionGroupName, groupName, infos);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public String deleteTool(String toolUniqueName) {
        this.toolRepo.deleteTool(toolUniqueName);
        return toolUniqueName;
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public void deleteTools(List<String> uniqueNames) {
        this.toolRepo.deleteTools(uniqueNames);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public void deleteTools(String definitionGroupName, String groupName) {
        this.toolRepo.deleteTools(definitionGroupName, groupName);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public void deleteToolsByDefinitionGroupName(String definitionGroupName) {
        this.toolRepo.deleteTools(definitionGroupName);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    @Transactional
    public String deleteToolByVersion(String uniqueName, String version) {
        this.toolRepo.deleteToolByVersion(uniqueName, version);
        return uniqueName;
    }

    private void setLatest(String toolUniqueName, String version) {
        this.toolRepo.setLatest(toolUniqueName, version);
    }

    private void setNotLatest(String toolUniqueName) {
        this.toolRepo.setNotLatest(toolUniqueName);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public ToolData getTool(String toolUniqueName) {
        Optional<Tool.Info> info = this.toolRepo.getTool(toolUniqueName);
        if (!info.isPresent()) {
            return null;
        }
        ToolData toolData = ToolData.from(info.get());
        ToolData.transform(toolData);
        return toolData;
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public List<ToolData> getTools(String definitionGroupName, String groupName) {
        return this.getToolDataList(this.toolRepo.getTools(definitionGroupName, groupName));
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public List<ToolData> getTools(String definitionGroupName) {
        return this.getToolDataList(this.toolRepo.getTools(definitionGroupName));
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public ToolData getToolByVersion(String toolUniqueName, String version) {
        Optional<Tool.Info> info = this.toolRepo.getToolByVersion(toolUniqueName, version);
        if (!info.isPresent()) {
            return null;
        }
        ToolData toolData = ToolData.from(info.get());
        ToolData.transform(toolData);
        return toolData;
    }

    @Fitable(id = FITABLE_ID)
    @Override
    public ListResult<ToolData> getAllToolVersions(String toolUniqueName) {
        List<ToolData> toolDataList = this.getToolDataList(this.toolRepo.getAllToolVersions(toolUniqueName));
        int count = this.toolRepo.getAllToolVersionsCount(toolUniqueName);
        return ListResult.create(toolDataList, count);
    }

    @Fitable(id = FITABLE_ID)
    @Override
    @Transactional
    public String upgradeTool(ToolData toolData) {
        notNull(toolData.getUniqueName(), "Tool unique name cannot be null.");
        String uniqueName = toolData.getUniqueName();
        String currentVersion = this.getTool(uniqueName).getVersion();
        try {
            this.setNotLatest(uniqueName);
            log.info("Succeeded in setting tool versions not latest. [toolUniqueName={}]", uniqueName);
        } catch (FitException e) {
            this.setLatest(uniqueName, currentVersion);
            log.error("Failed to set tool versions not latest.");
            throw e;
        }
        if (this.getToolByVersion(uniqueName, toolData.getVersion()) != null) {
            try {
                this.setLatest(uniqueName, toolData.getVersion());
                log.info("Succeeded in updating existing tool version as the latest. [toolUniqueName={}]", uniqueName);
            } catch (FitException e) {
                log.error("Failed to update existing tool version as the latest.");
                // 回滚之前的数据库插入操作。
                this.setLatest(uniqueName, currentVersion);
                throw e;
            }
            return uniqueName;
        }
        try {
            this.addTool(toolData);
            log.info("Succeeded in adding tool. [toolUniqueName={}]", toolData.getUniqueName());
        } catch (FitException e) {
            log.error("Failed to add tool.");
            // 回滚之前的数据库插入操作。
            this.setLatest(uniqueName, currentVersion);
            throw e;
        }
        return uniqueName;
    }

    @Override
    public ListResult<ToolData> getToolsByIdentifier(List<ToolIdentifier> toolIdentifiers) {
        List<ToolData> toolDataList = this.getToolDataList(this.toolRepo.getToolsByIdentifier(toolIdentifiers));
        return ListResult.create(toolDataList, toolDataList.size());
    }

    private List<ToolData> getToolDataList(List<Tool.Info> infos) {
        return infos.stream().map(ToolData::from).map(ToolData::transform).collect(Collectors.toList());
    }
}
