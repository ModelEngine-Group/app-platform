/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.service;

import modelengine.jade.app.engine.task.entity.EvalAlgorithmEntity;

import java.util.List;

/**
 * 表示评估算法服务。
 *
 * @author 何嘉斌
 * @since 2024-09-09
 */
public interface EvalAlgorithmService {
    /**
     * 检测算法节点唯一标识是否存在。
     *
     * @param nodeId 表示算法节点唯一标识的 {@link String}。
     * @return 表示检测结果的 {@link Boolean}。
     */
    Boolean exist(String nodeId);


    /**
     * 创建评估算法。
     *
     * @param entity 表示评估算法信息的 {@link List}{@code <}{@link EvalAlgorithmEntity}{@code >}。
     */
    void insert(List<EvalAlgorithmEntity> entity);
}