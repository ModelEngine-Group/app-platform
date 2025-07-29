/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jane.flow.graph.repo;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.flow.graph.entity.FlowGraphDefinition;
import modelengine.fit.jane.flow.graph.entity.FlowGraphQueryParam;
import modelengine.fit.jane.flow.graph.entity.FlowSaveEntity;
import modelengine.fit.jane.flow.graph.entity.elsa.GraphParam;
import modelengine.fit.jane.flow.graph.entity.elsa.response.GetPageResponse;
import modelengine.fit.jane.flow.graph.entity.elsa.response.SaveFlowsResponse;
import modelengine.fitframework.model.RangedResultSet;

/**
 * 定义所有使用elsaClient的场景
 *
 * @author 杨祥宇
 * @since 2023/12/13
 */
public interface FlowsGraphRepo {
    /**
     * elsa分页查询流程定义列表
     *
     * @param user 操作人
     * @param cookie cookie
     * @param graphData 流程定义图json字符串
     * @return 分页查询结果
     */
    GetPageResponse getPages(String user, String cookie, String graphData);

    /**
     * elsa保存流程
     *
     * @param user 操作人
     * @param cookie cookie
     * @param graphData 流程定义图json字符串
     * @return 保存结果
     */
    SaveFlowsResponse saveFlows(String user, String cookie, String graphData);

    /**
     * elsa保存流程
     *
     * @param flowSaveEntity 画布参数
     * @param context 操作人上下文
     * @return 返回值.0, -1
     */
    int saveFlow(FlowSaveEntity flowSaveEntity, OperationContext context);

    /**
     * elsa升级流程
     *
     * @param param 画布参数
     * @return 返回值.0, -1
     */
    int upgradeFlows(GraphParam param);

    /**
     * elsa获取流程信息
     *
     * @param flowSaveEntity 画布参数
     * @param context 操作人上下文
     * @return 流程定义图json字符串
     */
    String getFlow(FlowSaveEntity flowSaveEntity, OperationContext context);

    /**
     * elsa删除流程
     *
     * @param flowSaveEntity 画布参数
     * @param context 操作人上下文
     * @return 返回值.0, -1
     */
    int deleteFlow(FlowSaveEntity flowSaveEntity, OperationContext context);

    /**
     * getFlowList
     *
     * @param queryParam queryParam
     * @param context context
     * @return RangedResultSet<FlowGraphDefinition>
     */
    RangedResultSet<FlowGraphDefinition> getFlowList(FlowGraphQueryParam queryParam, OperationContext context);
}
