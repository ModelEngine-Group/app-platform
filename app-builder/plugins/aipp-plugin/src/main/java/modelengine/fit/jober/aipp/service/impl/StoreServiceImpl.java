/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.service.impl;

import static modelengine.fit.jober.aipp.enums.ToolCategoryEnum.HUGGINGFACE;
import static modelengine.fit.jober.aipp.init.serialization.AippComponentInitiator.COMPONENT_DATA;
import static modelengine.jade.common.Result.calculateOffset;

import modelengine.fel.tool.model.transfer.ToolData;
import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.dto.AppBuilderWaterFlowInfoDto;
import modelengine.fit.jober.aipp.dto.ModelDto;
import modelengine.fit.jober.aipp.dto.PluginToolDto;
import modelengine.fit.jober.aipp.dto.StoreNodeConfigResDto;
import modelengine.fit.jober.aipp.dto.StoreNodeInfoDto;
import modelengine.fit.jober.aipp.dto.StoreWaterFlowDto;
import modelengine.fit.jober.aipp.dto.ToolModelDto;
import modelengine.fit.jober.aipp.enums.AppCategory;
import modelengine.fit.jober.aipp.enums.NodeTypeEnum;
import modelengine.fit.jober.aipp.mapper.AppBuilderAppMapper;
import modelengine.fit.jober.aipp.po.AppBuilderAppPo;
import modelengine.fit.jober.aipp.service.StoreService;
import modelengine.fit.jober.aipp.util.JsonUtils;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Value;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.carver.ListResult;
import modelengine.jade.common.locale.LocaleUtil;
import modelengine.jade.store.entity.query.ModelQuery;
import modelengine.jade.store.entity.query.PluginToolQuery;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.entity.transfer.TaskData;
import modelengine.jade.store.service.EcoTaskService;
import modelengine.jade.store.service.HuggingFaceModelService;
import modelengine.jade.store.service.PluginToolService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 市场相关接口实现。
 *
 * @author 孙怡菲
 * @since 2024-05-10
 */
@Component
public class StoreServiceImpl implements StoreService {
    private static final Logger log = Logger.get(StoreServiceImpl.class);

    private final Map<String, String> tags;

    private final PluginToolService pluginToolService;

    private final EcoTaskService ecoTaskService;

    private final HuggingFaceModelService huggingFaceModelService;

    private final AppBuilderAppMapper appBuilderAppMapper;

    private final LazyLoader<Map<String, String>> uniqueNames;

    public StoreServiceImpl(PluginToolService pluginToolService, EcoTaskService ecoTaskService,
            HuggingFaceModelService huggingFaceModelService, AppBuilderAppMapper appBuilderAppMapper,
            @Value("${tool.tags}") Map<String, String> tags) {
        this.pluginToolService = pluginToolService;
        this.ecoTaskService = ecoTaskService;
        this.huggingFaceModelService = huggingFaceModelService;
        this.appBuilderAppMapper = appBuilderAppMapper;
        this.tags = tags;
        this.uniqueNames = new LazyLoader<>(this::getUniqueNames);
    }

    private Map<String, String> getUniqueNames() {
        Map<String, String> uniqueNameMap = new HashMap<>();
        PluginToolQuery query = new PluginToolQuery.Builder().toolName(null)
                .includeTags(new HashSet<>(this.tags.keySet()))
                .excludeTags(new HashSet<>())
                .mode("OR")
                .offset(null)
                .limit(null)
                .version(null)
                .build();
        ListResult<PluginToolData> pluginTools = this.pluginToolService.getPluginTools(query);
        if (pluginTools.getCount() == 0) {
            return uniqueNameMap;
        }
        pluginTools.getData()
                .forEach(pluginToolData -> uniqueNameMap.put(this.tags.get(pluginToolData.getTags()
                        .stream()
                        .filter(this.tags::containsKey)
                        .collect(Collectors.toList())
                        .get(0)), pluginToolData.getUniqueName()));
        return uniqueNameMap;
    }

    @Override
    public StoreNodeConfigResDto getBasicNodesAndTools(String tag, String mode, int pageNum, int pageSize,
            String version) {
        return StoreNodeConfigResDto.builder()
                .toolList(this.getToolModelList(tag, mode, pageNum, pageSize, version))
                .basicList(this.buildNodesConfig(NodeTypeEnum.BASIC.type()))
                .build();
    }

    @Override
    public PluginToolDto getPlugins(PluginToolQuery pluginToolQuery, OperationContext operationContext) {
        ListResult<PluginToolData> toolDataListResult = this.pluginToolService.getPluginTools(pluginToolQuery);
        return PluginToolDto.builder()
                .pluginToolData(toolDataListResult.getData())
                .total(toolDataListResult.getCount())
                .build();
    }

    @Override
    public List<StoreNodeInfoDto> getNode(String type) {
        return this.buildNodesConfig(type);
    }

    private List<ToolModelDto> getToolModelList(String tag, String mode, int pageNum, int pageSize, String version) {
        return this.buildToolNodesConfig(tag, mode, pageNum, pageSize, version)
                .getData()
                .stream()
                .map(toolData -> ToolModelDto.combine2ToolModelDto(toolData,
                        tag.equalsIgnoreCase(HUGGINGFACE.getName())
                                ? getDefaultModel(toolData, tag)
                                : StringUtils.EMPTY))
                .collect(Collectors.toList());
    }

