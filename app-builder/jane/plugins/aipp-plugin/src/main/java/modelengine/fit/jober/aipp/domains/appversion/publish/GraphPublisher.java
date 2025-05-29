/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.appversion.publish;

import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domain.AppBuilderFlowGraph;
import modelengine.fit.jober.aipp.domains.appversion.AppVersion;
import modelengine.fit.jober.aipp.domains.appversion.PublishContext;
import modelengine.fit.jober.aipp.repository.AppBuilderFlowGraphRepository;

import com.alibaba.fastjson.JSONObject;

import lombok.AllArgsConstructor;

/**
 * graph发布器.
 *
 * @author 张越
 * @since 2025-01-16
 */
@AllArgsConstructor
public class GraphPublisher implements Publisher {
    private final AppBuilderFlowGraphRepository flowGraphRepository;

    @Override
    public void publish(PublishContext context, AppVersion appVersion) {
        // graph数据设置版本信息.
        context.getAppearance().put(AippConst.ATTR_VERSION_KEY, context.getPublishData().getVersion());

        AppBuilderFlowGraph graph = appVersion.getFlowGraph();
        graph.setUpdateAt(context.getOperateTime());
        graph.setUpdateBy(context.getOperationContext().getOperator());
        graph.setName(context.getPublishData().getFlowGraph().getName());
        graph.setAppearance(JSONObject.toJSONString(context.getAppearance()));
        this.flowGraphRepository.updateOne(graph);
    }
}
