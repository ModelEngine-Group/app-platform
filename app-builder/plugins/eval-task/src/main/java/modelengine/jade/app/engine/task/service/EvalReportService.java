/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.service;

import modelengine.jade.app.engine.task.dto.EvalReportQueryParam;
import modelengine.jade.app.engine.task.entity.EvalReportEntity;
import modelengine.jade.app.engine.task.vo.EvalReportVo;
import modelengine.jade.common.vo.PageVo;

import java.util.List;

/**
 * 表示评估任务报告服务。
 *
 * @author 何嘉斌
 * @since 2024-08-14
 */
public interface EvalReportService {
    /**
     * 创建评估任务报告。
     *
     * @param entity 表示评估任务报告业务对象的 {@link List}{@code <}{@link EvalReportEntity}{@code >}。
     */
    void createEvalReport(List<EvalReportEntity> entity);

    /**
     * 分页查询评估任务报告元数据。
     *
     * @param queryParam 表示评估任务报告查询参数的 {@link EvalReportQueryParam}。
     * @return 表示评估任务报告元数据查询结果的 {@link PageVo}{@code <}{@link EvalReportEntity}{@code >}。
     */
    PageVo<EvalReportVo> listEvalReport(EvalReportQueryParam queryParam);
}