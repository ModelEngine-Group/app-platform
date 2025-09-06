/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.serializer.impl;

import modelengine.fit.jober.aipp.domain.AppBuilderFlowGraph;
import modelengine.fit.jober.aipp.po.AppBuilderFlowGraphPo;
import modelengine.fit.jober.aipp.serializer.BaseSerializer;

/**
 * 应用流程图数据序列化器
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
public class AppBuilderFlowGraphSerializer implements BaseSerializer<AppBuilderFlowGraph, AppBuilderFlowGraphPo> {
    @Override
    public AppBuilderFlowGraphPo serialize(AppBuilderFlowGraph appBuilderFlowGraph) {
        if (appBuilderFlowGraph == null) {
            return null;
        }
        return AppBuilderFlowGraphPo.builder()
                .id(appBuilderFlowGraph.getId())
                .name(appBuilderFlowGraph.getName())
                .appearance(appBuilderFlowGraph.getAppearance())
                .createAt(appBuilderFlowGraph.getCreateAt())
                .updateAt(appBuilderFlowGraph.getUpdateAt())
                .createBy(appBuilderFlowGraph.getCreateBy())
                .updateBy(appBuilderFlowGraph.getUpdateBy())
                .build();
    }

    @Override
    public AppBuilderFlowGraph deserialize(AppBuilderFlowGraphPo appBuilderFlowGraphPO) {
        if (appBuilderFlowGraphPO == null) {
            return null;
        }
        return AppBuilderFlowGraph.builder()
                .id(appBuilderFlowGraphPO.getId())
                .name(appBuilderFlowGraphPO.getName())
                .appearance(appBuilderFlowGraphPO.getAppearance())
                .createAt(appBuilderFlowGraphPO.getCreateAt())
                .updateAt(appBuilderFlowGraphPO.getUpdateAt())
                .createBy(appBuilderFlowGraphPO.getCreateBy())
                .updateBy(appBuilderFlowGraphPO.getUpdateBy())
                .build();
    }
}
