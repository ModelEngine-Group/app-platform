/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.service;

import modelengine.jade.app.engine.task.dto.EvalCaseQueryParam;
import modelengine.jade.app.engine.task.entity.EvalCaseEntity;
import modelengine.jade.app.engine.task.entity.EvalRecordEntity;
import modelengine.jade.app.engine.task.vo.EvalCaseVo;
import modelengine.jade.common.vo.PageVo;

import java.util.List;

/**
 * 表示评估任务用例服务。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
public interface EvalCaseService {
    /**
     * 创建评估任务用例。
     *
     * @param entity 表示评估任务用例业务对象的 {@link EvalCaseEntity}。
     * @param results 表示评估任务用例结果对象的 {@link List}{@code <}{@link EvalRecordEntity}{@code >}。
     */
    void createEvalCase(EvalCaseEntity entity, List<EvalRecordEntity> results);

    /**
     * 通过评估实例唯一标识查询评估用例。
     *
     * @param instanceId 表示评估任务实例唯一标识的 {@link Long}。
     * @return 表示查询结果评估任务用例业务对象的 {@link List}{@code <}{@link EvalCaseEntity}{@code >}。
     */
    List<EvalCaseEntity> getCaseByInstanceId(Long instanceId);


    /**
     * 通过评估实例唯一标识统计评估用例。
     *
     * @param instanceId 表示评估任务实例唯一标识的 {@link Long}。
     * @return 表示查询结果评估任务用例统计数据的 {@link int}。
     */
    int countByInstanceId(Long instanceId);

    /**
     * 通过评估实例唯一标识查询评估用例元数据。
     *
     * @param queryParam 表示评估用例查询参数的 {@link EvalCaseQueryParam}。
     * @return 表示评估用例元数据查询结果的 {@link PageVo}{@code <}{@link EvalCaseVo}{@code >}。
     */
    PageVo<EvalCaseVo> listEvalCase(EvalCaseQueryParam queryParam);
}