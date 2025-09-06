/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.mapper;

import modelengine.fit.jober.aipp.aop.Locale;
import modelengine.fit.jober.aipp.po.AppBuilderFlowGraphPo;

import java.util.List;

/**
 * AppBuilder流程图映射器
 *
 * @author 邬涨财
 * @since 2024-04-16
 */
public interface AppBuilderFlowGraphMapper {
    /**
     * 通过流程图id查询流程图信息
     *
     * @param id 要查询的流程图id
     * @return AppBuilder流程图信息
     */
    @Locale
    AppBuilderFlowGraphPo selectWithId(String id);

    /**
     * 插入一条流程图信息
     *
     * @param insert 要插入的流程图信息
     */
    void insertOne(AppBuilderFlowGraphPo insert);

    /**
     * 更新一条流程图信息
     *
     * @param update 被更新的流程图信息
     */
    void updateOne(AppBuilderFlowGraphPo update);

    /**
     * 根据流程图id删除流程图
     *
     * @param ids 被删除的流程图id
     */
    void delete(List<String> ids);
}
