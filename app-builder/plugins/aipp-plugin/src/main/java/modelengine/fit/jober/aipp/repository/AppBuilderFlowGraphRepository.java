/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.repository;

import modelengine.fit.jober.aipp.domain.AppBuilderFlowGraph;

import java.util.List;

/**
 * AppBuilder流程图持久化层
 *
 * @author 邬涨财
 * @since 2024-04-17
 */
public interface AppBuilderFlowGraphRepository {
    /**
     * 通过流程图id查询流程图信息
     *
     * @param id 要查询的流程图id
     * @return AppBuilder流程图信息
     */
    AppBuilderFlowGraph selectWithId(String id);

    /**
     * 插入一条流程图信息
     *
     * @param appBuilderFlowGraph 要插入的流程图信息
     */
    void insertOne(AppBuilderFlowGraph appBuilderFlowGraph);

    /**
     * 更新一条流程图信息
     *
     * @param appBuilderFlowGraph 被更新的流程图信息
     */
    void updateOne(AppBuilderFlowGraph appBuilderFlowGraph);

    /**
     * 根据流程图id删除流程图
     *
     * @param ids 被删除的流程图id集合
     */
    void delete(List<String> ids);
}
