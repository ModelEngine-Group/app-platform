/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.mapper;

import modelengine.jade.app.engine.task.po.EvalAlgorithmPo;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 表示评估算法持久层接口。
 *
 * @author 何嘉斌
 * @since 2024-09-09
 */
@Mapper
public interface EvalAlgorithmMapper {
    /**
     * 创建评估算法。
     *
     * @param po 表示评估算法信息的 {@link List}{@code <}{@link EvalAlgorithmPo}{@code >}。
     */
    void insert(List<EvalAlgorithmPo> po);

    /**
     * 统计节点唯一标识对应的评估算法数量。
     *
     * @param nodeId 表示评估算法唯一标识的 {@link String}。
     * @return 表示评估算法统计结果的 {@code int}。
     */
    int countByNodeId(String nodeId);
}