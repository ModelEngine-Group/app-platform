/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.appversion.publish;

import modelengine.jade.store.service.ToolService;
import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jober.WaterFlowService;
import modelengine.fit.jober.aipp.common.exception.AippErrCode;
import modelengine.fit.jober.aipp.common.exception.AippException;
import modelengine.fit.jober.aipp.common.exception.AippParamException;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.PublishContext;
import modelengine.fit.jober.aipp.domains.appversion.ToolSchemaBuilder;
import modelengine.fit.jober.aipp.enums.AppCategory;
import modelengine.jade.store.entity.transfer.AppData;
import modelengine.jade.store.entity.transfer.AppPublishData;
import modelengine.jade.store.entity.transfer.PluginData;
import modelengine.jade.store.entity.transfer.PluginToolData;
import modelengine.jade.store.service.AppService;
import modelengine.jade.store.service.PluginService;
import modelengine.jade.store.service.support.DeployStatus;

import lombok.AllArgsConstructor;
import modelengine.fitframework.util.MapBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 仓库工具发布器.
 *
 * @author 张越
 * @since 2025-01-16
 */
@AllArgsConstructor
public class StorePublisher implements Publisher {
    private final AppService appService;
    private final PluginService pluginService;
    private final ToolService toolService;

    @Override
    public void publish(PublishContext context, AppVersion appVersion) {
        AppPublishData appData = this.buildItemData(context, appVersion);
        String uniqueName = this.getUniqueName(context, appData);

        // 设置uniqueName，在工具中的唯一标识.
        appVersion.getData().setUniqueName(uniqueName);
    }

    private String getUniqueName(PublishContext context, AppPublishData appData) {
        if (context.isApp()) {
            return this.appService.publishApp(appData);
        }
        if (!context.isWaterFlow()) {
            throw new AippException(AippErrCode.ILLEGAL_AIPP_TYPE);
        }
        if (appData.getUniqueName() == null) {
            AppData.fillAppData(appData);
            this.pluginService.addPlugin(this.buildPluginData(appData));
            return appData.getUniqueName();
        }
        AppData.fillAppData(appData);
        PluginData pluginData = this.buildPluginData(appData);
        return this.toolService.upgradeTool(pluginData.getPluginToolDataList().get(0));
    }

    private AppPublishData buildItemData(PublishContext context, AppVersion appVersion) {
        AppCategory appCategory = AppCategory.findByType(context.getPublishData().getType())
                .orElseThrow(() -> new AippParamException(AippErrCode.INPUT_PARAM_IS_INVALID));
        AppPublishData itemData = new AppPublishData();
        itemData.setCreator(context.getOperationContext().getOperator());
        itemData.setModifier(context.getOperationContext().getOperator());
        itemData.setIcon(context.getPublishData().getIcon());
        itemData.setName(context.getPublishData().getName());
        itemData.setDescription(context.getPublishData().getDescription());
        itemData.setVersion(context.getPublishData().getVersion());
        itemData.setUniqueName(appVersion.getData().getUniqueName());
        itemData.setSchema(ToolSchemaBuilder.create(context).build());
        itemData.setSource(appCategory.getSource());
        itemData.setTags(Set.of(appCategory.getTag()));
        itemData.setRunnables(this.buildRunnables(context, appVersion));
        return itemData;
    }

    private Map<String, Object> buildRunnables(PublishContext context, AppVersion appVersion) {
        Map<String, Object> runnablesMap = new HashMap<>();
        runnablesMap.put("FIT", MapBuilder.get()
                .put("genericableId", WaterFlowService.GENERICABLE_WATER_FLOW_INVOKER)
                .put("fitableId", "water.flow.invoke")
                .build());
        Map<Object, Object> app = MapBuilder.get()
                .put("appId", appVersion.getData().getAppId())
                .put("aippId", appVersion.getData().getAppSuiteId())
                .put("version", context.getPublishData().getVersion())
                .put("appCategory", context.getPublishData().getAppCategory())
                .build();
        runnablesMap.put("APP", app);
        return runnablesMap;
    }

    private PluginData buildPluginData(AppData appData) {
        PluginData pluginData = new PluginData();
        pluginData.setDeployStatus(DeployStatus.RELEASED.name());
        pluginData.setCreator(appData.getCreator());
        pluginData.setModifier(appData.getModifier());
        pluginData.setPluginName(appData.getName());
        pluginData.setExtension(new HashMap<>());
        pluginData.setPluginId(Entities.generateId() + Entities.generateId());
        PluginToolData pluginToolData = this.buildPluginToolData(appData, pluginData);
        pluginData.setPluginToolDataList(Collections.singletonList(pluginToolData));
        pluginData.setDefinitionGroupDataList(List.of(AppData.toDefGroup(appData)));
        pluginData.setToolGroupDataList(List.of(AppData.toToolGroup(appData)));
        return pluginData;
    }

    private PluginToolData buildPluginToolData(AppData appData, PluginData pluginData) {
        PluginToolData pluginToolData = new PluginToolData();
        pluginToolData.setCreator(appData.getCreator());
        pluginToolData.setModifier(appData.getModifier());
        pluginToolData.setName(appData.getName());
        pluginToolData.setDescription(appData.getDescription());
        pluginToolData.setSchema(appData.getSchema());
        pluginToolData.setRunnables(appData.getRunnables());
        pluginToolData.setSource(appData.getSource());
        pluginToolData.setIcon(appData.getIcon());
        pluginToolData.setTags(appData.getTags());
        pluginToolData.setVersion(appData.getVersion());
        pluginToolData.setLikeCount(appData.getLikeCount());
        pluginToolData.setDownloadCount(appData.getDownloadCount());
        pluginToolData.setPluginId(pluginData.getPluginId());
        if (appData.getUniqueName() != null) {
            pluginToolData.setUniqueName(appData.getUniqueName());
        }
        pluginToolData.setDefName(appData.getDefName());
        pluginToolData.setDefGroupName(appData.getDefGroupName());
        pluginToolData.setGroupName(appData.getGroupName());
        return pluginToolData;
    }
}
