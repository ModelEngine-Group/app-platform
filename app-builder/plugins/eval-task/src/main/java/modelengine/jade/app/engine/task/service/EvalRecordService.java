/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.service;

import modelengine.jade.app.engine.task.dto.EvalRecordQueryParam;
import modelengine.jade.app.engine.task.entity.EvalRecordEntity;
import modelengine.jade.common.vo.PageVo;

import java.util.List;

/**
 * 表示评估任务记录服务。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
public interface EvalRecordService {
    /**
     * 创建评估任务用例结果。
     *
     * @param result 表示评估任务用例结果对象的 {@link List}{@code <}{@link EvalRecordEntity}{@code >}。
     */
    void createEvalRecord(List<EvalRecordEntity> result);

    /**
     * 分页查询评估任务用例结果。
     *
     * @param queryParam 表示评估任务用例结果查询参数的 {@link EvalRecordQueryParam}。
     * @return 表示评估任务用例结果查询结果的 {@link PageVo}{@code <}{@link EvalRecordEntity}{@code >}。
     */
    PageVo<EvalRecordEntity> listEvalRecord(EvalRecordQueryParam queryParam);

    /**
     * 通过评估用例唯一标识查询评估记录。
     *
     * @param caseIds 表示评估任务用例唯一标识的 {@link List}{@code <}{@link Long}{@code >}。
     * @return 表示查询结果评估记录业务对象的 {@link List}{@code <}{@link EvalRecordEntity}{@code >}。
     */
    List<EvalRecordEntity> getEntityByCaseIds(List<Long> caseIds);
}