    private ListResult<PluginToolData> buildToolNodesConfig(String tag, String mode, int pageNum, int pageSize,
            String version) {
        List<String> includeTag = new ArrayList<>();
        if (StringUtils.isNotBlank(tag)) {
            includeTag.add(tag);
        } else {
            includeTag.add("WATERFLOW");
            includeTag.add("FIT");
        }
        PluginToolQuery query = new PluginToolQuery.Builder().toolName(null)
                .includeTags(new HashSet<>(includeTag))
                .excludeTags(new HashSet<>(Collections.singleton(StringUtils.EMPTY)))
                .mode(StringUtils.isNotBlank(tag) ? mode : "OR")
                .offset(calculateOffset(pageNum, pageSize))
                .limit(pageSize)
                .version(version)
                .build();
        return this.pluginToolService.getPluginTools(query);
    }

    private String getDefaultModel(ToolData toolData, String tag) {
        Map<String, Object> map = ObjectUtils.cast(toolData.getRunnables().get(tag.toUpperCase(Locale.ROOT)));
        if (MapUtils.isEmpty(map) || !map.containsKey("taskName")) {
            return StringUtils.EMPTY;
        }
        String taskName = map.get("taskName") instanceof String ? (String) map.get("taskName") : StringUtils.EMPTY;
        TaskData task = ecoTaskService.getTask(taskName);
        if (task != null) {
            Map<String, Object> context = task.getContext();
            return context.get("defaultModel") instanceof String
                    ? (String) context.get("defaultModel")
                    : StringUtils.EMPTY;
        }
        return StringUtils.EMPTY;
    }

    private List<StoreNodeInfoDto> buildNodesConfig(String type) {
        String key;
        if (StringUtils.equals(type, NodeTypeEnum.BASIC.type())) {
            key = StringUtils.equals(LocaleUtil.getLocale().getLanguage(), Locale.ENGLISH.getLanguage())
                    ? AippConst.BASIC_NODE_COMPONENT_DATA_EN_KEY
                    : AippConst.BASIC_NODE_COMPONENT_DATA_ZH_KEY;
        } else {
            key = StringUtils.equals(LocaleUtil.getLocale().getLanguage(), Locale.ENGLISH.getLanguage())
                    ? AippConst.EVALUATION_NODE_COMPONENT_DATA_EN_KEY
                    : AippConst.EVALUATION_NODE_COMPONENT_DATA_ZH_KEY;
        }
        List<StoreNodeInfoDto> nodeList = JsonUtils.parseArray(COMPONENT_DATA.get(key), StoreNodeInfoDto[].class);
        this.setUniqueName(nodeList);
        return nodeList;
    }

    private void setUniqueName(List<StoreNodeInfoDto> nodeList) {
        nodeList.stream()
                .filter(nodeInfoDto -> this.tags.containsValue(nodeInfoDto.getType()))
                .forEach(nodeInfoDto -> nodeInfoDto.setUniqueName(uniqueNames.get()
                        .getOrDefault(nodeInfoDto.getType(), StringUtils.EMPTY)));
    }

    @Override
    public List<AppBuilderWaterFlowInfoDto> getWaterFlowInfos(String mode, int pageNum, int pageSize, String version) {
        List<PluginToolData> waterFlows =
                this.buildToolNodesConfig(AppCategory.WATER_FLOW.getTag(), mode, pageNum, pageSize, version).getData();
        List<String> storeIds = waterFlows.stream().map(ToolData::getUniqueName).collect(Collectors.toList());
        if (storeIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<AppBuilderAppPo> appInfos = appBuilderAppMapper.selectWithStoreId(storeIds);
        Map<String, StoreWaterFlowDto> appInfoMap = appInfos.stream()
                .collect(Collectors.toMap(info -> JsonUtils.parseObject(info.getAttributes())
                                .get("store_id")
                                .toString(),
                        info -> StoreWaterFlowDto.builder()
                                .version(info.getVersion())
                                .id(info.getId())
                                .tenantId(info.getTenantId())
                                .build()));
        return waterFlows.stream()
                .map(waterFlow -> buildWaterFlowInfo(waterFlow, appInfoMap))
                .collect(Collectors.toList());
    }

    private AppBuilderWaterFlowInfoDto buildWaterFlowInfo(ToolData waterFlow,
            Map<String, StoreWaterFlowDto> appInfoMap) {
        String uniqueName = waterFlow.getUniqueName();
        StoreWaterFlowDto appInfo = appInfoMap.get(uniqueName);
        return AppBuilderWaterFlowInfoDto.builder()
                .itemData(waterFlow)
                .appId(appInfo.getId())
                .version(appInfo.getVersion())
                .tenantId(appInfo.getTenantId())
                .build();
    }

    @Override
    public ModelDto getModels(String taskName, int pageNum, int pageSize) {
        return new ModelDto(this.huggingFaceModelService.getModels(new ModelQuery(taskName, pageNum, pageSize)),
                this.huggingFaceModelService.getCount(taskName));
    }
}
