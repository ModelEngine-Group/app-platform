/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.repository.pgsql.service;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.jade.store.entity.transfer.StoreToolData.from;

import modelengine.fel.tool.model.ListResult;
import modelengine.fel.tool.model.entity.ToolIdentifier;
import modelengine.fel.tool.model.transfer.DefinitionGroupData;
import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.jade.store.entity.query.QueryUtils;
import modelengine.jade.store.entity.query.ToolQuery;
import modelengine.jade.store.entity.transfer.StoreToolData;
import modelengine.jade.store.repository.pgsql.repository.StoreToolRepository;
import modelengine.jade.store.service.DefinitionGroupService;
import modelengine.jade.store.service.StoreToolService;
import modelengine.jade.store.service.TagService;
import modelengine.jade.store.service.ToolService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 包含额外信息的工具的 Http 请求的服务层实现。
 *
 * @author 李金绪
 * @since 2024-09-14
 */
@Component
public class DefaultStoreToolService implements StoreToolService {
    private static final String FITABLE_ID = "store-repository-pgsql";

    private final ToolService toolService;
    private final StoreToolRepository storeToolRepo;
    private final TagService tagService;
    private final DefinitionGroupService definitionGroupService;

    /**
     * 通过持久层接口来初始化 {@link DefaultStoreToolService} 的实例。
     *
     * @param toolService 表示工具服务的 {@link ToolService}。
     * @param storeToolRepo 表示存储工具的持久层接口 {@link StoreToolRepository}。
     * @param tagService 表示标签服务的 {@link TagService}。
     * @param definitionGroupService 表示定义组服务的 {@link DefinitionGroupService}。
     */
    public DefaultStoreToolService(ToolService toolService, StoreToolRepository storeToolRepo, TagService tagService,
            DefinitionGroupService definitionGroupService) {
        this.toolService = notNull(toolService, "The tool service can not be null.");
        this.storeToolRepo = notNull(storeToolRepo, "The store tool repository can not be null.");
        this.tagService = notNull(tagService, "The tag service can not be null.");
        this.definitionGroupService = notNull(definitionGroupService, "The definition group service can not be null.");
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public StoreToolData getTool(String toolUniqueName) {
        ToolData toolData = this.toolService.getTool(toolUniqueName);
        Set<String> tags = this.tagService.getTags(toolUniqueName);
        return from(toolData, tags);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public ListResult<StoreToolData> getTools(ToolQuery toolQuery) {
        return this.commonQuery(this.storeToolRepo::getTools, this.storeToolRepo::getToolsCount, toolQuery);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public ListResult<StoreToolData> searchTools(ToolQuery toolQuery) {
        return this.commonQuery(this.storeToolRepo::searchTools, this.storeToolRepo::searchToolsCount, toolQuery);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public ListResult<StoreToolData> getAllToolVersions(ToolQuery toolQuery) {
        ListResult<ToolData> toolDataResults = this.toolService.getAllToolVersions(toolQuery.getToolName());
        int count = toolDataResults.getCount();
        List<StoreToolData> storeToolDataList = toolDataResults.getData().stream().map(toolData -> {
            Set<String> tags = this.tagService.getTags(toolData.getUniqueName());
            return from(toolData, tags);
        }).collect(Collectors.toList());
        return ListResult.create(storeToolDataList, count);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public StoreToolData getToolByVersion(String toolUniqueName, String version) {
        ToolData toolData = this.toolService.getToolByVersion(toolUniqueName, version);
        Set<String> tags = this.tagService.getTags(toolUniqueName);
        return from(toolData, tags);
    }

    @Override
    @Fitable(id = FITABLE_ID)
    public ListResult<DefinitionGroupData> findExistDefGroups(List<String> defGroupNames) {
        List<String> existDefGroupNames = this.definitionGroupService.findExistDefGroups(new HashSet<>(defGroupNames));
        List<DefinitionGroupData> defGroupDatas =
                existDefGroupNames.stream().map(this.definitionGroupService::get).collect(Collectors.toList());
        return ListResult.create(defGroupDatas, defGroupDatas.size());
    }

    private ListResult<StoreToolData> commonQuery(Function<ToolQuery, List<ToolIdentifier>> toolQueryFunc,
            Function<ToolQuery, Integer> countQueryFunc, ToolQuery toolQuery) {
        if (toolQuery == null || QueryUtils.isPageInvalid(toolQuery.getOffset(), toolQuery.getLimit())) {
            return ListResult.empty();
        }
        ToolQuery.toUpperCase(toolQuery);
        List<ToolIdentifier> toolIdentifiers = toolQueryFunc.apply(toolQuery);
        if (CollectionUtils.isEmpty(toolIdentifiers)) {
            return ListResult.create(Collections.emptyList(), 0);
        }
        ListResult<ToolData> toolDataResult = this.toolService.getToolsByIdentifier(toolIdentifiers);
        List<StoreToolData> storeToolDataList = toolDataResult.getData().stream().map(toolData -> {
            Set<String> tags = this.tagService.getTags(toolData.getUniqueName());
            return from(toolData, tags);
        }).collect(Collectors.toList());

        toolQuery.setLimit(null);
        toolQuery.setOffset(null);
        int count = countQueryFunc.apply(toolQuery);
        return ListResult.create(storeToolDataList, count);
    }
}
