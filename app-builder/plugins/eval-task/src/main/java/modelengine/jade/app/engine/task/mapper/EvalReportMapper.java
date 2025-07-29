/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.mapper;

import modelengine.jade.app.engine.task.dto.EvalReportQueryParam;
import modelengine.jade.app.engine.task.entity.EvalReportEntity;
import modelengine.jade.app.engine.task.po.EvalReportPo;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 表示评估任务报告持久层接口。
 *
 * @author 何嘉斌
 * @since 2024-08-14
 */
@Mapper
public interface EvalReportMapper {
    /**
     * 创建评估任务报告。
     *
     * @param evalReportPo 表示评估任务报告信息的 {@link List}{@code <}{@link EvalReportPo}{@code >}。
     */
    void create(List<EvalReportPo> evalReportPo);

    /**
     * 分页查询评估任务报告元数据。
     *
     * @param queryParam 表示评估任务报告查询参数的 {@link EvalReportQueryParam}。
     * @return 表示评估任务报告元数据查询结果的 {@link List}{@code <}{@link EvalReportEntity}{@code >}。
     */
    List<EvalReportEntity> listEvalReport(EvalReportQueryParam queryParam);

    /**
     * 统计评估任务报告数量。
     *
     * @param queryParam 表示评估任务报告查询参数的 {@link EvalReportQueryParam}。
     * @return 表示评估任务报告集统计结果的 {@code int}。
     */
    int countEvalReport(EvalReportQueryParam queryParam);
}