/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.app.engine.task.controller;

import modelengine.jade.app.engine.task.dto.EvalReportQueryParam;
import modelengine.jade.app.engine.task.entity.EvalReportEntity;
import modelengine.jade.app.engine.task.service.EvalReportService;
import modelengine.jade.app.engine.task.vo.EvalReportVo;
import modelengine.jade.common.vo.PageVo;

import modelengine.fit.http.annotation.GetMapping;
import modelengine.fit.http.annotation.RequestBean;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.validation.Validated;

/**
 * 表示评估任务报告管理接口。
 *
 * @author 何嘉斌
 * @since 2024-08-14
 */
@Component
@RequestMapping(path = "/eval/task/report", group = "评估任务报告管理接口")
public class EvalReportController {
    private final EvalReportService evalReportService;

    /**
     * 评估任务报告管理控制器构造函数。
     *
     * @param evalReportService 表示评估任务报告服务的 {@link EvalReportService}。
     */
    public EvalReportController(EvalReportService evalReportService) {
        this.evalReportService = evalReportService;
    }

    /**
     * 查询评估评估任务报告元数据。
     *
     * @param queryParam 表示评估任务报告查询传输对象的 {@link EvalReportQueryParam}。
     * @return 表示评估任务报告查询结果的 {@link PageVo}{@code <}{@link EvalReportEntity}{@code >}。
     */
    @GetMapping(description = "查询评估数据报告元数据")
    public PageVo<EvalReportVo> queryEvalReport(@RequestBean @Validated EvalReportQueryParam queryParam) {
        return this.evalReportService.listEvalReport(queryParam);
    }
}