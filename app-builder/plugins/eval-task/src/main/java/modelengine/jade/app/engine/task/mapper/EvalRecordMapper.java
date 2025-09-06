/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.mapper;

import modelengine.jade.app.engine.task.dto.EvalRecordQueryParam;
import modelengine.jade.app.engine.task.entity.EvalRecordEntity;
import modelengine.jade.app.engine.task.po.EvalRecordPo;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 表示评估任务用用例果的持久层接口。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@Mapper
public interface EvalRecordMapper {
    /**
     * 创建评估评估记录。
     *
     * @param resultPo 表示评估记录信息的 {@link List}{@code <}{@link EvalRecordPo}{@code >}。
     */
    void create(List<EvalRecordPo> resultPo);

    /**
     * 查询评估任务用例结果。
     *
     * @param queryParam 表示评估任务用例结果查询参数的 {@link EvalRecordQueryParam}。
     * @return 表示评估任务用例结果查询结果的 {@link List}{@code <}{@link EvalRecordEntity}{@code >}。
     */
    List<EvalRecordEntity> listEvalRecord(EvalRecordQueryParam queryParam);

    /**
     * 统计评估任务用例结果数量。
     *
     * @param queryParam 表示评估任务用例结果查询参数的 {@link EvalRecordQueryParam}。
     * @return 表示评估任务用例结果统计结果的 {@code int}。
     */
    int countEvalRecord(EvalRecordQueryParam queryParam);

    /**
     * 通过评估用例唯一标识查询评估记录。
     *
     * @param caseIds 表示评估任务用例唯一标识的 {@link List}{@code <}{@link Long}{@code >}。
     * @return 表示查询结果评估记录业务对象的 {@link List}{@code <}{@link EvalRecordEntity}{@code >}。
     */
    List<EvalRecordEntity> getEntityByCaseIds(List<Long> caseIds);
}